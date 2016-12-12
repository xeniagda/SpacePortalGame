package com.loovjo.spg.gameobject;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.loovjo.loo2D.utils.FastImage;
import com.loovjo.loo2D.utils.Vector;
import com.loovjo.spg.gameobject.utils.CollisionLineSegment;
import com.loovjo.spg.gameobject.utils.LineSegment;

public class Part {

	public boolean RENDER_DEBUG = false;

	public Vector connectionRelativeToOwner; // Center
	public Vector connectionRelativeToMe; // Compared to center

	public float size;

	public Part owner;
	public GameObject objOwner;

	public float rotation; // Radians
	public float rotationVel = 0;

	public FastImage texture;

	public ArrayList<Part> connected = new ArrayList<Part>();

	public boolean DEBUG = true;

	public Vector lastPos;

	public float weight;

	// (0, 0) is center
	public ArrayList<LineSegment> collisionLines = new ArrayList<LineSegment>();

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

		this.lastPos = owner.getPosInSpace();
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

	public void setColMesh(FastImage colMesh) {

		HashMap<Integer, Vector> colMeshPixels = new HashMap<Integer, Vector>();
		Vector wh = new Vector(colMesh.getWidth(), colMesh.getHeight());

		for (int x = 0; x < colMesh.getWidth(); x++) {
			for (int y = 0; y < colMesh.getHeight(); y++) {

				if (colMesh.getRGB(x, y) != 0) {
					colMeshPixels.put(colMesh.getRGB(x, y) & 0x0000FF,
							new Vector(x, y).sub(wh.div(2)).div(wh).mul(getDimensionsInSpace()));
				}
			}
		}

		for (int r = 0; r < 256; r++) {
			if (colMeshPixels.containsKey(r) && colMeshPixels.containsKey(r + 1)) {
				Vector p1 = colMeshPixels.get(r);
				Vector p2 = colMeshPixels.get(r + 1);
				collisionLines.add(new LineSegment(p1, p2));
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

		g2.drawImage(texture.toBufferedImage(), (int) myPosOnScreen.getX() - getWidthInPixels() / 2,
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

			g2.drawString("" + (int) Math.toDegrees(getTotalRotation()), (int) myPosOnScreen.getX(),
					(int) myPosOnScreen.getY());
		}

		connected.forEach(part -> part.draw(g2, width, height));
	}

	public void update() {
		LineSegment spaceVel = getSpaceVel();

		rotation = (float) mod(rotation + rotationVel, Math.PI * 2);
		rotationVel /= 1.01;

		connected.forEach(Part::update);

		for (GameObject obj : objOwner.world.objects) {
			if (obj != this.objOwner) {
				hit: for (LineSegment ls : getCollisionLinesInSpace()) {
					for (CollisionLineSegment cls : obj.getIntersectors(ls)) {
						Vector collisionCenter = cls.intersection(ls);

						Vector colAngle = getPosInSpace().sub(collisionCenter);

						Vector bodyVel = spaceVel.getSpan();

						objOwner.applyForce(bodyVel.mul(-2));

						break hit;

					}
				}
			}
		}

		lastPos = getPosInSpace();
	}

	public LineSegment getSpaceVel() {
		return new LineSegment(lastPos, getPosInSpace());
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

	public void applyForceToParent(Vector force) {
		if (owner != null)
			owner.applyForce(force);
		else
			objOwner.applyForce(force);
	}

	// Note: May not be 100% physically accurate
	public void applyForce(Vector force) {
		System.out.println("Force: " + force);
		
		double velRot = -getRotation(force);
		
		double diff = mod(getTotalRotation() - velRot, Math.PI * 2);
		
		if (diff > Math.PI)
			diff -= Math.PI * 2;

		rotationVel -= diff * force.getLength();

		Vector pos = getPosInSpace();
		update();
		Vector delta = getPosInSpace().sub(pos);
		Vector deltaForce = force.sub(delta);
		applyForceToParent(deltaForce);
	}

	public void applyRotationForce(double d) {
		this.rotationVel += d;
	}

	public ArrayList<CollisionLineSegment> getIntersectors(LineSegment ln) {
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

}
