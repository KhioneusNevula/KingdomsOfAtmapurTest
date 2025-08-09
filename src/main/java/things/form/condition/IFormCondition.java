package things.form.condition;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;

import com.google.common.base.Predicates;

import _sim.plane.IPlane;
import _utilities.graph.EmptyGraph;
import _utilities.graph.IInvertibleRelationType;
import _utilities.graph.IRelationGraph;
import things.form.IForm;
import things.form.channelsystems.IChannelSystem;
import things.form.soma.ISoma;
import things.form.soma.abilities.IPartAbility;
import things.form.soma.component.IComponentPart;
import things.form.soma.stats.IPartStat;
import thinker.concepts.general_types.IConnectorConcept;

/** A condition to use to characterize a body */
public interface IFormCondition extends Predicate<IForm<?>> {

	/** Represents only the condition that a being has a body at all */
	public static final IFormCondition MUST_BE_CORPOREAL = new IFormCondition() {
		@Override
		public boolean test(IForm<?> t) {
			return t != null;
		}

		@Override
		public IRelationGraph<?, ? extends IFormRelationType> getConditionGraph() {
			return EmptyGraph.instance();
		}

		@Override
		public Collection<String> getParts() {
			return Collections.emptySet();
		}

		@Override
		public Collection<IChannelSystem> getChannelSystemRequirements() {
			return Collections.emptySet();
		}
	};

	/** A condition that simply returns false no matter what */
	public static final IFormCondition FALSE = new IFormCondition() {

		@Override
		public boolean test(IForm<?> t) {
			return false;
		}

		@Override
		public Collection<String> getParts() {
			return Collections.emptySet();
		}

		@Override
		public IRelationGraph<?, ? extends IFormRelationType> getConditionGraph() {
			return EmptyGraph.instance();
		}

		@Override
		public Collection<IChannelSystem> getChannelSystemRequirements() {
			return Collections.emptySet();
		}
	};

	/** A condition that simply returns true no matter what */
	public static final IFormCondition TRUE = new IFormCondition() {

		@Override
		public boolean test(IForm<?> t) {
			return true;
		}

		@Override
		public Collection<String> getParts() {
			return Collections.emptySet();
		}

		@Override
		public IRelationGraph<?, ? extends IFormRelationType> getConditionGraph() {
			return EmptyGraph.instance();
		}

		@Override
		public Collection<IChannelSystem> getChannelSystemRequirements() {
			return Collections.emptySet();
		}
	};

	/**
	 * Allowed types of relations for a Form condition
	 * 
	 * @author borah
	 *
	 */
	public static interface IFormRelationType extends IInvertibleRelationType {
		/**
		 * Whether this relation indicates some comparison of the value of a stat, e.g.
		 * equals, less than, etc
		 */
		public boolean characterizesValue();
	}

	/**
	 * Standard types of relations for a form condition
	 *
	 */
	public enum FormRelationType implements IFormRelationType {
		/** Expectation that two parts are connected */
		CONNECTED(String.class::isInstance),
		/** Expectation a part has some ability. Only works for Somas! */
		HAS_ABILITY(IPartAbility.class::isInstance), IS_ABILITY_OF(HAS_ABILITY, String.class::isInstance),
		/**
		 * Expectation that some part has a given sensable trait of the given value
		 * (using a logical connector to indicate the trait and value)
		 */
		HAS_SENSABLE_TRAIT, IS_SENSABLE_TRAIT_OF(HAS_SENSABLE_TRAIT, String.class::isInstance),
		/**
		 * Expectation that some part has a given stat (uses a logical connector to
		 * indicate the stat and the value)
		 */
		HAS_STAT, IS_STAT_OF(HAS_STAT, String.class::isInstance),
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

		private FormRelationType inverse;
		private boolean value;
		private Predicate<Object> pred = Predicates.alwaysTrue();

		private FormRelationType() {
			inverse = this;
		}

		private FormRelationType(Predicate<Object> pre) {
			inverse = this;
			this.pred = pre;
		}

		private FormRelationType(boolean value) {
			this();
			this.value = value;

		}

		private FormRelationType(boolean value, Predicate<Object> pre) {
			this();
			this.value = value;
			this.pred = pre;
		}

		private FormRelationType(FormRelationType inv) {
			inverse = inv;
			inv.inverse = this;
			this.value = inv.value;
		}

		private FormRelationType(FormRelationType inv, Predicate<Object> pre) {
			inverse = inv;
			inv.inverse = this;
			this.value = inv.value;
			this.pred = pre;
		}

		@Override
		public String checkEndType(Object node) {
			if (pred.test(node))
				return null;
			return node + " failed expected predicate " + pred;
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
	boolean test(IForm<?> t);

	/**
	 * Returns a graph of what this condition expects. Graph nodes may be:
	 * {@linkplain String}s for an abstract part, {@linkplain IComponentPart}s for
	 * distinctive parts, {@linkplain IPartStat}s, {@linkplain IPartAbility}s,
	 * {@linkplain Object}s that are stat values, and
	 * {@linkplain IConnectorConcept}s (to connect different ideas, e.g. Head
	 * -has_stat-> (propertyAndValue) -> {-has_stat-> Sensor_Planes, -exactly->3}
	 * .Anything else will be considered meaningless. <br>
	 * RelationTypes may be as those defined in {@link FormRelationType}, or
	 * something else provided that it is utilized probably in {@link #test(ISoma)}
	 * 
	 * The analysis scheme is as follows; Strings are pattern matchers, and look for
	 * parts with stats as indicated. Any two sets of relations is an "and"
	 * relation; "or" relations must be specified with a logical connector.
	 * 
	 * @return
	 */
	public IRelationGraph<?, ? extends IFormRelationType> getConditionGraph();

	/** Return what channel systems this Condition requires */
	public Collection<IChannelSystem> getChannelSystemRequirements();

	/**
	 * Get the strings for the part-matchers
	 * 
	 * @return
	 */
	public Collection<String> getParts();

	/**
	 * Returns if this soma only represents the condition that something HAVE a soma
	 * at all.
	 */
	public default boolean isBodyRequirement() {
		return this == MUST_BE_CORPOREAL;
	}

	/**
	 * Returns if this soma always returns false
	 */
	public default boolean isAlwaysFalse() {
		return this == FALSE;
	}

	/**
	 * Returns if this soma always erturns true
	 */
	public default boolean isAlwaysTrue() {
		return this == TRUE;
	}

}
