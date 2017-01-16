package com.loovjo.spg.chem.atom;

import java.util.ArrayList;
import java.util.Scanner;

import com.loovjo.loo2D.utils.FileLoader;

public class Atoms {

	private static ArrayList<Atom> ATOMS = new ArrayList<Atom>();

	public static void LOAD_ATOMS() {
		ATOMS.clear();

		Scanner s = new Scanner(FileLoader.getInputStream("/Chem/Atoms.txt"));
		while (s.hasNext()) {
			String line = s.nextLine();
			if (line.matches("^#.*")) {
				continue;
			}
			String[] parts = line.split("/+");
			if (parts.length == 13) {
				ATOMS.add(new Atom(Integer.parseInt(parts[0]), parts[1], parts[2], parts[3], Integer.parseInt(parts[4]),
						Integer.parseInt(parts[5]), Double.parseDouble(parts[6]), Double.parseDouble(parts[7]),
						Double.parseDouble(parts[8]), Double.parseDouble(parts[9]), Double.parseDouble(parts[10]),
						Double.parseDouble(parts[11])));
			} else if (parts.length == 12) {
				ATOMS.add(new Atom(Integer.parseInt(parts[0]), parts[1], parts[2], parts[3], -1,
						Integer.parseInt(parts[4]), Double.parseDouble(parts[5]), Double.parseDouble(parts[6]),
						Double.parseDouble(parts[7]), Double.parseDouble(parts[8]), Double.parseDouble(parts[9]),
						Double.parseDouble(parts[10])));

			}
		}
		s.close();
	}

	public static Atom getFromSymbol(String symbol) {
		assert(ATOMS.size() > 0);
		return ATOMS.stream().filter(a -> a.symbol.equalsIgnoreCase(symbol)).findAny().orElse(null);
	}

	public static Atom getFromName(String name) {
		assert(ATOMS.size() > 0);
		return ATOMS.stream().filter(a -> a.name.equalsIgnoreCase(name)).findAny().orElse(null);
	}

	public static Atom getFromNumber(int num) {
		assert(ATOMS.size() > 0);
		return ATOMS.stream().filter(a -> a.number == num).findAny().orElse(null);
	}
}
