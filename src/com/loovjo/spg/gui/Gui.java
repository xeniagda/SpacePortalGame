package com.loovjo.spg.gui;

import java.awt.Graphics2D;

import com.loovjo.loo2D.utils.Vector;

public interface Gui {

	public void draw(Graphics2D g, int width, int height);

	public void update(float timeStep);

	public void mousePressed(Vector pos, int button);

	public void mouseReleased(Vector pos, int button);

	public void mouseMoved(Vector pos);

	public void keyPressed(int button);

	public void keyReleased(int button);
}
