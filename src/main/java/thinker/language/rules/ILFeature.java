package thinker.language.rules;

import thinker.concepts.IConcept;

/**
 * A linguistic item representing a property of a lexical item. Although
 * features SHOULD usually just be what we consider to be linguistic features,
 * this class also lumps Parts of Speech in with that idea.
 * 
 * @author borah
 *
 */
public interface ILFeature extends IConcept {

	/** If this represents a part of speech */
	public boolean isPartOfSpeech();
}
