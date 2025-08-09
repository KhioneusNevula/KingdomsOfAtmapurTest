package metaphysics.magic;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import com.google.common.collect.Streams;

import metaphysics.spirit.ISpirit;
import things.interfaces.UniqueType;
import thinker.concepts.profile.IProfile;

/**
 * A {@linkplain ITether Tether} is stored in a part so that it can be used in
 * magic practice. Tethers are stored for any kind of interaction that leaves a
 * "mark" on the part. They connect to unique, profile-able things. <br>
 * TODO tethers in parts
 * 
 * @author borah
 *
 */
public interface ITether {

	/**
	 * Capabilities a given tether might have. By default, tethers have all of these
	 * capabilities at once
	 */
	public static enum TetherAbility {
		/** Whether the tethered thing can be summoned from afar */
		SUMMONING(1),
		/** Whether the tethered thing can be traveled to */
		TRAVELING(1 << 1),
		/** Whether the tethered thing's properties can be influenced from afar */
		INFLUENCE(1 << 2),
		/** Whether the tethered thing can be destroyed from afar */
		DESTROY(1 << 3),
		/** Whether the tethered thing can be affected across dimensions */
		DIMENSION_ACCESSIBLE(1 << 4),
		/** Whether the tethered thing can be affected across planes */
		PLANE_ACCESSIBLE(1 << 5);

		private int mtx;
		private static int allCaps = 0;
		static {
			for (TetherAbility cap : values()) {
				allCaps |= cap.mtx;
			}
		}

		private TetherAbility(int mtx) {
			this.mtx = mtx;
		}

		/** gets the bits representing this capability */
		public int getBits() {
			return mtx;
		}

		/** Whether this int contains the given capability */
		public static boolean contains(int caps, TetherAbility cap) {
			return (cap.getBits() & caps) != 0;
		}

		/** removes the given capabilities from the given integer */
		public static int remove(int ocaps, Iterable<TetherAbility> caps) {
			int finala = ocaps;
			for (TetherAbility cap : caps) {
				ocaps &= ~cap.getBits();
			}
			return finala;
		}

		/** removes the given capabilities from the given integer */
		public static int remove(int ocaps, TetherAbility... caps) {
			int finala = ocaps;
			for (TetherAbility cap : caps) {
				ocaps &= ~cap.getBits();
			}
			return finala;
		}

		/** Combines the two integer sets of capabilities into one */
		public static int combine(int ocaps, int caps) {
			return ocaps | caps;
		}

		/** Adds the capabilities into the given integer */
		public static int add(int ocaps, Iterable<TetherAbility> caps) {
			return Streams.stream(caps).mapToInt(TetherAbility::getBits).reduce(ocaps, (a, b) -> a | b);
		}

		/** Adds the capabilities into the given integer */
		public static int add(int ocaps, TetherAbility... caps) {
			return Arrays.stream(caps).mapToInt(TetherAbility::getBits).reduce(ocaps, (a, b) -> a | b);
		}

		/** Combines the capabilities into one integer */
		public static int combine(TetherAbility... caps) {
			return Arrays.stream(caps).mapToInt(TetherAbility::getBits).reduce(0, (a, b) -> a | b);
		}

		/** Combines the capabilities into one integer */
		public static int combine(Iterable<TetherAbility> caps) {
			return Streams.stream(caps).mapToInt(TetherAbility::getBits).reduce(0, (a, b) -> a | b);
		}

		/** Returns the capabilities contained in this integer */
		public static Collection<TetherAbility> get(int bits) {
			return Arrays.stream(TetherAbility.values()).filter((x) -> (x.getBits() & bits) != 0)
					.collect(Collectors.toUnmodifiableSet());
		}

		/** Returns an int representing all capabilities together */
		public static int getAllCaps() {
			return allCaps;
		}
	}

	public static enum TetherType {
		/**
		 * This kind of tether marks that a unique {@link ISpirit} is the possessor of a
		 * part.
		 */
		POSSESSOR,
		/**
		 * This kind of tether indicates that a unique {@link ISpirit} was formerly a
		 * possessor of a part. This may be present in, say, now-dead things
		 */
		FORMER_POSSESSOR,
		/** A tether to a place where this originated */
		PLACE_OF_ORIGIN,
		/** A tether to something which created this part */
		PROGENITOR,
		/** The destination of something which causes teleportation */
		PORTAL_DESTINATION,

		/** Some other kind of tether (e.g. in magic) */
		OTHER_TETHER

	}

	/** Return what type of connection this has */
	public TetherType getTetherType();

	/** Returns the profile of what this was tethered to */
	public IProfile getReferentProfile();

	/** Returns what type of thing this is tethered to */
	public default UniqueType getReferentType() {
		return getReferentProfile().getDescriptiveType();
	}

	/** Return capabilities of this tether */
	public int getCapabilities();

	/** Set capabilities of this tether */
	public void setCapabilities(int caps);

	/** Create an immaterial tether */
	public static ITether create(IProfile profile, TetherType type) {
		return new ImTether(profile, type);
	}

	/** Create an immaterial tether with certain capabilities */
	public static ITether createWith(IProfile profile, TetherType type, Iterable<TetherAbility> capabilities) {
		return createWith(profile, type, TetherAbility.combine(capabilities));
	}

	/** Create an immaterial tether with certain capabilities */
	public static ITether createWith(IProfile profile, TetherType type, TetherAbility... capabilities) {
		return createWith(profile, type, TetherAbility.combine(capabilities));
	}

	/** Create an immaterial tether with certain capabilities */
	public static ITether createWith(IProfile profile, TetherType type, int permissions) {
		ImTether tet = new ImTether(profile, type);
		tet.setCapabilities(permissions);
		return tet;
	}
}
