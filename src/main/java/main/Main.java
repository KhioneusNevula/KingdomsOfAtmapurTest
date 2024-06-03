package main;

import processing.core.PApplet;
import sim.WorldDimension;

public class Main {

	public static void main(String[] args) {
		GameUniverse universe = new GameUniverse();
		universe.setCurrentWorld(new WorldDimension("main world", 800, 500, universe));
		PApplet.runSketch(new String[] { "World" }, new WorldGraphics(universe, 15f));
	}

}
