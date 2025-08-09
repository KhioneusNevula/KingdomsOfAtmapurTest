package thinker.language;

import _utilities.graph.IRelationGraph;
import thinker.concepts.IConcept;
import thinker.concepts.relations.IConceptRelationType;
import thinker.knowledge.IKnowledgeMedium;
import thinker.language.phonetics.ILanguagePhonology;
import thinker.language.rules.ILanguageSyntax;
import thinker.language.words.ILexicon;

/**
 * The container of all of a language's rules
 * 
 * @author borah
 *
 */
public interface ILanguageTranslator {

	/** Return the lexicon of this language */
	public ILexicon getLexicon();

	/** Return the syntax of this language */
	public ILanguageSyntax getSyntax();

	/** Return the phonology of this language */
	public ILanguagePhonology getPhonology();

	/**
	 * Translates a graph of knowledge into an utterance of language. The Graph must
	 * have a format containing an event of some kind as the "verb," which is the
	 * center and may be related to agents, patients, locations, instruments, etc
	 * 
	 * @param speaker  the one who is speaking
	 * @param audience the one being spoken to (or null if no audience)
	 */
	public IKnowledgeMedium translateKnowledge(IRelationGraph<IConcept, IConceptRelationType> knowledge,
			IConcept speaker, IConcept audience);

}
