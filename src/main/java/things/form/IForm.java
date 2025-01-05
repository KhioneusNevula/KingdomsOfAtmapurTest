package things.form;

import java.util.Collection;
import java.util.UUID;

import _sim.RelativeSide;
import things.actor.IActor;
import things.form.graph.connections.CoverageType;
import things.form.graph.connections.IPartConnection;
import things.form.material.IMaterial;
import things.form.shape.IShape;
import things.form.soma.component.IComponentPart;
import things.stains.IStain;
import utilities.graph.IRelationGraph;

/**
 * Representation of a "body" or "visage" using graphs
 * 
 * @author borah
 *
 * @param <P>
 */
public interface IForm<P extends IPart> extends Cloneable {

	/**
	 * Create a copy of this Form, cloning each constituent part as well
	 * 
	 * @return
	 */
	public IForm<P> clone();

	/**
	 * Get graph representing the connections of different parts in this graph
	 * 
	 * @return
	 */
	public IRelationGraph<P, IPartConnection> getRepresentationGraph();

	/**
	 * Returns all parts in this with a specific name
	 * 
	 * @param name
	 * @return
	 */
	public Collection<P> getPartsByName(String name);

	/**
	 * Return a specific part in this with a specific id if it is present (null
	 * otherwise)
	 * 
	 * @param id
	 * @return
	 */
	public P getPartById(UUID id);

	/**
	 * Get graph representing the coverage values of different parts in this graph
	 * 
	 * @return
	 */
	public IRelationGraph<P, CoverageType> getCoverageGraph();

	/**
	 * Affix newPart to toPart using the given connection type. Return true if
	 * successful or false if not. NewPart does not have to be part of the body
	 * already, but toPart does.
	 * 
	 * @param toPart
	 * @param newPart
	 * @param coveringsides the sides of the other part that this body part covers
	 */
	public boolean attach(IComponentPart newPart, IComponentPart toPart, IPartConnection connectionType,
			Collection<RelativeSide> coveringsides);

	/**
	 * Called when part of this Form experiences a change in material (e.g.
	 * liquefies). If the part is therefore "destroyed," add its broken parts to the
	 * broken part list and add the part itself as a broken part to the list too
	 * 
	 * @param part
	 */
	public void onPartMaterialChange(IPart part, IMaterial formerMaterial);

	/**
	 * Called when part of this Form experiences a change in size (e.g. is shrunk)
	 * 
	 * @param part
	 * @param formerMaterial
	 */
	public void onPartSizeChange(IPart part, float formerMaterial);

	/**
	 * Called when part of this Form experiences a change in shape
	 * 
	 * @param part
	 * @param tick
	 */
	public void onPartShapeChange(IPart part, IShape formerShape);

	/**
	 * The part which experienced a change in stains, and the stains which were
	 * added/removed
	 * 
	 * @param part
	 * @param stains
	 */
	public void onPartStainChange(IPart part, Collection<IStain> stains);

	/**
	 * Return the actor that owns this s
	 * 
	 * @return
	 */
	public IActor getOwner();

	/**
	 * Run a tick on this Form; used to keep it constantly updating its info
	 * 
	 * @param ticks
	 */
	public void runTick(long ticks);

	/**
	 * Size of this, in terms of height in meters
	 * 
	 * @return
	 */
	float size();

	/**
	 * Set the actor that owns this
	 * 
	 * @param owner
	 */
	void setOwner(IActor owner);
}
