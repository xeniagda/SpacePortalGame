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
	
	public void update() {
		part.update();
		posInSpace = posInSpace.add(vel);
		
		vel = vel.div(world.FRICTION);
		
	}
	
	public ArrayList<CollisionLineSegment> getIntersectors(LineSegment ln) {
		return part.getIntersectors(ln);
	}

	public void applyForce(Vector force) {
		System.out.println("Force: " + force);
		
		vel = vel.add(force);
	}
	
	public String toString() {
		return "GameObject(name=\"" + name + "\")";
	}
	
}
