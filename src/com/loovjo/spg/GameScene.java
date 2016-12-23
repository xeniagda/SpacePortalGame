package com.loovjo.spg;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.Stack;

import com.loovjo.loo2D.scene.Scene;
import com.loovjo.loo2D.utils.Vector;
import com.loovjo.spg.gameobject.GameObject;
import com.loovjo.spg.gameobject.Part;
import com.loovjo.spg.gameobject.utils.CollisionLineSegment;
import com.loovjo.spg.gameobject.utils.LineSegment;

public class GameScene implements Scene {

	public float SPEED_FAST = 0.001f;
	public float SPEED_SLOW = 0.00001f;

	public World world;

	public int holding = -1;

	public Vector lastPos = new Vector(0, 0), currentPos = new Vector(0, 0);

	public boolean paused = true;

	public Thread updateThread;

	public float speed = 0.001f;

	public boolean goingFast = true;

	public GameScene() {

		load();
		
		updateThread = new Thread(new Runnable() {

			@Override
			public void run() {

				long lastTime = System.currentTimeMillis();

				while (true) {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					long delta = System.currentTimeMillis() - lastTime;
					lastTime = System.currentTimeMillis();

					update_(delta * speed);

				}
			}
		});
		updateThread.start();
	}

	public void load() {
		world = new World();
	}

	@Override
	public void update() {
	}

	public void update_(float timeStep) {
		if (!paused)
			world.updateWorld(timeStep);

		world.updateCamera(timeStep);
	}

	@Override
	public void render(Graphics g_, int width, int height) {
		world.draw(g_, width, height);

		Graphics2D g = (Graphics2D) g_;

		ArrayList<CollisionLineSegment> intersectors = world.getCollisions(
				new LineSegment(world.transformScreenToSpace(lastPos), world.transformScreenToSpace(currentPos)));

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
		if (holding == 3) {
			for (int i = 0; i < 3; i++) {
				Vector force = currentPos.sub(lastPos).div((float) Math.pow(10, i));
				if (intersectors.size() == 0)
					g.setColor(new Color[] { Color.red, Color.blue, Color.green }[i]);
				g.setStroke(new BasicStroke(i * 2));
				g.drawLine((int) lastPos.getX(), (int) lastPos.getY(), (int) (lastPos.getX() + force.getX()),
						(int) (lastPos.getY() + force.getY()));
			}
		}
		int fontSize = 12;
		g.setFont(new Font("Helvetica Nueue", Font.BOLD, fontSize));
		g.setColor(Color.white);
		
		g.drawString("Active: " + (world.active == null ? "None" : world.active.getID()), 0, fontSize);
		g.drawString("Speed: " + speed, 0, 2 * fontSize);

	}

	@Override
	public void mousePressed(Vector pos, int button) {
		if (holding == -1) {
			lastPos = pos;
		}
		if (button == 1) {
			Vector posInSpace = world.transformScreenToSpace(pos);

			Part closest = null;
			float distance = 0;

			Stack<Part> parts = new Stack<Part>();
			for (GameObject obj : world.objects) {
				parts.add(obj.part);
			}
			while (parts.size() > 0) {
				Part part = parts.pop();
				part.connected.forEach(p -> parts.add(p));
				float dist = part.getPosInSpace().getLengthTo(posInSpace);
				if (closest == null || dist < distance) {
					closest = part;
					distance = dist;
				}
			}
			world.active = closest;
		}

		holding = button;
	}

	@Override
	public void mouseReleased(Vector pos, int button) {
		if (holding == 3) {
			Vector diff = world.transformScreenToSpace(currentPos).sub(world.transformScreenToSpace(lastPos));

			world.active.applyForce(diff, world.transformScreenToSpace(currentPos));

			lastPos = pos;

		}
		holding = -1;
	}

	@Override
	public void mouseMoved(Vector pos) {
		if (holding == 1) {
			Vector delta = lastPos.sub(pos);
			world.camVel = world.camVel.add(delta.div(world.zoom).mul(12));
			lastPos = pos;
		}
		if (holding == -1) {
			lastPos = currentPos;
		}
		currentPos = pos;

	}

	@Override
	public void keyPressed(int keyCode) {

		if (keyCode == KeyEvent.VK_SPACE)
			world.camPos = world.getPlayer().posInSpace;
		if (keyCode == KeyEvent.VK_ESCAPE)
			paused = !paused;

		if (keyCode == KeyEvent.VK_1)
			world.zoom *= 1.3;
		if (keyCode == KeyEvent.VK_2)
			world.zoom /= 1.3;

		if (keyCode == KeyEvent.VK_R) {
			load();
		}

		if (keyCode == KeyEvent.VK_Z)
			world.getPlayer().part.applyRotationForce(0.1);
		if (keyCode == KeyEvent.VK_X)
			world.getPlayer().part.applyRotationForce(-0.1);
		if (keyCode == KeyEvent.VK_8) {
			speed *= 10;
		}
		if (keyCode == KeyEvent.VK_9) {
			speed /= 10;
		}
		if (keyCode == KeyEvent.VK_0) {
			speed = SPEED_SLOW;
		}
		if (keyCode == KeyEvent.VK_T) {
			if (world.active == null) {
				world.active = world.objects.get(0).part;
			} else {
				int idx = world.objects.indexOf(world.active.objOwner) + 1;
				if (idx == world.objects.size())
					idx = 0;
				world.active = world.objects.get(idx).part;
			}
		}
		if (keyCode == KeyEvent.VK_S) {
			world.updateWorld(speed);
		}
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

	}

}
