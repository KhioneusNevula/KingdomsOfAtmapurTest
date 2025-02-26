package thinker.concepts.general_types;

import java.util.UUID;

import thinker.concepts.IConcept;
import thinker.concepts.relations.ConceptRelationType;

/**
 * Used to functionally behave as a node which connects relations together.
 */
public interface ILogicConcept extends IConcept {

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
	public LogicType getLogicType();

	/**
	 * If this connector is an AND connector
	 * 
	 * @return
	 */
	public default boolean isAnd() {
		return this.getLogicType() == LogicType.AND;
	}

	/**
	 * If this connector is as described by {@link LogicType#PROPERTY_AND_VALUE}
	 * 
	 * @return
	 */
	public default boolean isPropertyAndValue() {
		return this.getLogicType() == LogicType.PROPERTY_AND_VALUE;
	}

	/**
	 * If this connector is as described by {@link LogicType#ELEMENT_AND_PROPERTY}
	 * 
	 * @return
	 */
	public default boolean isElementAndProperty() {
		return this.getLogicType() == LogicType.ELEMENT_AND_PROPERTY;
	}

	/**
	 * If this connector is an OR connector
	 * 
	 * @return
	 */
	public default boolean isOr() {
		return this.getLogicType() == LogicType.OR;
	}

	public static ILogicConcept or(UUID id) {
		return new LogicConcept(LogicType.OR, id);
	}

	public static ILogicConcept and(UUID id) {
		return new LogicConcept(LogicType.AND, id);
	}

	public static ILogicConcept propertyAndValue(UUID id) {
		return new LogicConcept(LogicType.PROPERTY_AND_VALUE, id);
	}

	public static ILogicConcept elementAndProperty(UUID id) {
		return new LogicConcept(LogicType.ELEMENT_AND_PROPERTY, id);
	}

	public static ILogicConcept or() {
		return or(UUID.randomUUID());
	}

	public static ILogicConcept and() {
		return and(UUID.randomUUID());
	}

	public static ILogicConcept propertyAndValue() {
		return propertyAndValue(UUID.randomUUID());
	}

	public static ILogicConcept elementAndProperty() {
		return elementAndProperty(UUID.randomUUID());
	}

	/**
	 * TYpes of logical connectors.
	 * 
	 * @author borah
	 *
	 */
	public enum LogicType {
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
		 * {@link ConceptRelationType#CHARACTERIZED_BY} relation has a property
		 * described using the {@link ConceptRelationType#CHARACTERIZED_BY} relation
		 * emerging from this connector, and that property has the value described by
		 * the {@link ConceptRelationType#HAS_VALUE} relation emerging from this
		 * connector.
		 */
		PROPERTY_AND_VALUE,
		/**
		 * Indicates that the part connected to this connector by the
		 * {@link ConceptRelationType#CONSTITUTES} has the given property indicated by
		 * the {@link ConceptRelationType#CHARACTERIZED_BY} connection from this
		 */
		ELEMENT_AND_PROPERTY
	}
}
