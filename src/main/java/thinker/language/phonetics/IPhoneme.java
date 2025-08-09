package thinker.language.phonetics;

/**
 * A single unit of sound in a linguistic utterance
 * 
 * @author borah
 *
 */
public interface IPhoneme {

	/** Beginning of a word */
	public static final IPhoneme BEGINNING = new IPhoneme() {

		@Override
		public boolean isVowel() {
			return false;
		}

		@Override
		public String getRepresentation() {
			return "";
		}

		@Override
		public <T extends Enum<?>> T getEnumProperty(Class<? extends T> property) {
			return null;
		}

	};

	/** End of a word */
	public static final IPhoneme END = new IPhoneme() {

		@Override
		public boolean isVowel() {
			return false;
		}

		@Override
		public String getRepresentation() {
			return "";
		}

		@Override
		public <T extends Enum<?>> T getEnumProperty(Class<? extends T> property) {
			return null;
		}
	};

	/** Returns a string representation of this phoneme */
	public String getRepresentation();

	/** If this is a representation of the beginning of a word */
	default boolean isBeginning() {
		return this == BEGINNING;
	}

	/** If this is a representation of the end of a word */
	default boolean isEnd() {
		return this == END;
	}

	/** If this phoneme is vocalic */
	public boolean isVowel();

	/** If this phoneme is a consonant */
	default boolean isConsonant() {
		return !isVowel();
	}

	/** gets a property of this phoneme */
	public <T extends Enum<?>> T getEnumProperty(Class<? extends T> property);

	/** Phoneme voicing */
	public static enum Voicing {
		VOICED, VOICELESS
	}

	/** Phoneme nasality */
	public static enum Nasality {
		NASAL, NONNASAL
	}

	/** phoneme roundedness property */
	public static enum Roundedness {
		ROUNDED, UNROUNDED
	}

	/** Vowel height property */
	public static enum VHeight {
		HIGH, HIGH_MID, MID, MID_LOW, LOW
	}

	/** Vowel frontness property */
	public static enum VFrontness {
		FRONT, FRONT_MID, MID, MID_BACK, BACK
	}

	/** Consonant place of articulation */
	public static enum CPOA {
		Bilabial, Labiodental, DENTAL, INTERDENTAL, ALVEOLAR, PALATOALVEOLAR, RETROFLEX, PALATAL, VELAR, UVULAR,
		PHARYNGEAL
	}

	/** Consonant aspiration (as a catch-all for phonation) */
	public static enum CAspiration {
		ASPIRATED, UNASPIRATED
	}

	/** Consonant centrality */
	public static enum CCentrality {
		CENTRAL, LATERAL
	}

	/** Consonant degree of constriction */
	public static enum CDOC {
		STOP, FRICATIVE, APPROXIMANT
	}

}
