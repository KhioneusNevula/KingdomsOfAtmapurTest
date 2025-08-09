package thinker.concepts.application;

import things.interfaces.IUnique;
import things.interfaces.UniqueType;
import thinker.concepts.IConcept;
import thinker.concepts.profile.IProfile;

/**
 * A ConceptApplier that only ever applies if the thing matches a profile that
 * checks for it
 * 
 * @author borah
 *
 */
public enum ProfileDirectApplier implements IConceptApplier {
	INSTANCE;

	@Override
	public UniqueType forType() {
		return UniqueType.ANY;
	}

	@Override
	public boolean applies(Object forThing, IConcept c) {
		if (c instanceof IProfile profile) {
			if (forThing instanceof IUnique iu) {
				return IProfile.match(profile, iu);
			}
			throw new UnsupportedOperationException("Should only be checked on Unique things!");
		} else {
			throw new UnsupportedOperationException("No profile associated with this applicator...");
		}
	}

}
