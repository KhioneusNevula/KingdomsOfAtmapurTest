package things.physical_form.components;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import things.physical_form.ISoma;
import things.physical_form.material.IMaterial;
import things.physical_form.material.MaterialProperty;
import things.physical_form.material.Phase;
import utilities.TriFunction.TriPredicate;

public interface IPartDestructionCondition {

	public static enum Standard implements IPartDestructionCondition {
		/**
		 * the condition where parts are considered destroyed if they enter any kind of
		 * non-solid phase, including liquid or granular. Most standard biological
		 * beings are like this
		 */
		DISINTEGRATED((s, c, m) -> m.getProperty(MaterialProperty.PHASE) != Phase.SOLID),
		/**
		 * The condition where parts are considered destroyed only if they become gas or
		 * plasma. Water elementals and the like would be in this category.
		 */
		VAPORIZED((s, c, m) -> m.getProperty(MaterialProperty.PHASE).isGaseous()),
		/**
		 * The condition where parts are cnosidered destroyed only if they become purely
		 * plasma. Air elementals and the like would be in this category
		 */
		PLASMIFIED((s, c, m) -> m.getProperty(MaterialProperty.PHASE) == Phase.PLASMA),
		/**
		 * The condition where parts are considered destroyed only if they become non
		 * liquid/non fluid, i.e. a solid or granular. Fire elementals and the like
		 * would be in this category.
		 */
		SOLIDIFIED((s, c, m) -> m.getProperty(MaterialProperty.PHASE).isSolidOrGranular()),
		/**
		 * The condition where parts are considered destroyed only if they become a hard
		 * solid, not fluid or granular. Dust elementals and the like would be in this
		 * category.
		 */
		HARDENED((s, c, m) -> m.getProperty(MaterialProperty.PHASE) == Phase.SOLID);

		TriPredicate<ISoma<?>, IComponentPart, IMaterial> pr;

		private Standard(TriPredicate<ISoma<?>, IComponentPart, IMaterial> pre) {
			pr = pre;
		}

		public boolean isDestroyed(ISoma<?> soma, IComponentPart part, IMaterial currentMaterial) {
			return pr.test(soma, part, currentMaterial);
		}
	}

	public static class CompositePredicate implements IPartDestructionCondition {

		private Set<? extends IPartDestructionCondition> conditions;

		private CompositePredicate(Set<? extends IPartDestructionCondition> iters) {
			conditions = iters;
		}

		public boolean isDestroyed(ISoma<?> soma, IComponentPart part, IMaterial currentMaterial) {
			for (IPartDestructionCondition ipdc : conditions) {
				if (ipdc.isDestroyed(soma, part, currentMaterial)) {
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
	public boolean isDestroyed(ISoma<?> soma, IComponentPart part, IMaterial currentMaterial);
}
