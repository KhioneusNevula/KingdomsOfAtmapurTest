package things.actor.categories;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

import _sim.RelativeSide;
import _sim.plane.Plane;
import _utilities.graph.IModifiableRelationGraph;
import _utilities.graph.RelationGraph;
import things.biology.genes.IGenome;
import things.biology.kinds.OrganicKindProperties;
import things.form.channelsystems.eat.FuelChannelSystem;
import things.form.channelsystems.energy.EnergyChannelSystem;
import things.form.graph.connections.CoverageType;
import things.form.graph.connections.CoverageType.CoverageDirection;
import things.form.graph.connections.IPartConnection;
import things.form.graph.connections.PartConnection;
import things.form.kinds.BasicKindProperties;
import things.form.kinds.multipart.MultipartSoma;
import things.form.kinds.settings.IKindSettings;
import things.form.material.Material;
import things.form.material.property.MaterialProperty;
import things.form.shape.IShape;
import things.form.shape.property.ShapeProperty;
import things.form.shape.property.ShapeProperty.Length;
import things.form.shape.property.ShapeProperty.RollableShape;
import things.form.shape.property.ShapeProperty.Thickness;
import things.form.soma.abilities.PartAbility;
import things.form.soma.component.IComponentPart;
import things.form.soma.component.StandardComponentPart;

/**
 * TODO better humanoid kind
 * 
 * @author borah
 *
 */
public class HumanoidKind extends FleshCreatureKind {

	public HumanoidKind(String name, float averageMass, float averageSize) {
		super(name, averageMass, averageSize);
	}

	private IComponentPart lifeCore;
	private IComponentPart body;
	private IComponentPart heart;
	private IComponentPart stomach;
	private IComponentPart liver;
	private IComponentPart left_lung;
	private IComponentPart right_lung;
	private IComponentPart left_arm;
	private IComponentPart right_arm;

	private IComponentPart head;

	@Override
	protected IModifiableRelationGraph<IComponentPart, IPartConnection> makePartGraph(IKindSettings settings) {
		RelationGraph<IComponentPart, IPartConnection> graph = new RelationGraph<>();
		int planes = settings.getSetting(BasicKindProperties.PLANES);
		IGenome genome = settings.getSetting(OrganicKindProperties.GENOME);
		Material flesh = Material.GENERIC_FLESH.buildCopy("flesh").prop(MaterialProperty.GENETICS, genome).build();
		UUID lifeCoreID = UUID.randomUUID();
		graph.add(lifeCore = new StandardComponentPart("life_core", lifeCoreID, flesh,
				IShape.builder().addProperty(ShapeProperty.ROLL_SHAPE, RollableShape.ROLLABLE_CYLINDER).build(), 0.001f,
				Plane.SPIRITUAL.getPrime(), Set.of(PartAbility.HEAL), Collections.emptyMap(), Collections.emptySet()));

		// body
		UUID bodyID = UUID.randomUUID();
		UUID heartID = UUID.randomUUID();
		UUID stomachID = UUID.randomUUID();
		UUID liverID = UUID.randomUUID();
		UUID left_lungID = UUID.randomUUID();
		UUID right_lungID = UUID.randomUUID();
		UUID right_armID = UUID.randomUUID();
		UUID left_armID = UUID.randomUUID();

		graph.add(body = new StandardComponentPart("body", bodyID, flesh,
				IShape.builder().addProperty(ShapeProperty.ROLL_SHAPE, RollableShape.ROLLABLE_CYLINDER).build(), 0.3f,
				planes, Set.of(PartAbility.HEAL), Collections.emptyMap(), Collections.emptySet()));
		graph.add(heart = new StandardComponentPart("heart", heartID, flesh,
				IShape.builder().addProperty(ShapeProperty.ROLL_SHAPE, RollableShape.ROLLABLE_OVOID).build(), 0.03f,
				planes, Set.of(PartAbility.HEAL), Collections.emptyMap(), Collections.emptySet()));
		graph.addEdge(heart, PartConnection.JOINED, lifeCore);
		graph.addEdge(heart, PartConnection.JOINED, body);
		graph.add(stomach = new StandardComponentPart("stomach", stomachID, flesh,
				IShape.builder().addProperty(ShapeProperty.ROLL_SHAPE, RollableShape.ROLLABLE_OVOID).build(), 0.04f,
				planes, Set.of(PartAbility.HEAL), Collections.emptyMap(), Collections.emptySet()));
		graph.addEdge(stomach, PartConnection.JOINED, body);
		graph.add(liver = new StandardComponentPart("liver", liverID, flesh,
				IShape.builder().addProperty(ShapeProperty.ROLL_SHAPE, RollableShape.ROLLABLE_OVOID).build(), 0.04f,
				planes, Set.of(PartAbility.HEAL), Collections.emptyMap(), Collections.emptySet()));
		graph.addEdge(liver, PartConnection.JOINED, body);

		IShape lung = IShape.builder().addProperty(ShapeProperty.ROLL_SHAPE, RollableShape.NON_ROLLABLE)
				.addProperty(ShapeProperty.LENGTH, Length.LONG).build();
		graph.add(left_lung = new StandardComponentPart("left_lung", left_lungID, flesh, lung, 0.04f, planes,
				Set.of(PartAbility.HEAL), Collections.emptyMap(), Collections.emptySet()));
		graph.addEdge(left_lung, PartConnection.JOINED, body);
		graph.add(right_lung = new StandardComponentPart("right_lung", right_lungID, flesh, lung, 0.04f, planes,
				Set.of(PartAbility.HEAL), Collections.emptyMap(), Collections.emptySet()));
		graph.addEdge(right_lung, PartConnection.JOINED, body);

		IShape arm = IShape.builder().addProperty(ShapeProperty.ROLL_SHAPE, RollableShape.ROLLABLE_CYLINDER)
				.addProperty(ShapeProperty.LENGTH, Length.LONG).addProperty(ShapeProperty.THICKNESS, Thickness.THIN)
				.build();
		graph.add(left_arm = new StandardComponentPart("left_arm", left_armID, flesh, arm, 0.04f, planes,
				Set.of(PartAbility.HEAL), Collections.emptyMap(), Collections.emptySet()));
		graph.addEdge(left_arm, PartConnection.JOINED, body);
		graph.add(right_arm = new StandardComponentPart("right_arm", right_armID, flesh, arm, 0.04f, planes,
				Set.of(PartAbility.HEAL), Collections.emptyMap(), Collections.emptySet()));
		graph.addEdge(right_arm, PartConnection.JOINED, body);
		// head
		UUID headID = UUID.randomUUID();
		graph.add(head = new StandardComponentPart("head", headID, flesh,
				IShape.builder().addProperty(ShapeProperty.ROLL_SHAPE, RollableShape.ROLLABLE_OVOID).build(), 0.1f,
				planes, Set.of(PartAbility.HEAL), Collections.emptyMap(), Collections.emptySet()));

		graph.addEdge(head, PartConnection.JOINED, body);

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
		}
		graph.addEdge(left_lung, CoverageType.covers(RelativeSide.LEFT), right_lung);
		graph.addEdge(right_lung, CoverageType.covers(RelativeSide.RIGHT), left_lung);
		graph.addEdge(head, CoverageType.covers(RelativeSide.TOP), body);
		graph.addEdge(body, CoverageType.covers(RelativeSide.BOTTOM), head);
		graph.addEdge(left_arm, CoverageType.covers(RelativeSide.LEFT), body);
		graph.addEdge(right_arm, CoverageType.covers(RelativeSide.RIGHT), body);
		return graph;
	}

	@Override
	protected void addSystemsWithoutPopulating(MultipartSoma createdSoma, IKindSettings set) {
		super.addSystemsWithoutPopulating(createdSoma, set);
		createdSoma.addChannelSystem(new FuelChannelSystem("food", Material.GENERIC_FLESH, "mouth", "stomach",
				set.getSetting(OrganicKindProperties.CAN_EAT),
				((EnergyChannelSystem) createdSoma.getSystemByName("life")).getChannelResources().iterator().next(),
				Function.identity(), "satiation"), false);
	}

}
