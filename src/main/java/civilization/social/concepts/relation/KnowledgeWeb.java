package civilization.social.concepts.relation;

import civilization.social.concepts.IConcept;
import utilities.RelationalGraph;

/**
 * A graph storage of knowledge in a mind(?), culture, etc
 * 
 * @author borah
 *
 */
public class KnowledgeWeb extends RelationalGraph<IConcept, RelationType, IConcept> {

	public KnowledgeWeb() {
		super(true);
	}

}
