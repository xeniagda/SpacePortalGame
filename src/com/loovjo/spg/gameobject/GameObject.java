package com.loovjo.spg.gameobject;

import java.awt.Graphics;
import java.util.ArrayList;

import com.loovjo.loo2D.utils.Vector;
import com.loovjo.spg.World;
import com.loovjo.spg.gameobject.utils.CollisionLineSegment;
import com.loovjo.spg.gameobject.utils.LineSegment;

public class GameObject {

	public Vector posInSpace; // Center

	public Vector vel;

	public Part part;

	public World world;

	public String name;

	public GameObject(World world, Vector posInSpace, String name) {
		this.world = world;
		this.posInSpace = posInSpace;

		vel = new Vector(0, 0);

		this.name = name;
	}

	public void draw(Graphics g, int width, int height) {
		part.draw(g, width, height);
	}

	public void update(float timeStep) {
		part.update(timeStep);
		
		posInSpace = posInSpace.add(vel.mul(timeStep));

		// vel = vel.div((float) Math.pow(world.FRICTION, timeStep));
		
		if (vel.getLength() > World.MAX_SPEED) {
			vel.setLength(World.MAX_SPEED);
		}
		
	}

	public ArrayList<CollisionLineSegment> getIntersectors(LineSegment ln) {
		return part.getIntersectors(ln);
	}

	public void applyForce(Vector force) {

		vel = vel.add(force.div((float)getTotalWeight()));
	}

	public String toString() {
		return "GameObject(name=\"" + name + "\")";
	}

	public double getTotalWeight() {
		return part.getTotalOwnWeight();
	}

}
