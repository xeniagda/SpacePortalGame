package com.loovjo.spg.gameobject;

import java.awt.Graphics;
import java.util.ArrayList;

import com.loovjo.loo2D.utils.Vector;
import com.loovjo.spg.World;
import com.loovjo.spg.gameobject.utils.LineSegment;

public class GameObject {
	
	public Vector posInSpace; // Center
	
	public Part part;
	
	public World world;
	
	public void draw(Graphics g, int width, int height) {
		part.draw(g, width, height);
	}
	
	public ArrayList<LineSegment> getIntersectors(LineSegment ln) {
		return part.getIntersectors(ln);
	}
	
}
