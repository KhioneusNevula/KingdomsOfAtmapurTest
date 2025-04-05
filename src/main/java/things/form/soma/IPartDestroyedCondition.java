package things.form.soma;

import static things.form.soma.IPartDestroyedCondition.compose;

import java.util.Set;
import java.util.function.BiPredicate;

import com.google.common.collect.ImmutableSet;

import things.form.material.property.MaterialProperty;
import things.form.material.property.Phase;
import things.form.shape.property.ShapeProperty;
import things.form.shape.property.ShapeProperty.Shapedness;
import things.form.soma.component.IComponentPart;

/** A condition checking the state of a component part in a soma */
@FunctionalInterface
public interface IPartDestroyedCondition {

	public static enum Standard implements IPartDestroyedCondition {

		/**
		 * the condition where parts are considered destroyed if their integrity is 0
		 */
		DISINTEGRATED((s, c) -> c.getShape().getProperty(ShapeProperty.INTEGRITY) == 0),
		/**
		 * the condition where parts are considered destroyed if they enter any kind of
		 * non-solid phase, including liquid or granular.
		 */
		MADE_NONSOLID((s, c) -> c.getMaterial().getProperty(MaterialProperty.PHASE) != Phase.SOLID),
		/**
		 * The condition where parts are considered destroyed only if they become gas or
		 * plasma. Water elementals and the like would be in this category.
		 */
		VAPORIZED((s, c) -> c.getMaterial().getProperty(MaterialProperty.PHASE).isGaseous()),
		/**
		 * The condition where parts are cnosidered destroyed only if they become purely
		 * plasma. Air elementals and the like would be in this category
		 */
		PLASMIFIED((s, c) -> c.getMaterial().getProperty(MaterialProperty.PHASE) == Phase.PLASMA),
		/**
		 * The condition where parts are considered destroyed only if they become non
		 * liquid/non fluid, i.e. a solid or granular. Fire elementals and the like
		 * would be in this category.
		 */
		SOLIDIFIED((s, c) -> c.getMaterial().getProperty(MaterialProperty.PHASE).isSolidOrGranular()),
		/**
		 * The condition where parts are considered destroyed only if they become a hard
		 * solid, not fluid or granular. Dust elementals and the like would be in this
		 * category.
		 */
		HARDENED((s, c) -> c.getMaterial().getProperty(MaterialProperty.PHASE) == Phase.SOLID),

		/**
		 * The condition where parts are considered destroyed when "mashed up"
		 */
		MASHED((s, c) -> c.getShape().getProperty(ShapeProperty.SHAPEDNESS) == Shapedness.AMORPHIC);

		BiPredicate<ISoma, IComponentPart> pr;

		private Standard(BiPredicate<ISoma, IComponentPart> pre) {
			pr = pre;
		}

		public boolean isDestroyed(ISoma soma, IComponentPart part) {
			return pr.test(soma, part);
		}
	}

	public static class DisjointPartStateCondition implements IPartDestroyedCondition {

		/**
		 * Indicates something is destroyed when it is either disintegrated, granulated,
		 * or mashed.
		 */
		public static final DisjointPartStateCondition DISINTEGRATE_NONSOLID_MASHED = compose(Standard.DISINTEGRATED,
				Standard.MADE_NONSOLID, Standard.MASHED);

		private Set<? extends IPartDestroyedCondition> conditions;

		private DisjointPartStateCondition(Set<? extends IPartDestroyedCondition> iters) {
			conditions = iters;
		}

		public boolean isDestroyed(ISoma soma, IComponentPart part) {
			for (IPartDestroyedCondition ipdc : conditions) {
				if (ipdc.isDestroyed(soma, part)) {
					return true;
				}
			}
			return false;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this)
				return true;
			if (obj instanceof DisjointPartStateCondition cp) {
				return this.conditions.equals(cp.conditions);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return conditions.hashCode();
		}

		@Override
		public String toString() {
			return this.getClass().getSimpleName() + this.conditions;
		}
	}

	public static DisjointPartStateCondition compose(Iterable<? extends IPartDestroyedCondition> cons) {
		return new DisjointPartStateCondition(ImmutableSet.copyOf(cons));
	}

	public static DisjointPartStateCondition compose(IPartDestroyedCondition... cons) {
		return new DisjointPartStateCondition(Set.of(cons));
	}

	/**
	 * Whether the part is destroyed by being the material it is currently
	 * 
	 * @param part
	 * @param currentMaterial
	 * @return
	 */
	public boolean isDestroyed(ISoma soma, IComponentPart part);

}
