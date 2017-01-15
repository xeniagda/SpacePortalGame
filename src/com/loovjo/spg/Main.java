package com.loovjo.spg;

import com.loovjo.loo2D.MainWindow;
import com.loovjo.loo2D.utils.FileLoader;
import com.loovjo.loo2D.utils.Vector;
import com.loovjo.spg.chem.atom.Atoms;

public class Main extends MainWindow {

	private static final long serialVersionUID = 5482536724482123863L;

	public Main() {
		super("SPG", new GameScene(), new Vector(1024, 768), true);
	}

	public static void main(String[] args) {
		SETUP();
		new Main();
	}

	public static void SETUP() {

		FileLoader.setLoaderClass(Main.class);
		Atoms.LOAD_ATOMS();

	}

}
