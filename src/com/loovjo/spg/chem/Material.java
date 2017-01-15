package com.loovjo.spg.chem;

import java.awt.Color;
import java.util.Map.Entry;

import com.loovjo.spg.chem.atom.Atom;

public class Material {

	public final Molecule mol;

	// In 5.9747*10^23 atoms. 1 amount of H is 1g
	// Check
	// https://www.wolframalpha.com/input/?i=1+%2F+(hydrogen+mass+in+grams)
	public final double amount;

	@Deprecated
	public Material(Molecule mol, double amount) {

		if (amount <= 0 || mol == null) {
			this.mol = null;
			this.amount = 0;
		} else {
			this.mol = mol;
			this.amount = amount;
		}
	}

	public float getWeight() { // In grams
		if (empty())
			return 0;
		float res = 0;
		for (Entry<Atom, Integer> part : mol.getAtoms().entrySet()) {
			res += part.getKey().number * part.getValue() * amount;
		}
		return res;
	}
	

	public boolean canMixWith(Material other) {
		if (empty() || other.empty()) {
			return true;
		}
		return other.mol.equals(mol);
	}

	public Material mix(Material other) {
		assert (canMixWith(other));

		if (empty())
			return other;
		return new Material(mol, amount + other.amount);
	}

	public Material multiply(float f) {
		return new Material(mol, amount * f);
	}

	public boolean empty() {
		return amount <= 0 || mol == null;
	}

	public String toString() {
		return empty() ? "Material(null)" : "Material(" + amount + " of " + mol + ")";
	}

	public static Material makeFromWeight(Molecule mol, double weight) {
		float weightOfOne = new Material(mol, 1).getWeight();
		return new Material(mol, weight / weightOfOne);
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Material) {
			Material o = (Material) other;
			return o.mol.equals(mol) && o.amount == amount;
		}
		return false;
	}

	public Color getColor() {
		return mol.getColor();
	}

}
