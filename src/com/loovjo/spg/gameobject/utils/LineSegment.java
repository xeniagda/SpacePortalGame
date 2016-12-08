package com.loovjo.spg.gameobject.utils;

import java.awt.geom.Line2D;

import com.loovjo.loo2D.utils.Vector;

public class LineSegment {

	public final Vector pos1, pos2;

	public LineSegment(Vector start, Vector end) {
		pos1 = start;
		pos2 = end;
	}

	public float getLength() {
		return pos1.getLengthTo(pos2);
	}

	public Vector intersection(LineSegment other) { // Gives null if no
													// intersection

		if (Line2D.linesIntersect(pos1.getX(), pos1.getY(), pos2.getX(), pos2.getY(), other.pos1.getX(),
				other.pos1.getY(), other.pos2.getX(), other.pos2.getY())) {

			float x1 = pos1.getX();
			float x2 = pos2.getX();
			float x3 = other.pos1.getX();
			float x4 = other.pos2.getX();
			float y1 = pos1.getY();
			float y2 = pos2.getY();
			float y3 = other.pos1.getY();
			float y4 = other.pos2.getY();

			return new Vector(((x1 * y2 - y1 * x2) * (x3 - x4) - (x1 - x2) * (x3 * y4 - y3 * x4))
					/ ((x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4)),

					((x1 * y2 - y1 * x2) * (y3 - y4) - (y1 - y2) * (x3 * y4 - y3 * x4))
							/ ((x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4)));
		}
		return null;
	}

	public LineSegment add(Vector v) {
		return new LineSegment(pos1.add(v), pos2.add(v));
	}

	public LineSegment sub(Vector v) {
		return new LineSegment(pos1.sub(v), pos2.sub(v));
	}
	public LineSegment mul(Vector v) {
		return new LineSegment(pos1.mul(v), pos2.mul(v));
	}
	public LineSegment div(Vector v) {
		return new LineSegment(pos1.div(v), pos2.div(v));
	}
	public LineSegment rotate(float rad) {
		double f = Math.toDegrees(rad);
		return new LineSegment(pos1.rotate(f), pos2.rotate(f));
	}

	public LineSegment clone() {
		return new LineSegment(new Vector(pos1.getX(), pos1.getY()), new Vector(pos2.getX(), pos2.getY()));
	}
	
	public String toString() {
		return "LineSegment(pos1=" + pos1 + ", pos2=" + pos2 + ")";
	}

}
