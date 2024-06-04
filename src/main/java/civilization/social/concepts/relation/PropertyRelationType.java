package civilization.social.concepts.relation;

import civilization.social.concepts.relation.PropertyRelationType.IsPropertyOfRelationType;

/**
 * A relation type that signifies a specific (observable or socially assigned)
 * property of a concept. In a property relation, LEFT is the bearer of a
 * property and the active role, while RIGHTT is the property itself and the
 * object role. TODO implement
 * 
 * @author borah
 *
 */
public class PropertyRelationType implements IConceptRelationType<IsPropertyOfRelationType> {

	private PropertyRelationType() {
	}

	@Override
	public String getUniqueName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IsPropertyOfRelationType inverse() {
		return null;
	}

	@Override
	public boolean leftIsAgent() {
		return true;
	}

	@Override
	public boolean leftIsObject() {
		return false;
	}

	@Override
	public boolean bidirectional() {
		return false;
	}

	@Override
	public boolean creates() {
		return false;
	}

	@Override
	public boolean transfers() {
		return false;
	}

	@Override
	public boolean transforms() {
		return false;
	}

	@Override
	public boolean requiresArgument() {
		return false;
	}

	@Override
	public boolean requiresAction() {
		return false;
	}

	@Override
	public boolean consumes() {
		return false;
	}

	@Override
	public boolean atLocation() {
		return false;
	}

	@Override
	public boolean madeOf() {
		return false;
	}

	@Override
	public boolean tradeWorth() {
		return false;
	}

	@Override
	public boolean dominates() {
		return false;
	}

	@Override
	public boolean isProperty() {
		return true;
	}

	@Override
	public boolean linguistic() {
		return false;
	}

	@Override
	public boolean damages() {
		return false;
	}

	@Override
	public boolean social() {
		return false;
	}

	public class IsPropertyOfRelationType implements IConceptRelationType<PropertyRelationType> {

		@Override
		public String getUniqueName() {
			return null;
		}

		@Override
		public PropertyRelationType inverse() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean bidirectional() {
			return false;
		}

		@Override
		public boolean leftIsAgent() {
			return false;
		}

		@Override
		public boolean leftIsObject() {
			return true;
		}

		@Override
		public boolean creates() {
			return false;
		}

		@Override
		public boolean transfers() {
			return false;
		}

		@Override
		public boolean transforms() {
			return false;
		}

		@Override
		public boolean requiresArgument() {
			return false;
		}

		@Override
		public boolean requiresAction() {
			return false;
		}

		@Override
		public boolean consumes() {
			return false;
		}

		@Override
		public boolean atLocation() {
			return false;
		}

		@Override
		public boolean madeOf() {
			return false;
		}

		@Override
		public boolean tradeWorth() {
			return false;
		}

		@Override
		public boolean dominates() {
			return false;
		}

		@Override
		public boolean isProperty() {
			return true;
		}

		@Override
		public boolean linguistic() {
			return false;
		}

		@Override
		public boolean damages() {
			return false;
		}

		@Override
		public boolean social() {
			return false;
		}

	}

}
