package things.form.soma;

import static things.form.soma.IPartHealth.average;
import static things.form.soma.IPartHealth.fromCondition;
import static things.form.soma.IPartHealth.minimum;

import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import things.form.shape.property.ShapeProperty;
import things.form.soma.component.IComponentPart;

/**
 * This maps the state of a part to a value between 0.0f and 1.0f used to
 * represent things like the health of a part
 */
@FunctionalInterface
public interface IPartHealth {

	public static enum Standard implements IPartHealth {

		/**
		 * the health value where part health is based on its
		 * {@link ShapeProperty#INTEGRITY}
		 */
		INTEGRITY((s, c) -> c.getShape().getProperty(ShapeProperty.INTEGRITY));

		BiFunction<ISoma, IComponentPart, Float> pr;

		private Standard(BiFunction<ISoma, IComponentPart, Float> pre) {
			pr = pre;
		}

		public float health(ISoma soma, IComponentPart part) {
			return pr.apply(soma, part);
		}
	}

	public static class BinaryPartHealth implements IPartHealth {

		private IPartDestroyedCondition fromcon;

		private BinaryPartHealth(IPartDestroyedCondition fromcon) {
			this.fromcon = fromcon;
		}

		@Override
		public float health(ISoma soma, IComponentPart part) {
			return fromcon.isDestroyed(soma, part) ? 0f : 1f;
		}

		@Override
		public String toString() {
			return this.getClass().getSimpleName() + "{" + fromcon + "}";
		}

		@Override
		public int hashCode() {
			return fromcon.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			return obj == this || (obj instanceof BinaryPartHealth bph ? this.fromcon.equals(bph.fromcon) : false);
		}

	}

	/**
	 * Returns a binary health signaller based on a part destruction condition; i.e.
	 * destroyed = 0f, not destroyed = 1f
	 */
	public static BinaryPartHealth fromCondition(IPartDestroyedCondition condition) {
		return new BinaryPartHealth(condition);
	}

	/**
	 * Part health checker that calculates the minimum of its constituent elements
	 */
	public static class MinPartHealth implements IPartHealth {

		/**
		 * Indicates something's health depends on its integrity, solidity, and
		 * shapedness
		 */
		public static final MinPartHealth INTEGRITY_SOLIDITY_SHAPEDNESS = minimum(Standard.INTEGRITY,
				fromCondition(IPartDestroyedCondition.Standard.MADE_NONSOLID),
				fromCondition(IPartDestroyedCondition.Standard.MASHED));

		private Set<? extends IPartHealth> conditions;

		private MinPartHealth(Set<? extends IPartHealth> iters) {
			conditions = iters;
		}

		public float health(ISoma soma, IComponentPart part) {
			float tot = Float.MAX_VALUE;
			for (IPartHealth ipdc : conditions) {
				tot = Math.min(tot, ipdc.health(soma, part));
			}
			return tot;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this)
				return true;
			if (obj instanceof MinPartHealth cp) {
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

	/**
	 * Part health checker that calculates the average of its constituent elements
	 */
	public static class AveragedPartHealth implements IPartHealth {

		/**
		 * Indicates something is destroyed when it is either disintegrated, granulated,
		 * or mashed.
		 */
		public static final AveragedPartHealth INTEGRITY_SOLIDITY_SHAPEDNESS = average(Standard.INTEGRITY,
				fromCondition(IPartDestroyedCondition.Standard.MADE_NONSOLID),
				fromCondition(IPartDestroyedCondition.Standard.MASHED));

		private Map<? extends IPartHealth, Float> conditions;

		private AveragedPartHealth(Map<? extends IPartHealth, Float> iters) {
			conditions = ImmutableMap.copyOf(iters);
		}

		private AveragedPartHealth(Set<? extends IPartHealth> iters) {
			float weights = 1f / iters.size();
			conditions = Maps.asMap(iters, (x) -> weights);
		}

		public float health(ISoma soma, IComponentPart part) {
			float tot = 0;
			for (IPartHealth ipdc : conditions.keySet()) {
				tot += conditions.get(ipdc) * ipdc.health(soma, part);
			}
			return tot;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this)
				return true;
			if (obj instanceof AveragedPartHealth cp) {
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

	public static MinPartHealth minimum(Iterable<? extends IPartHealth> cons) {
		return new MinPartHealth(ImmutableSet.copyOf(cons));
	}

	public static MinPartHealth minimum(IPartHealth... cons) {
		return new MinPartHealth(Set.of(cons));
	}

	public static AveragedPartHealth weightedAverage(Map<? extends IPartHealth, Float> cons) {
		return new AveragedPartHealth(ImmutableMap.copyOf(cons));
	}

	public static AveragedPartHealth weightedAverage(Iterable<Map.Entry<? extends IPartHealth, Float>> cons) {
		return new AveragedPartHealth(ImmutableMap.copyOf(cons));
	}

	public static AveragedPartHealth weightedAverage(Map.Entry<IPartHealth, Float>... cons) {
		return new AveragedPartHealth(ImmutableMap.ofEntries(cons));
	}

	public static AveragedPartHealth average(Iterable<? extends IPartHealth> cons) {
		return new AveragedPartHealth(ImmutableSet.copyOf(cons));
	}

	public static AveragedPartHealth average(IPartHealth... cons) {
		return new AveragedPartHealth(Set.of(cons));
	}

	/**
	 * What health this part is at
	 * 
	 * @param part
	 * @param currentMaterial
	 * @return
	 */
	public float health(ISoma soma, IComponentPart part);

}
