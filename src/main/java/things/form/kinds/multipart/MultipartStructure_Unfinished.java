package things.form.kinds.multipart;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import _sim.RelativeSide;
import things.actor.IActor;
import things.form.IForm;
import things.form.IPart;
import things.form.graph.connections.CoverageType;
import things.form.graph.connections.IPartConnection;
import things.form.kinds.IKind;
import things.form.material.IMaterial;
import things.form.shape.IShape;
import things.form.soma.ISoma;
import things.form.soma.component.IComponentPart;
import things.stains.IStain;
import utilities.MathUtils;
import utilities.graph.IModifiableRelationGraph;
import utilities.graph.IRelationGraph;
import utilities.graph.ImmutableGraphView;
import utilities.graph.NodeNotFoundException;

/**
 * A generic multipart thing which may be a visage or body. <br>
 * TODO design this so we can have illusions
 * 
 * @author borah
 *
 * @param <P>
 */
public class MultipartStructure_Unfinished<P extends IPart> implements IForm<P> {

	protected IModifiableRelationGraph<P, IPartConnection> partGraph;
	protected IModifiableRelationGraph<P, CoverageType> coverage;
	protected Table<String, UUID, P> partsByNameAndId;
	protected Set<P> allParts;
	protected IActor owner;
	protected float size;
	protected int visplanes;
	protected boolean canrender;
	protected boolean isAllHoles;

	public MultipartStructure_Unfinished(IModifiableRelationGraph<P, IPartConnection> parts,
			IModifiableRelationGraph<P, CoverageType> coverage, float size) {
		this.partsByNameAndId = HashBasedTable.create();
		this.allParts = new HashSet<>();
		this.partGraph = parts;
		this.size = size;
		this.coverage = coverage;
		this.isAllHoles = true;
		visplanes = 1;
		float checkSize = 0;
		for (P part : parts) {
			this.partsByNameAndId.put(part.getName(), part.getID(), part);
			part.setOwner((ISoma) this);
			checkSize += part.getRelativeSize();
			visplanes = MathUtils.primeUnion(visplanes, part.detectionPlanes());
			this.allParts.add(part);
			this.isAllHoles = this.isAllHoles || part.isHole();
		}
		if (checkSize != 1f) {
			for (P part : parts) {
				part.changeSize(part.getRelativeSize() / checkSize, false);
			}
		}

		if (partGraph.isEmpty() || isAllHoles)
			canrender = false;
		else
			canrender = true;
	}

	@Override
	public IRelationGraph<P, IPartConnection> getRepresentationGraph() {
		return ImmutableGraphView.of(this.partGraph);
	}

	@Override
	public Collection<P> getPartsByName(String name) {
		return this.partsByNameAndId.row(name).values();
	}

	@Override
	public P getPartById(UUID id) {
		return this.partsByNameAndId.column(id).values().stream().findFirst().orElse(null);
	}

	@Override
	public IRelationGraph<P, CoverageType> getCoverageGraph() {
		return ImmutableGraphView.of(this.coverage);
	}

	@Override
	public boolean attach(IComponentPart newPart, IComponentPart toPart, IPartConnection connectionType,
			Collection<RelativeSide> coveringsides) {
		if (!partGraph.contains(toPart)) {
			throw new NodeNotFoundException(toPart);
		}
		this.partGraph.add((P) newPart);
		return false;
	}

	@Override
	public void onPartMaterialChange(IPart part, IMaterial formerMaterial) {
		// TODO Finish multipartStructure

	}

	@Override
	public IKind getKind() {

		return null;
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
		return null;
	}

	@Override
	public void runTick(long ticks) {

	}

	@Override
	public float size() {
		return 0;
	}

	@Override
	public void setOwner(IActor owner) {

	}

	@Override
	public void setCoveragePercentage(P coverer, P covered, RelativeSide side, float val) {

	}

	@Override
	public MultipartStructure_Unfinished<P> clone() {

		try {
			return (MultipartStructure_Unfinished<P>) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

}
