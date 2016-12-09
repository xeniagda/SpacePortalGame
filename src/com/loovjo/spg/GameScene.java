package com.loovjo.spg;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;

import com.loovjo.loo2D.scene.Scene;
import com.loovjo.loo2D.utils.Vector;
import com.loovjo.spg.gameobject.utils.CollisionLineSegment;
import com.loovjo.spg.gameobject.utils.LineSegment;

public class GameScene implements Scene {

	public World world = new World();

	public Vector lastPos;
	public int holding = -1;

	public Vector lastLinePos = new Vector(0, 0);
	public Vector currentPos = new Vector(0, 0);

	@Override
	public void update() {
		world.update();
		
		if (holding == 3) {
			Vector posInSpace = world.transformScreenToSpace(currentPos);
			Vector force = posInSpace.sub(world.getPlayer().posInSpace);
			world.getPlayer().applyForce(force.div(100f));
		}
	}

	@Override
	public void render(Graphics g, int width, int height) {

		world.draw(g, width, height);

		ArrayList<CollisionLineSegment> intersectors = world.getCollisions(
				new LineSegment(world.transformScreenToSpace(lastLinePos), world.transformScreenToSpace(currentPos)));

		g.setColor(Color.green);

		for (LineSegment ln : intersectors) {
			Vector screenPos1 = world.transformSpaceToScreen(ln.pos1);
			Vector screenPos2 = world.transformSpaceToScreen(ln.pos2);
			g.drawLine((int) screenPos1.getX(), (int) screenPos1.getY(), (int) screenPos2.getX(),
					(int) screenPos2.getY());
		}
		if (intersectors.size() > 0) {
			g.setColor(Color.black);
		}
		g.drawLine((int) lastLinePos.getX(), (int) lastLinePos.getY(), (int) currentPos.getX(),
				(int) currentPos.getY());

	}

	@Override
	public void mousePressed(Vector pos, int button) {
		lastPos = pos;
		holding = button;
	}

	@Override
	public void mouseReleased(Vector pos, int button) {
		lastPos = null;
		holding = -1;
	}

	@Override
	public void mouseMoved(Vector pos) {
		if (holding == 1) {
			Vector delta = lastPos.sub(pos);
			world.camVel = world.camVel.add(delta.div(world.zoom).div(5));
			lastPos = pos;
		}
		lastLinePos = currentPos;
		currentPos = pos;
		
	}

	@Override
	public void keyPressed(int keyCode) {
		if (keyCode == KeyEvent.VK_UP)
			world.camAccel.setY(-1);
		if (keyCode == KeyEvent.VK_RIGHT)
			world.camAccel.setX(1);
		if (keyCode == KeyEvent.VK_DOWN)
			world.camAccel.setY(1);
		if (keyCode == KeyEvent.VK_LEFT)
			world.camAccel.setX(-1);
		if (keyCode == KeyEvent.VK_SPACE)
			world.movingCamToPlayer = true;

		if (keyCode == KeyEvent.VK_1)
			world.zoom *= 1.3;
		if (keyCode == KeyEvent.VK_2)
			world.zoom /= 1.3;

		if (keyCode == KeyEvent.VK_Z)
			world.getPlayer().part.applyRotationForce(0.1);
		if (keyCode == KeyEvent.VK_X)
			world.getPlayer().part.applyRotationForce(-0.1);

	}

	@Override
	public void keyReleased(int keyCode) {
		if (keyCode == KeyEvent.VK_UP)
			world.camAccel.setY(0);
		if (keyCode == KeyEvent.VK_RIGHT)
			world.camAccel.setX(0);
		if (keyCode == KeyEvent.VK_DOWN)
			world.camAccel.setY(0);
		if (keyCode == KeyEvent.VK_LEFT)
			world.camAccel.setX(0);
	}

	@Override
	public void keyTyped(char key) {

	}

	@Override
	public void mouseWheal(MouseWheelEvent e) {
		System.out.println(e);
	}

}
