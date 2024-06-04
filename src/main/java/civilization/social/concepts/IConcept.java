package civilization.social.concepts;

/**
 * A kind of knowledge; e.g. a recognized creature type is a type of concept.
 * Concepts are expected to be "self-contained," i.e. they don't depend on
 * references to multiple external objects, and they should have stable equality
 * checks and consistent hashcodes. As such, typically concepts are expected to
 * be <em> immutable </em>
 * 
 * @author borah
 *
 */
public interface IConcept {

	/**
	 * What kind of concept this is
	 * 
	 * @author borah
	 *
	 */
	public static enum ConceptType {
		COLLECTION, NUMBER, WORD, RELATION, MAP_TILE, PURPOSE, ACTOR_TYPE, WORLD_TYPE, SOUL_TYPE, GROUP_TYPE,
		PHENOMENON_TYPE, INDIVIDUAL_PROFILE, GROUP_PROFILE, ITEM_PROFILE, STRUCTURE_PROFILE, PLACE_PROFILE,
		LANGUAGE_PROFILE, TILE_PROFILE, OTHER_TYPE, OTHER_PROFILE, WORLD_PROFILE, OTHER;

		public boolean isProfileType() {
			return this.name().toLowerCase().endsWith("profile");
		}

		public boolean isObjectType() {
			return this.name().toLowerCase().endsWith("type") || this == MAP_TILE;
		}

		/**
		 * whether this concept type is of the "other" category
		 * 
		 * @return
		 */
		public boolean isUncategorizable() {
			return this.name().toLowerCase().startsWith("other");
		}
	}

	/**
	 * The specific unique name of this concept;
	 * 
	 * @return
	 */
	public String getUniqueName();

	/**
	 * Gets the type of this concept
	 * 
	 * @return
	 */
	public ConceptType getConceptType();
}
