package com.loovjo.spg.chem;

import com.loovjo.spg.chem.atom.Atoms;

public class Molecules {
	
	/*
	 * A list of commonly used molecules.
	 * If you're building a molecule from code, please put it here instead.
	 */
	
	public static Molecule WATER = Molecule.makeMolecule(Atoms.getFromSymbol("H"), 2, Atoms.getFromSymbol("O"), 1);
	public static Molecule OXYGEN = Molecule.makeMolecule(Atoms.getFromSymbol("O"), 2);
	public static Molecule CARBON_DIOXIDE = Molecule.makeMolecule(Atoms.getFromSymbol("O"), 2, Atoms.getFromSymbol("C"), 1);
	public static Molecule OZONE = Molecule.makeMolecule(Atoms.getFromSymbol("O"), 3);
	public static Molecule GLUCOSE = Molecule.makeMolecule(Atoms.getFromSymbol("C"), 6, Atoms.getFromSymbol("H"), 12, Atoms.getFromSymbol("O"), 6);
	
}
