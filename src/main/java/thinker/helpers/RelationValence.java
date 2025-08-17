package thinker.helpers;

import java.util.Collections;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

import com.google.common.collect.ImmutableSet;

import _utilities.couplets.Triplet;
import thinker.concepts.IConcept;
import thinker.concepts.relations.IConceptRelationType;
import thinker.knowledge.IKnowledgeRepresentation;

/**
 * Relation valences for edges
 * 
 * @author borah
 *
 */
public enum RelationValence {
	IS((edge, base) -> base.is(edge.getFirst(), edge.getSecond(), edge.getThird()),
			(edge, base) -> base.addConfidentRelation(edge.getFirst(), edge.getSecond(), edge.getThird()),
			(edge, base) -> base.removeRelation(edge.getFirst(), edge.getSecond(), edge.getThird()), (edge, base) -> {
				base.addConfidentRelation(edge.getFirst(), edge.getSecond(), edge.getThird());
				base.setOpposite(edge.getFirst(), edge.getSecond(), edge.getThird());
			}),
	OPPOSITE((edge, base) -> base.isOpposite(edge.getFirst(), edge.getSecond(), edge.getThird()), IS.oppose,
			(edge, base) -> base.removeRelation(edge.getFirst(), edge.getSecond(), edge.getThird()), IS.set),
	NOT((edge, base) -> base.isNot(edge.getFirst(), edge.getSecond(), edge.getThird()),
			(edge, base) -> base.removeRelation(edge.getFirst(), edge.getSecond(), edge.getThird()),
			(edge, base) -> base.addConfidentRelation(edge.getFirst(), edge.getSecond(), edge.getThird()), IS.set),
	IS_AND_OPPOSITE(IS, OPPOSITE), OPPOSITE_AND_NOT(OPPOSITE, NOT), IS_AND_NOT(IS, NOT), ALL(IS, OPPOSITE, NOT);

	private Set<RelationValence> valences = Collections.singleton(this);
	BiPredicate<Triplet<IConcept, IConceptRelationType, IConcept>, IKnowledgeRepresentation> pre = null;
	BiConsumer<Triplet<IConcept, IConceptRelationType, IConcept>, IKnowledgeRepresentation> set = null;
	BiConsumer<Triplet<IConcept, IConceptRelationType, IConcept>, IKnowledgeRepresentation> remove = null;
	BiConsumer<Triplet<IConcept, IConceptRelationType, IConcept>, IKnowledgeRepresentation> oppose = null;

	private RelationValence(RelationValence... others) {
		valences = ImmutableSet.copyOf(others);
	}

	private RelationValence(
			BiPredicate<Triplet<IConcept, IConceptRelationType, IConcept>, IKnowledgeRepresentation> pre,
			BiConsumer<Triplet<IConcept, IConceptRelationType, IConcept>, IKnowledgeRepresentation> set,
			BiConsumer<Triplet<IConcept, IConceptRelationType, IConcept>, IKnowledgeRepresentation> remove,
			BiConsumer<Triplet<IConcept, IConceptRelationType, IConcept>, IKnowledgeRepresentation> oppose) {
		this.pre = pre;
		this.set = set;
		this.remove = remove;
		this.oppose = oppose;
	}

	/**
	 * Check a relation's valence
	 * 
	 * @param edge
	 * @param base
	 * @return
	 */
	public boolean checkRelation(Triplet<IConcept, IConceptRelationType, IConcept> edge,
			IKnowledgeRepresentation base) {
		if (pre == null) {
			return valences.stream().allMatch((a) -> a.pre.test(edge, base));
		}
		return pre.test(edge, base);
	}

	/**
	 * Change relation's valence and create it if needed
	 * 
	 * @param edge
	 * @param base
	 */
	public void establishRelation(Triplet<IConcept, IConceptRelationType, IConcept> edge,
			IKnowledgeRepresentation base) {
		if (set == null) {
			throw new UnsupportedOperationException(
					"Cannot set a relation when there are two or more conflicting valence types");
		}
		set.accept(edge, base);
	}

	/**
	 * Remove relation; if this is "NOT", then it creates the relation instead
	 * 
	 * @param edge
	 * @param base
	 */
	public void removeRelation(Triplet<IConcept, IConceptRelationType, IConcept> edge, IKnowledgeRepresentation base) {
		if (remove == null) {
			throw new UnsupportedOperationException(
					"Cannot remove a relation when there are two or more conflicting valence types");
		}
		remove.accept(edge, base);
	}

	/**
	 * For {@link #IS}, this method turns a relationship into an OPPOSITE
	 * relationship. For {@link #OPPOSITE} and {@link #NOT}, it turns the
	 * reelationship into an IS relationship
	 * 
	 * @param edge
	 * @param base
	 */
	public void invertRelation(Triplet<IConcept, IConceptRelationType, IConcept> edge, IKnowledgeRepresentation base) {
		if (oppose == null) {
			throw new UnsupportedOperationException(
					"Cannot invert a relation when there are two or more conflicting valence types");
		}
		oppose.accept(edge, base);
	}

	/**
	 * Return true if there are multiple valence types here
	 * 
	 * @return
	 */
	public boolean isMulti() {
		return valences.size() > 1;
	}

	/**
	 * Return what 'smaller' valences compose this one
	 * 
	 * @return
	 */
	public Set<RelationValence> getValences() {
		return valences;
	}
}