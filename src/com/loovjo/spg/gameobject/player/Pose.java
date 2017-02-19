package com.loovjo.spg.gameobject.player;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Pose {

	public static final Pose PLAYER_POSE_1 = new Pose(
			new Pose(Math.PI, new Pose(0)), // Arm 1
			new Pose(Math.PI, new Pose(0)), // Arm 2
			new Pose(Math.PI, new Pose(0)), // Leg 1
			new Pose(Math.PI, new Pose(0)), // Leg 2
			new Pose(0) // Head
	);
	public static final Pose PLAYER_POSE_2 = new Pose(
			new Pose(3 * Math.PI / 4, new Pose(-3 * Math.PI / 4)), // Arm 1
			new Pose(3 * Math.PI / 4, new Pose(-3 * Math.PI / 4)), // Arm 2
			new Pose(Math.PI / 2, new Pose(3 * Math.PI / 4)), // Leg 1
			new Pose(Math.PI / 2, new Pose(3 * Math.PI / 4)), // Leg 2
			new Pose(Math.PI / 4) // Head
	);
	

	public final List<Pose> children;

	public final Optional<Double> wantedRotation;

	public Pose(double rot, List<Pose> children) {
		this.children = children;
		wantedRotation = Optional.of(rot);
	}

	public Pose(double rot, Pose... children) {
		this.children = Arrays.asList(children);
		wantedRotation = Optional.of(rot);
	}

	public Pose(Pose... children) {
		this.children = Arrays.asList(children);
		wantedRotation = Optional.empty();
	}

	public Pose(List<Pose> children) {
		this.children = children;
		wantedRotation = Optional.empty();
	}

	@Override
	public Pose clone() {
		if (wantedRotation.isPresent())
			return new Pose(wantedRotation.get(), children.stream().map(Pose::clone).collect(Collectors.toList()));
		return new Pose(children.stream().map(Pose::clone).collect(Collectors.toList()));
	}

}
