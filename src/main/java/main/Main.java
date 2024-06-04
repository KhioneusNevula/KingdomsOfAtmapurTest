package main;

import java.util.UUID;

import processing.core.PApplet;
import sim.GameMapTile;
import sim.Tile;

public class Main {

	public static void main(String[] args) {
		GameUniverse universe = new GameUniverse(UUID.randomUUID());
		universe.setCurrentTile(new GameMapTile(new Tile("overworld"), 800, 500, universe));
		PApplet.runSketch(new String[] { "World" }, new WorldGraphics(universe, 15f));
	}

}
