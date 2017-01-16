package com.loovjo.spg.gui.machines;

import java.awt.Graphics2D;

import com.loovjo.spg.chem.Material;
import com.loovjo.spg.chem.Molecule;
import com.loovjo.spg.gui.Board;

public abstract class Machine {

	public final int x, y;

	public Board owner;

	public boolean canRecieveFrom(Machine m, int port) {
		return true;
	}
	
	public Material recieve(Material m, Machine mach, int port) {
		return Material.makeFromWeight(null, 0);
	}
	
	public Material take(Material m, Machine mach, int port) {
		return Material.makeFromWeight(null, 0);
	}

	public Machine(int x, int y, Board owner) {
		this.x = x;
		this.y = y;
		this.owner = owner;
	}

	public void draw(Graphics2D g, int posX, int posY, int width, int height) {
	}

	public void update(float timeStep) {
	}

	public void clicked(int button) {

	}
	
	public String getInfo() {
		return "Coords: " + x + ", " + y;
	}

	public Molecule getMol(int inPort) {
		return null;
	}
	
}
