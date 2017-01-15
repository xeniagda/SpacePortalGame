package com.loovjo.spg.gui;

import static org.junit.Assert.*;

import org.junit.Test;

import com.loovjo.spg.Main;
import com.loovjo.spg.chem.Material;
import com.loovjo.spg.chem.Molecules;
import com.loovjo.spg.gui.machines.MachineContainer;

public class BoardTest {

	@Test
	public void test() {
		Main.SETUP();
		
		
		Board board = new Board();
		
		board.transfer(board.getMachine(2, 2), 0, board.getMachine(2, 4), 0, Material.makeFromWeight(Molecules.WATER, 0.1));
		
		assertEquals(4.9, ((MachineContainer)board.getMachine(2, 2)).content.getWeight(), 0.0000001);
	}

}
