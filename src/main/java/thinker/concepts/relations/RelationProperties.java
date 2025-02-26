package thinker.concepts.relations;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import thinker.concepts.general_types.IProfile;
import thinker.mind.memory.StorageType;
import thinker.mind.memory.TruthType;
import utilities.property.IProperty;

public class RelationProperties {

	private RelationProperties() {
	}

	/**
	 * A property representing if this relation is negative, i.e. NOT X
	 */
	public static final IProperty<Boolean> NEGATED = IProperty.make("negated", boolean.class, false);

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
