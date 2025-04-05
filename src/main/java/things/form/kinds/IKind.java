package things.form.kinds;

import java.util.Collection;
import java.util.Collections;

import _sim.GameUniverse;
import metaphysics.being.IBeing;
import party.collective.ICollective;
import party.kind.IKindCollective;
import things.actor.IActor;
import things.form.channelsystems.IChannelSystem;
import things.form.kinds.settings.IKindSettings;
import things.form.soma.ISoma;
import thinker.concepts.IConcept;

/**
 * A Kind is a blueprint/template which something is generated from, and also
 * the category it falls into; it can be a Species or just an object class
 * 
 * @author borah
 *
 */
public interface IKind {

	/**
	 * The kind for things which are generated in a custom fashion and therefore do
	 * not have a distinctive species. Use this for unique beings and objects, for
	 * example
	 */
	public static final IKind MISCELLANEOUS = new IKind() {

		@Override
		public String name() {
			return "_miscellaneous";
		}

		@Override
		public String toString() {
			return "|>MISCELLANEOUS_OBJECT<|";
		}

		@Override
		public ISoma generateSoma(IKindSettings settings, IActor actorFor) {
			throw new UnsupportedOperationException("cannot generate body using settings " + settings + " for "
					+ actorFor + " using miscellaneous kind");
		}

		@Override
		public IBeing generateBeing(IKindSettings settings) {
			throw new UnsupportedOperationException(
					"cannot generate being for settings " + settings + " using miscellaneous kind");
		}

		@Override
		public boolean isDisembodied() {
			return true;
		}

		@Override
		public IKindCollective generateCollective(GameUniverse forUniverse) {
			return null;
		}

		@Override
		public IChannelSystem getChannelSystemByName(String name) {
			return null;
		}

		@Override
		public Collection<IChannelSystem> getGeneratableChannelSystems() {
			return Collections.emptySet();
		}
	};

	/**
	 * The name of this Kind
	 * 
	 * @return
	 */
	public String name();

	/**
	 * Generate a Soma for something of this Kind from the given settings for the
	 * given actor (which may be null?) (without populating its body with the
	 * internal systems; do that externally). Return null if this kind can only
	 * generate disembodied beings?
	 * 
	 * @param <R>
	 * @param settings
	 * @return
	 */
	public ISoma generateSoma(IKindSettings settings, IActor actorFor);

	/** Whether this Kind can only be used to generate disembodied beings */
	public boolean isDisembodied();

	/**
	 * If this kind ONLY generates disembodied beings, generate one being from the
	 * given settings
	 */
	default public IBeing generateBeing(IKindSettings settings) {
		return null;
	}

	/** Returns channel systems that this Kind can generate. */
	public Collection<IChannelSystem> getGeneratableChannelSystems();

	/**
	 * Returns a permissible channel system that this kind can generate using its
	 * name
	 */
	public <T extends IChannelSystem> T getChannelSystemByName(String name);

	/**
	 * Generates a new {@link ICollective} for this {@link IKind} in the universe.
	 * Return null if that's not possible
	 */
	public IKindCollective generateCollective(GameUniverse forUniverse);

}
