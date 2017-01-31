package com.loovjo.spg.gameobject;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.loovjo.loo2D.utils.FastImage;
import com.loovjo.loo2D.utils.Vector;
import com.loovjo.spg.World;
import com.loovjo.spg.gameobject.utils.CollisionLineSegment;
import com.loovjo.spg.gameobject.utils.LineSegment;

public class Part {

	public static int BLUR_RES = 3;

	public Vector connectionRelativeToOwner; // Center
	public Vector connectionRelativeToMe; // Compared to center

	public float size;

	public Part owner;
	public GameObject objOwner;

	public float rotation; // Radians
	public float rotationVel = 0;

	public FastImage texture;

	public ArrayList<Part> connected = new ArrayList<Part>();

	public boolean DEBUG = false;

	public Vector lastPos;

	public float weight;

	public boolean isSpreadingForceToParents = false;
	public Vector force, origin;

	public boolean hasRotLimit = false;
	public float rotLimitMin = 0;
	public float rotLimitMax = 0;

	public float collisionTime = 0;

	private BufferedImage lastBlurred = null;

	// (0, 0) is center
	public ArrayList<LineSegment> collisionLines = new ArrayList<LineSegment>();

	private Vector __vel = new Vector(0, 0);

	public Part(Vector connectionRelativeToOwner, Vector connectionRelativeToMe, Part owner, float rotation, float size,
			float weight, FastImage texture) {
		this.connectionRelativeToOwner = connectionRelativeToOwner;
		this.connectionRelativeToMe = connectionRelativeToMe;
		this.owner = owner;
		this.rotation = rotation;
		this.size = size;
		this.texture = texture;
		this.objOwner = owner.objOwner;

		this.weight = weight;

		// this.collisionLines.add(new LineSegment(new Vector(-0.3, 0.3), new
		// Vector(0, -1)));
	}

	public Part(Vector connectionRelativeToOwner, Vector connectionRelativeToMe, Part owner, float rotation, float size,
			float weight, FastImage texture, FastImage colMesh) {
		this(connectionRelativeToOwner, connectionRelativeToMe, owner, rotation, size, weight, texture);

		setColMesh(colMesh);
	}

	public Part(Vector connectionRelativeToOwner, Vector connectionRelativeToMe, GameObject owner, float rotation,
			float size, float weight, FastImage texture) {
		this.connectionRelativeToOwner = connectionRelativeToOwner;
		this.connectionRelativeToMe = connectionRelativeToMe;
		this.owner = null;
		this.rotation = rotation;
		this.size = size;
		this.texture = texture;
		this.objOwner = owner;
		this.weight = weight;
	}

	public Part(Vector connectionRelativeToOwner, Vector connectionRelativeToMe, GameObject owner, float rotation,
			float size, float weight, FastImage texture, FastImage colMesh) {
		this(connectionRelativeToOwner, connectionRelativeToMe, owner, rotation, size, weight, texture);

		setColMesh(colMesh);
	}

	public void setRotLimit(double min, double max) {
		hasRotLimit = true;
		rotLimitMin = (float) min + rotation;
		rotLimitMax = (float) max + rotation;
	}

	public void setColMesh(FastImage colMesh) {

		HashMap<Integer, Vector> colMeshPixels = new HashMap<Integer, Vector>();
		Vector wh = new Vector(colMesh.getWidth(), colMesh.getHeight());

		for (int x = 0; x < colMesh.getWidth(); x++) {
			for (int y = 0; y < colMesh.getHeight(); y++) {

				if (colMesh.getRGB(x, y) != 0) {
					colMeshPixels.put(colMesh.getRGB(x, y) & 0x00FFFF,
							new Vector(x + 0.5, y + 0.5).sub(wh.div(2)).div(wh).mul(getDimensionsInSpace()));
				}
			}
		}
		for (int g = 0; g < 256; g++) {
			for (int b = 0; b < 255; b++) {
				int key = 256 * g + b;
				int next = 256 * g + b + 1;
				if (colMeshPixels.containsKey(key) && colMeshPixels.containsKey(next)) {
					Vector p1 = colMeshPixels.get(key);
					Vector p2 = colMeshPixels.get(next);
					collisionLines.add(new LineSegment(p1, p2));
				}
			}
		}

	}

	public void draw(Graphics g, int width, int height) {
		Graphics2D g2 = (Graphics2D) g;

		AffineTransform old = g2.getTransform();

		Vector myPos = getPosInSpace();
		Vector myPosOnScreen = objOwner.world.transformSpaceToScreen(myPos);

		g2.translate((int) myPosOnScreen.getX(), (int) myPosOnScreen.getY());
		g2.rotate(getTotalRotation());
		g2.translate((int) -myPosOnScreen.getX(), (int) -myPosOnScreen.getY());

		if (lastBlurred == null)
			lastBlurred = blurTexture();

		if (getWidthInPixels() / texture.getWidth() < 0.5) {
			if (lastBlurred.getWidth() != (int) getWidthInPixels()) {
				lastBlurred = blurTexture();
			}
		}

		BufferedImage resized = lastBlurred;

		g2.drawImage(resized, (int) myPosOnScreen.getX() - getWidthInPixels() / 2,
				(int) myPosOnScreen.getY() - getHeightInPixels() / 2, getWidthInPixels(), getHeightInPixels(), null);

		g2.setTransform(old);

		if (DEBUG) {

			boolean isActive = this == objOwner.world.active;

			g2.setColor(Color.blue);
			g2.fillOval((int) myPosOnScreen.getX() - 5, (int) myPosOnScreen.getY(), 10, 10);
			for (LineSegment ln : getCollisionLinesInSpace()) {
				Vector pos1Screen = objOwner.world.transformSpaceToScreen(ln.pos1);
				Vector pos2Screen = objOwner.world.transformSpaceToScreen(ln.pos2);
				g2.setColor(Color.red);
				if (isActive) {
					g2.setColor(Color.green);
				}
				g2.setStroke(new BasicStroke(1));
				g2.drawLine((int) pos1Screen.getX(), (int) pos1Screen.getY(), (int) pos2Screen.getX(),
						(int) pos2Screen.getY());
			}

			// g2.drawString("" + (int) Math.toDegrees(getTotalRotation()) + ",
			// " + getLastVel(), (int) myPosOnScreen.getX(), (int)
			// myPosOnScreen.getY());

			g.setColor(new Color((int) (255 - collisionTime), (int) collisionTime, 0));

			Vector velPosOnScreen = objOwner.world.transformSpaceToScreen(getPosInSpace().add(getVel()));
			g2.drawLine((int) myPosOnScreen.getX(), (int) myPosOnScreen.getY(), (int) velPosOnScreen.getX(),
					(int) velPosOnScreen.getY());

			g.setColor(Color.BLUE);
			velPosOnScreen = objOwner.world.transformSpaceToScreen(getPosInSpace().add(getStepVel()));
			g2.drawLine((int) myPosOnScreen.getX(), (int) myPosOnScreen.getY(), (int) velPosOnScreen.getX(),
					(int) velPosOnScreen.getY());
		}

		connected.forEach(part -> part.draw(g2, width, height));
	}

	// Gives a blurred version of the texture, so that when the screen is zoomed
	// out, the texture doesn't look pixelated.
	private BufferedImage blurTexture() {

		float width = getWidthInPixels(), height = getHeightInPixels();

		float wRatio = texture.getWidth() / width;
		float hRatio = texture.getWidth() / width;

		FastImage result = new FastImage((int) width, (int) height, texture.getType());
		for (int x = 0; x < result.getWidth(); x++) {
			for (int y = 0; y < result.getHeight(); y++) {
				int[] sum = new int[4];
				int iters = 0;

				for (int x_ = 0; x_ < wRatio && (x * wRatio + x_ < texture.getWidth()); x_ += BLUR_RES) {
					for (int y_ = 0; y_ < hRatio && (y * hRatio + y_ < texture.getHeight()); y_ += BLUR_RES) {

						int col = texture.getRGB((int) (x * wRatio + x_), (int) (y * hRatio + y_));
						sum[0] += (col & 0xFF000000) >> 24;
						sum[1] += (col & 0x00FF0000) >> 16;
						sum[2] += (col & 0x0000FF00) >> 8;
						sum[3] += (col & 0x000000FF);
						iters++;
					}
				}
				if (iters == 0)
					iters++; // To avoid division by zero.
				int[] res = new int[] { sum[0] / iters, sum[1] / iters, sum[2] / iters, sum[3] / iters };

				result.setRGB(x, y, (res[0] << 24) | (res[1] << 16) | (res[2] << 8) | res[3]);
			}
		}
		return result.toBufferedImage();
	}

	public void update(float timeStep) {

		Vector spaceVel = getStepVel();
		__setVel(spaceVel.div(timeStep));

		rotation = rotation + rotationVel * timeStep;
		// rotationVel /= Math.pow(objOwner.world.FRICTION, timeStep);

		if (hasRotLimit) {
			if (rotation < rotLimitMin) {
				rotation = rotLimitMin;
				rotationVel *= -0.5;
			}
			if (rotation > rotLimitMax) {
				rotation = rotLimitMax;
				rotationVel *= -0.5;
			}
		}
		/*
		 * if (rotationVel > World.MAX_ROT) rotationVel = World.MAX_ROT; if
		 * (rotationVel < -World.MAX_ROT) rotationVel = -World.MAX_ROT;
		 */

		for (GameObject obj : objOwner.world.objects) {
			if (obj == this.objOwner)
				continue;

			checkCollosion(obj.part);

		}

		collisionTime = 255;

		collisionTime = Math.max(collisionTime - timeStep * 255, 0);

		connected.forEach(part -> part.update(timeStep));

		if (isSpreadingForceToParents) {

			Vector delta = getStepVel();
			Vector deltaForce = force.sub(delta);

			applyForceToParent(deltaForce.mul(0.8f), origin);

			isSpreadingForceToParents = false;
		}

		lastPos = getPosInSpace();

	}

	// Note: This assumes that very big objects doesn't move very quickly or
	// rotates fast.
	private void checkCollosion(Part part) {

		for (LineSegment colLine : getCollisionLinesInSpace()) {

			LineSegment ls = new LineSegment(colLine.pos1,
					colLine.pos1.add(getStepVel().mul(part.size).sub(part.getStepVel().mul(size))));

			for (CollisionLineSegment cls : part.getIntersectors(ls)) {

				System.out.println(getID() + "<->" + part.getID());

				Vector back = cls.collision.getVel().mul((float) cls.collision.objOwner.getTotalWeight())
						.sub(getVel().mul((float) objOwner.getTotalWeight() * 2));

				applyForce(back, cls.collision.getPosInSpace());
				cls.collision.applyForce(back.mul(-1f), getPosInSpace());
			}
		}

		part.connected.forEach(p -> checkCollosion(p));
	}

	public String getID() {
		if (owner == null) {
			return objOwner.name;
		}
		return owner.getID() + "@" + owner.connected.indexOf(this);
	}

	public Vector getStepVel() {
		return lastPos == null ? new Vector(0, 0) : getPosInSpace().sub(lastPos);
	}

	public Vector getVel() {
		return __vel;
	}

	private void __setVel(Vector vel) {
		this.__vel = vel;
	}

	public float mod(double a, double b) {
		return (float) (a - b * Math.floor(a / b));
	}

	public List<LineSegment> getCollisionLinesInSpace() {
		float rotation = getTotalRotation();
		List<LineSegment> inSpace = collisionLines.stream().map(LineSegment::clone)
				.map(l -> l.rotate(rotation).add(getPosInSpace())).collect(Collectors.toList());

		return inSpace;
	}

	public float getTotalRotation() {
		if (owner == null)
			return rotation;
		else
			return mod(rotation + owner.getTotalRotation(), Math.PI * 2);
	}

	public Vector getPosInSpace() {
		Vector ownerPos = null;
		float rot = rotation;
		if (owner != null) {
			ownerPos = owner.getPosInSpace();
			rot = owner.getTotalRotation();
		} else
			ownerPos = objOwner.posInSpace;

		Vector ownerConnectionPoint = ownerPos.add(connectionRelativeToOwner.rotate(Math.toDegrees(rot)));
		Vector myPos = ownerConnectionPoint.add(connectionRelativeToMe.rotate(Math.toDegrees(getTotalRotation())));

		return myPos;
	}

	public float getSizeWHRatio() {
		return size * objOwner.world.zoom / ((texture.getWidth() + texture.getHeight()) / 2);
	}

	public Vector getDimensionsInSpace() {
		return objOwner.world.transformScreenToSpace(new Vector(getWidthInPixels(), getHeightInPixels()));
	}

	public int getWidthInPixels() {
		return (int) (texture.getWidth() * getSizeWHRatio());
	}

	public int getHeightInPixels() {
		return (int) (texture.getHeight() * getSizeWHRatio());
	}

	public void applyForceToParent(Vector force, Vector origin) {
		if (owner != null)
			owner.applyForce(force, origin.sub(connectionRelativeToOwner));
		else
			objOwner.applyForce(force);
	}

	// Note: May not be 100% physically accurate.
	public void applyForce(Vector force, Vector originInSpace) {

		Vector forceStartRelativeToMe = originInSpace.sub(getPosInSpace());
		Vector forceEndRelativeToMe = forceStartRelativeToMe.add(force);

		double rotDiff = mod(forceStartRelativeToMe.getRotation() - forceEndRelativeToMe.getRotation(), 360);

		if (rotDiff > 180)
			rotDiff -= 360;

		double len = (forceStartRelativeToMe.add(forceEndRelativeToMe).getLength()) / 2;

		rotationVel += rotDiff / 20 * grad(len) / getTotalOwnWeight();

		isSpreadingForceToParents = true;
		this.force = force;
		this.origin = originInSpace;
	}

	// Gives a nice gradient, (x = 0) = 0, (x -> ∞) = 1
	private double grad(double x) {
		return 2 / (1 + Math.exp(-x)) - 1;
	}

	public void applyRotationForce(double d) {
		this.rotationVel += d / getTotalOwnWeight();
	}

	public ArrayList<CollisionLineSegment> getIntersectors(LineSegment ln) {

		if (ln.pos1.getLengthTo(getPosInSpace()) > size && ln.pos2.getLengthTo(getPosInSpace()) > size)
			return new ArrayList<CollisionLineSegment>();

		ArrayList<CollisionLineSegment> intersectors = new ArrayList<CollisionLineSegment>();
		for (LineSegment ls : getCollisionLinesInSpace()) {
			if (ls.intersection(ln) != null) {
				intersectors.add(new CollisionLineSegment(ls, this));
			}
		}
		for (Part child : connected) {
			intersectors.addAll(child.getIntersectors(ln));
		}
		return intersectors;
	}

	public static float getRotation(Vector vec) {
		return (float) Math.toRadians(new Vector(vec.getY(), -vec.getX()).getRotation());
	}

	public double getTotalOwnWeight() {
		return weight + connected.stream().mapToDouble(c -> c.getTotalOwnWeight()).sum();
	}

}
