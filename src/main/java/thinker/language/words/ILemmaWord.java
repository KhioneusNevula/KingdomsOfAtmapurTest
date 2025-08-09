package thinker.language.words;

import thinker.concepts.IConcept;

/**
 * Represents the fundamental form of a word encoding a concept. Can be stored
 * in a Concept graph, therefore
 * 
 * @author borah
 *
 */
public interface ILemmaWord extends IWordForm, IConcept {

	/** If multiple words that are homonyms exist, then they can be indexed. */
	public int getIndex();
}
