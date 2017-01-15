package com.loovjo.spg.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.concurrent.CopyOnWriteArrayList;

import com.loovjo.loo2D.utils.Vector;
import com.loovjo.spg.chem.Material;
import com.loovjo.spg.chem.Molecules;
import com.loovjo.spg.gameobject.utils.Textures;
import com.loovjo.spg.gui.machines.Machine;
import com.loovjo.spg.gui.machines.MachineContainer;
import com.loovjo.spg.gui.machines.MachinePipe;

public class Board implements Gui {

	private final int widthInCells = 10, heightInCells = 10;

	private CopyOnWriteArrayList<Machine> machines = new CopyOnWriteArrayList<Machine>();

	// Render settings in pixels
	private int cellSize = 32;
	private int roundSize = 80;
	private int spaceBetweenCells = 5;

	private int w, h;

	public Board() {
		machines.add(new MachineContainer(2, 2, Material.makeFromWeight(Molecules.WATER, 5), 5, this));
		machines.add(new MachineContainer(2, 4, Material.makeFromWeight(Molecules.WATER, 0.2), 0.3, this));
	}

	@Override
	public void draw(Graphics2D g, int sWidth, int sHeight) {

		int originX = (sWidth - getWidthInPixels()) / 2;
		int originY = (sHeight - getHeightInPixels()) / 2;
		this.w = sWidth;
		this.h = sHeight;

		g.setColor(Color.gray);
		g.fillRoundRect(originX, originY, getWidthInPixels(), getHeightInPixels(), roundSize, roundSize);

		g.setColor(Color.gray.darker());
		g.drawRoundRect(originX, originY, getWidthInPixels(), getHeightInPixels(), roundSize, roundSize);
		g.setColor(Color.gray.darker().darker());
		g.drawRoundRect(originX + 1, originY, getWidthInPixels() - 2, getHeightInPixels(), roundSize, roundSize);
		g.drawRoundRect(originX, originY + 1, getWidthInPixels(), getHeightInPixels() - 2, roundSize, roundSize);
		g.setColor(Color.gray.darker().darker().darker());
		g.drawRoundRect(originX + 1, originY + 1, getWidthInPixels() - 2, getHeightInPixels() - 2, roundSize,
				roundSize);

		for (int x = 0; x < widthInCells; x++) {
			for (int y = 0; y < heightInCells; y++) {
				Vector pos = transformCellsToScreen(x, y);
				int xPos = (int) pos.getX();
				int yPos = (int) pos.getY();

				Machine m = getMachine(x, y);
				if (m != null) {
					m.draw(g, xPos, yPos, cellSize, cellSize);
				} else {
					g.drawImage(Textures.GUI_CELL.toBufferedImage(), xPos, yPos, cellSize, cellSize, null);
				}
			}
		}
	}

	public Machine getMachine(int x, int y) {
		return machines.stream().filter(m -> m.x == x && m.y == y).findFirst().orElse(null);
	}

	public Machine getMachine(Vector v) {
		return getMachine((int) v.getX(), (int) v.getY());
	}

	public int getWidthInPixels() {
		return (cellSize + spaceBetweenCells) * widthInCells - spaceBetweenCells * 2 + roundSize;
	}

	public int getHeightInPixels() {
		return (cellSize + spaceBetweenCells) * widthInCells - spaceBetweenCells * 2 + roundSize;
	}

	public Vector transformScreenToCells(Vector pos) {
		return pos
				.sub(new Vector((w - getWidthInPixels()) / 2 + roundSize / 2 - spaceBetweenCells,
						(h - getHeightInPixels()) / 2 + roundSize / 2 - spaceBetweenCells))
				.div(cellSize + spaceBetweenCells);
	}

	public Vector transformCellsToScreen(double x, double y) {
		return new Vector(x, y).mul(cellSize + spaceBetweenCells)
				.add(new Vector((w - getWidthInPixels()) / 2 + roundSize / 2 - spaceBetweenCells,
						(h - getHeightInPixels()) / 2 + roundSize / 2 - spaceBetweenCells));
	}

	public Vector transformCellsToScreen(Vector v) {
		return transformCellsToScreen(v.getX(), v.getY());
	}

	@Override
	public void update(float timeStep) {
		machines.forEach(m -> m.update(timeStep));
	}

	@Override
	public void mousePressed(Vector pos, int button) {
		Vector p = transformScreenToCells(pos);
		int x = (int) p.getX();
		int y = (int) p.getY();

		Machine m = getMachine(x, y);
		if (m != null) {
			m.clicked(button);
		} else {
			machines.add(new MachinePipe(x, y, 0, 3, Material.makeFromWeight(Molecules.WATER, 1), 0.1, this));
		}
	}

	public void transfer(Machine m1, int port1, Machine m2, int port2, Material m) {
		if (!m2.canRecieveFrom(m1, port1))
			return;
		
		Material taken = m1.take(m, m2, port1);
		
		Material left = m2.recieve(taken, m1, port2);
		
		if (!left.empty()) {
			Material a = m1.recieve(left, null, port1);
			
			if (!a.empty()) {
				System.out.println("Left: " + a);
			}
		}
	}

	@Override
	public void mouseReleased(Vector pos, int button) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseMoved(Vector pos) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(int button) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(int button) {
		// TODO Auto-generated method stub

	}

}
