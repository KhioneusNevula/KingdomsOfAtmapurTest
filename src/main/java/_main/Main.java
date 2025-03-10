package _main;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.ImmutableMap;

import _sim.MapLayer;
import _sim.dimension.Dim;
import _sim.dimension.DimensionBuilder;
import _sim.vectors.IVector;
import _sim.world.GameUniverse;
import _sim.world.WorldProperty;
import _utilities.couplets.Triplet;
import _utilities.graph.IRelationGraph;
import _utilities.graph.RelationGraph;
import processing.core.PApplet;
import things.actor.Actor;
import things.actor.categories.HumanoidKind;
import things.actor.categories.StickToolKind;
import things.blocks.basic.BasicBlock;
import things.blocks.fluid.BasicFluidBlock;
import things.form.graph.connections.PartConnection;
import things.form.kinds.BasicKindProperties;
import things.form.kinds.settings.KindSettings;
import things.form.material.Material;
import things.form.shape.IShape;
import things.form.shape.property.ShapeProperty;
import things.form.shape.property.ShapeProperty.RollableShape;
import thinker.concepts.IConcept;
import thinker.concepts.general_types.ILogicConcept;
import thinker.concepts.general_types.IProfile.ProfileType;
import thinker.concepts.general_types.LabelConcept;
import thinker.concepts.general_types.PrincipleConcept;
import thinker.concepts.general_types.Profile;
import thinker.concepts.general_types.ValueConcept;
import thinker.concepts.knowledge.INoosphereKnowledgeBase;
import thinker.concepts.relations.ConceptRelationType;
import thinker.mind.needs.Need;

public class Main {

	public static void main(String[] args) {
		GameUniverse universe = new GameUniverse(UUID.randomUUID());

		doWhatever(universe);

		PApplet.runSketch(new String[] { "World" }, new WorldGraphics(universe, 15f));

		doWhateverConcurrent(universe);

	}

	public static final int ENTITY_COUNT = 500;
	public static final int NODES_EACH = 1000;

	public static void doWhateverConcurrent(GameUniverse universe) {

		// generating graphs idk why
		long time1 = System.currentTimeMillis();
		List<RelationGraph<Integer, PartConnection>> graphs = new ArrayList<>(ENTITY_COUNT);
		for (int x = 0; x < ENTITY_COUNT; x++) {
			RelationGraph<Integer, PartConnection> graph = new RelationGraph<>(Set.of(BasicKindProperties.COLOR));
			graphs.add(graph);
			int node_count = NODES_EACH / 4;
			node_count = x % 20 == 0 ? NODES_EACH : node_count;
			for (int i = 0; i < node_count; i++) {
				graph.add(i);
			}
			for (int i = 0; i < node_count; i++) {
				for (int j = 0; j < node_count; j++) {
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
					if (universe.rand().nextFloat() <= 0.4) {
						graph.setProperty(i, con, j, BasicKindProperties.COLOR, Color.pink);
					}
				}
			}
		}
		long time2 = System.currentTimeMillis();
		System.out.println("Created (" + graphs.size() + ")");
		int x = 0;
		for (RelationGraph<Integer, PartConnection> graph : graphs) {

			if (x % 100 == 0) {
				IRelationGraph<Integer, PartConnection> sub = graph
						.subgraph(graph.traverseBFS(0, Collections.singleton(PartConnection.HOLDING), (a) -> {
						}, (a, b) -> true),
								(edge) -> graph.getProperty(edge, BasicKindProperties.COLOR, false) != Color.pink);
				System.out.println(x + ":" + sub);
				// System.out.println(graph.subgraph(sub).representation());
			}
			if (graph.isEmpty())
				continue;
			Iterator<Triplet<Integer, PartConnection, Integer>> iter = graph.edgeIterator();
			for (Triplet<Integer, PartConnection, Integer> noda = iter.next(); iter.hasNext(); noda = iter.next()) {
				if (noda.getFirst() % 4 != 0) {
					iter.remove();
				}
			}
			x++;
		}
		long time3 = System.currentTimeMillis();
		System.out.println("Creation: " + ((time2 - time1) / 1000d) + " seconds"); // timing the graph processes idk
		System.out.println("Processes (" + x + "): " + ((time3 - time2) / 1000d) + " seconds"); // timing the graph
																								// processes idk
	}

	public static void doWhatever(GameUniverse universe) {

		universe.setUpWorld(DimensionBuilder.of(Dim.EARTH).createTileRectangle(0, 0, 1, 1).addProp(
				WorldProperty.LAYER_BLOCKS,
				ImmutableMap.of(MapLayer.LOWEST, BasicBlock.STONE.getDefaultState(), MapLayer.FLOOR,
						BasicBlock.STONE.getDefaultState(), MapLayer.STANDARD_LAYER,
						BasicFluidBlock.AIR.getDefaultState(), MapLayer.ROOF, BasicFluidBlock.AIR.getDefaultState())));
		universe.loadMap(universe.getTile(Dim.EARTH, 0, 0), true);

		long time1 = System.currentTimeMillis();
		for (int i = 0; i < 100; i++) {

			Actor singleActor = new Actor(UUID.randomUUID());
			if (i % 3 == 0) {
				HumanoidKind kind = new HumanoidKind("human", 70f, 3f);
				singleActor.setKind(kind);
				singleActor.makeBody(KindSettings.NONE, true);
			} else {
				StickToolKind kind = new StickToolKind("hammer_idk", 1f, 1f,
						IShape.builder().addProperty(ShapeProperty.ROLL_SHAPE, RollableShape.ROLLABLE_OVOID).build(),
						0.5f);
				singleActor.setKind(kind);
				singleActor.makeBody(KindSettings.builder()
						.prop(StickToolKind.HEAD_MATERIAL, i % 2 == 0 ? Material.STONE : Material.ICE)
						.prop(StickToolKind.HANDLE_MATERIAL, i % 2 == 0 ? Material.STONE : Material.WOOD).build(),
						true);
			}
			singleActor.setPosition(IVector.of(universe.getMainMap().getMapWidth() / 2f,
					universe.getMainMap().getMapHeight() / 2f, MapLayer.STANDARD_LAYER));
			singleActor.setVelocity(
					IVector.fromAngle(universe.rand().nextDouble() * 360, 2 + 0.1 * universe.rand().nextDouble()));
			universe.getMainMap().queueAction(() -> universe.getMainMap().spawnIntoWorld(singleActor));
			System.out.println(singleActor.report());
		}
		long time2 = System.currentTimeMillis();
		System.out.println("Actor Creation+Printing: " + ((time2 - time1) / 1000d) + " seconds");

		doNoosphereGraphStuff(universe);

		long time3 = System.currentTimeMillis();
		System.out.println("Noosphere stuffs(" + universe.getNoosphere().countConcepts() + "): "
				+ ((time3 - time2) / 1000d) + " seconds");

	}

	private static void doNoosphereGraphStuff(GameUniverse universe) {
		INoosphereKnowledgeBase noo = universe.getNoosphere();
		noo.learnConcept(IConcept.NOTHING);
		noo.learnConcept(Need.HEALTH);
		IConcept twelve = new ValueConcept(12);
		IConcept important = new LabelConcept("important");
		IConcept death = new PrincipleConcept("death", true, false, true, false);
		IConcept food = new LabelConcept("food");
		IConcept zazagod = new Profile(UUID.randomUUID(), ProfileType.FORM).setIdentifierName("Zaza-god");
		IConcept loves = new Profile(UUID.randomUUID(), ProfileType.GROUP).setIdentifierName("The-Loves");
		final int loveCount = 1000;
		IConcept[] loveMems = new IConcept[loveCount];
		String[] nums = { "one", "u", "ee", "or", "ive", "ix", "ven", "ate", "ine", "en", "leven", "elve" };
		for (int i = 0; i < loveCount; i++) {
			IConcept lovex = new Profile(UUID.randomUUID(), ProfileType.FORM)
					.setIdentifierName("Lovel" + (i >= nums.length ? "" + i : nums[i]));
			loveMems[i] = lovex;
			noo.learnConcept(lovex);
		}
		noo.learnConcept(twelve);
		noo.learnConcept(important);
		noo.learnConcept(death);
		noo.learnConcept(food);
		noo.learnConcept(zazagod);
		noo.learnConcept(loves);
		noo.addConfidentRelation(IConcept.EXISTENCE, ConceptRelationType.MUTUAL_EXCLUSION, IConcept.NOTHING);
		noo.addConfidentRelation(IConcept.EXISTENCE, ConceptRelationType.QUANTIFIED_AS, twelve);
		noo.addConfidentRelation(loves, ConceptRelationType.QUANTIFIED_AS, twelve);
		noo.addConfidentRelation(IConcept.EXISTENCE, ConceptRelationType.CHARACTERIZED_BY, important);
		noo.addDubiousRelation(IConcept.EXISTENCE, ConceptRelationType.SUSTAINED_BY, Need.HEALTH, 0.5f);
		noo.addConfidentRelation(IConcept.EXISTENCE, ConceptRelationType.CONSUMED_BY, death);
		noo.addConfidentRelation(Need.HEALTH, ConceptRelationType.SUSTAINED_BY, food);
		noo.addConfidentRelation(IConcept.EXISTENCE, ConceptRelationType.CREATED_BY, zazagod);
		noo.addConfidentRelation(death, ConceptRelationType.CREATED_BY, zazagod);
		noo.addConfidentRelation(IConcept.NOTHING, ConceptRelationType.CONSUMED_BY, zazagod);
		noo.addConfidentRelation(IConcept.EXISTENCE, ConceptRelationType.WITH, loves);
		for (IConcept lov : loveMems) {
			noo.addConfidentRelation(lov, ConceptRelationType.MEMBER_OF, loves);
			ILogicConcept or = ILogicConcept.or();
			noo.addConfidentRelation(lov, ConceptRelationType.CHARACTERIZED_BY, or);
			noo.addConfidentRelation(or, ConceptRelationType.CHARACTERIZED_BY, food);
			noo.addConfidentRelation(or, ConceptRelationType.HAS_VALUE, twelve);
		}

	}

}
