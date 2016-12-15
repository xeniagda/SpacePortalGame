package com.loovjo.spg;

import com.loovjo.loo2D.MainWindow;
import com.loovjo.loo2D.utils.FileLoader;
import com.loovjo.loo2D.utils.Vector;

public class Main extends MainWindow {
	
	private static final long serialVersionUID = 5482536724482123863L;

	public Main() {
		super("SPG", new GameScene(), new Vector(1024, 768), true);
	}

	public static void main(String[] args) {
		FileLoader.setLoaderClass(Main.class);
		DRAW_DEBUG = true;
		
		new Main();
	}
	
}
