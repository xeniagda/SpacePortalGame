package com.loovjo.spg.chem;

import java.awt.Color;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.loovjo.spg.chem.atom.Atom;

public class Molecule {

	private final HashMap<Atom, Integer> atoms = new HashMap<Atom, Integer>();
	private final Color color;

	public Molecule(HashMap<Atom, Integer> atoms, Color color) {
		for (Entry<Atom, Integer> e : atoms.entrySet()) {
			this.atoms.put(e.getKey(), e.getValue());
		}
		this.color =color;
	}

	public HashMap<Atom, Integer> getAtoms() { // Gives a clone
		HashMap<Atom, Integer> result = new HashMap<Atom, Integer>();

		for (Entry<Atom, Integer> e : atoms.entrySet()) {
			result.put(e.getKey(), e.getValue());
		}

		return result;
	}

	public String toString(boolean unicodeSubs) {
		String subDigits = "₀₁₂₃₄₅₆₇₈₉";
		Iterator<Atom> i = atoms.keySet().stream().sorted(new Comparator<Atom>() {
			@Override
			public int compare(Atom o1, Atom o2) {
				return o1.number - o2.number;
			}
		}).iterator();

		String res = "";

		for (; i.hasNext();) {
			Atom a = i.next();
			String count = atoms.get(a).toString();
			if (a != null) {
				res += a.symbol;
			}
			else {
				res += "null";
			}
			if (count.equals("1")) {
				continue;
			}
			if (unicodeSubs) {
				for (int j = 0; j < subDigits.length(); j++) {
					count = count.replace(Integer.toString(j), subDigits.charAt(j) + "");
				}
				res += count;
			} else {
				res += "_" + count;
			}
		}
		return res;
	}
	
	
	
	public String toString() {
		return toString(true);
	}

	public static Molecule makeMolecule(Color col, Object... obj) {
		// Make in format (col, atom, count, atom, count, atom, count)
		HashMap<Atom, Integer> atoms = new HashMap<Atom, Integer>();

		assert (obj.length % 2 == 0);

		for (int i = 0; i < obj.length; i += 2) {
			atoms.put((Atom) obj[i], (int) obj[i + 1]);
		}

		return new Molecule(atoms, col);
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Molecule) {
			Molecule o = (Molecule) other;
			return o.toString().equals(toString()); // TODO: Add proper comparing.
		}
		return false;
	}

	public Color getColor() {
		return new Color(color.getRGB());
	}

}
