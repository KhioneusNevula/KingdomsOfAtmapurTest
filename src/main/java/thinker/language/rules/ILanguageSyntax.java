package thinker.language.rules;

import java.util.Collection;

/**
 * Set of syntactic rules in a language. CFG style?
 * <ul>
 * <li>
 * </ul>
 */
public interface ILanguageSyntax {

	/** Gets the highest level phrase */
	public IPhrase getSentencePhrase();

	/** A syntactic constituent */
	public static interface IConstituent {
		/** A string to identify this constituent */
		public String identifier();
	}

	/** A final word-producer */
	public static interface ITerminal {

		/** Returns the part of speech of this element */
		public ILFeature getPartOfSpeech();
	}

	public static interface IHead extends IConstituent {

		/** Features this head expects */
		public Collection<ILFeature> expectedFeatures();
	}

	public static interface IXBar extends IConstituent {
		public IConstituent getArgument();

		public IConstituent getHead();

		/** If head is on left */
		public boolean headOnLeft();
	}

	public static interface IPhrase extends IConstituent {
		public IConstituent getSpecifier();

		public IXBar getXBar();

		/** If the specifier is on the left */
		public boolean specifierOnLeft();

		/** To indicate the type of this Phrase */
		public IHead getHead();

	}

}
