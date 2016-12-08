package com.loovjo.spg;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseWheelEvent;

import com.loovjo.loo2D.scene.Scene;
import com.loovjo.loo2D.utils.Vector;
import com.loovjo.spg.gameobject.utils.LineSegment;

public class LineSegmentTestScene implements Scene {

	public LineSegment ln = new LineSegment(new Vector(200, 200), new Vector(300, 400));

	public LineSegment ln2 = new LineSegment(new Vector(0, 0), new Vector(0, 0));
	public boolean grabbing = false;

	@Override
	public void update() {

	}

	@Override
	public void render(Graphics g, int width, int height) {
		Vector intersection = ln.intersection(ln2);
		if (intersection != null) {
			g.setColor(Color.green);
			g.fillOval((int)intersection.getX() - 10, (int)intersection.getY() - 10, 20, 20);
		}
		else
			g.setColor(Color.red);
		
		g.drawLine((int) ln.pos1.getX(), (int) ln.pos1.getY(), (int) ln.pos2.getX(), (int) ln.pos2.getY());
		g.drawLine((int) ln2.pos1.getX(), (int) ln2.pos1.getY(), (int) ln2.pos2.getX(), (int) ln2.pos2.getY());

	}

	@Override
	public void mousePressed(Vector pos, int button) {
		grabbing = !grabbing;
		mouseMoved(pos);
	}

	@Override
	public void mouseReleased(Vector pos, int button) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseMoved(Vector pos) {
		if (grabbing) {
			ln2 = new LineSegment(pos, ln2.pos2);
		}
		else
			ln2 = new LineSegment(ln2.pos1, pos);
	}

	@Override
	public void keyPressed(int keyCode) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(int keyCode) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(char key) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseWheal(MouseWheelEvent e) {
		// TODO Auto-generated method stub

	}

}
