package com.loovjo.spg;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.loovjo.loo2D.utils.ImageLoader;
import com.loovjo.loo2D.utils.Vector;
import com.loovjo.spg.gameobject.GameObject;
import com.loovjo.spg.gameobject.Part;
import com.loovjo.spg.gameobject.Player;
import com.loovjo.spg.gameobject.utils.CollisionLineSegment;
import com.loovjo.spg.gameobject.utils.LineSegment;
import com.loovjo.spg.gameobject.utils.Textures;

public class World {

	public float zoom = 80; // Pixel per unit

	public ArrayList<GameObject> objects = new ArrayList<GameObject>();

	public Vector camPos = new Vector(0, 0); // In units

	public int tick;

	public Vector camVel = new Vector(0, 0);
	public Vector camAccel = new Vector(0, 0);

	public BufferedImage background;
	public float background_depth = 10;

	public boolean movingCamToPlayer = false;

	public int width, height;

	public float DEFAULT_SPREAD = 0.3f;
	public float FRICTION = 2f;

	public static float MAX_SPEED = 10;
	public static float MAX_ROT = 3;

	public Part active = null;

	public World() {
		try {
			background = Textures.BACKGROUND.toBufferedImage();
		} catch (Exception e) {
			e.printStackTrace();
		}

		Player player = new Player(this, new Vector(0, 0), "Player");

		Part playerBody = new Part(new Vector(0, 0), new Vector(0, 0), player, 0, 1, 1, Textures.PLAYER_BODY,
				Textures.PLAYER_BODY_COLMESH);
		Part armLeft1 = new Part(new Vector(-0.2, -0.5), new Vector(0, -0.3), playerBody, (float) Math.PI / 2 * 3, 0.5f,
				1, Textures.PLAYER_ARM, Textures.PLAYER_ARM_COLMESH);

		Part armRight1 = new Part(new Vector(0.2, -0.5), new Vector(0, -0.3), playerBody, (float) Math.PI / 2, 0.5f, 1,
				Textures.PLAYER_ARM, Textures.PLAYER_ARM_COLMESH);
		Part armLeft2 = new Part(new Vector(0, -0.2), new Vector(0, -0.2), armLeft1, 0, 0.4f, 1, Textures.PLAYER_ARM,
				Textures.PLAYER_ARM_COLMESH);
		Part armRight2 = new Part(new Vector(0, -0.2), new Vector(0, -0.2), armRight1, 0, 0.4f, 1, Textures.PLAYER_ARM,
				Textures.PLAYER_ARM_COLMESH);

		Part head = new Part(new Vector(0, -0.5), new Vector(0, -0.3), playerBody, 0, 0.7f, 1, Textures.PLAYER_HEAD,
				Textures.PLAYER_HEAD_COLMESH);

		armLeft1.setRotLimit(-Math.PI / 2, Math.PI / 2);
		armRight1.setRotLimit(-Math.PI / 2, Math.PI / 2);
		armLeft2.setRotLimit(-Math.PI, Math.PI);
		armRight2.setRotLimit(-Math.PI, Math.PI);
		head.setRotLimit(-Math.PI / 4, Math.PI / 4);

		armLeft1.connected.add(armLeft2);
		armRight1.connected.add(armRight2);

		if (true) {
			playerBody.connected.add(armLeft1);
			playerBody.connected.add(armRight1);
			playerBody.connected.add(head);
		}
		player.part = playerBody;

		loadSnake();
		loadSpaceShip();

		objects.add(player);

		active = getPlayer().part;
	}

	public void loadSpaceShip() {
		GameObject spaceShip = new GameObject(this, new Vector(0, 0), "SpaceShip");

		Part mainRoom = new Part(new Vector(0, 0), new Vector(0, 0), spaceShip, 0, 10, 400,
				Textures.SPACE_SHIP_MAIN_ROOM, Textures.SPACE_SHIP_MAIN_ROOM_COLMESH);
		spaceShip.part = mainRoom;

		objects.add(spaceShip);
	}

	public void loadSnake() {
		GameObject obj = new GameObject(this, new Vector(10, 0), "Snake");
		Part first = new Part(new Vector(0, 0), new Vector(0, 0), obj, 0, 0.7f, 10000f,
				ImageLoader.getImage("/DebugSnakeThing/Part1.png"),
				ImageLoader.getImage("/DebugSnakeThing/ColMesh.png"));
		obj.part = first;
		
		/*
		Part second = new Part(new Vector(0, 0.7), new Vector(0, 0), first, 0, 0.7f, 10000f,
				ImageLoader.getImage("/DebugSnakeThing/Part2.png"),
				ImageLoader.getImage("/DebugSnakeThing/ColMesh.png"));
		// first.connected.add(second);

		Part third = new Part(new Vector(0, 0.7), new Vector(0, 0), second, 0, 0.7f, 10000f,
				ImageLoader.getImage("/DebugSnakeThing/Part3.png"),
				ImageLoader.getImage("/DebugSnakeThing/ColMesh.png"));
		second.connected.add(third);

		Part fourth = new Part(new Vector(0, 0.7), new Vector(0, 0), third, 0, 0.7f, 10000f,
				ImageLoader.getImage("/DebugSnakeThing/Part4.png"),
				ImageLoader.getImage("/DebugSnakeThing/ColMesh.png"));
		third.connected.add(fourth);*/

		objects.add(obj);
	}

	public void draw(Graphics g, int width, int height) {

		this.width = width;
		this.height = height;

		g.drawImage(background, (int) -camPos.getX() - background.getWidth() / 2,
				(int) -camPos.getY() - background.getHeight() / 2, null);

		getPlayer().draw(g, width, height);
		objects.forEach(obj -> obj.draw(g, width, height));
	}

	public Part getPlayerLeftLastArm() {
		return getPlayer().part.connected.get(0).connected.get(0);
	}

	public void updateWorld(float timeStep) {
		tick++;
		objects.forEach(o -> o.update(timeStep));
	}

	public void updateCamera(float timeStep) {

		camVel = camVel.add(camAccel.mul(timeStep));
		camVel = camVel.div((float) Math.pow(304f, timeStep));
		camPos = camPos.add(camVel.mul(timeStep));

		if (movingCamToPlayer) {
			camVel = camVel.add(getPlayer().posInSpace.sub(camPos).mul(timeStep));
		}

	}

	public ArrayList<CollisionLineSegment> getCollisions(LineSegment line) {
		ArrayList<CollisionLineSegment> collisions = new ArrayList<CollisionLineSegment>();
		for (GameObject obj : objects)
			collisions.addAll(obj.getIntersectors(line));
		return collisions;
	}

	public Player getPlayer() {
		return (Player) objects.stream().filter(p -> p instanceof Player).findAny().get();
	}

	public Vector getCamPosCenterInSpace() {
		return (new Vector(width / 2 / zoom, height / 2 / zoom)).sub(camPos);
	}

	public Vector transformSpaceToScreen(Vector space) {
		return space.add(getCamPosCenterInSpace()).mul(zoom);
	}

	public Vector transformScreenToSpace(Vector screen) {
		return screen.div(zoom).sub(getCamPosCenterInSpace());
	}

}
