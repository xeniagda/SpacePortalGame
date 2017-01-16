package com.loovjo.spg.gameobject.parts;

import com.loovjo.loo2D.utils.Vector;
import com.loovjo.spg.gameobject.GameObject;
import com.loovjo.spg.gameobject.Part;
import com.loovjo.spg.gui.Board;
import com.loovjo.spg.utils.Textures;

public class SpaceShipMainComputerRoom extends Part {

	public SpaceShipMainComputerRoom(GameObject owner) {
		super(new Vector(0, 0), new Vector(0, 0), owner, 0, 10, 400, Textures.SPACE_SHIP_MAIN_ROOM, Textures.SPACE_SHIP_MAIN_ROOM_COLMESH);
	}
	
	@Override
	public void update(float timeStep) {
		super.update(timeStep);
		
		if (objOwner.world.getPlayer().posInSpace.getLengthTo(getPosInSpace()) < 1) {
			objOwner.world.openGui(new Board());
		}
	}
	
}
