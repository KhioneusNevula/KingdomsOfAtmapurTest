package thinker.concepts.relations.util;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import _utilities.property.IProperty;
import thinker.concepts.profile.IProfile;
import thinker.mind.memory.StorageType;
import thinker.mind.memory.TruthType;

public class RelationProperties {

	private RelationProperties() {
	}

	/**
	 * A property representing if this relation is negated, i.e. NOT X. This is the
	 * default state of any situation where there is COMPLETE relation. For a full,
	 * logical negation, see {@link #OPPOSITE}
	 */
	public static final IProperty<Boolean> NOT = IProperty.make("not", boolean.class, false);

	/**
	 * A property representing if this relation is fully opposite (Existence
	 * IS(OPPOSITE) Nothing, so anything that doesn't match existence MUST match
	 * nothing), i.e. OPPOSITE_OF X. This is equivalent to a logical not. For a
	 * weaker "Not" that only indicates a lack of a relation, see {@link #NOT}
	 */
	public static final IProperty<Boolean> OPPOSITE = IProperty.make("opposite", boolean.class, false);

	/**
	 * This property represents the source of a given social relation, i.e. who
	 * informed you of it
	 */
	public static final IProperty<UUID> INFO_SOURCE = IProperty.make("info_source", UUID.class, () -> null);

	/**
	 * This property represents the temporality of the relation, i.e. whether the
	 * relation is current, always true, periodically true, or only true at a
	 * certain point in time
	 */
	public static final IProperty<TruthType> TRUTH_TYPE = IProperty.make("ttype", TruthType.class, TruthType.GNOMIC);

	/**
	 * This property represents how confident the information is when stored
	 */
	public static final IProperty<StorageType> STORAGE_TYPE = IProperty.make("stype", StorageType.class,
			StorageType.CONFIDENT);

	/**
	 * The level of confidence in this information; only relevant if
	 * {@link #STORAGE_TYPE} is {@link StorageType#DUBIOUS}. Alternatively, how
	 * likely a relation is to be deleted after a few ticks, if the
	 * {@link #STORAGE_TYPE} is {@link StorageType#TEMPORARY}
	 */
	public static final IProperty<Float> CONFIDENCE = IProperty.make("confidence", float.class, 1f);
	/**
	 * The level of likelihood of this relation being true, as an enum
	 */
	public static final IProperty<Likelihood> LIKELIHOOD = IProperty.make("likelihood", Likelihood.class,
			Likelihood.CERTAIN);

	/** Stores the set of all groups which recognize the given relation */
	public static final IProperty<Set<IProfile>> KNOWN_BY_GROUPS = IProperty.make("knowers", Set.class,
			new HashSet<>());

	public static enum Likelihood {
		IMPOSSIBLE, UNLIKELY, LIKELY, CERTAIN
	}

}
