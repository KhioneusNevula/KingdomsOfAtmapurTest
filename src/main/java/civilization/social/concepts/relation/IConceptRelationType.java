package civilization.social.concepts.relation;

import civilization.social.concepts.IConcept;
import utilities.IInvertibleRelationType;

/**
 * Class that represents a kind of relationship between two concepts or entities
 * 
 * @author borah
 * @param <T> the inverse relation type
 *
 */
public interface IConceptRelationType<T extends IConceptRelationType<?>> extends IConcept, IInvertibleRelationType<T> {

}
