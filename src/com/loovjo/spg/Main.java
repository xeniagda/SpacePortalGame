package com.loovjo.spg;

import com.loovjo.loo2D.MainWindow;
import com.loovjo.loo2D.scene.Scene;
import com.loovjo.loo2D.utils.FileLoader;
import com.loovjo.loo2D.utils.Vector;

public class Main extends MainWindow {
	
	public Main() {
		super("SPG", new GameScene(), new Vector(1024, 768), true);
	}

	public static void main(String[] args) {
		FileLoader.setLoaderClass(Main.class);
		new Main();
	}
	
}
