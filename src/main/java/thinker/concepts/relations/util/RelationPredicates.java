package thinker.concepts.relations.util;

import java.util.Arrays;
import java.util.function.Predicate;

import thinker.concepts.IConcept;
import thinker.concepts.general_types.IConnectorConcept;
import thinker.concepts.general_types.IConnectorConcept.ConnectorType;
import thinker.concepts.profile.IProfile;

/** Utility class to store different relation endpoint predicates */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class RelationPredicates {

	private RelationPredicates() {
	}

	private static Predicate or(Predicate p, Predicate p2) {
		return new Predicate() {
			@Override
			public boolean test(Object t) {
				return p.test(t) || p2.test(t);
			}

			@Override
			public Predicate and(Predicate other) {
				return RelationPredicates.and(this, other);
			}

			@Override
			public Predicate or(Predicate other) {
				return RelationPredicates.or(this, other);
			}

			@Override
			public Predicate negate() {
				return RelationPredicates.negate(this);
			}

			@Override
			public String toString() {
				return "(" + p + " || " + p2 + ")";
			}
		};
	}

	private static Predicate and(Predicate p, Predicate p2) {
		return new Predicate() {
			@Override
			public boolean test(Object t) {
				return p.test(t) && p2.test(t);
			}

			@Override
			public Predicate and(Predicate other) {
				return RelationPredicates.and(this, other);
			}

			@Override
			public Predicate or(Predicate other) {
				return RelationPredicates.or(this, other);
			}

			@Override
			public Predicate negate() {
				return RelationPredicates.negate(this);
			}

			@Override
			public String toString() {
				return "(" + p + " && " + p2 + ")";
			}
		};
	}

	private static Predicate negate(Predicate p) {
		return new Predicate() {
			@Override
			public boolean test(Object t) {
				return !p.test(t);
			}

			@Override
			public Predicate and(Predicate other) {
				return RelationPredicates.and(this, other);
			}

			@Override
			public Predicate or(Predicate other) {
				return RelationPredicates.or(this, other);
			}

			@Override
			public Predicate negate() {
				return p;
			}

			@Override
			public String toString() {
				return "!" + p;
			}
		};
	}

	private static enum RequiresNonUtilityConcept implements Predicate {
		RequireNonUtilityConcept;

		@Override
		public boolean test(Object t) {
			if (t instanceof IConcept p) {
				return !p.isUtilityConcept();
			}
			return false;
		}

		@Override
		public Predicate and(Predicate other) {
			return RelationPredicates.and(this, other);
		}

		@Override
		public Predicate or(Predicate other) {
			return RelationPredicates.or(this, other);
		}

		@Override
		public Predicate negate() {
			return RelationPredicates.negate(this);
		}

	}

	private static enum RequiresIndefiniteProfile implements Predicate {
		RequiresIndefiniteProfileCondition, RequiresTypeProfileCondition, RequiresAnyMatcherProfileCondition;

		@Override
		public boolean test(Object t) {
			if (t instanceof IProfile p) {
				switch (this) {
				case RequiresAnyMatcherProfileCondition:
					return p.isAnyMatcher();
				case RequiresTypeProfileCondition:
					return p.isTypeProfile();
				default:
					return p.isIndefinite();
				}
			} else {
				return false;
			}
		}

		@Override
		public Predicate and(Predicate other) {
			return RelationPredicates.and(this, other);
		}

		@Override
		public Predicate or(Predicate other) {
			return RelationPredicates.or(this, other);
		}

		@Override
		public Predicate negate() {
			return RelationPredicates.negate(this);
		}

	}

	public static <T> Predicate<T> requireIndefiniteProfile() {
		return RequiresIndefiniteProfile.RequiresIndefiniteProfileCondition;
	}

	public static <T> Predicate<T> requireTypeProfile() {
		return RequiresIndefiniteProfile.RequiresTypeProfileCondition;
	}

	public static <T> Predicate<T> requireAnyMatcherProfile() {
		return RequiresIndefiniteProfile.RequiresAnyMatcherProfileCondition;
	}

	public static <T> Predicate<T> requireNonUtilityConcept() {
		return RequiresNonUtilityConcept.RequireNonUtilityConcept;
	}

	public static <T> Predicate<T> requireExactConcept(IConcept... con) {
		return new Predicate<T>() {
			@Override
			public boolean test(T o) {
				for (IConcept c : con) {
					if (o.equals(c)) {
						return true;
					}
				}
				return false;
			}

			@Override
			public Predicate<T> and(Predicate<? super T> other) {
				return RelationPredicates.and(this, other);
			}

			@Override
			public Predicate<T> or(Predicate<? super T> other) {
				return RelationPredicates.or(this, other);
			}

			@Override
			public Predicate<T> negate() {
				return RelationPredicates.negate(this);
			}

			@Override
			public String toString() {
				return "RequireEquals" + Arrays.toString(con);
			}
		};
	}

	public static <T> Predicate<T> requireConnectorConceptType(ConnectorType tipa) {
		return new Predicate<T>() {
			@Override
			public boolean test(T o) {
				return o instanceof IConnectorConcept cc && cc.getConnectorType() == ConnectorType.EVENTIVE;
			}

			@Override
			public Predicate<T> and(Predicate<? super T> other) {
				return RelationPredicates.and(this, other);
			}

			@Override
			public Predicate<T> or(Predicate<? super T> other) {
				return RelationPredicates.or(this, other);
			}

			@Override
			public Predicate<T> negate() {
				return RelationPredicates.negate(this);
			}

			@Override
			public String toString() {
				return "RequireConnectorConcept(" + tipa + ")";
			}
		};
	}

}
