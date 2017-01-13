package com.loovjo.spg.chem;

import java.util.Map.Entry;

import com.loovjo.spg.chem.atom.Atom;

public class Material {

	public final Molecule mol;
	
	
	// In 5.9747*10^23 atoms. 1 amount of H is 1g
	// Check https://www.wolframalpha.com/input/?i=1+%2F+(hydrogen+mass+in+grams)
	public final double amount;

	public Material(Molecule mol, double amount) {
		this.mol = mol;
		this.amount = amount;
	}

	public float getWeight() { // In grams
		float res = 0;
		for (Entry<Atom, Integer> part : mol.getAtoms().entrySet()) {
			res += part.getKey().number * part.getValue() * amount;
		}
		return res;
	}
	
	public String toString() {
		return "Material(" + amount + " of " + mol + ")";
	}
	
	public static Material makeFromWeight(Molecule mol, double wieght) {
		float weightOfOne = new Material(mol, 1).getWeight();
		return new Material(mol, wieght / weightOfOne);
	}
	
}
