package _main;

import java.awt.Color;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.ImmutableMap;

import _sim.MapLayer;
import _sim.dimension.Dim;
import _sim.dimension.DimensionBuilder;
import _sim.vectors.IVector;
import _sim.world.GameUniverse;
import _sim.world.WorldProperty;
import processing.core.PApplet;
import things.actor.Actor;
import things.blocks.basic.BasicBlock;
import things.blocks.fluid.BasicFluidBlock;
import things.form.channelsystems.signal.SignalChannelSystem;
import things.form.graph.connections.PartConnection;
import things.form.kinds.BasicKindProperty;
import things.form.kinds.settings.KindSettings;
import things.form.kinds.singlepart.SingleComponentKind;
import things.form.material.Material;
import things.form.shape.IShape;
import things.form.shape.property.ShapeProperty;
import things.form.shape.property.ShapeProperty.RollableShape;
import things.form.shape.property.ShapeProperty.Sharpness;
import utilities.graph.RelationGraph;

public class Main {

	public static void main(String[] args) {
		GameUniverse universe = new GameUniverse(UUID.randomUUID());

		doWhatever(universe);

		PApplet.runSketch(new String[] { "World" }, new WorldGraphics(universe, 15f));

		doWhateverConcurrent(universe);

	}

	public static final int ENTITY_COUNT = 1000;
	public static final int NODES_EACH = 100;

	public static void doWhateverConcurrent(GameUniverse universe) {

		// generating graphs idk why
		long time1 = System.currentTimeMillis();
		for (int x = 0; x < ENTITY_COUNT; x++) {
			RelationGraph<Integer, PartConnection> graph = new RelationGraph<>();
			for (int i = 0; i < NODES_EACH; i++) {
				graph.add(i);
			}
			for (int i = 0; i < NODES_EACH; i++) {
				for (int j = 0; j < NODES_EACH; j++) {
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

			if (x % 100 == 0) {
				RelationGraph<Integer, PartConnection> sub = graph.traverseBFS(0,
						Collections.singleton(PartConnection.HOLDING), (a) -> {
						}, (a, b) -> true);
				// System.out.println(x + ":" + sub.representation());
				// System.out.println(graph.subgraph(sub).representation());
			}
		}
		long time2 = System.currentTimeMillis();
		System.out.println(((time2 - time1) / 1000d) + " seconds"); // timing the graph processes idk
	}

	public static void doWhatever(GameUniverse universe) {

		universe.setUpWorld(DimensionBuilder.of(Dim.EARTH).createTileRectangle(0, 0, 1, 1).addProp(
				WorldProperty.LAYER_BLOCKS,
				ImmutableMap.of(MapLayer.LOWEST, BasicBlock.STONE.getDefaultState(), MapLayer.FLOOR,
						BasicBlock.STONE.getDefaultState(), MapLayer.STANDARD_LAYER,
						BasicFluidBlock.AIR.getDefaultState(), MapLayer.ROOF, BasicFluidBlock.AIR.getDefaultState())));
		universe.loadMap(universe.getTile(Dim.EARTH, 0, 0), true);

		for (int i = 0; i < 20; i++) {

			Actor singleActor = new Actor(UUID.randomUUID());
			SingleComponentKind kind = new SingleComponentKind("diva");
			singleActor.setKind(kind);
			singleActor
					.makeBody(KindSettings.builder().prop(BasicKindProperty.SIZE, 1f).prop(BasicKindProperty.MASS, 20f)
							.prop(BasicKindProperty.MATERIAL, i % 2 == 0 ? Material.STONE : Material.ICE)
							.prop(BasicKindProperty.SHAPE, IShape.builder("rockshape")
									.addProperty(ShapeProperty.ROLL_SHAPE,
											i % 2 == 0 ? RollableShape.ROLLABLE_OVOID : RollableShape.NON_ROLLABLE)
									.addProperty(ShapeProperty.SHARPNESS, Sharpness.ROUNDED).build())
							.prop(BasicKindProperty.COLOR, Color.gray)
							.prop(BasicKindProperty.CHANNEL_SYSTEMS,
									Set.of(new SignalChannelSystem("nerves", Material.NERVE_TISSUE, "brain")))
							.prop(BasicKindProperty.SINGLE_PART_NAME, "brain").build(), true);
			singleActor.setPosition(IVector.of(universe.getMainMap().getMapWidth() / 2f,
					universe.getMainMap().getMapHeight() / 2f, MapLayer.STANDARD_LAYER));
			singleActor.setVelocity(
					IVector.fromAngle(universe.rand().nextDouble() * 360, 2 + 0.1 * universe.rand().nextDouble()));
			universe.getMainMap().queueAction(() -> universe.getMainMap().spawnIntoWorld(singleActor));
			System.out.println(singleActor.report());
		}
	}

}
