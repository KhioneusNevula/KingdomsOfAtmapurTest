package thinker.language.words;

import java.util.List;

import thinker.language.phonetics.IPhoneme;

/** A single linguistic lexical unit */
public interface IWordForm {

	/** The stem of the word form as a string of phonemes */
	public List<IPhoneme> getStem();

}
