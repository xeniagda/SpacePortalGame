package com.loovjo.spg.gui.machines;

import java.awt.Graphics2D;

import com.loovjo.loo2D.utils.FastImage;
import com.loovjo.spg.gui.Board;

public abstract class Machine {
	
	public final int x, y;
	
	public Board owner;
	public FastImage texture;
	
	
	//public void takeIn(Material m) {
		
	//}
	
	public Machine(int x, int y, FastImage texture, Board owner) {
		this.x = x;
		this.y = y;
		this.owner = owner;
		this.texture = texture;
	}
	
	public void draw(Graphics2D g, int posX, int posY, int width, int height) {
		g.drawImage(texture.toBufferedImage(), posX, posY, width, height, null);
	}
	
	public void clicked(int button) {
		
	}
	
}
