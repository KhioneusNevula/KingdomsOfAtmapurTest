package thinker.concepts.general_types;

import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;

import things.interfaces.UniqueType;
import thinker.concepts.IConcept;
import thinker.concepts.profile.IProfile;
import thinker.concepts.relations.IConceptRelationType;

/**
 * A generic superinterface for {@link IProfile}s and {@link IPropertyConcept}s,
 * indicating any kind of concept which describes things
 */
public interface IDescriptiveConcept extends IConcept {

	/**
	 * Return what kind of world-entities this label corresponds to. If this is
	 * empty or contains {@link UniqueType#ANY}, then it is considered equivalent to
	 * {@link UniqueType#ANY}
	 * 
	 * @return
	 */
	public Collection<UniqueType> getDescriptiveTypes();

	/**
	 * A predicate (for {@link IConceptRelationType#checkEndType(Object)} that casts
	 * its input to an {@link IDescriptiveConcept} and checks if it matches any of
	 * the given unique types
	 */
	public static <T> AnyUniqueTypesPredicate<T> matchesAnyUniqueTypesPredicate(UniqueType... types) {
		return new AnyUniqueTypesPredicate<>(types);
	}

	class AnyUniqueTypesPredicate<T> implements Predicate<T> {

		private Set<UniqueType> types;

		AnyUniqueTypesPredicate(UniqueType... types) {
			this.types = Set.of(types);
		}

		@Override
		public boolean test(T obj) {
			IDescriptiveConcept dc = (IDescriptiveConcept) obj;
			if (dc.getDescriptiveTypes().isEmpty() || dc.getDescriptiveTypes().contains(UniqueType.ANY))
				return true;
			for (UniqueType t : types) {
				if (dc.getDescriptiveTypes().contains(t)) {
					return true;
				}
			}
			return false;
		}

		@Override
		public String toString() {
			return this.getClass().getSimpleName() + "" + this.types;
		}

		@Override
		public boolean equals(Object obj) {
			return obj instanceof AnyUniqueTypesPredicate c && c.types.equals(this.types);
		}

		@Override
		public int hashCode() {
			return this.getClass().hashCode() + types.hashCode();
		}

	}
}
