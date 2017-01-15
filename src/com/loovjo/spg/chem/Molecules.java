package com.loovjo.spg.chem;

import java.awt.Color;
import java.util.HashMap;

import com.loovjo.spg.chem.atom.Atom;
import com.loovjo.spg.chem.atom.Atoms;

public class Molecules {
	
	/*
	 * A list of commonly used molecules.
	 * If you're building a molecule from code, please put it here instead.
	 */
	
	public static Molecule WATER = Molecule.makeMolecule(Color.blue, Atoms.getFromSymbol("H"), 2, Atoms.getFromSymbol("O"), 1);
	public static Molecule OXYGEN = Molecule.makeMolecule(new Color(0, 0, 0, 0), Atoms.getFromSymbol("O"), 2);
	public static Molecule CARBON_DIOXIDE = Molecule.makeMolecule(new Color(0, 0, 0, 0), Atoms.getFromSymbol("O"), 2, Atoms.getFromSymbol("C"), 1);
	public static Molecule OZONE = Molecule.makeMolecule(new Color(0, 0, 0, 0), Atoms.getFromSymbol("O"), 3);
	public static Molecule GLUCOSE = Molecule.makeMolecule(Color.white, Atoms.getFromSymbol("C"), 6, Atoms.getFromSymbol("H"), 12, Atoms.getFromSymbol("O"), 6);
	
	private static class Energon extends Molecule {

		public Energon() {
			super(new HashMap<Atom, Integer>(), Color.YELLOW);
		}
		
		public String toString() {
			return "Energy()";
		}
		
	}
	public static Molecule ENERGON = new Energon();
}
