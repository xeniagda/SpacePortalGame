package com.loovjo.spg.gui.machines;

import java.awt.Graphics2D;

import com.loovjo.spg.chem.Material;
import com.loovjo.spg.gameobject.utils.Textures;
import com.loovjo.spg.gui.Board;

public class MachineContainer extends Machine {

	public Material content;

	public final double capacity;

	public MachineContainer(int x, int y, Material content, double capacity, Board owner) {
		super(x, y, owner);
		this.content = content;
		this.capacity = capacity;
	}

	@Override
	public void draw(Graphics2D g, int posX, int posY, int width, int height) {

		g.setColor(content.getColor());

		int size = (int) (0.654 * height * (content.getWeight() / capacity));

		g.fillRect(posX + width / 8, posY + (int) (0.816 * width) - size, 3 * width / 4, size);
		g.drawImage(Textures.MACHINE_CONTAINER.toBufferedImage(), posX, posY, width, height, null);

	}

	@Override
	public boolean canRecieveFrom(Machine m, int port) {
		return content.getWeight() < capacity;
	}
	
	@Override
	public Material recieve(Material m, Machine mach, int port) {
		if (m.canMixWith(content)) {
			content = content.mix(m);

			if (content.getWeight() > capacity) {
				double difference = content.getWeight() - capacity;
				content = Material.makeFromWeight(content.mol, capacity);
				

				return Material.makeFromWeight(content.mol, difference);
			}

			return Material.makeFromWeight(null, 0);
		}
		return m;
	}

	public String toString() {
		return "Container(" + content + ")";
	}

	@Override
	public Material take(Material m, Machine mach, int port) {
		if (content.canMixWith(m) && !content.empty()) {
			double amount = Math.min(m.getWeight(), content.getWeight());

			content = Material.makeFromWeight(content.mol, content.getWeight() - m.getWeight());
			return Material.makeFromWeight(content.mol, amount);
		}
		return Material.makeFromWeight(null, 0);
	}

}
