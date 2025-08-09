package thinker.mind.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

import _sim.world.GameMap;
import _utilities.collections.ImmutableCollection;
import metaphysics.being.IBeing;
import metaphysics.soul.ISoul;
import metaphysics.spirit.ISpirit;
import party.agent.IAgent;
import party.util.IAgentAccess;
import things.actor.IActor;
import things.form.soma.ISoma;
import things.form.soma.component.IComponentPart;
import thinker.mind.memory.IMindKnowledgeBase;
import thinker.mind.personality.IPersonality;

/**
 * An extension of {@link IAgentAccess} with info about a component part that a
 * spirit is tied to or none, a collection of accessible parts, and the current
 * ticks
 * 
 * @author borah
 *
 */
public interface IBeingAccess extends IAgentAccess {

	/**
	 * Create thinker info for an untethered thinker
	 * 
	 * @param being
	 * @param ticks
	 * @return
	 */
	public static IBeingAccess create(IBeing being, long ticks, GameMap map) {
		return new UntetheredMindAccess(being, ticks, map);
	}

	/** Create thinker info for a tethered thinker */
	public static IBeingAccess create(IBeing being, IComponentPart part, Collection<? extends IComponentPart> access,
			long ticks) {
		return new TetheredMindAccess(being, Objects.requireNonNull(part), access, ticks);
	}

	/**
	 * Returns the actor as an {@link Optional} in case the being is noncorporeal
	 */
	public default Optional<IActor> maybeActor() {
		return this.maybeSoma().map(ISoma::getOwner);
	}

	/**
	 * Returns the soma this is part of as an {@link Optional}, in case the being is
	 * noncorporeal
	 */
	public default Optional<ISoma> maybeSoma() {
		return this.maybeTetherPart().map(IComponentPart::getSomaOwner);
	}

	/** Parts that this has access to or an empty collection if none */
	public Collection<? extends IComponentPart> partAccess();

	/**
	 * what part this spirit is tethered to as an {@link Optional} in case the being
	 * is noncorporeal
	 */
	public Optional<IComponentPart> maybeTetherPart();

	/** The being that is thinking */
	public IBeing being();

	/** Get the knowledge of the being */
	public default IMindKnowledgeBase knowledge() {
		return being().getKnowledge();
	}

	/** Get the personality of the being */
	public default IPersonality personality() {
		return being().getPersonality();
	}

	/** If this thinker has a body */
	public default boolean isCorporeal() {
		return maybeTetherPart().isPresent();
	}

	/**
	 * If {@link #being()} is a spirit {@link ISpirit}, return that; otherwise an
	 * empty {@link Optional}
	 */
	public default Optional<ISpirit> maybeSpirit() {
		return Optional.of(being()).filter((b) -> b instanceof ISpirit).map(ISpirit.class::cast);
	}

	/**
	 * If {@link #being()} is a soul {@link ISoul}, return that; otherwise an empty
	 * {@link Optional}
	 */
	public default Optional<ISoul> maybeSoul() {
		return Optional.of(being()).filter((b) -> b instanceof ISoul).map(ISoul.class::cast);
	}

	class TetheredMindAccess implements IBeingAccess {
		private IBeing being;
		private long ticks;
		private IComponentPart part;
		private Collection<? extends IComponentPart> access;

		public TetheredMindAccess(IBeing being, IComponentPart part, Collection<? extends IComponentPart> access,
				long ticks) {
			this.being = being;
			this.ticks = ticks;
			this.part = part;
			this.access = ImmutableCollection.from(access);
		}

		@Override
		public Collection<? extends IComponentPart> partAccess() {
			return access;
		}

		@Override
		public IAgent agent() {
			return being;
		}

		@Override
		public IBeing being() {
			return being;
		}

		@Override
		public Optional<IComponentPart> maybeTetherPart() {
			return Optional.of(part);
		}

		@Override
		public GameMap gameMap() {
			return maybeActor().map(IActor::getMap)
					.orElseThrow(() -> new IllegalStateException("No game map available for " + this));
		}

		@Override
		public long ticks() {
			return ticks;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof TetheredMindAccess uti) {
				return this.being.equals(uti.being) && this.ticks == uti.ticks && this.part.equals(uti.part)
						&& this.access.equals(uti.access);
			}
			return super.equals(obj);
		}

		@Override
		public int hashCode() {
			return this.being.hashCode() + Long.hashCode(ticks) + this.part.hashCode() + this.access.hashCode();
		}

		@Override
		public String toString() {
			return "{being=" + this.being + ",ticks=" + this.ticks + ",part=" + this.part + ",access=" + this.access
					+ "}";
		}
	}

	class UntetheredMindAccess implements IBeingAccess {
		private IBeing being;
		private long ticks;
		private GameMap map;

		public UntetheredMindAccess(IBeing being, long ticks, GameMap map) {
			this.being = being;
			this.ticks = ticks;
			this.map = map;
		}

		@Override
		public GameMap gameMap() {
			return this.map;
		}

		@Override
		public Collection<? extends IComponentPart> partAccess() {
			return Collections.emptySet();
		}

		@Override
		public IAgent agent() {
			return being;
		}

		@Override
		public IBeing being() {
			return being;
		}

		@Override
		public Optional<IComponentPart> maybeTetherPart() {
			return Optional.empty();
		}

		@Override
		public long ticks() {
			return ticks;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof UntetheredMindAccess uti) {
				return this.being.equals(uti.being) && this.ticks == uti.ticks;
			}
			return super.equals(obj);
		}

		@Override
		public int hashCode() {
			return this.being.hashCode() + Long.hashCode(ticks);
		}

		@Override
		public String toString() {
			return "{being=" + this.being + ",ticks=" + this.ticks + ",map=" + this.map + "}";
		}
	}

}
