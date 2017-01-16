package com.loovjo.spg.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.plaf.synth.SynthSpinnerUI;

import com.loovjo.loo2D.utils.Vector;
import com.loovjo.spg.chem.Material;
import com.loovjo.spg.chem.Molecules;
import com.loovjo.spg.gui.machines.Machine;
import com.loovjo.spg.gui.machines.MachineBattery;
import com.loovjo.spg.gui.machines.MachineContainer;
import com.loovjo.spg.gui.machines.MachineNuclearGenerator;
import com.loovjo.spg.gui.machines.MachinePipe;
import com.loovjo.spg.utils.Textures;

public class Board implements Gui {

	private final int widthInCells = 10, heightInCells = 10;

	private CopyOnWriteArrayList<Machine> machines = new CopyOnWriteArrayList<Machine>();

	// Render settings in pixels
	private int cellSize = 32;
	private int roundSize = 90;
	private int spaceBetweenCells = 5;

	private int w, h;

	private int selX, selY;

	public boolean[] pressedKeys = new boolean[256];

	public Board() {
		machines.add(new MachineContainer(2, 2, Material.makeFromWeight(Molecules.URANIUM, 5), 5, this));
		machines.add(new MachineNuclearGenerator(2, 4, 1, 0.2, this));
		machines.add(new MachineBattery(4, 4, 5, this));
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
				if (x == selX && y == selY) {
					g.setStroke(new BasicStroke());

					g.drawRect(xPos, yPos, cellSize, cellSize);
				}
			}
		}
		if (pressedKeys[KeyEvent.VK_ALT]) {
			Machine m = getMachine(selX, selY);
			if (m != null) {
				String[] info = m.getInfo().split("\n");
				g.setColor(Color.white);
				g.setFont(new Font("Helvetica", Font.PLAIN, 12));

				for (int i = 0; i < info.length; i++) {
					int y = i * g.getFont().getSize();
					if (y > roundSize / 2) {
						g.drawString(info[i], 2 + originX, originY + y + g.getFont().getSize());
					} else {
						int y1 = y + g.getFont().getSize() / 3;
						g.drawString(info[i], 2 + originX + roundSize / 2 - (int) Math.sqrt(roundSize * y1 - y1 * y1),
								originY + y + g.getFont().getSize());
					}
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

		selX = x;
		selY = y;

		Machine m = getMachine(x, y);
		if (m != null) {
			m.clicked(button);
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
				System.out.println("Left: " + a + " from " + m1 + ":" + port1 + " (" + m2 + ":" + port2 + ")");
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

		pressedKeys[button] = true;

		if (button == KeyEvent.VK_H) {
			selX--;
		}
		if (button == KeyEvent.VK_J) {
			selY++;
		}
		if (button == KeyEvent.VK_K) {
			selY--;
		}
		if (button == KeyEvent.VK_L) {
			selX++;
		}
		if (button == KeyEvent.VK_R) {
			Machine m = getMachine(selX, selY);
			if (m != null && m instanceof MachinePipe) {
				((MachinePipe) m).clicked(pressedKeys[KeyEvent.VK_SHIFT] ? 1 : 3);
			} else {
				machines.add(new MachinePipe(selX, selY, 0, 1, 0, 0, 1, 0.7,
						this));
			}
		}
		if (button == KeyEvent.VK_X) {
			Machine m = getMachine(selX, selY);
			if (m != null) {
				machines.remove(m);
			}
		}
		if (button == KeyEvent.VK_I) {
			Machine m = getMachine(selX, selY);
			if (m != null && m instanceof MachinePipe) {
				((MachinePipe) m).inPort += pressedKeys[KeyEvent.VK_SHIFT] ? -1 : 1;
			}
		}
		if (button == KeyEvent.VK_O) {
			Machine m = getMachine(selX, selY);
			if (m != null && m instanceof MachinePipe) {
				((MachinePipe) m).outPort += pressedKeys[KeyEvent.VK_SHIFT] ? -1 : 1;
			}
		}
	}

	@Override
	public void keyReleased(int button) {
		pressedKeys[button] = false;
	}

}
