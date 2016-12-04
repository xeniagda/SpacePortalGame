package com.loovjo.spg;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;

import com.loovjo.loo2D.scene.Scene;
import com.loovjo.loo2D.utils.Vector;

public class GameScene implements Scene {

	public World world = new World();

	@Override
	public void update() {
		world.update();
	}

	@Override
	public void render(Graphics g, int width, int height) {
		world.draw(g, width, height);
	}

	@Override
	public void mousePressed(Vector pos, int button) {

	}

	@Override
	public void mouseReleased(Vector pos, int button) {

	}

	@Override
	public void mouseMoved(Vector pos) {

	}

	@Override
	public void keyPressed(int keyCode) {
		if (keyCode == KeyEvent.VK_1)
			world.zoom += 0.5;
		if (keyCode == KeyEvent.VK_2)
			world.zoom -= 0.5;
		if (keyCode == KeyEvent.VK_UP)
			world.camAccel.setY(-1);
		if (keyCode == KeyEvent.VK_RIGHT)
			world.camAccel.setX(1);
		if (keyCode == KeyEvent.VK_DOWN)
			world.camAccel.setY(1);
		if (keyCode == KeyEvent.VK_LEFT)
			world.camAccel.setX(-1);
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
