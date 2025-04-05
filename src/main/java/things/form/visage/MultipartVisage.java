package things.form.visage;

import java.awt.Color;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

import _graphics.WorldGraphics;
import _sim.RelativeSide;
import _sim.vectors.IVector;
import _utilities.MathUtils;
import _utilities.couplets.Triplet;
import _utilities.graph.IModifiableRelationGraph;
import _utilities.graph.IRelationGraph;
import _utilities.graph.ImmutableGraphView;
import processing.core.PConstants;
import things.actor.IActor;
import things.form.IPart;
import things.form.graph.connections.CoverageType;
import things.form.graph.connections.CoverageType.CoverageDirection;
import things.form.graph.connections.IPartConnection;
import things.form.graph.connections.PartConnection;
import things.form.kinds.IKind;
import things.form.material.IMaterial;
import things.form.material.property.MaterialProperty;
import things.form.shape.IShape;
import things.form.shape.property.ShapeProperty;
import things.form.soma.ISoma;
import things.form.soma.component.IComponentPart;
import things.stains.IStain;

/**
 * A generic multipart visage. Also the sueprclass to MultipartSoma, since a
 * Soma is just a visage with physical properties. <br>
 * Allows you to make illusions
 * 
 * @author borah
 *
 * @param <P>
 */
public class MultipartVisage<P extends IPart> implements IVisage<P> {

	protected IModifiableRelationGraph<P, IPartConnection> partGraph;
	protected IModifiableRelationGraph<P, CoverageType> coverage;
	protected Table<String, UUID, P> partsByNameAndId;
	protected P centerPart;
	protected Set<P> contiguousParts;
	protected Set<P> allParts;
	protected IActor owner;
	protected float size;
	protected int visplanes;
	protected boolean canrender;
	protected boolean isAllHoles;
	protected IKind kind = IKind.MISCELLANEOUS;
	protected UUID uuid = new UUID(0, 0);
	/*
	 * private int intplanes;
	 * 
	 * private Map<String, IChannelSystem> systems = new HashMap<>();
	 * 
	 * private Multimap<IChannelCenter, P> channelSystemCenters =
	 * MultimapBuilder.hashKeys().hashSetValues() .build();
	 * 
	 * private Multimap<IChannelSystem, P> channelSystemParts =
	 * MultimapBuilder.hashKeys().hashSetValues() .build();
	 * 
	 * private Map<IPartStat<?>, Pair<Object, Integer>> aggregateStats;
	 * 
	 * private Map<IResource<? extends Comparable<?>>, Comparable<?>>
	 * aggregateResources;
	 * 
	 * private float mass;
	 * 
	 * private IPartDestroyedCondition destructCondition;
	 * 
	 * private boolean isDestroyed;
	 * 
	 * private List<ISoma> brokenParts;
	 * 
	 * private Map<ISoul, P> spirits = new HashMap<>();
	 */

	public MultipartVisage(IModifiableRelationGraph<P, IPartConnection> parts,
			IModifiableRelationGraph<P, CoverageType> coverage, float size, P centerPart) {
		this.partsByNameAndId = HashBasedTable.create();
		this.allParts = new HashSet<>();
		this.partGraph = parts;
		this.size = size;
		this.coverage = coverage;
		this.isAllHoles = true;
		visplanes = 1;
		this.centerPart = centerPart;
		float checkSize = 0;
		for (P part : parts) {
			this.partsByNameAndId.put(part.getName(), part.getUUID(), part);
			part.setOwner(this);
			part.setTrueOwner(this);
			checkSize += part.getRelativeSize();
			visplanes = MathUtils.primeUnion(visplanes, part.detectionPlanes());
			this.allParts.add(part);

			this.isAllHoles = this.isAllHoles && part.isHole();
		}
		if (checkSize != 1f) {
			for (P part : parts) {
				part.changeSize(part.getRelativeSize() / checkSize, false);
			}
		}
		this.contiguousParts = Sets.newHashSet(partGraph.nodeTraversalIteratorBFS(centerPart,
				Set.of(PartConnection.JOINED, PartConnection.MERGED), (a, b) -> true));

		if (partGraph.isEmpty() || isAllHoles) {
			canrender = false;
		} else
			canrender = true;
	}

	public UUID getUUID() {
		return uuid;
	}

	public void setUUID(UUID id) {
		this.uuid = id;
	}

	@Override
	public IKind getKind() {
		return kind;
	}

	/**
	 * Set this body's kind
	 * 
	 * @param kind
	 */
	public void setKind(IKind kind) {
		this.kind = kind;
	}

	@Override
	public IRelationGraph<P, IPartConnection> getPartGraph() {
		return ImmutableGraphView.of(partGraph);
	}

	@Override
	public Collection<P> getPartsByName(String name) {
		return partsByNameAndId.row(name).values();
	}

	@Override
	public P getPartById(UUID id) {
		return partsByNameAndId.column(id).values().stream().findFirst().orElse(null);
	}

	@Override
	public IRelationGraph<P, CoverageType> getCoverageGraph() {
		return ImmutableGraphView.of(coverage);
	}

	protected void onAddPart(P newPart) {
		this.partGraph.add(newPart);
		this.coverage.add(newPart);
		this.allParts.add(newPart);

		float totalSize = (float) partGraph.stream().mapToDouble((a) -> (double) a.getRelativeSize()).sum();

		partGraph.forEach((p) -> p.changeSize(p.getRelativeSize() / totalSize, false));

		if (!canrender && !newPart.isHole()) {
			canrender = true;
		}
		/**
		 * for (IPartStat<?> stat : newPart.getStats()) { this.unionStat(stat,
		 * newPart.getStat(stat)); } for (IChannelSystem sys : this.getChannelSystems())
		 * { if (!newPart.getChannelCenters(sys).isEmpty()) {
		 * newPart.getChannelCenters(sys).forEach((cen) -> {
		 * this.channelSystemCenters.put(cen, newPart); this.channelSystemParts.put(sys,
		 * newPart);
		 * 
		 * }); } }
		 **/
	}

	@Override
	public boolean attach(P newPart, P toPart, IPartConnection connectionType, Collection<RelativeSide> coveringsides,
			boolean changeOwner) {
		if (!partGraph.contains(toPart)) {
			throw new IllegalArgumentException(toPart + " is not part of " + this);
		}
		boolean newed = !partGraph.contains(newPart);
		if (newed) {
			this.onAddPart(newPart);
			if (changeOwner) {
				newPart.setOwner(this);
				newPart.setTrueOwner(this);
			}
		}
		boolean one = this.partGraph.addEdge(newPart, connectionType, toPart);
		if (connectionType.isAttachment()) {
			if (this.contiguousParts.contains(newPart)) {
				contiguousParts.add(toPart);
			} else if (this.contiguousParts.contains(toPart)) {
				contiguousParts.add(newPart);
			}
		}
		boolean two = true;
		for (RelativeSide covering : coveringsides) {
			two = two && this.coverage.addEdge(newPart, CoverageType.covers(covering), toPart);
		}
		/**
		 * for (IChannelSystem sys : this.systems.values()) { sys.onBodyNew(this,
		 * newPart, connectionType, toPart, newed); }
		 */

		return one && two;
	}

	@Override
	public IActor getOwner() {
		return owner;
	}

	@Override
	public void runTick(long ticks) {
		/**
		 * for (P p : this.partGraph) { if (p.hasControlCenter()) {
		 * p.getChannelCenters(ChannelRole.CONTROL).forEach((x) -> x.controlTick(this,
		 * p, ticks)); } if (p.hasAutomaticChannelCenter()) {
		 * p.getAutomaticChannelCenters().forEach((x) -> x.automaticTick(this, p,
		 * ticks)); }
		 * 
		 * p.getStains().forEach((stain) -> stain.getSubstance().stainTick(p, stain,
		 * this, ticks)); Iterator<IPartStatusEffectInstance> effs =
		 * p.getEffectInstances().iterator(); if (effs.hasNext()) { for
		 * (IPartStatusEffectInstance effect = effs.next(); effs.hasNext(); effect =
		 * effs.next()) { if (effect.remainingDuration() == 0 ||
		 * effect.getEffect().shouldRemove(effect, p)) { effs.remove(); } else {
		 * effect.tick(); } } } }
		 */

	}

	@Override
	public float size() {
		return size;
	}

	@Override
	public void setOwner(IActor owner) {
		this.owner = owner;
	}

	@Override
	public boolean canRender() {
		return canrender;
	}

	@Override
	public int visibilityPlanes() {
		return visplanes;
	}

	protected void drawPart(P part, WorldGraphics g, IVector loc) {
		Color color = part.getMaterial().getProperty(MaterialProperty.COLOR);
		if (!part.getStains().isEmpty()) {
			IStain mainStain = Collections.max(part.getStains(),
					(a, b) -> Float.compare(a.getAmount() * a.getSubstance().getProperty(MaterialProperty.STAINING),
							b.getAmount() * b.getSubstance().getProperty(MaterialProperty.STAINING)));
			color = mainStain.getSubstance().getProperty(MaterialProperty.COLOR);
		}
		g.stroke(color.darker().darker().getRGB());
		g.fill(color.getRGB());

		IShape shape = part.getShape();
		float h = shape.getProperty(ShapeProperty.LENGTH).factor / 2 * part.getRelativeSize() * this.size
				* g.getWorld().getMainMap().getBlockRenderSize();
		float w = shape.getProperty(ShapeProperty.THICKNESS).factor / 2 * part.getRelativeSize() * this.size
				* g.getWorld().getMainMap().getBlockRenderSize();
		switch (shape.getProperty(ShapeProperty.ROLL_SHAPE)) {
		case NON_ROLLABLE:
		case ROLLABLE_OVOID:
			g.ellipseMode(PConstants.CENTER);
			g.ellipse((float) loc.getUnadjustedX(), (float) loc.getUnadjustedY(), w, h);
			return;
		case ROLLABLE_CYLINDER:
			g.rectMode(PConstants.CENTER);
			g.rect((float) loc.getUnadjustedX(), (float) loc.getUnadjustedY(), w, h);
			return;
		}
	}

	@Override
	public void draw(WorldGraphics g) {
		// TODO render multipart visage?
		Iterator<Triplet<P, CoverageType, P>> iterator = this.coverage.edgeTraversalIteratorBFS(centerPart,
				CoverageType.getCoverageTypes(CoverageDirection.COVERED_ON), (a, b) -> true);
		float step = this.size / this.partGraph.size() * g.getWorld().getMainMap().getBlockRenderSize();
		Map<P, IVector> positions = new HashMap<>();
		Set<P> drawn = new HashSet<>();
		drawPart(centerPart, g, IVector.ZERO);
		drawn.add(centerPart);
		for (Triplet<P, CoverageType, P> edge : (Iterable<Triplet<P, CoverageType, P>>) () -> iterator) {
			if (drawn.contains(edge.getThird()))
				continue;
			IVector firstpos = positions.getOrDefault(edge.getFirst(), IVector.ZERO);
			IVector dir = edge.getSecond().getSide().getChangeVector().scaleMagnitudeBy(step);
			IVector newPos = firstpos.add(dir);
			if (newPos != IVector.ZERO) {
				positions.put(edge.getThird(), newPos);
			}
			drawPart(edge.getThird(), g, newPos);
			drawn.add(edge.getThird());
		}

	}

	@Override
	public String visageReport() {
		return "{parts=" + this.partGraph.representation() + ",coverage=" + coverage.representation() + "}";
	}

	@Override
	public String toString() {
		return "MultipartVisage{parts=" + this.partGraph + ",coverage=" + coverage + "}";
	}

	@Override
	public void setCoveragePercentage(P coverer, P covered, RelativeSide side, float amount) {
		this.coverage.setProperty(coverer, CoverageType.covers(side), covered, CoverageType.COVERAGE_PERCENT, amount);
	}

	@Override
	public void onPartMaterialChange(IPart part, IMaterial formerMaterial) {

	}

	@Override
	public void onPartStainChange(IPart part, Collection<IStain> stains) {

	}

	@Override
	public void onPartShapeChange(IPart part, IShape formerShape) {

	}

	@Override
	public void onPartSizeChange(IPart part, float formerMaterial) {

	}

	@Override
	public boolean isAllHoles() {
		return this.isAllHoles;
	}

	@SuppressWarnings("unchecked")
	@Override
	public MultipartVisage<P> clone() {
		MultipartVisage<P> copy;
		try {
			copy = (MultipartVisage<P>) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}

		copy.partsByNameAndId = HashBasedTable.create();
		copy.allParts = new HashSet<>();
		for (Cell<String, UUID, P> cell : this.partsByNameAndId.cellSet()) {
			P newPart = (P) cell.getValue().clone();
			copy.partsByNameAndId.put(cell.getRowKey(), cell.getColumnKey(), newPart);
			copy.allParts.add(newPart);
		}
		copy.partGraph = partGraph.deepCopy((part) -> copy.partsByNameAndId.get(part.getName(), part.getUUID()));
		copy.coverage = coverage.deepCopy((part) -> copy.partsByNameAndId.get(part.getName(), part.getUUID()));

		copy.centerPart = copy.partsByNameAndId.get(centerPart.getName(), centerPart.getUUID());
		copy.contiguousParts = new HashSet<>();
		for (P part : this.contiguousParts) {
			copy.contiguousParts.add(copy.partsByNameAndId.get(part.getName(), part.getUUID()));
		}
		return copy;
	}
}
