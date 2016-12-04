package com.loovjo.spg.gameobject;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import com.loovjo.loo2D.utils.FastImage;
import com.loovjo.loo2D.utils.Vector;

public class Part {

	public boolean RENDER_DEBUG = false;

	public Vector connectionRelativeToOwner; // Center
	public Vector connectionRelativeToMe; // Compared to center

	public float size;

	public Part owner;
	public GameObject objOwner;

	public float rotation; // Radians

	public FastImage texture;

	public ArrayList<Part> connected = new ArrayList<Part>();

	public Part(Vector connectionRelativeToOwner, Vector connectionRelativeToMe, Part owner, float rotation, float size,
			FastImage texture) {
		this.connectionRelativeToOwner = connectionRelativeToOwner;
		this.connectionRelativeToMe = connectionRelativeToMe;
		this.owner = owner;
		this.rotation = rotation;
		this.size = size;
		this.texture = texture;
		this.objOwner = owner.objOwner;
	}

	public Part(Vector connectionRelativeToOwner, Vector connectionRelativeToMe, GameObject owner, float rotation,
			float size, FastImage texture) {
		this.connectionRelativeToOwner = connectionRelativeToOwner;
		this.connectionRelativeToMe = connectionRelativeToMe;
		this.owner = null;
		this.rotation = rotation;
		this.size = size;
		this.texture = texture;
		this.objOwner = owner;
	}

	public void draw(Graphics g, Vector ownerPosInSpace, float rot, int width, int height) {
		Graphics2D g2 = (Graphics2D) g;

		float totalRot = rot + this.rotation;

		AffineTransform old = g2.getTransform();

		Vector ownerConnectionPoint = ownerPosInSpace.add(connectionRelativeToOwner.rotate(Math.toDegrees(rot)));
		Vector myPos = ownerConnectionPoint.add(connectionRelativeToMe.rotate(Math.toDegrees(totalRot)));
		Vector myPosOnScreen = objOwner.world.transformSpaceToScreen(myPos);

		g2.translate((int) myPosOnScreen.getX(), (int) myPosOnScreen.getY());
		g2.rotate(totalRot);
		g2.translate((int) -myPosOnScreen.getX(), (int) -myPosOnScreen.getY());

		g2.drawImage(texture.toBufferedImage(), (int) myPosOnScreen.getX() - getWidthInPixels() / 2,
				(int) myPosOnScreen.getY() - getHeightInPixels() / 2, getWidthInPixels(), getHeightInPixels(), null);

		g2.setTransform(old);

		if (RENDER_DEBUG) {
			Vector ownerPosOnScreen = objOwner.world.transformSpaceToScreen(ownerPosInSpace);
			g2.setColor(Color.red);
			g2.fillRect((int) ownerPosOnScreen.getX() - 5, (int) ownerPosOnScreen.getY() - 5, 10, 10);

			Vector ownerConnectionOnScreen = objOwner.world.transformSpaceToScreen(ownerConnectionPoint);
			g2.setColor(Color.green);
			g2.fillRect((int) ownerConnectionOnScreen.getX() - 5, (int) ownerConnectionOnScreen.getY() - 5, 10, 10);

			g2.setColor(Color.blue);
			g2.fillRect((int) myPosOnScreen.getX() - 5, (int) myPosOnScreen.getY() - 5, 10, 10);
		}
		
		connected.forEach(part -> part.draw(g2, myPos, totalRot, width, height));
	}

	public float getSizeWHRation() {
		return objOwner.world.transformSpaceToScreen(size) / ((texture.getWidth() + texture.getHeight()) / 2);
	}

	public int getWidthInPixels() {
		return (int) (texture.getWidth() * getSizeWHRation());
	}

	public int getHeightInPixels() {
		return (int) (texture.getHeight() * getSizeWHRation());
	}
}
