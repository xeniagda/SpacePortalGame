package com.loovjo.spg.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import com.loovjo.loo2D.utils.Vector;
import com.loovjo.spg.gameobject.utils.Textures;
import com.loovjo.spg.gui.machines.Machine;

public class Board implements Gui {

	private final int widthInCells = 10, heightInCells = 10;

	private ArrayList<Machine> machines = new ArrayList<Machine>();

	// Render settings in pixels
	private int cellSize = 32;
	private int roundSize = 80;
	private int spaceBetweenCells = 5;

	private int w, h;

	public Board() {
		machines.add(new Machine(2, 3, Textures.GUI_BASE_MACHINE, this) {
			public void clicked(int button) {
				System.out.println("Hej");
			}
		});
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
				int xPos = originX + (cellSize + spaceBetweenCells) * x - spaceBetweenCells / 2 + roundSize / 2;
				int yPos = originY + (cellSize + spaceBetweenCells) * y - spaceBetweenCells / 2 + roundSize / 2;

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

	@Override
	public void update(float timeStep) {

	}

	@Override
	public void mousePressed(Vector pos, int button) {
		Vector p = transformScreenToCells(pos);
		int x = (int) p.getX();
		int y = (int) p.getY();

		Machine m = getMachine(x, y);
		if (m != null) {
			m.clicked(button);
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
