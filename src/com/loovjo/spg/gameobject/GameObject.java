package com.loovjo.spg.gameobject;

import java.awt.Graphics;

import com.loovjo.loo2D.utils.Vector;
import com.loovjo.spg.World;

public class GameObject {
	
	public Vector posInSpace; // Center
	
	public Part part;
	
	public World world;
	
	public void draw(Graphics g, Vector camPos, int width, int height) {
		part.draw(g, posInSpace.add(camPos), 1.2f, width, height);
	}
	
}
