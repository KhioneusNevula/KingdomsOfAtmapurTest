package things;

import java.util.Collection;
import java.util.UUID;

import sim.RelativeSide;
import things.interfaces.IActor;
import things.physical_form.IPart;
import things.physical_form.components.IComponentPart;
import things.physical_form.graph.CoverageType;
import things.physical_form.graph.IPartConnection;
import utilities.graph.IRelationGraph;

/**
 * Representation of a "body" or "visage" as a graph
 * 
 * @author borah
 *
 * @param <P>
 */
public interface IMultipart<P extends IPart> {

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
	 * successful or false if not
	 * 
	 * @param toPart
	 * @param newPart
	 * @param coveringsides the sides of the other part that this body part covers
	 */
	public boolean attach(IComponentPart newPart, IComponentPart toPart, IPartConnection connectionType,
			Collection<RelativeSide> coveringsides);

	/**
	 * Return the actor that owns this s
	 * 
	 * @return
	 */
	public IActor getOwner();

	/**
	 * Run a tick on this multipart entity; used to keep it constantly updating its
	 * info
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
