package com.loovjo.spg.chem.atom;

public class Atom {

	Atom(int number, String symbol, String name, String origin, int group, int period, double density,
			double meltPoint, double boilPoint, double heatCapacity, double electroNegativity, double earthAmount) {
		this.number = number;
		this.symbol = symbol;
		this.name = name;
		this.origin = origin;
		this.group = group;
		this.period = period;
		this.density = density;
		this.meltPoint = meltPoint;
		this.boilPoint = boilPoint;
		this.heatCapacity = heatCapacity;
		this.electroNegativity = electroNegativity;
		this.earthAmount = earthAmount;
	}

	public final int number;
	public final String symbol;
	public final String name;
	public final String origin;
	public final int group;
	public final int period;
	public final double density;
	public final double meltPoint;
	public final double boilPoint;
	public final double heatCapacity;
	public final double electroNegativity;
	public final double earthAmount;

	public String toString() {
		return String.format("Atom([%d %s] %s \"%s\")", this.number, this.symbol, this.name, this.origin);
	}
}
