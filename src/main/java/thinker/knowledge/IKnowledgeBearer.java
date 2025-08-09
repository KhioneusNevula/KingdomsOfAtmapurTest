package thinker.knowledge;

import com.google.common.base.Functions;

import _utilities.graph.IRelationGraph;
import _utilities.graph.ImmutableGraphView;
import thinker.concepts.IConcept;
import thinker.concepts.relations.IConceptRelationType;
import thinker.knowledge.base.IKnowledgeBase;
import thinker.knowledge.base.individual.IIndividualKnowledgeBase;
import thinker.knowledge.node.ConceptNode;
import thinker.knowledge.node.IConceptNode;

/**
 * Something which can have some kind of knowledge representation of the world
 * 
 * @author borah
 *
 */
public interface IKnowledgeBearer {

	/**
	 * Return this bearer's storage of knowledge
	 * 
	 * @return
	 */
	public IKnowledgeBase getKnowledge();

	/**
	 * Returns a view of the internal knowledge graph. If this is an
	 * {@link IIndividualKnowledgeBase}, return the conglomerated graph view
	 * ({@link IIndividualKnowledgeBase#getConglomeratedConceptGraphView()}) .
	 */
	public default IRelationGraph<IConcept, IConceptRelationType> getKnowledgeGraph() {
		if (this.getKnowledge() instanceof IIndividualKnowledgeBase ikb) {
			return ikb.getConglomeratedConceptGraphView();
		}
		return ImmutableGraphView
				.of(((IRelationGraph<IConceptNode, IConceptRelationType>) getKnowledge().getUnmappedConceptGraphView())
						.mappedView((a) -> new ConceptNode(a), IConceptNode::getConcept, Functions.identity(),
								Functions.identity()));
	}

}
