package _main;

import java.awt.Color;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.ImmutableMap;

import processing.core.PApplet;
import sim.IVector;
import sim.MapLayer;
import sim.world.Dim;
import sim.world.DimensionBuilder;
import sim.world.GameUniverse;
import sim.world.WorldProperty;
import things.actors.Actor;
import things.blocks.basic.BasicBlock;
import things.blocks.fluid.BasicFluidBlock;
import things.physical_form.channelsystems.signal.SignalChannelSystem;
import things.physical_form.graph.PartConnection;
import things.physical_form.kinds.BasicKindProperty;
import things.physical_form.kinds.KindSettings;
import things.physical_form.kinds.single.SingleComponentKind;
import things.physical_form.material.IShape;
import things.physical_form.material.Material;
import things.physical_form.material.ShapeProperty;
import things.physical_form.material.ShapeProperty.RollableShape;
import things.physical_form.material.ShapeProperty.Sharpness;
import utilities.graph.RelationGraph;

public class Main {

	public static void main(String[] args) {
		GameUniverse universe = new GameUniverse(UUID.randomUUID());

		// generating graphs idk why
		long time1 = System.currentTimeMillis();
		for (int x = 0; x < 100; x++) {
			RelationGraph<Integer, PartConnection> graph = new RelationGraph<>();
			for (int i = 0; i < 100; i++) {
				graph.add(i);
			}
			for (int i = 0; i < 100; i++) {
				for (int j = 0; j < 100; j++) {
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
				System.out
						.println(x + ":" + graph.traverseBFS(0, Collections.singleton(PartConnection.HOLDING), (a) -> {
						}, (a, b) -> true).representation());
			}
		}
		long time2 = System.currentTimeMillis();
		System.out.println(((time2 - time1) / 1000d) + " seconds"); // timing the graph processes idk

		universe.setUpWorld(DimensionBuilder.of(Dim.EARTH).createTileRectangle(0, 0, 1, 1).addProp(
				WorldProperty.LAYER_BLOCKS,
				ImmutableMap.of(MapLayer.LOWEST, BasicBlock.STONE.getDefaultState(), MapLayer.FLOOR,
						BasicBlock.ICE.getDefaultState(), MapLayer.STANDARD_LAYER,
						BasicFluidBlock.AIR.getDefaultState(), MapLayer.ROOF, BasicFluidBlock.AIR.getDefaultState())));
		universe.loadMap(universe.getTile(Dim.EARTH, 0, 0), true);
		PApplet.runSketch(new String[] { "World" }, new WorldGraphics(universe, 15f));

		for (int i = 0; i < 20; i++) {

			Actor singleActor = new Actor(UUID.randomUUID());
			SingleComponentKind kind = new SingleComponentKind("diva");
			singleActor.setKind(kind);
			singleActor
					.makeBody(KindSettings.builder().prop(BasicKindProperty.SIZE, 1f).prop(BasicKindProperty.MASS, 20f)
							.prop(BasicKindProperty.MATERIAL, Material.STONE)
							.prop(BasicKindProperty.SHAPE, IShape.builder("rockshape")
									.addProperty(ShapeProperty.ROLL_SHAPE,
											i % 2 == 0 ? RollableShape.ROLLABLE_OVOID : RollableShape.NON_ROLLABLE)
									.addProperty(ShapeProperty.SHARPNESS, Sharpness.ROUNDED).build())
							.prop(BasicKindProperty.COLOR, Color.gray)
							.prop(BasicKindProperty.CHANNEL_SYSTEMS,
									Set.of(new SignalChannelSystem("nerves", Material.NERVE_TISSUE, "brain")))
							.prop(BasicKindProperty.SINGLE_PART_NAME, "brain").build(), true);
			singleActor.setPosition(IVector.of(4.5, 4.5, MapLayer.STANDARD_LAYER));
			singleActor.setVelocity(
					IVector.fromAngle(universe.rand().nextDouble() * 360, 2 + 5 * universe.rand().nextDouble()));
			universe.getMainMap().spawnIntoWorld(singleActor);
			System.out.println(singleActor.report());
		}

	}

}
