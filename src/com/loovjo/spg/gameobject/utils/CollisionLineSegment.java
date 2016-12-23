package com.loovjo.spg.gameobject.utils;

import com.loovjo.loo2D.utils.Vector;
import com.loovjo.spg.gameobject.Part;

public class CollisionLineSegment extends LineSegment {

	// This class is basically a wrapper around a LineSegment and a Part, and it
	// is used to represent a collision between some objects

	public final Part collision;

	public CollisionLineSegment(Vector start, Vector end, Part collision) {
		super(start, end);
		this.collision = collision;
	}

	public CollisionLineSegment(LineSegment ls, Part collision) {
		super(ls.pos1, ls.pos2);
		this.collision = collision;
	}

	public String toString() {
		return "Cls(pos1=" + pos1 + ", pos2=" + pos2 + ", col=" + collision + ")";
	}

}
