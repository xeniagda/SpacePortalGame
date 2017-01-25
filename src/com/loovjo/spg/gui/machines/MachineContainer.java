package com.loovjo.spg.gui.machines;

import java.awt.Graphics2D;

import com.loovjo.spg.chem.Material;
import com.loovjo.spg.chem.Molecule;
import com.loovjo.spg.gui.Board;
import com.loovjo.spg.utils.Textures;

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
	public boolean canRecieve(Material m, Machine mach, int port) {
		return m == null || content.canMixWith(m) && (content.getWeight() + 0.0000001) < capacity;
	}

	@Override
	public Material recieve(Material m, Machine mach, int port) {
		if (!canRecieve(m, mach, port))
			return m;
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

	@Override
	public boolean canTake(Material m, Machine mach, int port) {
		return m == null || mach == null || m.getWeight() > 0;
	}

	@Override
	public Material take(Material m, Machine mach, int port) {
		if (canTake(m, mach, port) && content.canMixWith(m) && !content.empty()) {
			if (content.getWeight() < m.getWeight()) {
				double weight = content.getWeight();
				content = Material.makeFromWeight(null, 0);
				return Material.makeFromWeight(m.mol, weight);
			}

			double amount = Math.min(m.getWeight(), content.getWeight());

			content = Material.makeFromWeight(content.mol, content.getWeight() - m.getWeight());
			
			return Material.makeFromWeight(m.mol, amount);
		}
		return Material.makeFromWeight(null, 0);
	}

	public String toString() {
		return "Container(content=" + content + ",capacity=" + capacity + ")";
	}

	public String getInfo() {
		return super.getInfo() + "\nContent: " + content + "\nCapacity: " + capacity;
	}

	@Override
	public Molecule getMol(int port) {
		return content.mol;
	}

}
