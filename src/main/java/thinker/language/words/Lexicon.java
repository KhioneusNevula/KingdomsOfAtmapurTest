package thinker.language.words;

import java.util.Collection;
import java.util.Set;

import _utilities.collections.FilteredCollectionView;
import _utilities.collections.ImmutableMappedCollection;
import _utilities.collections.MappedCollection;
import _utilities.graph.IInvertibleRelationType;
import _utilities.graph.RelationGraph;
import _utilities.property.IProperty;
import thinker.concepts.IConcept;
import thinker.language.rules.ILFeature;

public class Lexicon implements ILexicon {

	/** whether a feature is negative */
	private static final IProperty<Boolean> NEGATIVE = IProperty.make("negative", boolean.class, false);

	private RelationGraph<IConcept, LexicalRelation> graph = new RelationGraph<>();

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj instanceof Lexicon lex) {
			return this.graph.equals(lex.graph);
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return graph.hashCode();
	}

	@Override
	public void forgetLexicalItem(ILemmaWord item) {
		graph.remove(item);
	}

	@Override
	public void deleteSemanticAssociation(ILemmaWord word, IConcept concept) {
		graph.removeEdge(word, LexicalRelation.MEANS, concept);
	}

	@Override
	public void deleteFeatureFrom(ILemmaWord word, ILFeature feat) {
		graph.removeEdge(word, LexicalRelation.HAS_FEATURE, feat);
	}

	@Override
	public void associate(ILemmaWord word, IConcept concept) {
		graph.add(word);
		graph.add(concept);
		graph.addEdge(word, LexicalRelation.MEANS, concept);
	}

	@Override
	public void addFeature(ILemmaWord word, ILFeature feature, boolean negative) {
		graph.add(word);
		graph.add(feature);
		graph.addEdge(word, LexicalRelation.HAS_FEATURE, feature);
		graph.setProperty(word, LexicalRelation.HAS_FEATURE, feature, NEGATIVE, negative);
	}

	@Override
	public Collection<ILemmaWord> getAllWords() {
		return new MappedCollection<ILemmaWord, IConcept>(
				new FilteredCollectionView<>(graph, (a) -> a instanceof ILemmaWord), (a) -> a, (a) -> (ILemmaWord) a,
				ILemmaWord.class);
	}

	@Override
	public Collection<ILFeature> getAllFeatures() {
		return new MappedCollection<ILFeature, IConcept>(
				new FilteredCollectionView<>(graph, (a) -> a instanceof ILFeature), (a) -> a, (a) -> (ILFeature) a,
				ILFeature.class);
	}

	@Override
	public Collection<IConcept> getAllMeanings() {
		return new FilteredCollectionView<>(graph, (a) -> !(a instanceof ILemmaWord || a instanceof ILFeature));

	}

	@Override
	public boolean hasWord(ILemmaWord word) {
		return graph.contains(word);
	}

	@Override
	public boolean hasConcept(IConcept concept) {
		return graph.contains(concept);
	}

	@Override
	public Collection<IConcept> getConceptsFor(ILemmaWord word) {
		return graph.getNeighbors(word, LexicalRelation.MEANS);
	}

	@Override
	public Collection<ILemmaWord> getWordsFor(IConcept concept) {
		return new ImmutableMappedCollection<>(graph.getNeighbors(concept, LexicalRelation.MEANING_OF), (a) -> a,
				(a) -> (ILemmaWord) a, ILemmaWord.class);
	}

	@Override
	public Collection<ILFeature> getFeaturesFor(ILemmaWord concept) {
		return new ImmutableMappedCollection<>(graph.getNeighbors(concept, LexicalRelation.HAS_FEATURE), (a) -> a,
				(a) -> (ILFeature) a, ILFeature.class);
	}

	@Override
	public Collection<ILemmaWord> getWordsWithFeature(ILFeature feat) {
		return new ImmutableMappedCollection<>(graph.getNeighbors(feat, LexicalRelation.IS_FEATURE_OF), (a) -> a,
				(a) -> (ILemmaWord) a, ILemmaWord.class);
	}

	@Override
	public boolean hasFeature(ILemmaWord word, ILFeature feature) {
		return graph.containsEdge(word, feature);
	}

	@Override
	public boolean isNegativeFeature(ILemmaWord word, ILFeature feature) {
		return graph.containsEdge(word, feature)
				&& graph.getProperty(word, LexicalRelation.HAS_FEATURE, feature, NEGATIVE);
	}

	@Override
	public void forgetConcept(IConcept meaning) {
		graph.remove(meaning);
	}

	@Override
	public void forgetFeature(ILFeature feat) {
		graph.remove(feat);
	}

	@Override
	public boolean recognizesFeature(ILFeature concept) {
		return graph.contains(concept);
	}

	private static enum LexicalRelation implements IInvertibleRelationType {
		HAS_FEATURE(null, ILFeature.class), IS_FEATURE_OF(HAS_FEATURE, ILemmaWord.class), MEANS(null, IConcept.class),
		MEANING_OF(MEANS, ILFeature.class, ILemmaWord.class);

		public LexicalRelation inverse;
		private Set<Class<?>> endClass;

		private LexicalRelation(LexicalRelation other, Class<?>... otherClass) {
			if (other != null) {
				this.inverse = other;
				other.inverse = this;
			} else {
				this.inverse = this;
			}
			this.endClass = Set.of(otherClass);
		}

		@Override
		public String checkEndType(Object node) {
			for (Class<?> clazz : this.endClass) {
				if (clazz.isAssignableFrom(node.getClass())) {
					return null;
				}
			}
			return node + " is not an instanceof any of these classes: " + endClass;
		}

		@Override
		public IInvertibleRelationType invert() {
			return inverse;
		}

		@Override
		public boolean bidirectional() {
			return this.inverse == this;
		}
	}
}
