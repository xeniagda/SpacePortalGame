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

	public final double transferRate;

	public MachinePipe(int x, int y, int inDirection, int outDirection, int inPort, int outPort, double transferRate,
			double capacity, Board owner) {
		super(x, y, Material.makeFromWeight(null, 0), capacity, owner);
		assert (inDirection != outDirection);
		this.inDirection = inDirection;
		this.outDirection = outDirection;
		this.transferRate = transferRate;

		this.inPort = inPort;
		this.outPort = outPort;
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

		double full = Math.min(1, content.getWeight() / (float) capacity);

		if (full > 0) {
			g.setColor(content.getColor());
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
		return super.take(m, mach, port);

	}

	@Override
	public boolean canRecieve(Material m, Machine mach, int port) {
		return super.canRecieve(m, mach, port) && (mach == null ? true : new Vector(mach.x, mach.y).equals(getStart()));
	}

	@Override
	public Material recieve(Material m, Machine mach, int port) {
		return super.recieve(m, mach, port);
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
		Material currentTransfer = Material.makeFromWeight(content.mol, transferRate * timeStep);

		if (content.empty() && owner.getMachine(getStart()) != null
				&& !(owner.getMachine(getStart()) instanceof MachinePipe)) {
			currentTransfer = Material.makeFromWeight(owner.getMachine(getStart()).getMol(inPort),
					transferRate * timeStep);
		}

		double before = content.getWeight();

		if (owner.getMachine(getStart()) != null && canRecieve(currentTransfer, owner.getMachine(getStart()), 0)) {
			owner.transfer(owner.getMachine(getStart()), inPort, this, 0, currentTransfer);
		}

		double diff = content.getWeight() - before;

		if (owner.getMachine(getDestination()) != null && diff == 0) {
			owner.transfer(this, 0, owner.getMachine(getDestination()), outPort, currentTransfer);
		}

	}

	public String toString() {
		return "MachinePipe(transferRate=" + transferRate + ",content=" + content + ",capacity=" + capacity + ")";
	}

	@Override
	public String getInfo() {
		return super.getInfo() + "\ninPort: " + inPort + "\noutPort: " + outPort + "\nTransfer per second: "
				+ transferRate + "\nIO: " + inDirection + "/" + outDirection + "\nCanRecieveFromStart: "
				+ canRecieve(Material.makeFromWeight(content.mol, 0.01), owner.getMachine(getStart()), 0) + ", "
				+ (content.empty() ? "" : content.getWeight() - capacity);
	}
}
