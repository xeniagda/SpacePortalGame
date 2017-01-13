package com.loovjo.spg.chem;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.loovjo.loo2D.utils.FileLoader;
import com.loovjo.spg.Main;
import com.loovjo.spg.chem.atom.Atoms;

public class ChemistryTesting {

	@Before
	public void setup() {
		FileLoader.setLoaderClass(Main.class);
		Atoms.LOAD_ATOMS();
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
		assertEquals(1d, new Material(Molecule.makeMolecule(Atoms.getFromSymbol("H"), 1), 1).getWeight(), 0.0001);
		assertEquals(220d, new Material(Molecules.CARBON_DIOXIDE, 10).getWeight(), 0.0001);
		assertEquals(Math.PI, Material.makeFromWeight(Molecules.CARBON_DIOXIDE, Math.PI).getWeight(), 0.0001);
	}

}
