package com.loovjo.spg.gui.machines;

import java.awt.Color;
import java.awt.Graphics2D;

import com.loovjo.spg.chem.Material;
import com.loovjo.spg.chem.Molecule;
import com.loovjo.spg.chem.Molecules;
import com.loovjo.spg.gui.Board;
import com.loovjo.spg.utils.Textures;

public class MachineNuclearGenerator extends Machine {

	// Port 0 - Material in
	// Port 1 - Energy out

	private Material energy = Material.makeFromWeight(null, 0);
	private final double energyCapacity;
	
	private final double efficiency;

	/*
	 * Efficiency = Grams of substance -> grams of energy Speed = Grams of
	 * substance / second
	 */
	public MachineNuclearGenerator(int x, int y, double efficiency, double energyCapacity, Board owner) {
		super(x, y, owner);
		this.efficiency = efficiency;
		this.energyCapacity = energyCapacity;
	}

	@Override
	public void draw(Graphics2D g, int posX, int posY, int width, int height) {
		float green = (float) (energy.getWeight() / energyCapacity);
		g.setColor(new Color(0, green, 0.5f * (1 - green)));

		int size = (int) (0.676 * height * (energy.getWeight() / energyCapacity));

		g.fillRect(posX + width / 8, posY + (int) (0.828 * width - size), 3 * width / 4, (int) (size));
		g.drawImage(Textures.MACHINE_NUCLEAR.toBufferedImage(), posX, posY, width, height, null);

	}

	@Override
	public Material take(Material m, Machine mach, int port) {

		if (port == 1 && energy.canMixWith(m) && !energy.empty()) {
			double amount = Math.min(m.getWeight(), energy.getWeight());

			energy = Material.makeFromWeight(energy.mol, energy.getWeight() - m.getWeight());
			return Material.makeFromWeight(energy.mol, amount);
		}

		return Material.makeFromWeight(null, 0);
	}

	@Override
	public Material recieve(Material m, Machine mach, int port) {
		if (port == 0 && m.getRadioActivity() > 0) {
			energy = energy
					.mix(Material.makeFromWeight(Molecules.ENERGON, m.getWeight() * m.getRadioActivity() * efficiency));
			
			if (energy.getWeight() > energyCapacity) {
				energy = Material.makeFromWeight(energy.mol, energyCapacity);
				
				return m;
			}
			return Material.makeFromWeight(null, 0);
		}
		return m;
	}

	public String toString() {
		return "MachineNuclearGenerator(eff=" + efficiency + ", capacity=" + energyCapacity + ", stored=" + energy
				+ ")";
	}
	
	public String getInfo() {
		return super.getInfo() + "\nEnergy: " + energy + "\nCapcity: " + energyCapacity + "\nEfficiency: " + efficiency;
	}
	
	public Molecule getMol(int port) {
		if (port == 0)
			return null;
		if (port == 1)
			return Molecules.ENERGON;
		return null;
	}
	
}
