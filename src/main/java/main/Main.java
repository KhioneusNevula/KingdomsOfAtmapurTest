package main;

import processing.core.PApplet;
import sim.WorldDimension;

public class Main {

	public static void main(String[] args) {
		GameRunner world = new GameRunner(new WorldDimension("main world", 800, 500));
		PApplet.runSketch(new String[] { "World" }, new WorldGraphics(world, 30f));
	}

}
