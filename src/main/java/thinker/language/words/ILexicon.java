package thinker.language.words;

import java.util.Arrays;
import java.util.Collection;

import thinker.concepts.IConcept;
import thinker.language.rules.ILFeature;
import thinker.language.words.ILexicon.DeicticConcept.Deixis;

/**
 * A representation of all the words in a language and their properties. Some
 * principles of a lexicon are:
 * <ul>
 * <li>Since they are based on a basic
 * </ul>
 */
public interface ILexicon {

	public static final class DeicticConcept implements IConcept {

		public static enum Deixis {
			SPEAKER, AUDIENCE,
			/** Deixis to both speaker and audience */
			ALL,
			/** For deixis to things that are not immanent */
			ELSE
		}

		private String label;
		private Deixis deixis;
		private String clabel;

		private DeicticConcept(String label, Deixis person) {
			this.label = label;
			this.deixis = person;
			clabel = Arrays.stream(label.split(" "))
					.map((a) -> a.isBlank() ? a : Character.toUpperCase(a.charAt(0)) + a.substring(1))
					.reduce("Language", (a, b) -> a + b);
		}

		/**
		 * Gets the general target of a deixis, i.e. speaker, audience, both, or other
		 */
		public Deixis getDeixisTarget() {
			return deixis;
		}

		@Override
		public ConceptType getConceptType() {
			return ConceptType.DEIXIS;
		}

		@Override
		public String getUnderlyingName() {
			return "language_" + label.toLowerCase().replace(" ", "_") + "_concept";
		}

		@Override
		public String toString() {
			return clabel;
		}
	}

	/**
	 * Concept representing the first-deixis speaker of an utterance
	 */
	public static final DeicticConcept SPEAKER = new DeicticConcept("speaker", Deixis.SPEAKER);

	/**
	 * Concept representing the location of the first-deixis speaker of an utterance
	 */
	public static final DeicticConcept SPEAKER_LOCATION = new DeicticConcept("speaker location", Deixis.SPEAKER);

	/**
	 * Concept representing the first-deixis speaker and their group, excluding the
	 * audience, of an utterance
	 */
	public static final DeicticConcept SPEAKER_GROUP = new DeicticConcept("speaker plural exclusive", Deixis.SPEAKER);

	/** Concept representing the second-deixis audience of an utterance */
	public static final DeicticConcept AUDIENCE = new DeicticConcept("audience", Deixis.AUDIENCE);

	/**
	 * Concept representing the location of the second-deixis audience of an
	 * utterance
	 */
	public static final DeicticConcept AUDIENCE_LOCATION = new DeicticConcept("audience location", Deixis.AUDIENCE);

	/**
	 * Concept representing the time of a speaker at time of utterance
	 */
	public static final DeicticConcept SPEAKER_TIME = new DeicticConcept("speaker time", Deixis.SPEAKER);

	/**
	 * Concept representing the time of an audience (if it differs from the
	 * speaker?)
	 */
	public static final DeicticConcept AUDIENCE_TIME = new DeicticConcept("audience time", Deixis.AUDIENCE);

	/**
	 * Concept representing a time after time of an utterance
	 */
	public static final DeicticConcept FUTURE = new DeicticConcept("future", Deixis.ELSE);

	/**
	 * Concept representing a time before time of an utterance
	 */
	public static final DeicticConcept PAST = new DeicticConcept("past", Deixis.ELSE);

	/** Removes association between this word and this concept */
	public void deleteSemanticAssociation(ILemmaWord word, IConcept concept);

	/** Removes this feature from this word */
	public void deleteFeatureFrom(ILemmaWord word, ILFeature feat);

	/** Creates a lexical association between the given word and concept */
	public void associate(ILemmaWord word, IConcept concept);

	/**
	 * Adds a feature to this word, and also mark whether it is a negative feature
	 */
	public void addFeature(ILemmaWord word, ILFeature feature, boolean isNegative);

	/** Return all root words in the language */
	public Collection<ILemmaWord> getAllWords();

	/** Return all possible features in this language */
	public Collection<ILFeature> getAllFeatures();

	/** Return all concepts in the language */
	public Collection<IConcept> getAllMeanings();

	/** IF the language has a specific lemma in it */
	public boolean hasWord(ILemmaWord word);

	/** IF the language has a specific concept in it */
	public boolean hasConcept(IConcept concept);

	/** IF the language has a specific concept in it */
	public boolean recognizesFeature(ILFeature concept);

	/**
	 * Get the concepts represented by a given word. Multiple concepts can only be
	 * understood to have an OR relation to a word, since a word which covers the
	 * intersection of multiple concepts should be represented by a single
	 * intersectional concept instead.
	 */
	public Collection<IConcept> getConceptsFor(ILemmaWord word);

	/** Gets the lemmas for a given concept. */
	public Collection<ILemmaWord> getWordsFor(IConcept concept);

	/** Returns the linguistic features of a given word */
	public Collection<ILFeature> getFeaturesFor(ILemmaWord concept);

	/** Get all words with the given feature */
	public Collection<ILemmaWord> getWordsWithFeature(ILFeature feat);

	/** If this word has this specific feature */
	public boolean hasFeature(ILemmaWord word, ILFeature feature);

	/** Whether this feature is negative for this word */
	public boolean isNegativeFeature(ILemmaWord word, ILFeature feature);

	/**
	 * Removes a lexical item from this lexicon.
	 */
	void forgetLexicalItem(ILemmaWord item);

	/** Removes a concept from this lexicon. */
	void forgetConcept(IConcept meaning);

	/** Removes a feature from this lexicon. */
	void forgetFeature(ILFeature feat);
}
