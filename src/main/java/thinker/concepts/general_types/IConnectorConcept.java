package thinker.concepts.general_types;

import java.util.Set;
import java.util.UUID;

import thinker.concepts.IConcept;
import thinker.concepts.profile.IProfile;
import thinker.concepts.relations.technical.KnowledgeRelationType;

/**
 * Used to functionally behave as a node which connects relations together.
 */
public interface IConnectorConcept extends IConcept {

	/**
	 * Return the id used to disambiguate this
	 * 
	 * @return
	 */
	public UUID getID();

	/**
	 * What type of logic this is, i.e. and/or
	 * 
	 * @return
	 */
	public ConnectorType getConnectorType();

	/**
	 * If this connector is as described by {@link ConnectorType#PROPERTY_AND_VALUE}
	 * 
	 * @return
	 */
	public default boolean isPropertyAndValue() {
		return this.getConnectorType() == ConnectorType.PROPERTY_AND_VALUE;
	}

	/**
	 * If this connector is as described by {@link ConnectorType#PART_AND_PROPERTY}
	 * 
	 * @return
	 */
	public default boolean isPartAndProperty() {
		return this.getConnectorType() == ConnectorType.PART_AND_PROPERTY;
	}

	public static IConnectorConcept or(UUID id) {
		return new ConnectorConcept(ConnectorType.OR, id);
	}

	public static IConnectorConcept and(UUID id) {
		return new ConnectorConcept(ConnectorType.AND, id);
	}

	public static IConnectorConcept propertyAndValue(UUID id) {
		return new ConnectorConcept(ConnectorType.PROPERTY_AND_VALUE, id);
	}

	public static IConnectorConcept partAndProperty(UUID id) {
		return new ConnectorConcept(ConnectorType.PART_AND_PROPERTY, id);
	}

	public static IConnectorConcept or() {
		return or(UUID.randomUUID());
	}

	public static IConnectorConcept and() {
		return and(UUID.randomUUID());
	}

	public static IConnectorConcept propertyAndValue() {
		return propertyAndValue(UUID.randomUUID());
	}

	public static IConnectorConcept elementAndProperty() {
		return partAndProperty(UUID.randomUUID());
	}

	/**
	 * TYpes of logical connectors.
	 * 
	 * @author borah
	 *
	 */
	public enum ConnectorType {
		/**
		 * A simple and connection; something is related to all of the things this
		 * connects to
		 */
		AND,
		/**
		 * A simple connection; something is related to any one of the things this
		 * connects to
		 */
		OR,
		/**
		 * A more complex connection; something joined to this connector by a
		 * {@link KnowledgeRelationType#CHARACTERIZED_BY} relation has a property
		 * described using the {@link KnowledgeRelationType#CHARACTERIZED_BY} relation
		 * emerging from this connector, and that property has the value described by
		 * the {@link KnowledgeRelationType#HAS_VALUE} relation emerging from this
		 * connector.
		 */
		PROPERTY_AND_VALUE,
		/**
		 * Indicates that the part connected to this connector by the
		 * {@link KnowledgeRelationType#PART_OF} has the given property indicated by the
		 * {@link KnowledgeRelationType#CHARACTERIZED_BY} connection from this
		 */
		PART_AND_PROPERTY,
		/**
		 * Indicates that this connector is the version of the {@link IProfile} or
		 * {@link IPrincipleConcept} connected to this by
		 * {@link KnowledgeRelationType#WAS}. The {@link KnowledgeRelationType#AT_TIME}
		 * relation indicates when this version existed. Any other relations from this
		 * connector represent the state or actions of this profile at that time.
		 */
		EVENTIVE;

		public static Set<ConnectorType> logicalConnectors() {
			return Set.of(AND, OR);
		}
	}
}
