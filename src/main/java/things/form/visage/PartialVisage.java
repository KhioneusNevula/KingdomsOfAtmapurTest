package things.form.visage;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import com.google.common.collect.Streams;

import _graphics.WorldGraphics;
import _sim.RelativeSide;
import _utilities.collections.FilteredCollectionView;
import _utilities.graph.IRelationGraph;
import things.actor.IActor;
import things.form.IPart;
import things.form.graph.connections.CoverageType;
import things.form.graph.connections.IPartConnection;
import things.form.kinds.IKind;
import things.form.material.IMaterial;
import things.form.shape.IShape;
import things.stains.IStain;

public class PartialVisage<P extends IPart> implements IVisage<P> {

	private IVisage<P> whole;
	private IRelationGraph<P, IPartConnection> parts;
	private IRelationGraph<P, CoverageType> coverage;

	public PartialVisage(IVisage<P> form) {
		whole = form;
		parts = form.getPartGraph().subgraph(Collections.emptySet());
		coverage = form.getCoverageGraph().subgraph(Collections.emptySet());
	}

	public PartialVisage<P> addSensableParts(Iterable<? extends P> addas) {
		Streams.stream(addas).sequential().forEach((a) -> {
			parts.add(a);
			coverage.add(a);
		});

		return this;
	}

	public PartialVisage<P> removeSensableParts(Iterable<? extends P> addas) {
		Streams.stream(addas).sequential().forEach((a) -> {
			parts.subgraphRemove(a);
			coverage.subgraphRemove(a);
		});

		return this;
	}

	@Override
	public IKind getKind() {
		return whole.getKind();
	}

	@Override
	public void setUUID(UUID id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public PartialVisage<P> clone() {
		PartialVisage<P> copy;
		try {
			copy = (PartialVisage<P>) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
		copy.parts = whole.getPartGraph().subgraph(parts);
		copy.whole = whole;
		copy.coverage = whole.getCoverageGraph().subgraph(parts);
		return copy;
	}

	@Override
	public IRelationGraph<P, IPartConnection> getPartGraph() {
		return this.parts;
	}

	@Override
	public Collection<P> getPartsByName(String name) {
		return new FilteredCollectionView<>(whole.getPartsByName(name), parts::contains);
	}

	@Override
	public P getPartById(UUID id) {
		P part = whole.getPartById(id);
		if (parts.contains(part)) {
			return part;
		}
		return null;
	}

	@Override
	public IRelationGraph<P, CoverageType> getCoverageGraph() {
		return coverage;
	}

	@Override
	public boolean attach(P newPart, P toPart, IPartConnection connectionType, Collection<RelativeSide> coveringsides,
			boolean co) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void onPartMaterialChange(IPart part, IMaterial formerMaterial) {
	}

	@Override
	public void onPartSizeChange(IPart part, float formerMaterial) {
	}

	@Override
	public void onPartShapeChange(IPart part, IShape formerShape) {
	}

	@Override
	public void onPartStainChange(IPart part, Collection<IStain> stains) {
	}

	@Override
	public IActor getOwner() {
		return whole.getOwner();
	}

	@Override
	public boolean isPartial() {
		return true;
	}

	@Override
	public IVisage<?> getFullVisage() {
		return this.whole;
	}

	@Override
	public void runTick(long ticks) {
	}

	@Override
	public float size() {

		return whole.size();
	}

	@Override
	public void setOwner(IActor owner) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setCoveragePercentage(P coverer, P covered, RelativeSide side, float amount) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isAllHoles() {
		return parts.stream().allMatch(IPart::isHole);
	}

	@Override
	public UUID getUUID() {
		return whole.getUUID();
	}

	@Override
	public boolean canRender() {
		return !this.parts.isEmpty() && !this.isAllHoles();
	}

	@Override
	public int visibilityPlanes() {
		return whole.visibilityPlanes();
	}

	@Override
	public void draw(WorldGraphics g) {
		whole.draw(g);
	}

	@Override
	public String visageReport() {
		return "PartialGraph{parts=" + this.parts + ", coverage=" + this.coverage + "}";
	}

}
