package com.loovjo.spg.gui;

import static org.junit.Assert.*;

import org.junit.Test;

import com.loovjo.spg.Main;
import com.loovjo.spg.chem.Material;
import com.loovjo.spg.chem.Molecules;
import com.loovjo.spg.gui.machines.MachineContainer;
import com.loovjo.spg.gui.machines.MachinePipe;

public class BoardTest {

	@Test
	public void testTransferring() {
		Main.SETUP();

		Board board = new Board(null);

		float cap = 0.9999f;
		float transferAmount = 0.4f;

		MachineContainer container = new MachineContainer(0, 1, Material.makeFromWeight(Molecules.URANIUM, cap), 1,
				board);

		board.addMachine(container);

		MachinePipe outPipe = new MachinePipe(0, 2, 0, 2, 0, 0, 1, 0.7, board);
		board.addMachine(outPipe);

		MachinePipe inPipe = new MachinePipe(0, 0, 0, 2, 0, 0, 1, 0.7, board);

		board.addMachine(inPipe);

		inPipe.recieve(Material.makeFromWeight(container.getMol(0), inPipe.capacity), null, 0);

		Material transfer = Material.makeFromWeight(container.getMol(0), transferAmount);

		assertEquals(inPipe.capacity, inPipe.content.getWeight(), 0.0001);
		assertTrue(outPipe.content.empty());
		assertFalse(container.content.empty());
		assertTrue(container.canRecieve(transfer, outPipe, 0));

		System.out.println("\n");

		outPipe.recieve(Material.makeFromWeight(container.getMol(0), 1), null, 0);

		assertEquals(outPipe.capacity, outPipe.content.getWeight(), 0.00001);

		assertTrue(outPipe.canTake(transfer, container, 0));

		System.out.println("Before: " + outPipe + " -> " + transfer + " -> " + container);

		board.transfer(outPipe, 0, container, 0, transfer);

		System.out.println(container + ", " + outPipe);

		assertFalse(outPipe.content.empty());
		assertFalse(container.canRecieve(transfer, null, 0));
		assertEquals(container.capacity, container.content.getWeight(), 0.00001);
		assertEquals(outPipe.capacity - (1 - cap), outPipe.content.getWeight(), 0.00001);
		
		System.out.println("\n");
		container.take(Material.makeFromWeight(container.getMol(0), 1 - cap), null, 0);
		assertEquals(cap, container.content.getWeight(), 0.00001);
		
		System.out.println("Before: " + inPipe + " -> " + transfer + " " + container);
		
		board.transfer(inPipe, 0, container, 0, transfer);
		
		System.out.println(inPipe + ", " + container);
		
		assertTrue(inPipe.canRecieve(transfer, null, 0));
		assertFalse(container.canRecieve(transfer, null, 0));
	}

}
