package com.loovjo.spg.chem;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.loovjo.spg.Main;
import com.loovjo.spg.chem.atom.Atoms;

public class ChemistryTesting {

	@Before
	public void setup() {
		Main.SETUP();
	}

	@Test
	public void testAtoms() {
		assertEquals(2, Atoms.getFromSymbol("He").number);
		assertEquals("Promethium", Atoms.getFromSymbol("Pm").name);
		assertEquals("He", Atoms.getFromName("Helium").symbol);
		assertEquals("Carbon", Atoms.getFromSymbol("C").name);
		assertEquals(62, Atoms.getFromName("Samarium").number);
		assertNull(Atoms.getFromName("Nopeium"));
		assertNull(Atoms.getFromSymbol("Nl"));
		assertNull(Atoms.getFromNumber(-1));
	}
	
	@Test
	public void testMolecules() {
		assertEquals("H_12C_6O_6", Molecules.GLUCOSE.toString(false));
		assertEquals("O_2", Molecules.OXYGEN.toString(false));
		assertEquals("H_2O", Molecules.WATER.toString(false));
		assertEquals("CO_2", Molecules.CARBON_DIOXIDE.toString(false));
	}
	
	@Test
	public void testMaterial() {
		assertEquals(1d, Material.makeFromWeight(Molecule.makeMolecule(null, Atoms.getFromSymbol("H"), 1), 1).getWeight(), 0.0001);
		assertEquals(220d, Material.makeFromWeight(Molecules.CARBON_DIOXIDE, 220).getWeight(), 0.0001);
		assertEquals(Math.PI, Material.makeFromWeight(Molecules.CARBON_DIOXIDE, Math.PI).getWeight(), 0.0001);
		
		assertEquals(2, Material.makeFromWeight(Molecules.CARBON_DIOXIDE, 1).mix(Material.makeFromWeight(Molecules.CARBON_DIOXIDE, 1)).getWeight(), 0.001);
		
		assertTrue(Material.makeFromWeight(Molecules.WATER, 1).canMixWith(Material.makeFromWeight(Molecules.WATER, 2)));
		assertTrue(Material.makeFromWeight(Molecules.WATER, 3).canMixWith(Material.makeFromWeight(Molecules.WATER, 1)));
		assertTrue(Material.makeFromWeight(Molecules.WATER, 3).canMixWith(Material.makeFromWeight(null, 0)));
		
		assertEquals(Material.makeFromWeight(Molecules.CARBON_DIOXIDE, 1), Material.makeFromWeight(null, 0).mix(Material.makeFromWeight(Molecules.CARBON_DIOXIDE, 1)));
		
	}

}
