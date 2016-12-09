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

	public Vector vel = new Vector(0, 0);

	public float size;

	public Part owner;
	public GameObject objOwner;

	public float rotation; // Radians
	public float rotationVel = 0;

	public FastImage texture;

	public ArrayList<Part> connected = new ArrayList<Part>();

	public boolean DEBUG = true;

	public Vector lastPos;

	// (0, 0) is center
	public ArrayList<LineSegment> collisionLines = new ArrayList<LineSegment>();

	public Part(Vector connectionRelativeToOwner, Vector connectionRelativeToMe, Part owner, float rotation, float size,
			FastImage texture) {
		this.connectionRelativeToOwner = connectionRelativeToOwner;
		this.connectionRelativeToMe = connectionRelativeToMe;
		this.owner = owner;
		this.rotation = rotation;
		this.size = size;
		this.texture = texture;
		this.objOwner = owner.objOwner;

		this.lastPos = owner.getPosInSpace();
		// this.collisionLines.add(new LineSegment(new Vector(-0.3, 0.3), new
		// Vector(0, -1)));
	}

	public Part(Vector connectionRelativeToOwner, Vector connectionRelativeToMe, Part owner, float rotation, float size,
			FastImage texture, FastImage colMesh) {
		this(connectionRelativeToOwner, connectionRelativeToMe, owner, rotation, size, texture);

		setColMesh(colMesh);
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

	public Part(Vector connectionRelativeToOwner, Vector connectionRelativeToMe, GameObject owner, float rotation,
			float size, FastImage texture, FastImage colMesh) {
		this(connectionRelativeToOwner, connectionRelativeToMe, owner, rotation, size, texture);

		setColMesh(colMesh);
	}

	public void setColMesh(FastImage colMesh) {

		System.out.println(getWidthInPixels());
		System.out.println(getHeightInPixels());
		System.out.println(getDimensionsInSpace());

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

		System.out.println(colMeshPixels);
		System.out.println(collisionLines);

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
			g2.setColor(Color.blue);
			g2.fillOval((int) myPosOnScreen.getX() - 5, (int) myPosOnScreen.getY(), 10, 10);
			for (LineSegment ln : getCollisionLinesInSpace()) {
				Vector pos1Screen = objOwner.world.transformSpaceToScreen(ln.pos1);
				Vector pos2Screen = objOwner.world.transformSpaceToScreen(ln.pos2);

				g2.setColor(Color.red);
				g2.setStroke(new BasicStroke(1));
				g2.drawLine((int) pos1Screen.getX(), (int) pos1Screen.getY(), (int) pos2Screen.getX(),
						(int) pos2Screen.getY());
			}
		}

		connected.forEach(part -> part.draw(g2, width, height));
	}

	public void update() {
		LineSegment spaceVel = getSpaceVel();

		rotation += rotationVel;
		rotationVel /= 1.01;

		double velRot = Math.toRadians(vel.mul(new Vector(1, -1)).getRotation());
		double diff = mod(rotation - velRot, Math.PI * 2);

		if (diff > Math.PI)
			diff -= Math.PI * 2;

		rotation -= diff * vel.getLength();

		vel = vel.div(objOwner.world.FRICTION);

		connected.forEach(Part::update);

		for (LineSegment ls : getCollisionLinesInSpace()) {
			for (GameObject obj : objOwner.world.objects) {
				if (obj != this.objOwner) {
					if (obj.posInSpace.getLengthTo(objOwner.posInSpace) < obj.part.size * 2) {
						for (CollisionLineSegment cls : obj.getIntersectors(ls)) {
							Vector collisionCenter = cls.intersection(ls);
							applyForce(collisionCenter.sub(getPosInSpace())
									.mul(-spaceVel.getLength() - 0.5f * cls.collision.getSpaceVel().getLength()));

						}
					}
				}
			}
		}

		lastPos = getPosInSpace();
	}

	public LineSegment getSpaceVel() {
		return new LineSegment(lastPos, getPosInSpace());
	}

	public double mod(double a, double b) {
		return a - b * Math.floor(a / b);
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
			return rotation + owner.getTotalRotation();
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

	// Spreaddir:
	// 0 -> both ways
	// 1 -> parent
	// 2 -> children
	public void applyForce(Vector force, float spread, int spreadDir) {
		vel = vel.add(force);
		if (spreadDir != 2) {
			if (owner != null) {
				owner.applyForce(force.mul(spread), spread, 1);
			} else {
				objOwner.applyForce(force.mul(spread));
			}
		}
		if (spreadDir != 1)
			connected.forEach(c -> c.applyForce(force.mul(spread), spread, 2));
	}

	public void applyForce(Vector force) {
		applyForce(force, objOwner.world.DEFAULT_SPREAD, 0);
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

}
