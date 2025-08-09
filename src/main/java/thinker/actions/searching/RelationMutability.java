package thinker.actions.searching;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Table;

import thinker.concepts.relations.descriptive.IProfileInterrelationType;
import thinker.concepts.relations.descriptive.PRelationCategory;
import thinker.concepts.relations.descriptive.PRelationNature;
import thinker.concepts.relations.descriptive.ProfileInterrelationType;

/**
 * The enumeration of how mutable a being considers different kinds of relations
 * by default
 * 
 * @author borah
 *
 */
public enum RelationMutability {
	/**
	 * Mutability for something's location (usually the most mutable)
	 */
	PHYSICAL_LOCATION(PRelationNature.PHYSICAL, PRelationCategory.LOCATION),
	/**
	 * Mutability for social relations (usually reasonably mutable)
	 */
	SOCIAL_RELATION(PRelationNature.SOCIAL, PRelationCategory.CONNECTION),
	/**
	 * Mutability for what something is made of or joined to (usually not super
	 * mutable)
	 */
	PHYSICAL_CONNECTION(PRelationNature.PHYSICAL, PRelationCategory.CONNECTION),
	/**
	 * Mutability for what something is inherently, (usually not super mutable)
	 */
	KIND(PRelationNature.NATURAL, PRelationCategory.KIND),
	/**
	 * Mutability for what something is connected to metaphysically (usually not
	 * super mutable)
	 */
	METAPHYSICAL_CONNECTION(PRelationNature.METAPHYSICAL, PRelationCategory.CONNECTION)

	;

	/** stores all relmuts by their nature and category */
	private static final Table<PRelationNature, PRelationCategory, RelationMutability> categ = HashBasedTable.create();
	/** stores the relation types associated with a relmut */
	private final Set<IProfileInterrelationType> relTypes;
	private PRelationNature relnat;
	private PRelationCategory relcat;

	private RelationMutability(PRelationNature relnat, PRelationCategory relcat) {
		this.relnat = relnat;
		this.relcat = relcat;
		ImmutableSet.Builder<IProfileInterrelationType> setbuil = ImmutableSet.builder();
		for (ProfileInterrelationType ptype : ProfileInterrelationType.values()) {
			if (ptype.getRelationNature() == relnat && ptype.getRelationCategory() == relcat) {
				setbuil.add(ptype);
			}
		}
		relTypes = setbuil.build();

	}

	/**
	 * Return a relation mutability based on its nature, category (or throw error if
	 * none are found)
	 * 
	 * @param nat
	 * @param cat
	 * @return
	 */
	public static RelationMutability fromNatCat(PRelationNature nat, PRelationCategory cat) {
		if (!categ.contains(nat, cat)) {
			Arrays.stream(values()).filter((a) -> a.relcat == cat && a.relnat == nat).findAny()
					.ifPresentOrElse((rm) -> categ.put(nat, cat, rm), () -> {
						throw new IllegalStateException("Relmut not found for " + nat + " and " + cat);
					});
		}
		return categ.get(nat, cat);
	}

	/**
	 * The domain of reality this mutability's relation is associated with
	 * 
	 * @return
	 */
	public PRelationNature getRelationNature() {
		return relnat;
	}

	/**
	 * The manner in which this relation exists
	 * 
	 * @return
	 */
	public PRelationCategory getRelationCategory() {
		return relcat;
	}

	/**
	 * Return all the relation types that thiis mutability category encompasses
	 * 
	 * @return
	 */
	public Collection<IProfileInterrelationType> getRelationTypes() {
		return this.relTypes;
	}

	@Override
	public String toString() {
		return this.name() + "(" + relnat + "," + relcat + ")";
	}
}
