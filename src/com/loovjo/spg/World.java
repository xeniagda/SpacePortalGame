package com.loovjo.spg;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.loovjo.loo2D.utils.ImageLoader;
import com.loovjo.loo2D.utils.Vector;
import com.loovjo.spg.gameobject.GameObject;
import com.loovjo.spg.gameobject.Part;
import com.loovjo.spg.gameobject.Player;

public class World {

	public float zoom = 40; // Pixel per unit

	public ArrayList<GameObject> objects = new ArrayList<GameObject>();
	public Player player;

	public Vector camPos = new Vector(0, 0); // In units

	public int tick;

	public Vector camVel = new Vector(0, 0);
	public Vector camAccel = new Vector(0, 0);

	public BufferedImage background;
	public float background_depth = 10;

	public boolean movingCamToPlayer = false;

	public int width, height;

	public World() {
		try {
			background = ImageIO.read(Main.class.getResourceAsStream("/Space_background.jpg"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		player = new Player();
		player.world = this;
		player.posInSpace = new Vector(0, 0);
		Part playerBody = new Part(new Vector(0, 0), new Vector(0, 0), player, 0f, 1,
				ImageLoader.getImage("/Player/Body.png"));
		Part armLeft1 = new Part(new Vector(-0.2, -0.5), new Vector(-0.3, 0), playerBody, 0, 0.5f,
				ImageLoader.getImage("/Player/Arm1.png"), ImageLoader.getImage("/Player/Arm1_colmesh.png"));
		Part armRight1 = new Part(new Vector(0.2, -0.5), new Vector(0.3, 0), playerBody, 0, 0.5f,
				ImageLoader.getImage("/Player/Arm1.png"), ImageLoader.getImage("/Player/Arm1_colmesh.png"));
		Part armLeft2 = new Part(new Vector(-0.2, 0), new Vector(-0.2, 0), armLeft1, 0, 0.4f,
				ImageLoader.getImage("/Player/Arm1.png"), ImageLoader.getImage("/Player/Arm1_colmesh.png"));
		Part armRight2 = new Part(new Vector(0.2, 0), new Vector(0.2, 0), armRight1, 0, 0.4f,
				ImageLoader.getImage("/Player/Arm1.png"), ImageLoader.getImage("/Player/Arm1_colmesh.png"));

		Part head = new Part(new Vector(0, -0.5), new Vector(0, -0.3), playerBody, 0, 0.7f,
				ImageLoader.getImage("/Player/Head.png"), ImageLoader.getImage("/Player/Head_colmesh.png"));

		armLeft1.connected.add(armLeft2);
		armRight1.connected.add(armRight2);

		playerBody.connected.add(armLeft1);
		playerBody.connected.add(armRight1);
		playerBody.connected.add(head);

		player.part = playerBody;
	}

	public void draw(Graphics g, int width, int height) {

		this.width = width;
		this.height = height;

		g.drawImage(background, (int) -camPos.getX() - background.getWidth() / 2,
				(int) -camPos.getY() - background.getHeight() / 2, null);

		player.draw(g, width, height);

		objects.forEach(obj -> obj.draw(g, width, height));
	}

	public void update() {
		tick++;

		camVel = camVel.add(camAccel.div(100));
		camVel = camVel.div(1.1f);
		camPos = camPos.add(camVel);

		if (movingCamToPlayer) {
			camVel = camVel.add(player.posInSpace.sub(camPos).div(100)).div(1.1f);
			if (camPos.getLengthToSqrd(camVel) < 3)
				movingCamToPlayer = false;
		}
		
		player.part.update();
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
