package com.loovjo.spg.gui.machines;

import java.awt.Color;
import java.awt.Graphics2D;

import com.loovjo.spg.chem.Material;
import com.loovjo.spg.gui.Board;
import com.loovjo.spg.utils.Textures;

public class MachineBattery extends MachineContainer {

	public MachineBattery(int x, int y, double capacity, Board owner) {
		super(x, y, Material.makeFromWeight(null, 0), capacity, owner);
	}

	@Override
	public void draw(Graphics2D g, int posX, int posY, int width, int height) {

		g.setColor(content.getColor());
		int size = (int) (0.654 * height * (content.getWeight() / capacity));

		g.fillRect(posX + width / 8, posY + (int) (0.816 * width) - size, 3 * width / 4, size);
		g.drawImage(Textures.MACHINE_BATTERY.toBufferedImage(), posX, posY, width, height, null);

	}

	@Override
	public Material take(Material m, Machine mach, int port) {
		if (!m.empty() && m.mol.isEnergon()) {
			return super.take(m, mach, port);
		}
		return Material.makeFromWeight(null, 0);
	}

	@Override
	public Material recieve(Material m, Machine mach, int port) {
		if (!m.empty() && m.mol.isEnergon()) {
			return super.recieve(m, mach, port);
		}
		return m;
	}

}
