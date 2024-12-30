package _main;

import java.util.Collections;
import java.util.UUID;

import processing.core.PApplet;
import sim.world.Dim;
import sim.world.DimensionBuilder;
import sim.world.GameUniverse;
import things.physical_form.graph.PartConnection;
import utilities.graph.RelationGraph;

public class Main {

	public static void main(String[] args) {
		GameUniverse universe = new GameUniverse(UUID.randomUUID());
		universe.setUpWorld(DimensionBuilder.of(Dim.EARTH).createTileRectangle(0, 0, 1, 1));
		universe.loadMap(universe.getTile(Dim.EARTH, 0, 0), true);
		PApplet.runSketch(new String[] { "World" }, new WorldGraphics(universe, 15f));

		long time1 = System.currentTimeMillis();
		for (int x = 0; x < 100; x++) {
			RelationGraph<Integer, PartConnection> graph = new RelationGraph<>();
			for (int i = 0; i < 1000; i++) {
				graph.add(i);
			}
			for (int i = 0; i < 1000; i++) {
				for (int j = 0; j < 1000; j++) {
					if (i == j)
						continue;
					if (universe.rand().nextFloat() < 0.99) {
						continue;
					}
					PartConnection con = PartConnection.JOINED;
					if (universe.rand().nextFloat() <= 0.4) {
						con = PartConnection.HOLDING;
					}
					graph.addEdge(i, con, j);

				}
			}

			if (x % 10 == 0) {
				System.out.println(x + ":" + graph);
				System.out
						.println(x + ":" + graph.traverseBFS(0, Collections.singleton(PartConnection.HOLDING), (a) -> {
						}, (a, b) -> true).representation());
			}
		}
		long time2 = System.currentTimeMillis();
		System.out.println(((time2 - time1) / 1000d) + " seconds");
		System.out.println(1.0 / 0.0);

	}

}
