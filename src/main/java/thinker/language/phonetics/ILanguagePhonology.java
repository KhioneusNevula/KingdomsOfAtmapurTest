package thinker.language.phonetics;

import java.util.Collection;

/** Rules of a language's phonology */
public interface ILanguagePhonology {
	/** Gets all phonemes in the language */
	public Collection<IPhoneme> getAllPhonemes();

	/**
	 * How probable this phoneme bigram is in the language (either may be
	 * {@link IPhoneme#BEGINNING} or {@link IPhoneme#END}). A probability of 0
	 * indicates impossibility. Either phoneme may be "incomplete," i.e.
	 * representing only some properties
	 */
	public float bigramProbability(IPhoneme one, IPhoneme two);

}
