package com.loovjo.spg;

import java.awt.Graphics;
import java.util.ArrayList;

import com.loovjo.loo2D.utils.ImageLoader;
import com.loovjo.loo2D.utils.Vector;
import com.loovjo.spg.gameobject.GameObject;
import com.loovjo.spg.gameobject.Part;
import com.loovjo.spg.gameobject.Player;

public class World {

	public float zoom = 40; // Pixel per unit

	public ArrayList<GameObject> objects;
	public Player player;
	
	public Vector camPos = new Vector(0, 0); // In units

	public int tick;
	
	public Vector camVel = new Vector(0, 0);
	public Vector camAccel = new Vector(0, 0);
	
	
	public World() {
		player = new Player();
		player.world = this;
		player.posInSpace = new Vector(0, 0);
		Part playerBody = new Part(new Vector(0, 0), new Vector(0, 0), player, 0f, 1,
				ImageLoader.getImage("/Player/Body.png"));
		Part armLeft1 = new Part(new Vector(-0.2, -0.5), new Vector(-0.3, 0), playerBody, 0, 0.5f,
				ImageLoader.getImage("/Player/Arm1.png"));
		Part armRight1 = new Part(new Vector(0.2, -0.5), new Vector(0.3, 0), playerBody, 0, 0.5f,
				ImageLoader.getImage("/Player/Arm1.png"));
		Part armLeft2 = new Part(new Vector(-0.2, 0), new Vector(-0.2, 0), armLeft1, 0, 0.4f,
				ImageLoader.getImage("/Player/Arm1.png"));
		Part armRight2 = new Part(new Vector(0.2, 0), new Vector(0.2, 0), armRight1, 0, 0.4f,
				ImageLoader.getImage("/Player/Arm1.png"));

		Part head = new Part(new Vector(0, -0.5), new Vector(0, -0.3), playerBody, 0, 0.7f,
				ImageLoader.getImage("/Player/Head.png"));

		armLeft1.connected.add(armLeft2);
		armRight1.connected.add(armRight2);

		playerBody.connected.add(armLeft1);
		playerBody.connected.add(armRight1);
		playerBody.connected.add(head);

		player.part = playerBody;
	}

	public void draw(Graphics g, int width, int height) {
		
		
		
		player.draw(g, (transformScreenToSpace(new Vector(width / 2, height / 2))).sub(camPos), width, height);
	}

	public void update() {
		tick++;

		player.part.rotation += 0.03;
		player.part.connected.get(0).rotation = 0.7f * (float) Math.sin(tick / 10f) - 0.5f;
		player.part.connected.get(1).rotation = 0.7f * (float) -Math.sin(tick / 10f) + 0.5f;
		player.part.connected.get(0).connected.get(0).rotation = 0.3f * (float) Math.sin(tick / 10f);
		player.part.connected.get(1).connected.get(0).rotation = 0.3f * (float) -Math.sin(tick / 10f);
		
		camVel = camVel.add(camAccel.div(100));
		camVel = camVel.div(1.05f);
		camPos = camPos.add(camVel);
	}

	public Vector transformSpaceToScreen(Vector space) {
		return space.mul(zoom);
	}

	public Vector transformScreenToSpace(Vector screen) {
		return screen.div(zoom);
	}

	public float transformSpaceToScreen(float n) {
		return n * zoom;
	}

	public float transformScreenToSpace(float n) {
		return n / zoom;
	}

}
