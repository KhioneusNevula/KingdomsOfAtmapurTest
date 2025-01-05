package things.form.soma;

import static things.form.soma.IPartDestructionCondition.compose;

import java.util.Set;
import java.util.function.BiPredicate;

import com.google.common.collect.ImmutableSet;

import things.form.material.property.MaterialProperty;
import things.form.material.property.Phase;
import things.form.shape.property.ShapeProperty;
import things.form.shape.property.ShapeProperty.Shapedness;
import things.form.soma.component.IComponentPart;

public interface IPartDestructionCondition {

	public static enum Standard implements IPartDestructionCondition {
		/**
		 * the condition where parts are considered destroyed if they enter any kind of
		 * non-solid phase, including liquid or granular. Most standard biological
		 * beings are like this
		 */
		DISINTEGRATED((s, c) -> c.getMaterial().getProperty(MaterialProperty.PHASE) != Phase.SOLID),
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

	public static class CompositePredicate implements IPartDestructionCondition {

		/**
		 * Indicates something is destroyed when it is either disintegrated or mashed.
		 */
		public static final CompositePredicate DISINTEGRATE_MASH = compose(Standard.DISINTEGRATED, Standard.MASHED);

		private Set<? extends IPartDestructionCondition> conditions;

		private CompositePredicate(Set<? extends IPartDestructionCondition> iters) {
			conditions = iters;
		}

		public boolean isDestroyed(ISoma soma, IComponentPart part) {
			for (IPartDestructionCondition ipdc : conditions) {
				if (ipdc.isDestroyed(soma, part)) {
					return true;
				}
			}
			return false;
		}
	}

	public static CompositePredicate compose(Iterable<? extends IPartDestructionCondition> cons) {
		return new CompositePredicate(ImmutableSet.copyOf(cons));
	}

	public static CompositePredicate compose(IPartDestructionCondition... cons) {
		return new CompositePredicate(Set.of(cons));
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
