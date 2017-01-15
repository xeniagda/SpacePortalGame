package com.loovjo.spg.gui.machines;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import com.loovjo.loo2D.utils.Vector;
import com.loovjo.spg.chem.Material;
import com.loovjo.spg.gui.Board;

public class MachinePipe extends MachineContainer {

	public int inDirection; // 0 = up, 1 = right, 2 = down, 3 = left
	public int inPort;
	public int outDirection; // 0 = up, 1 = right, 2 = down, 3 = left
	public int outPort;

	public final Material transfer;

	public MachinePipe(int x, int y, int inDirection, int outDirection, Material transfer, double capacity,
			Board owner) {
		super(x, y, Material.makeFromWeight(null, 0), capacity, owner);
		assert (inDirection != outDirection);
		this.inDirection = inDirection;
		this.outDirection = outDirection;
		this.transfer = transfer;

	}

	@Override
	public void draw(Graphics2D g, int xPos, int yPos, int width, int height) {
		Vector start = owner.transformCellsToScreen(getStartMid());
		int xStart = (int) start.getX();
		int yStart = (int) start.getY();

		Vector end = owner.transformCellsToScreen(getDestinationMid());
		int xEnd = (int) end.getX();
		int yEnd = (int) end.getY();

		g.setColor(Color.black);
		g.setStroke(new BasicStroke((width + height) / 10));
		g.drawLine(xStart, yStart, xEnd, yEnd);

		int dx = xEnd - xStart;
		int dy = yEnd - yStart;

		float full = Math.min(1, content.getWeight() / (float) capacity);

		if (full > 0) {
			g.setColor(Color.red);
			g.setStroke(new BasicStroke((width + height) / 20));

			g.drawLine(xStart, yStart, (int) (xStart + dx * full), (int) (yStart + dy * full));
		}
	}

	public Vector getStart() {
		switch (inDirection & 3) {
		case 0:
			return new Vector(x, y - 1);
		case 1:
			return new Vector(x + 1, y);
		case 2:
			return new Vector(x, y + 1);
		case 3:
			return new Vector(x - 1, y);
		}
		return null;
	}

	public Vector getStartMid() {
		switch (inDirection & 3) {
		case 0:
			return new Vector(x + 0.5, y);
		case 1:
			return new Vector(x + 1, y + 0.5);
		case 2:
			return new Vector(x + 0.5, y + 1);
		case 3:
			return new Vector(x, y + 0.5);
		}
		return null;
	}

	public Vector getDestination() {
		switch (outDirection & 3) {
		case 0:
			return new Vector(x, y - 1);
		case 1:
			return new Vector(x + 1, y);
		case 2:
			return new Vector(x, y + 1);
		case 3:
			return new Vector(x - 1, y);
		}
		return null;
	}

	public Vector getDestinationMid() {
		switch (outDirection & 3) {
		case 0:
			return new Vector(x + 0.5, y);
		case 1:
			return new Vector(x + 1, y + 0.5);
		case 2:
			return new Vector(x + 0.5, y + 1);
		case 3:
			return new Vector(x, y + 0.5);
		}
		return null;
	}

	@Override
	public Material take(Material m, Machine mach, int port) {
		if (new Vector(mach.x, mach.y).equals(getDestination()))
			return super.take(m, mach, port);
		return m;
	}

	@Override
	public boolean canRecieveFrom(Machine m, int port) {
		return m == null || super.canRecieveFrom(m, port) && new Vector(m.x, m.y).equals(getStart());
	};

	@Override
	public Material recieve(Material m, Machine mach, int port) {
		if (canRecieveFrom(mach, port)) {
			return super.recieve(m, mach, port);
		}
		System.out.println("Nope!");
		return m;
	}

	@Override
	public void clicked(int button) {
		if (button == 1) {
			do {
				inDirection = (inDirection + 1) & 3;
			} while (inDirection == outDirection);
		}
		if (button == 3) {
			do {
				outDirection = (outDirection + 1) & 3;
			} while (outDirection == inDirection);
		}
	}

	@Override
	public void update(float timeStep) {
		Material currentTransfer = transfer.multiply(timeStep);

		if (owner.getMachine(getDestination()) != null)
			owner.transfer(this, 0, owner.getMachine(getDestination()), outPort, currentTransfer);

		if (owner.getMachine(getStart()) != null)
			owner.transfer(owner.getMachine(getStart()), inPort, this, 0, currentTransfer);
	}

}
