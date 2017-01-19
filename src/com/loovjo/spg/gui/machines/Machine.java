package com.loovjo.spg.gui.machines;

import java.awt.Graphics2D;

import com.loovjo.spg.chem.Material;
import com.loovjo.spg.chem.Molecule;
import com.loovjo.spg.gui.Board;

public abstract class Machine {

	public final int x, y;

	public Board owner;
	
	
	/*
	 * Gives true if this machine can recieve m from mach in port.
	 * If m or mach is null, this should return true.
	 */
	public boolean canRecieve(Material m, Machine mach, int port) {
		return true;
	}
	
	public Material recieve(Material m, Machine mach, int port) {
		return Material.makeFromWeight(null, 0);
	}
	/*
	 * Same as canRecieve, but instead of recieving, it's taking
	 */
	public boolean canTake(Material m, Machine mach, int port) {
		return true;
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
