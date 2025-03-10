package things.actor.categories;

import java.util.Collections;
import java.util.UUID;

import _sim.RelativeSide;
import _utilities.graph.IModifiableRelationGraph;
import _utilities.graph.RelationGraph;
import _utilities.property.IProperty;
import _utilities.property.PropertyImpl;
import things.form.graph.connections.CoverageType;
import things.form.graph.connections.IPartConnection;
import things.form.graph.connections.PartConnection;
import things.form.kinds.BasicKindProperties;
import things.form.kinds.multipart.MultipartKind;
import things.form.kinds.settings.IKindSettings;
import things.form.material.IMaterial;
import things.form.material.Material;
import things.form.shape.IShape;
import things.form.shape.property.ShapeProperty;
import things.form.shape.property.ShapeProperty.Length;
import things.form.shape.property.ShapeProperty.RollableShape;
import things.form.shape.property.ShapeProperty.Thickness;
import things.form.soma.component.IComponentPart;
import things.form.soma.component.StandardComponentPart;

public class StickToolKind extends MultipartKind {

	public static final PropertyImpl<IMaterial> HEAD_MATERIAL = IProperty.make("head_material", IMaterial.class,
			Material.STONE);

	public static final PropertyImpl<IMaterial> HANDLE_MATERIAL = IProperty.make("handle_material", IMaterial.class,
			Material.WOOD);

	private static final UUID headID = UUID.randomUUID();

	private static final UUID handleID = UUID.randomUUID();

	private IShape headShape;
	private float relativeHeadSize;

	public StickToolKind(String name, float averageMass, float averageSize, IShape headShape, float relativeHeadSize) {
		super(name, averageMass, averageSize);
		this.relativeHeadSize = relativeHeadSize;
		if (relativeHeadSize >= 1 || relativeHeadSize <= 0) {
			throw new IllegalArgumentException(relativeHeadSize + " " + name);
		}
		this.headShape = headShape;
	}

	@Override
	protected IModifiableRelationGraph<IComponentPart, IPartConnection> makePartGraph(IKindSettings settings) {
		RelationGraph<IComponentPart, IPartConnection> graph = new RelationGraph<>();
		IComponentPart head = null;
		graph.add(head = new StandardComponentPart("head", headID, settings.getSetting(HEAD_MATERIAL), headShape,
				settings.getOrDefault(BasicKindProperties.SIZE, getAverageSize()) * relativeHeadSize,
				settings.getSetting(BasicKindProperties.PLANES), Collections.emptySet(), Collections.emptyMap(),
				Collections.emptySet()));
		IComponentPart handle = null;
		graph.add(handle = new StandardComponentPart("handle", handleID, settings.getSetting(HANDLE_MATERIAL),
				IShape.builder().addProperty(ShapeProperty.LENGTH, Length.LONG)
						.addProperty(ShapeProperty.ROLL_SHAPE, RollableShape.ROLLABLE_CYLINDER)
						.addProperty(ShapeProperty.THICKNESS, Thickness.THIN).build(),
				settings.getOrDefault(BasicKindProperties.SIZE, getAverageSize()) * (1 - relativeHeadSize),
				settings.getSetting(BasicKindProperties.PLANES), Collections.emptySet(), Collections.emptyMap(),
				Collections.emptySet()));
		graph.addEdge(head, PartConnection.JOINED, handle);
		return graph;
	}

	@Override
	protected IModifiableRelationGraph<IComponentPart, CoverageType> makeCoverageGraph(
			IModifiableRelationGraph<IComponentPart, IPartConnection> partGraph, IKindSettings settings) {
		IComponentPart handlematch = IComponentPart.dummy(handleID);
		IComponentPart headmatch = IComponentPart.dummy(headID);
		RelationGraph<IComponentPart, CoverageType> coverage = new RelationGraph<>();
		coverage.addAll(partGraph);
		coverage.addEdge(headmatch, CoverageType.covers(RelativeSide.FRONT), handlematch);
		return coverage;
	}

	@Override
	protected IComponentPart identifyCenter(IModifiableRelationGraph<IComponentPart, IPartConnection> graph,
			IKindSettings settings) {

		return graph.stream().filter((a) -> a.getUUID().equals(handleID)).findAny().orElseThrow();
	}

}
