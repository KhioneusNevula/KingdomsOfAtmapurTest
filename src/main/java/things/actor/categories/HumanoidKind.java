package things.actor.categories;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

import _sim.GameUniverse;
import _sim.RelativeSide;
import _sim.plane.Plane;
import _utilities.graph.IModifiableRelationGraph;
import _utilities.graph.RelationGraph;
import metaphysics.soul.AnimalSoul;
import party.kind.IKindCollective;
import party.kind.spawning.IKindSpawningContext;
import things.biology.genes.IGenomeEncoding;
import things.biology.kinds.OrganicKindProperties;
import things.form.channelsystems.eat.FuelChannelSystem;
import things.form.graph.connections.CoverageType;
import things.form.graph.connections.CoverageType.CoverageDirection;
import things.form.graph.connections.IPartConnection;
import things.form.graph.connections.PartConnection;
import things.form.kinds.BasicKindProperties;
import things.form.kinds.settings.IKindSettings;
import things.form.material.Material;
import things.form.material.condition.IMaterialCondition;
import things.form.material.property.MaterialProperty;
import things.form.sensing.sensors.ISensor;
import things.form.sensing.sensors.StandardSightSensor;
import things.form.shape.IShape;
import things.form.shape.property.ShapeProperty;
import things.form.shape.property.ShapeProperty.Length;
import things.form.shape.property.ShapeProperty.RollableShape;
import things.form.shape.property.ShapeProperty.Thickness;
import things.form.soma.IPartDestroyedCondition;
import things.form.soma.IPartHealth;
import things.form.soma.abilities.PartAbility;
import things.form.soma.component.IComponentPart;
import things.form.soma.component.StandardComponentPart;
import things.form.soma.stats.FloatPartStats;
import things.interfaces.UniqueType;
import thinker.concepts.profile.Profile;
import thinker.mind.memory.MemoryBase;
import thinker.mind.perception.Perception;
import thinker.mind.perception.sensation.Sensation;
import thinker.mind.personality.PersonalityTraits;

/**
 * TODO better humanoid kind
 * 
 * @author borah
 *
 */
public class HumanoidKind extends FleshCreatureKind {

	public static final String FOOD_SYSTEM_NAME = "food";
	public static final String MOUTH_CHANNEL_CENTER_NAME = "mouth";
	public static final String STOMACH_CHANNEL_CENTER_NAME = "stomach";
	public static final String FOOD_NEED_NAME = "satiation";
	public static final FuelChannelSystem FOOD_SYSTEM = new FuelChannelSystem(FOOD_SYSTEM_NAME, Material.GENERIC_FLESH,
			MOUTH_CHANNEL_CENTER_NAME, STOMACH_CHANNEL_CENTER_NAME, IMaterialCondition.ORGANIC,
			LIFE_ENERGY_SYSTEM.getChannelResources().iterator().next(), Function.identity(), FOOD_NEED_NAME);

	public HumanoidKind(String name, float averageMass, float averageSize) {
		super(name, averageMass, averageSize, (s, p) -> {

			AnimalSoul soul = new AnimalSoul(s.getUUID(), name,
					IPartDestroyedCondition.DisjointPartStateCondition.DISINTEGRATE_NONSOLID_MASHED,
					IPartHealth.Standard.INTEGRITY, new MemoryBase(new Profile(s.getUUID(), UniqueType.FIGURE)
							.setIdentifierName(s.getOwner().getProfile().getIdentifierName())));
			LIFE_ENERGY_SYSTEM.getChannelSystemNeeds()
					.forEach((need) -> ((Perception) soul.getPerception()).addBlockage(need));
			soul.getPersonality().addTendency(PersonalityTraits.HANGRINESS_GEN
					.apply(s.getSystemByName(FOOD_SYSTEM_NAME).getChannelSystemNeeds().iterator().next()));
			return soul;
		}, FOOD_SYSTEM);
	}

	@Override
	protected void fillOutKnowledge(IKindCollective collective, GameUniverse universe) {
		super.fillOutKnowledge(collective, universe);

	}

	@Override
	public HumanoidKind setSpawnContext(IKindSpawningContext spawnContext) {
		super.setSpawnContext(spawnContext);
		return this;
	}

	private IComponentPart lifeCore;
	private IComponentPart body;
	private IComponentPart heart;
	public static final String HEART_NAME = "heart";
	private IComponentPart stomach;
	public static final String STOMACH_NAME = "stomach";
	private IComponentPart liver;
	public static final String LIVER_NAME = "liver";
	private IComponentPart left_lung;
	private IComponentPart right_lung;
	public static final String LUNG_NAME = "lung";
	private IComponentPart left_arm;
	private IComponentPart right_arm;
	public static final String ARM_NAME = "arm";

	private IComponentPart head;
	public static final String HEAD_NAME = "head";
	private IComponentPart brain;
	public static final String BRAIN_NAME = "brain";
	private IComponentPart left_eye;
	private IComponentPart right_eye;
	public static final String EYE_NAME = "eye";

	@Override
	protected IModifiableRelationGraph<IComponentPart, IPartConnection> makePartGraph(IKindSettings settings) {
		RelationGraph<IComponentPart, IPartConnection> graph = new RelationGraph<>();
		int planes = settings.getSetting(BasicKindProperties.PLANES);
		IGenomeEncoding genome = settings.getSetting(OrganicKindProperties.GENOME);
		Material flesh = Material.GENERIC_FLESH.buildCopy("flesh").prop(MaterialProperty.GENETICS, genome).build();
		UUID lifeCoreID = UUID.randomUUID();
		graph.add(lifeCore = new StandardComponentPart(LIFE_CORE_NAME, lifeCoreID, flesh,
				IShape.builder().addProperty(ShapeProperty.ROLL_SHAPE, RollableShape.ROLLABLE_CYLINDER).build(), 0.001f,
				Plane.SPIRITUAL.getPrime(), Set.of(PartAbility.HEAL), Collections.emptyMap()));
		lifeCore.changeStat(FloatPartStats.BLOOD_REGENERATION,
				FloatPartStats.BLOOD_REGENERATION.getDefaultValue(lifeCore), false);

		// body
		UUID bodyID = UUID.randomUUID();
		UUID heartID = UUID.randomUUID();
		UUID stomachID = UUID.randomUUID();
		UUID liverID = UUID.randomUUID();
		UUID left_lungID = UUID.randomUUID();
		UUID right_lungID = UUID.randomUUID();
		UUID right_armID = UUID.randomUUID();
		UUID left_armID = UUID.randomUUID();

		graph.add(body = new StandardComponentPart(BODY_NAME, bodyID, flesh,
				IShape.builder().addProperty(ShapeProperty.ROLL_SHAPE, RollableShape.ROLLABLE_CYLINDER).build(), 0.3f,
				planes, Set.of(PartAbility.HEAL), Collections.emptyMap()));
		graph.add(heart = new StandardComponentPart(HEART_NAME, heartID, flesh,
				IShape.builder().addProperty(ShapeProperty.ROLL_SHAPE, RollableShape.ROLLABLE_OVOID).build(), 0.03f,
				planes, Set.of(PartAbility.HEAL), Collections.emptyMap()));
		graph.addEdge(heart, PartConnection.JOINED, lifeCore);
		graph.addEdge(heart, PartConnection.JOINED, body);
		graph.add(stomach = new StandardComponentPart(STOMACH_NAME, stomachID, flesh,
				IShape.builder().addProperty(ShapeProperty.ROLL_SHAPE, RollableShape.ROLLABLE_OVOID).build(), 0.04f,
				planes, Set.of(PartAbility.HEAL), Collections.emptyMap()));
		graph.addEdge(stomach, PartConnection.JOINED, body);
		graph.add(liver = new StandardComponentPart(LIVER_NAME, liverID, flesh,
				IShape.builder().addProperty(ShapeProperty.ROLL_SHAPE, RollableShape.ROLLABLE_OVOID).build(), 0.04f,
				planes, Set.of(PartAbility.HEAL), Collections.emptyMap()));
		graph.addEdge(liver, PartConnection.JOINED, body);

		IShape lung = IShape.builder().addProperty(ShapeProperty.ROLL_SHAPE, RollableShape.NON_ROLLABLE)
				.addProperty(ShapeProperty.LENGTH, Length.LONG).build();
		graph.add(left_lung = new StandardComponentPart(LUNG_NAME, left_lungID, flesh, lung, 0.04f, planes,
				Set.of(PartAbility.HEAL), Collections.emptyMap()));
		graph.addEdge(left_lung, PartConnection.JOINED, body);
		graph.add(right_lung = new StandardComponentPart(LUNG_NAME, right_lungID, flesh, lung, 0.04f, planes,
				Set.of(PartAbility.HEAL), Collections.emptyMap()));
		graph.addEdge(right_lung, PartConnection.JOINED, body);

		IShape arm = IShape.builder().addProperty(ShapeProperty.ROLL_SHAPE, RollableShape.ROLLABLE_CYLINDER)
				.addProperty(ShapeProperty.LENGTH, Length.LONG).addProperty(ShapeProperty.THICKNESS, Thickness.THIN)
				.build();
		graph.add(left_arm = new StandardComponentPart(ARM_NAME, left_armID, flesh, arm, 0.04f, planes,
				Set.of(PartAbility.HEAL), Collections.emptyMap()));
		graph.addEdge(left_arm, PartConnection.JOINED, body);
		graph.add(right_arm = new StandardComponentPart(ARM_NAME, right_armID, flesh, arm, 0.04f, planes,
				Set.of(PartAbility.HEAL), Collections.emptyMap()));
		graph.addEdge(right_arm, PartConnection.JOINED, body);
		// head
		UUID headID = UUID.randomUUID();
		graph.add(head = new StandardComponentPart(HEAD_NAME, headID, flesh,
				IShape.builder().addProperty(ShapeProperty.ROLL_SHAPE, RollableShape.ROLLABLE_OVOID).build(), 0.1f,
				planes, Set.of(PartAbility.HEAL), Collections.emptyMap()));

		graph.addEdge(head, PartConnection.JOINED, body);
		UUID brainID = UUID.randomUUID();
		graph.add(brain = new StandardComponentPart(BRAIN_NAME, brainID, flesh,
				IShape.builder().addProperty(ShapeProperty.ROLL_SHAPE, RollableShape.ROLLABLE_OVOID).build(), 0.08f,
				planes, Set.of(PartAbility.HEAL), Map.of(Sensation.PAIN.getSensitivityStat(), 0.001f)));

		graph.addEdge(head, PartConnection.JOINED, brain);

		UUID leyeID = UUID.randomUUID();
		graph.add(left_eye = new StandardComponentPart(EYE_NAME, leyeID, flesh,
				IShape.builder().addProperty(ShapeProperty.ROLL_SHAPE, RollableShape.ROLLABLE_OVOID).build(), 0.005f,
				planes, Set.of(PartAbility.HEAL, ISensor.SIGHT_SENSOR),
				Map.of(StandardSightSensor.SENSOR_PLANES, Plane.PHYSICAL.getPrime(), FloatPartStats.SIGHT_DISTANCE, 40f,
						Sensation.PAIN.getSensitivityStat(), 10f)));

		UUID reyeID = UUID.randomUUID();
		graph.add(right_eye = new StandardComponentPart(EYE_NAME, reyeID, flesh,
				IShape.builder().addProperty(ShapeProperty.ROLL_SHAPE, RollableShape.ROLLABLE_OVOID).build(), 0.005f,
				planes, Set.of(PartAbility.HEAL, ISensor.SIGHT_SENSOR),
				Map.of(StandardSightSensor.SENSOR_PLANES, Plane.PHYSICAL.getPrime(), FloatPartStats.SIGHT_DISTANCE, 40f,
						Sensation.PAIN.getSensitivityStat(), 10f)));
		graph.addEdge(head, PartConnection.JOINED, left_eye);
		graph.addEdge(head, PartConnection.JOINED, right_eye);

		return graph;
	}

	@Override
	protected IModifiableRelationGraph<IComponentPart, CoverageType> makeCoverageGraph(
			IModifiableRelationGraph<IComponentPart, IPartConnection> partGraph, IKindSettings settings) {
		RelationGraph<IComponentPart, CoverageType> graph = new RelationGraph<>();
		graph.addAll(partGraph);
		for (CoverageType type : CoverageType.getCoverageTypes(CoverageDirection.COVERS)) {
			graph.addEdge(body, type, heart);
			graph.addEdge(heart, type, lifeCore);
			graph.addEdge(body, type, stomach);
			graph.addEdge(body, type, liver);
			graph.addEdge(body, type, left_lung);
			graph.addEdge(body, type, right_lung);
			graph.addEdge(head, type, brain);
		}
		graph.addEdge(left_lung, CoverageType.covers(RelativeSide.LEFT), right_lung);
		graph.addEdge(right_lung, CoverageType.covers(RelativeSide.RIGHT), left_lung);
		graph.addEdge(head, CoverageType.covers(RelativeSide.TOP), body);
		graph.addEdge(body, CoverageType.covers(RelativeSide.BOTTOM), head);
		graph.addEdge(left_arm, CoverageType.covers(RelativeSide.LEFT), body);
		graph.addEdge(right_arm, CoverageType.covers(RelativeSide.RIGHT), body);
		for (CoverageType type : (Iterable<CoverageType>) () -> EnumSet
				.of(RelativeSide.TOP, RelativeSide.HIND, RelativeSide.LEFT, RelativeSide.RIGHT, RelativeSide.BOTTOM)
				.stream().map(CoverageType::covers).iterator()) {
			graph.addEdge(head, type, left_eye);
			graph.addEdge(head, type, right_eye);
		}
		return graph;
	}

}
