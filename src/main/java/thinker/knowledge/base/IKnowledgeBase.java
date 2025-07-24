package thinker.knowledge.base;

import java.util.UUID;

import thinker.concepts.IConcept;
import thinker.concepts.application.IConceptAssociationInfo;
import thinker.concepts.profile.TypeProfile;
import thinker.concepts.relations.IConceptRelationType;
import thinker.constructs.IRecipeConcept;
import thinker.knowledge.IKnowledgeRepresentation;

/**
 * Interface representing a container of connections for an individual or group.
 * As a general paradigm:
 * <ul>
 * <li>An "and" concept should not be represented generally, but rather using a
 * {@link TypeProfile} or {@link IRecipeConcept}. Specifically, a
 * {@link TypeProfile} bundles together traits and is assumed to have an "and"
 * relation among all its traits, basically.
 * <li>An "or" relation is the default relation things have when multiple edges
 * extend from them, other than {@link TypeProfile}s.
 * <li>A {@link IConceptAssociationInfo} associator should not be connected to
 * anything with an and relation; {@link IConceptAssociationInfo}s are always in
 * or-structure with one another. If you want "and", add it to the existing
 * instanceof {@link IConceptAssociationInfo} rather than making a new one
 * </ul>
 * 
 * @author borah
 *
 */
public interface IKnowledgeBase extends IKnowledgeRepresentation {

	/**
	 * Return the concept representing the self
	 * 
	 * @return
	 */
	public IConcept getSelfConcept();

	/**
	 * Return the UUID of the source of this relation
	 * 
	 * @param from
	 * @param type
	 * @param to
	 * @return
	 */
	public UUID getInfoSource(IConcept from, IConceptRelationType type, IConcept to);

	/**
	 * set the id of the source of this relation
	 * 
	 * @param from
	 * @param type
	 * @param to
	 * @param source
	 */
	public void setInfoSource(IConcept from, IConceptRelationType type, IConcept to, UUID source);

	@Override
	public IKnowledgeBase clone();

}
