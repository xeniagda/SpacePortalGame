package com.loovjo.spg.gameobject.parts;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import com.loovjo.loo2D.utils.Vector;
import com.loovjo.spg.chem.Material;
import com.loovjo.spg.chem.Molecules;
import com.loovjo.spg.gameobject.GameObject;
import com.loovjo.spg.gameobject.Part;
import com.loovjo.spg.gui.Board;
import com.loovjo.spg.gui.machines.Machine;
import com.loovjo.spg.utils.Textures;

public class SpaceShipMainComputerRoom extends Part {

	private Board gui;

	public SpaceShipMainComputerRoom(GameObject owner) {
		super(new Vector(0, 0), new Vector(0, 0), owner, 0, 10, 400, Textures.SPACE_SHIP_MAIN_ROOM,
				Textures.SPACE_SHIP_MAIN_ROOM_COLMESH);
		gui = new Board(objOwner.world);
	
		Part part = this;
		
		gui.addMachine(new Machine(0, 0, gui) {
			@Override
			public boolean canRecieve(Material m, Machine mach, int port) {
				return m == null || m.canMixWith(Material.makeFromWeight(Molecules.ENERGON, 1));
			}
			
			@Override
			public Material recieve(Material m, Machine mach, int port) {
				if (canRecieve(m, mach, port)) {
					part.objOwner.applyForce(new Vector(0, m.getWeight()));
					
					return Material.makeFromWeight(null, 0);
				}
				return m;
			}
			
			@Override
			public void draw(Graphics2D g, int posX, int posY, int width, int height) {
				g.drawImage(Textures.MACHINE_PORT.toBufferedImage(), posX, posY, width, height, null);
			}
		});
	}

	@Override
	public void update(float timeStep) {
		super.update(timeStep);

		if (objOwner.world.getPlayer().posInSpace.getLengthTo(getPosInSpace()) < 1) {
			objOwner.world.keyBindings.put(KeyEvent.VK_SPACE, world -> {
				world.openGui(gui);
				return world;
			});
		}
		gui.update(timeStep);
	}

}
