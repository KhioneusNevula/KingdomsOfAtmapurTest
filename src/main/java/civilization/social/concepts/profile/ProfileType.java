package civilization.social.concepts.profile;

public enum ProfileType {
	/**
	 * A profile of a (real or imagined) individual being or creature
	 */
	INDIVIDUAL,
	/** A profile of a (real or imagined) group of beings */
	GROUP,
	/**
	 * A profile of an item, e.g. artifact, relic, etc, or an object such as a tree
	 * or rock
	 */
	ITEM,
	/** A profile of a structure, e.g. a house, stockpile, etc */
	STRUCTURE,
	/** A profile of a place of some kind, e.g. a landform */
	PLACE,
	/** A profile of a language */
	LANGUAGE, OTHER;

	public boolean individual() {
		return this == INDIVIDUAL;
	}

	public boolean group() {
		return this == GROUP;
	}

	public boolean item() {
		return this == ITEM;
	}

	public boolean structure() {
		return this == STRUCTURE;
	}

	public boolean place() {
		return this == PLACE;
	}

	/**
	 * whether this profile represents a decision-making agent
	 * 
	 * @return
	 */
	public boolean isAgent() {
		return this.individual() || this.group();
	}

	/**
	 * whether this profile represents a location
	 * 
	 * @return
	 */
	public boolean isLocational() {
		return this == STRUCTURE || this == PLACE;
	}

	public boolean language() {
		return this == LANGUAGE;
	}

	/** if this profile does not fit into a standard category */
	public boolean other() {
		return this == OTHER;
	}

}