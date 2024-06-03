package civilization.social.concepts.collection;

import java.util.Collection;

import civilization.social.concepts.IConcept;

/**
 * A small, immutable collection of concepts
 * 
 * @author borah
 *
 */
public abstract class ConceptCollection<T extends IConcept> implements IConcept {

	protected Collection<T> concepts;

	protected void setConcepts(Collection<T> concepts) {
		this.concepts = concepts;
	}

	@Override
	public String getUniqueName() {
		return "set_" + concepts;
	}

	public Collection<T> getConcepts() {
		return concepts;
	}

}
