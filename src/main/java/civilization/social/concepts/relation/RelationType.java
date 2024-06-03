package civilization.social.concepts.relation;

/**
 * An enum of basic kinds of relations that can exist between concepts and
 * entities. relations should be read as going from left to right, i.e. LEFT
 * does something to RIGHT.
 * 
 * @author borah
 *
 */
public enum RelationType implements IConceptRelationType<RelationType> {
	/** relation involving LEFT giving ARG to RIGHT. LEFT is active role. */
	GIVES(prop().transfers().requireArgument().requireAction()),
	/**
	 * relation involving LEFT receiving ARG from RIGHT. LEFT is receptive role.
	 * inverse of {@link #GIVES}
	 */
	RECEIVES(GIVES, prop().transfers().requireArgument(), false),
	/**
	 * LEFT produces RIGHT, e.g. a tree which produces wood. LEFT is active role.
	 * inverse of {@link #PRODUCED_BY}
	 */
	PRODUCES(prop().creates()),
	/**
	 * LEFT is produced from RIGHT, e.g. fruit is produced by a tree. LEFT is
	 * passive role. inverse of {@link #PRODUCES}
	 */
	PRODUCED_BY(PRODUCES, prop().creates(), false),

	/**
	 * LEFT is crafted from RIGHT by means of the recipe(s) ARG. LEFT is passive
	 * role. inverse of {@link #CRAFTS_INTO}
	 */
	CRAFTED_FROM(prop().transforms().requireArgument()),
	/**
	 * LEFT can be crafted into RIGHT by means of the recipe(s) ARG. LEFT is active
	 * role. inverse of {@link #CRAFTED_FROM}
	 */
	CRAFTS_INTO(CRAFTED_FROM, prop().transforms().requireArgument().requireAction(), true),
	/**
	 * LEFT eats RIGHT. LEFT is active role. inverse of {@link #EATEN_BY}
	 */
	EATS(prop().consumes().requireAction()),
	/**
	 * LEFT is eaten by RIGHT. LEFT is passive role. inverse of {@link #EATS}
	 */
	EATEN_BY(EATS, prop().consumes(), false),

	/**
	 * LEFT is fuel for RIGHT. LEFT is passive role. inverse of
	 * {@link #CONSUMES_FUEL}
	 */
	FUEL_FOR(prop().consumes()),
	/**
	 * LEFT consumes RIGHT as fuel. LEFT is active role. inverse of
	 * {@link #FUEL_FOR}
	 */
	CONSUMES_FUEL(FUEL_FOR, prop().consumes(), true),
	/**
	 * LEFT is found at the location of RIGHT. LEFT is active role. inverse of
	 * {@link #LOCATION_OF}
	 */
	FOUND_AT(prop().at()),
	/**
	 * LEFT is the location of RIGHT. LEFT is passive role. inverse of
	 * {@link #FOUND_AT}
	 */
	LOCATION_OF(FOUND_AT, prop().at(), false),
	/**
	 * LEFT is made out of RIGHT (e.g. a House is made of Wood). LEFT is active
	 * role. inverse of {@link #CONSTITUTES}
	 */
	MADE_OF(prop().madeOf()),
	/**
	 * LEFT constitutes the substance, form, or parts of RIGHT (e.g. Wood
	 * constitutes the material of a House). LEFT is passive role. inverse of
	 * {@link #MADE_OF}
	 */
	CONSTITUTES(MADE_OF, prop().madeOf(), false),
	/**
	 * LEFT is equivalent to RIGHT in its value, worth, price, etc, i.e. LEFT can be
	 * traded for RIGHT. Bidirectional.
	 */
	TRADE_EQUIVALENT(prop().tradeWorth()),

	/**
	 * LEFT can be traded for ARG amount of RIGHT. LEFT is active. inverse of
	 * {@link #PRICE_OF}
	 */
	COSTS(prop().tradeWorth().requireArgument().requireAction()),
	/**
	 * ARG amount of LEFT can be traded for RIGHT. LEFT is passive role. inverse of
	 * {@link #COSTS}
	 */
	PRICE_OF(COSTS, prop().tradeWorth().requireArgument(), false),
	/**
	 * LEFT dominates RIGHT, e.g. LEFT is a ruler who rules over or controls RIGHT.
	 * LEFT is active role. inverse of {@link #SUBMITS_TO}
	 */
	DOMINATES(prop().dominates()),
	/**
	 * LEFT submits to and is dominated by RIGHT, e.g. LEFT is a subject to the
	 * rulership of RIGHT. LEFT is passvie role. inverse of {@link #DOMINATES}
	 */
	SUBMITS_TO(DOMINATES, prop().dominates(), false);

	private RelationType inverse;
	private Properties properties;
	private boolean actor = true;

	private RelationType(Properties properties) {
		this.properties = properties;
		this.actor = true;
	}

	private RelationType(RelationType inverseOf, Properties props) {
		this(props);
		this.inverse = inverseOf;
		inverseOf.inverse = this;
	}

	/**
	 * Converts the inverse relationship into the "object" in relation to this if
	 * agent is true; otherwise, convert the inverse to the "agent"
	 * 
	 * @param inverseOf
	 * @param props
	 * @param actor
	 */
	private RelationType(RelationType inverseOf, Properties props, boolean agent) {
		this(props);
		this.actor = agent;
		this.inverse = inverseOf;
		inverseOf.inverse = this;
		inverseOf.actor = !agent;
	}

	/**
	 * If true, then this party is the agent or initial stage of the relational
	 * action of this relation. Result is undefined if bidirectional.
	 * 
	 * @return
	 */
	public boolean isAgent() {
		return actor;
	}

	/**
	 * If true, then this is the object or result stage of the relational action of
	 * this relation. Result is undefined if bidirectional.
	 * 
	 * @return
	 */
	public boolean isObject() {
		return !actor;
	}

	@Override
	public String getUniqueName() {
		return "basic_rtype_" + this.name().toLowerCase();
	}

	@Override
	public RelationType inverse() {
		return inverse == null ? this : inverse;
	}

	@Override
	public boolean bidirectional() {
		return inverse == null;
	}

	/**
	 * Whether this relation is one where the agent creates the object
	 * 
	 * @return
	 */
	public boolean creates() {
		return properties.creates;
	}

	/**
	 * Whether this relation is one where the agent transfers something to the
	 * object
	 * 
	 * @return
	 */
	public boolean transfers() {
		return properties.transfers;
	}

	/**
	 * Whether this relation is one where the agent transforms into the object or is
	 * crafted into the object.
	 * 
	 * @return
	 */
	public boolean transforms() {
		return properties.transforms;
	}

	/**
	 * Whether this relation requires an argument, e.g. a transfer relation might
	 * have an argument of what is transferred.
	 * 
	 * @return
	 */
	public boolean requiresArgument() {
		return properties.requireArgument;
	}

	/**
	 * Whether this relation requires an action to be enacted. E.g. a giving
	 * relation requires a giving action to take place
	 * 
	 * @return
	 */
	public boolean requiresAction() {
		return properties.requireAction;
	}

	/**
	 * Whether the agent of this relation consumes or destroys the object.
	 * 
	 * @return
	 */
	public boolean consumes() {
		return properties.consumes;
	}

	/**
	 * Whether this constitutes a relation where the agent is at the location of the
	 * object
	 * 
	 * @return
	 */
	public boolean atLocation() {
		return properties.at;
	}

	/**
	 * Whether this constitutes a relation where the agent is made of the object.
	 * E.g. a house is made of wood
	 * 
	 * @return
	 */
	public boolean madeOf() {
		return properties.madeOf;
	}

	/**
	 * Whether this constitutes a relation where some quantity of the agent can be
	 * traded for some quantity of the object.
	 * 
	 * @return
	 */
	public boolean tradeWorth() {
		return properties.tradeWorth;
	}

	/**
	 * whether this constitutes a relation where the agent socially or
	 * interpersonally dominates the object, e.g. a king dominates their subjects
	 * 
	 * @return
	 */
	public boolean dominates() {
		return properties.dominates;
	}

	private static Properties prop() {
		return new Properties();
	}

	private static class Properties {
		private boolean creates;
		private boolean transfers;
		private boolean transforms;
		private boolean requireArgument;
		private boolean consumes;
		private boolean requireAction;
		private boolean madeOf;
		private boolean at;
		private boolean tradeWorth;
		private boolean dominates;

		private Properties() {
		}

		Properties creates() {
			this.creates = true;
			return this;
		}

		Properties transfers() {
			this.transfers = true;
			return this;
		}

		Properties transforms() {
			this.transforms = true;
			return this;
		}

		Properties requireArgument() {
			this.requireArgument = true;
			return this;
		}

		Properties requireAction() {
			this.requireAction = true;
			return this;
		}

		Properties consumes() {
			this.consumes = true;
			return this;
		}

		Properties madeOf() {
			this.madeOf = true;
			return this;
		}

		Properties at() {
			this.at = true;
			return this;
		}

		Properties tradeWorth() {
			this.tradeWorth = true;
			return this;
		}

		Properties dominates() {
			this.dominates = true;
			return this;
		}

	}

}
