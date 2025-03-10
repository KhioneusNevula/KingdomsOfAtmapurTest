package things.form.soma.condition;

import java.util.Collection;
import java.util.function.Predicate;

import _sim.plane.IPlane;
import things.form.graph.connections.IPartConnection;
import things.form.soma.ISoma;
import things.form.soma.abilities.IPartAbility;
import things.form.soma.component.IComponentPart;
import things.form.soma.stats.IPartStat;
import thinker.concepts.general_types.ILogicConcept;
import utilities.graph.IInvertibleRelationType;
import utilities.graph.IRelationGraph;

/**
 * A condition of proper functionality for a body
 * 
 * @author borah
 *
 */
public interface ISomaCondition extends Predicate<IRelationGraph<IComponentPart, IPartConnection>> {

	/**
	 * Allowed types of relations for a soma condition
	 * 
	 * @author borah
	 * @author borah
	 *
	 */
	public static interface ISomaRelationType extends IInvertibleRelationType {
		/**
		 * Whether this relation indicates some comparison of the value of a stat, e.g.
		 * equals, less than, etc
		 */
		public boolean characterizesValue();
	}

	/**
	 * Standard types of relations for a soma condition
	 *
	 */
	public enum SomaRelationType implements ISomaRelationType {
		/** Expectation that two parts are connected */
		CONNECTED,
		/** Expectation a part has some ability */
		HAS_ABILITY, IS_ABILITY_OF(HAS_ABILITY),
		/**
		 * Expectation that some part has a given stat (uses a logical connector to
		 * indicate the stat and the value)
		 */
		HAS_STAT, IS_STAT_OF(HAS_STAT),
		/**
		 * Expectation that a stat (indicated from a connector) has the exact value as
		 * indicated. Alternatively, indicates a part has a quantity as indicated.
		 */
		EQUALS(true),
		/**
		 * Expectation that a stat has anything but the value indiccated; alternatively
		 * indicates a part has the quantity indicated.
		 */
		NOT_EQUALS(true),
		/**
		 * Expectation that a stat (indicated from a connector) has a greater value than
		 * indicated. Alternatively, indicates a part has a quantity as indicated.
		 */
		GREATER_THAN(true),
		/**
		 * Inverse of {@link #GREATER_THAN}
		 */
		LESS_THAN_OR_EQUAL_TO(GREATER_THAN),
		/**
		 * Expectation that a stat (indicated from a connector) has a lower value than
		 * indicated. Alternatively, indicates a part has a quantity as indicated.
		 */
		LESS_THAN(true),
		/**
		 * Inverse of {@link #LESS_THAN}
		 */
		GREATER_THAN_OR_EQUAL_TO(LESS_THAN),
		/**
		 * Expectation that the stat is a multiple of the numeric value (used for
		 * {@linkplain IPlane planes})
		 */
		MULTIPLE_OF(true), FACTOR_OF(MULTIPLE_OF);

		private SomaRelationType inverse;
		private boolean value;

		private SomaRelationType() {
			inverse = this;
		}

		private SomaRelationType(boolean value) {
			this();
			this.value = value;
		}

		private SomaRelationType(SomaRelationType inv) {
			inverse = inv;
			inv.inverse = this;
			this.value = inv.value;
		}

		@Override
		public IInvertibleRelationType invert() {
			return inverse;
		}

		@Override
		public boolean bidirectional() {
			return inverse == this;
		}

		@Override
		public boolean characterizesValue() {
			return value;
		}
	}

	/**
	 * Tests if the given graph, representing a body/soma, is permissible by this
	 * condition
	 */
	@Override
	boolean test(IRelationGraph<IComponentPart, IPartConnection> t);

	/**
	 * Returns a graph of what this condition expects. Graph nodes may be:
	 * {@linkplain String}s for an abstract part, {@linkplain IComponentPart}s for
	 * distinctive parts, {@linkplain IPartStat}s, {@linkplain IPartAbility}s,
	 * {@linkplain Object}s that are stat values, and {@linkplain ILogicConcept}s
	 * (to connect different ideas, e.g. Head -has_stat-> (propertyAndValue) ->
	 * {-has_stat-> Sensor_Planes, -exactly->3} .Anything else will be considered
	 * meaningless. <br>
	 * RelationTypes may be as those defined in {@link SomaRelationType}, or
	 * something else provided that it is utilized probably in {@link #test(ISoma)}
	 * 
	 * The analysis scheme is as follows; Strings are pattern matchers, and look for
	 * parts with stats as indicated.
	 * 
	 * @return
	 */
	public IRelationGraph<?, ? extends ISomaRelationType> getConditionGraph();

	/**
	 * Get the strings for the part-matchers
	 * 
	 * @return
	 */
	public Collection<String> getParts();

}
