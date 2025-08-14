package thinker.helpers;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.base.Predicates;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;
import com.google.common.collect.Streams;

import _utilities.StringUtils;
import _utilities.UnimplementedException;
import _utilities.couplets.Pair;
import _utilities.couplets.Triplet;
import thinker.concepts.IConcept;
import thinker.concepts.relations.IConceptRelationType;
import thinker.helpers.RelationsHelper.RelationValence;
import thinker.knowledge.IKnowledgeRepresentation;

/**
 * A multimap that presents relation types radiating out from a focus object as
 * keys and their associated concepts as values. All map operations should be
 * expected to work like normal maps!
 * 
 * @author borah
 *
 */
public class ConceptRelationsMap implements Multimap<IConceptRelationType, IConcept> {

	private IConcept focus;
	private IKnowledgeRepresentation base;
	private Predicate<Triplet<IConcept, IConceptRelationType, IConcept>> checkrel;
	private RelationValence valence;
	private EntrySet entryset = new EntrySet();
	private MapViewMap mapviewmap = new MapViewMap();
	private KeySet keyset = new KeySet();
	private KeyMultiSet keymultiset = new KeyMultiSet();
	private ValueCollection valuecol = new ValueCollection();

	/**
	 * Makes a relmap with {@link RelationValence#IS}
	 * 
	 * @param focus
	 * @param base
	 */
	public ConceptRelationsMap(IConcept focus, IKnowledgeRepresentation base,
			Predicate<Triplet<IConcept, IConceptRelationType, IConcept>> checkrel) {
		this(focus, base, RelationValence.IS, checkrel);
	}

	/**
	 * Makes a relmap with null for the predicate argument
	 * 
	 * @param focus
	 * @param base
	 */
	public ConceptRelationsMap(IConcept focus, IKnowledgeRepresentation base, RelationValence val) {
		this(focus, base, val, null);
	}

	/**
	 * Makes a relmap with {@link RelationValence#IS} and no predicate
	 * 
	 * @param focus
	 * @param base
	 */
	public ConceptRelationsMap(IConcept focus, IKnowledgeRepresentation base) {
		this(focus, base, RelationValence.IS, null);
	}

	/**
	 * Make a concept relations map
	 * 
	 * @param event
	 * @param knowl
	 * @param valence
	 * @param checkrel can be null
	 */
	public ConceptRelationsMap(IConcept event, IKnowledgeRepresentation knowl, RelationValence valence,
			Predicate<Triplet<IConcept, IConceptRelationType, IConcept>> checkrel) {
		this.focus = event;
		this.base = knowl;
		this.checkrel = checkrel;
		this.valence = valence;
	}

	/**
	 * Stream of all edges
	 * 
	 * @return
	 */
	private Stream<Triplet<IConcept, IConceptRelationType, IConcept>> edgeStream() {
		Stream<Triplet<IConcept, IConceptRelationType, IConcept>> stream = Streams.stream(base.getOutgoingEdges(focus))
				.filter((a) -> valence.checkRelation(a, base));
		if (checkrel != null) {
			stream = stream.filter(checkrel);
		}
		return stream;
	}

	/**
	 * Stream of edges connected by arelation of the given type
	 * 
	 * @param key
	 * @return
	 */
	private Stream<Triplet<IConcept, IConceptRelationType, IConcept>> edgeStreamByType(IConceptRelationType key) {
		Stream<Triplet<IConcept, IConceptRelationType, IConcept>> stream = Streams
				.stream(base.getOutgoingEdges(focus, key)).filter((a) -> valence.checkRelation(a, base));
		if (checkrel != null) {
			stream = stream.filter(checkrel);
		}
		return stream;
	}

	/**
	 * Stream of edges connected to the given node
	 * 
	 * @param key
	 * @return
	 */
	private Stream<Triplet<IConcept, IConceptRelationType, IConcept>> edgeStreamByNeighbor(IConcept value) {

		Stream<Triplet<IConcept, IConceptRelationType, IConcept>> stream = Streams
				.stream(base.getRelationTypesBetween(focus, value))
				.map((a) -> Triplet.of(focus, (IConceptRelationType) a, value))
				.filter((a) -> valence.checkRelation(a, base));
		if (checkrel != null) {
			stream = stream.filter(checkrel);
		}
		return stream;
	}

	/**
	 * stream the 'collections' of each 'key'
	 * 
	 * @return
	 */
	private Stream<Collection<IConcept>> valueCollectionsStream() {
		return multikeyStream().map(this::get);
	}

	/**
	 * Stream of all relations, with relations that have multiple concepts being
	 * repeated
	 * 
	 * @return
	 */
	private Stream<IConceptRelationType> multikeyStream() {
		return edgeStream().map(Triplet::getSecond);
	}

	/**
	 * Stream of all relations, with keys being returned distinctly
	 * 
	 * @return
	 */
	private Stream<IConceptRelationType> keyStreamDistinct() {
		return multikeyStream().distinct();
	}

	/**
	 * Stream of entries
	 * 
	 * @return
	 */
	private Stream<Entry<IConceptRelationType, IConcept>> entryStream() {
		return edgeStream().<Map.Entry<IConceptRelationType, IConcept>>map(
				(a) -> new GraphEntry(a.getSecond(), a.getThird()));
	}

	/**
	 * Stream of map values
	 * 
	 * @return
	 */
	private Stream<IConcept> valueStream() {
		return edgeStream().map(Triplet::getThird);
	}

	@Override
	public int size() {
		return (int) edgeStream().count();
	}

	@Override
	public boolean isEmpty() {
		return edgeStream().findAny().isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		if (key instanceof IConceptRelationType evt) {
			return edgeStreamByType(evt).findAny().isPresent();
		}
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		if (value instanceof IConcept c) {
			return edgeStreamByNeighbor(c).findAny().isPresent();
		}
		return false;
	}

	@Override
	public Map<IConceptRelationType, Collection<IConcept>> asMap() {

		return mapviewmap;
	}

	@Override
	public void clear() {
		for (Triplet<IConcept, IConceptRelationType, IConcept> edge : edgeStream().collect(Collectors.toSet())) {
			base.removeRelation(edge.getFirst(), edge.getSecond(), edge.getThird());
		}
	}

	@Override
	public boolean containsEntry(Object arg0, Object arg1) {
		if (arg0 instanceof IConceptRelationType evt && arg1 instanceof IConcept ct) {
			if (checkrel != null) {
				if (!checkrel.test(Triplet.of(focus, evt, ct))) {
					return false;
				}
			}
			return valence.pre.test(Triplet.of(focus, evt, ct), base);
		}
		return false;
	}

	@Override
	public Collection<Entry<IConceptRelationType, IConcept>> entries() {
		return this.entryset;
	}

	@Override
	public Collection<IConcept> get(IConceptRelationType arg0) {
		return new GetSet(arg0);
	}

	@Override
	public Set<IConceptRelationType> keySet() {
		return keyset;
	}

	@Override
	public Multiset<IConceptRelationType> keys() {
		return keymultiset;
	}

	@Override
	public boolean put(IConceptRelationType arg0, IConcept arg1) {
		if (valence.checkRelation(Triplet.of(focus, arg0, arg1), base)) {
			return false;
		}
		base.learnConcept(arg1);
		valence.establishRelation(Triplet.of(focus, arg0, arg1), base);
		return true;
	}

	@Override
	public boolean putAll(Multimap<? extends IConceptRelationType, ? extends IConcept> arg0) {
		boolean chang = false;
		for (Map.Entry<? extends IConceptRelationType, ? extends IConcept> entrada : arg0.entries()) {
			chang = this.put(entrada.getKey(), entrada.getValue()) || chang;
		}
		return chang;
	}

	@Override
	public boolean putAll(IConceptRelationType arg0, Iterable<? extends IConcept> arg1) {
		boolean chang = false;
		for (IConcept con : arg1) {
			chang = this.put(arg0, con) || chang;
		}
		return chang;
	}

	@Override
	public boolean remove(Object arg0, Object arg1) {
		if (arg0 instanceof IConceptRelationType type && arg1 instanceof IConcept concept) {
			if (valence.checkRelation(Triplet.of(focus, type, concept), base)) {
				valence.removeRelation(Triplet.of(focus, type, concept), base);
				return true;
			}
		}
		return false;
	}

	@Override
	public Collection<IConcept> removeAll(Object arg0) {
		if (arg0 instanceof IConceptRelationType type) {
			Set<IConcept> conc = Sets.newHashSet(edgeStreamByType(type).map(Triplet::getThird).iterator());
			Iterator<IConcept> cit = conc.iterator();
			for (IConcept con : conc) {
				base.removeRelation(focus, type, con);
			}
			return conc;
		}
		return Collections.emptySet();
	}

	@Override
	public Collection<IConcept> replaceValues(IConceptRelationType key, Iterable<? extends IConcept> arg1) {
		Set<IConcept> setta = new HashSet<>(this.get(key));
		ConceptRelationsMap.this.removeAll(key);
		ConceptRelationsMap.this.putAll(key, arg1);
		return setta;
	}

	@Override
	public Collection<IConcept> values() {
		return valuecol;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Map map) {
			return this.size() == map.size() && this.entries().stream().allMatch(map.entrySet()::contains);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return focus.hashCode() + base.hashCode() + valence.hashCode();
	}

	@Override
	public String toString() {
		return StringUtils.formatAsSetFromStream(entryStream());
	}

	private class ValueCollection implements Collection<IConcept> {

		@Override
		public int size() {
			return ConceptRelationsMap.this.size();
		}

		@Override
		public boolean isEmpty() {
			return ConceptRelationsMap.this.isEmpty();
		}

		@Override
		public boolean contains(Object o) {
			return ConceptRelationsMap.this.containsValue(o);
		}

		@Override
		public Iterator<IConcept> iterator() {
			return valueStream().iterator();
		}

		@Override
		public Object[] toArray() {
			return valueStream().toArray();
		}

		@Override
		public <T> T[] toArray(T[] a) {
			return valueStream().toArray((x) -> a);
		}

		@Override
		public boolean add(IConcept e) {
			throw new UnsupportedOperationException("??");
		}

		@Override
		public boolean remove(Object o) {
			Optional<Triplet<IConcept, IConceptRelationType, IConcept>> op = edgeStream()
					.filter((a) -> a.getThird().equals(o)).findAny();
			if (op.isPresent()) {
				return ConceptRelationsMap.this.remove(op.get().getEdgeType(), op.get().getEdgeEnd());
			}
			return false;
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			for (Object o : c) {
				if (!ConceptRelationsMap.this.containsValue(o)) {
					return false;
				}
			}
			return true;
		}

		@Override
		public boolean addAll(Collection<? extends IConcept> c) {
			throw new UnsupportedOperationException("??");
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			boolean chang = false;
			for (Object o : c) {
				chang = remove(o) || chang;
			}
			return chang;
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			throw new UnimplementedException("Whooo cares");
		}

		@Override
		public void clear() {
			ConceptRelationsMap.this.clear();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this)
				return true;
			if (obj instanceof Collection c) {
				return c.size() == this.size() && c.containsAll(this);
			}
			return super.equals(obj);
		}

		@Override
		public String toString() {
			return StringUtils.formatAsSetFromStream(valueStream());
		}

	}

	/**
	 * Internal implementation for the colletion returned by "get"
	 * 
	 * @author borah
	 *
	 */
	private class GetSet implements Set<IConcept> {

		private IConceptRelationType key;
		private ConceptRelationsMap crmap = ConceptRelationsMap.this;

		private GetSet(IConceptRelationType key) {
			this.key = key;
		}

		/**
		 * return a stream of the concepts from this relation type 'key'
		 * 
		 * @return
		 */
		private Stream<IConcept> keyStream() {
			return ConceptRelationsMap.this.edgeStreamByType(key).map((a) -> a.getThird());
		}

		@Override
		public int size() {
			return (int) keyStream().count();
		}

		@Override
		public boolean isEmpty() {
			return keyStream().findAny().isEmpty();
		}

		@Override
		public boolean contains(Object o) {
			return keyStream().anyMatch((e) -> e.equals(o));
		}

		@Override
		public Iterator<IConcept> iterator() {
			return keyStream().iterator();
		}

		@Override
		public Object[] toArray() {
			return keyStream().toArray();
		}

		@Override
		public <T> T[] toArray(T[] a) {
			return keyStream().toArray((x) -> a);
		}

		@Override
		public boolean add(IConcept e) {
			return ConceptRelationsMap.this.put(key, e);
		}

		@Override
		public boolean remove(Object o) {
			return ConceptRelationsMap.this.remove(key, o);
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			for (Object o : c) {
				if (!ConceptRelationsMap.this.containsEntry(key, o)) {
					return false;
				}
			}
			return true;
		}

		@Override
		public boolean addAll(Collection<? extends IConcept> c) {
			boolean chang = false;
			for (IConcept o : c) {
				chang = this.add(o) || chang;
			}
			return chang;
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			throw new UnimplementedException("Let's be honest who needs this...");
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			boolean chang = false;
			for (Object o : c) {
				chang = this.remove(o) || chang;
			}
			return chang;
		}

		@Override
		public void clear() {
			ConceptRelationsMap.this.removeAll(key);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this || obj instanceof GetSet gs && (gs.crmap == this.crmap && gs.key.equals(this.key)))
				return true;
			if (obj instanceof Collection x) {
				return x.size() == this.size() && this.containsAll(x);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return ConceptRelationsMap.this.hashCode() + this.key.hashCode();
		}

		@Override
		public String toString() {
			return StringUtils.formatAsSetFromStream(keyStream());
		}

	}

	private class GraphEntry implements Map.Entry<IConceptRelationType, IConcept> {

		private IConceptRelationType key;
		private IConcept val;

		private GraphEntry(IConceptRelationType key, IConcept val) {
			this.key = key;
			this.val = val;
		}

		@Override
		public IConceptRelationType getKey() {
			return key;
		}

		@Override
		public IConcept getValue() {
			return val;
		}

		@Override
		public IConcept setValue(IConcept value) {
			ConceptRelationsMap.this.get(key).remove(val);
			ConceptRelationsMap.this.get(key).add(value);
			IConcept vala = val;
			val = value;
			return vala;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this)
				return true;
			if (obj instanceof Map.Entry e) {
				return this.key.equals(e.getKey()) && this.val.equals(e.getValue());
			}
			return false;
		}

		@Override
		public int hashCode() {
			return this.key.hashCode();
		}

		@Override
		public String toString() {
			return this.key + "=" + this.val;
		}

	}

	private class KeyMultiSet implements Multiset<IConceptRelationType> {

		private MultisetEntrySet multisetentryset = new MultisetEntrySet();

		@Override
		public int size() {
			return ConceptRelationsMap.this.size();
		}

		@Override
		public boolean isEmpty() {
			return ConceptRelationsMap.this.isEmpty();
		}

		@Override
		public void clear() {
			ConceptRelationsMap.this.clear();
		}

		@Override
		public Object[] toArray() {
			return multikeyStream().toArray();
		}

		@Override
		public <T> T[] toArray(T[] a) {
			return multikeyStream().toArray((t) -> a);
		}

		@Override
		public boolean addAll(Collection<? extends IConceptRelationType> c) {
			throw new UnsupportedOperationException("???");
		}

		@Override
		public boolean add(IConceptRelationType arg0) {
			throw new UnsupportedOperationException("???");
		}

		@Override
		public int add(IConceptRelationType arg0, int arg1) {
			throw new UnsupportedOperationException("???");
		}

		@Override
		public boolean contains(Object arg0) {
			return ConceptRelationsMap.this.containsKey(arg0);
		}

		@Override
		public boolean containsAll(Collection<?> arg0) {
			for (Object o : arg0) {
				if (!ConceptRelationsMap.this.containsKey(o)) {
					return false;
				}

			}
			return false;
		}

		@Override
		public int count(Object arg0) {
			if (arg0 instanceof IConceptRelationType crt) {
				return (int) edgeStreamByType(crt).count();
			}
			return 0;
		}

		@Override
		public Set<IConceptRelationType> elementSet() {
			return ConceptRelationsMap.this.keySet();
		}

		@Override
		public Set<Entry<IConceptRelationType>> entrySet() {
			return multisetentryset;
		}

		@Override
		public Iterator<IConceptRelationType> iterator() {
			return multikeyStream().iterator();
		}

		@Override
		public boolean remove(Object arg0) {
			if (arg0 instanceof IConceptRelationType crt) {
				Optional<Triplet<IConcept, IConceptRelationType, IConcept>> obj = edgeStreamByType(crt).findAny();
				if (obj.isPresent()) {
					return ConceptRelationsMap.this.remove(obj.get().getSecond(), obj.get().getThird());
				}
				return false;
			}
			return false;
		}

		@Override
		public int remove(Object arg0, int arg1) {
			int before = this.count(arg0);
			for (int i = 0; i < arg1; i++) {
				if (!this.remove(arg0)) {
					return before;
				}
			}
			return before;
		}

		@Override
		public boolean removeAll(Collection<?> arg0) {
			boolean chang = false;
			for (Object o : arg0) {
				chang = this.remove(o) || chang;
			}
			return chang;
		}

		@Override
		public boolean retainAll(Collection<?> arg0) {
			throw new UnimplementedException("No one cares about this....");
		}

		@Override
		public int setCount(IConceptRelationType arg0, int arg1) {
			throw new UnimplementedException("Be so fr...");
		}

		@Override
		public boolean setCount(IConceptRelationType arg0, int arg1, int arg2) {

			throw new UnimplementedException("Be so fr...");
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this)
				return true;
			if (obj instanceof Multiset ma) {
				return this.entrySet().equals(ma.entrySet());
			}
			return false;
		}

		@Override
		public int hashCode() {
			return ConceptRelationsMap.this.hashCode();
		}

		@Override
		public String toString() {
			return this.entrySet().toString();
		}

		private class MultisetEntrySet implements Set<Multiset.Entry<IConceptRelationType>> {

			@Override
			public int size() {
				return ConceptRelationsMap.this.keySet().size();
			}

			@Override
			public boolean isEmpty() {
				return ConceptRelationsMap.this.isEmpty();
			}

			@Override
			public boolean contains(Object o) {
				if (o instanceof Multiset.Entry entry) {
					return count(entry.getElement()) == entry.getCount();
				}
				return false;
			}

			private Multiset<IConceptRelationType> makeActualMultiset() {
				Multiset<IConceptRelationType> countMap = HashMultiset.create();
				edgeStream().map((a) -> a.getSecond()).forEach((a) -> countMap.add(a));
				return countMap;
			}

			@Override
			public Iterator<Entry<IConceptRelationType>> iterator() {
				return makeActualMultiset().entrySet().iterator();
			}

			@Override
			public Object[] toArray() {
				return makeActualMultiset().toArray();
			}

			@Override
			public <T> T[] toArray(T[] a) {
				return makeActualMultiset().toArray(a);
			}

			@Override
			public boolean add(Entry<IConceptRelationType> e) {
				throw new UnsupportedOperationException("???");
			}

			@Override
			public boolean remove(Object o) {
				throw new UnsupportedOperationException("???");
			}

			@Override
			public boolean containsAll(Collection<?> c) {
				boolean chang = false;
				for (Object o : c) {
					if (!this.contains(o)) {
						return false;
					}
				}
				return chang;
			}

			@Override
			public boolean addAll(Collection<? extends Entry<IConceptRelationType>> c) {
				throw new UnsupportedOperationException("???");
			}

			@Override
			public boolean retainAll(Collection<?> c) {
				throw new UnsupportedOperationException("???");
			}

			@Override
			public boolean removeAll(Collection<?> c) {
				throw new UnsupportedOperationException("???");
			}

			@Override
			public void clear() {
				ConceptRelationsMap.this.clear();
			}

			@Override
			public boolean equals(Object obj) {
				if (obj == this)
					return true;
				if (obj instanceof Collection s) {
					return s.size() == this.size() && s.containsAll(this);
				}
				return false;
			}

			@Override
			public int hashCode() {
				return KeyMultiSet.this.hashCode();
			}

			@Override
			public String toString() {
				return makeActualMultiset().toString();
			}

		}

	}

	private class KeySet implements Set<IConceptRelationType> {

		@Override
		public int size() {
			return (int) keyStreamDistinct().count();
		}

		@Override
		public boolean isEmpty() {
			return ConceptRelationsMap.this.isEmpty();
		}

		@Override
		public boolean contains(Object o) {
			return ConceptRelationsMap.this.containsKey(o);
		}

		@Override
		public Iterator<IConceptRelationType> iterator() {
			return keyStreamDistinct().iterator();
		}

		@Override
		public Object[] toArray() {
			return keyStreamDistinct().toArray();
		}

		@Override
		public <T> T[] toArray(T[] a) {
			return keyStreamDistinct().toArray((x) -> a);
		}

		@Override
		public boolean add(IConceptRelationType e) {
			throw new UnsupportedOperationException("???");
		}

		@Override
		public boolean remove(Object o) {
			return !ConceptRelationsMap.this.removeAll(o).isEmpty();
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			for (Object o : c) {
				if (!ConceptRelationsMap.this.containsKey(o)) {
					return false;
				}

			}
			return false;
		}

		@Override
		public boolean addAll(Collection<? extends IConceptRelationType> c) {
			throw new UnsupportedOperationException("???");
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			throw new UnimplementedException("Why do you even need this method bruh");
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			boolean chang = false;
			for (Object o : c) {
				chang = !ConceptRelationsMap.this.removeAll(o).isEmpty() || chang;

			}
			return chang;
		}

		@Override
		public void clear() {
			ConceptRelationsMap.this.clear();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this)
				return true;
			if (obj instanceof Collection s) {
				return this.size() == s.size() && s.containsAll(this);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return ConceptRelationsMap.this.hashCode();
		}

		@Override
		public String toString() {
			return StringUtils.formatAsSetFromStream(keyStreamDistinct());
		}

	}

	private class EntrySet implements Set<Map.Entry<IConceptRelationType, IConcept>> {

		@Override
		public int size() {
			return ConceptRelationsMap.this.size();
		}

		@Override
		public boolean isEmpty() {
			return ConceptRelationsMap.this.isEmpty();
		}

		@Override
		public boolean contains(Object o) {
			if (o instanceof Map.Entry men)
				return ConceptRelationsMap.this.containsEntry(men.getKey(), men.getValue());
			return false;
		}

		@Override
		public Iterator<Entry<IConceptRelationType, IConcept>> iterator() {
			return entryStream().iterator();
		}

		@Override
		public Object[] toArray() {
			return entryStream().toArray();
		}

		@Override
		public <T> T[] toArray(T[] a) {
			return entryStream().toArray((x) -> a);
		}

		@Override
		public boolean add(Entry<IConceptRelationType, IConcept> e) {
			return ConceptRelationsMap.this.put(e.getKey(), e.getValue());
		}

		@Override
		public boolean remove(Object o) {
			if (o instanceof Map.Entry e) {
				return ConceptRelationsMap.this.remove(e.getKey(), e.getValue());
			}
			return false;
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			for (Object o : c) {
				if (o instanceof Map.Entry e) {
					if (!ConceptRelationsMap.this.containsEntry(e.getKey(), e.getValue())) {
						return false;
					}
				}
			}
			return false;
		}

		@Override
		public boolean addAll(Collection<? extends Entry<IConceptRelationType, IConcept>> c) {
			boolean chang = false;
			for (Entry<IConceptRelationType, IConcept> e : c) {
				chang = ConceptRelationsMap.this.put(e.getKey(), e.getValue()) || chang;
			}
			return chang;
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			throw new UnimplementedException("Why do you even need this method bruh");
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			boolean chang = false;
			for (Object o : c) {
				if (o instanceof Map.Entry e) {
					chang = ConceptRelationsMap.this.remove(e.getKey(), e.getValue()) || chang;
				}
			}
			return chang;
		}

		@Override
		public void clear() {
			ConceptRelationsMap.this.clear();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this)
				return true;
			if (obj instanceof Collection s) {
				return s.size() == this.size() && this.containsAll(s);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return ConceptRelationsMap.this.hashCode();
		}

		@Override
		public String toString() {
			return StringUtils.formatAsSetFromStream(entryStream());
		}

	}

	private class MapViewMap implements Map<IConceptRelationType, Collection<IConcept>> {

		private MapViewEntrySet entryset = new MapViewEntrySet();
		private MapViewValueCollection valuecol = new MapViewValueCollection();

		@Override
		public int size() {
			return ConceptRelationsMap.this.keySet().size();
		}

		@Override
		public boolean isEmpty() {
			return ConceptRelationsMap.this.keySet().isEmpty();
		}

		@Override
		public boolean containsKey(Object key) {
			return ConceptRelationsMap.this.containsKey(key);
		}

		@Override
		public boolean containsValue(Object value) {
			return valueCollectionsStream().anyMatch((a) -> value.equals(a));
		}

		@Override
		public Collection<IConcept> get(Object key) {
			if (key instanceof IConceptRelationType keya) {
				return ConceptRelationsMap.this.get(keya);
			}
			throw new IllegalArgumentException("(ಠ_ಠ)");
		}

		@Override
		public Collection<IConcept> put(IConceptRelationType key, Collection<IConcept> value) {
			Set<IConcept> setta = new HashSet<>(ConceptRelationsMap.this.get(key));
			ConceptRelationsMap.this.removeAll(key);
			ConceptRelationsMap.this.putAll(key, value);
			return setta;
		}

		@Override
		public Collection<IConcept> remove(Object o) {
			if (o instanceof IConceptRelationType key) {
				Set<IConcept> setta = new HashSet<>(ConceptRelationsMap.this.get(key));
				ConceptRelationsMap.this.removeAll(key);
				return setta;
			}
			throw new IllegalArgumentException("(ಠ_ಠ)");

		}

		@Override
		public void putAll(Map<? extends IConceptRelationType, ? extends Collection<IConcept>> m) {
			for (IConceptRelationType coco : m.keySet()) {
				this.put(coco, m.get(coco));
			}
		}

		@Override
		public void clear() {
			ConceptRelationsMap.this.clear();
		}

		@Override
		public Set<IConceptRelationType> keySet() {
			return ConceptRelationsMap.this.keySet();
		}

		@Override
		public Collection<Collection<IConcept>> values() {
			return valuecol;
		}

		@Override
		public Set<Entry<IConceptRelationType, Collection<IConcept>>> entrySet() {
			return entryset;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this)
				return true;
			if (obj instanceof Map ma) {
				return ma.size() == this.size()
						&& keyStreamDistinct().allMatch((k) -> ma.containsKey(k) && ma.get(k).equals(this.get(k)));
			}
			return false;
		}

		@Override
		public int hashCode() {
			return ConceptRelationsMap.this.hashCode();
		}

		@Override
		public String toString() {
			return StringUtils.formatAsSetFromStream(entryCollectionsStream());
		}

		private class MapViewValueCollection implements Collection<Collection<IConcept>> {

			@Override
			public int size() {

				return MapViewMap.this.size();
			}

			@Override
			public boolean isEmpty() {
				return MapViewMap.this.isEmpty();
			}

			@Override
			public boolean contains(Object o) {
				return Streams.stream(this.iterator()).anyMatch((a) -> o.equals(a));
			}

			@Override
			public Iterator<Collection<IConcept>> iterator() {
				return valueCollectionsStream().iterator();
			}

			@Override
			public Object[] toArray() {
				return valueCollectionsStream().toArray();
			}

			@Override
			public <T> T[] toArray(T[] a) {
				return valueCollectionsStream().toArray((x) -> a);
			}

			@Override
			public boolean add(Collection<IConcept> e) {
				throw new UnsupportedOperationException("???");
			}

			@Override
			public boolean remove(Object o) {
				Optional<IConceptRelationType> op = keyStreamDistinct().map((a) -> Pair.of(a, MapViewMap.this.get(a)))
						.filter((a) -> o.equals(a.getSecond())).map(Pair::getFirst).findAny();
				if (op.isPresent()) {
					return !ConceptRelationsMap.this.removeAll(op.get()).isEmpty();
				}
				return false;
			}

			@Override
			public boolean containsAll(Collection<?> c) {
				for (Object o : c) {
					if (!contains(o)) {
						return false;
					}
				}
				return true;
			}

			@Override
			public boolean addAll(Collection<? extends Collection<IConcept>> c) {
				throw new UnsupportedOperationException("???");
			}

			@Override
			public boolean retainAll(Collection<?> c) {
				throw new UnimplementedException("omfg shush");
			}

			@Override
			public boolean removeAll(Collection<?> c) {
				boolean chang = false;
				for (Object o : c) {
					chang = this.remove(o) || chang;
				}
				return chang;
			}

			@Override
			public void clear() {
				ConceptRelationsMap.this.clear();
			}

			@Override
			public boolean equals(Object obj) {
				if (obj == this)
					return true;
				if (obj instanceof Collection x) {
					return this.size() == x.size() && x.containsAll(this);
				}
				return false;
			}

			@Override
			public int hashCode() {
				return ConceptRelationsMap.this.hashCode();
			}

			@Override
			public String toString() {
				return StringUtils.formatAsSetFromStream(valueCollectionsStream());
			}

		}

		private class MapViewEntry implements Map.Entry<IConceptRelationType, Collection<IConcept>> {

			private IConceptRelationType key;

			private MapViewEntry(IConceptRelationType ke) {
				this.key = ke;
			}

			@Override
			public IConceptRelationType getKey() {
				return key;
			}

			@Override
			public Collection<IConcept> getValue() {
				return MapViewMap.this.get(key);
			}

			@Override
			public Collection<IConcept> setValue(Collection<IConcept> value) {
				return MapViewMap.this.put(key, value);
			}

			@Override
			public boolean equals(Object obj) {
				if (obj == this) {
					return true;
				}
				if (obj instanceof Map.Entry e) {
					return this.key.equals(e.getKey()) && this.getValue().equals(e.getValue());
				}
				return false;
			}

			@Override
			public int hashCode() {
				return this.key.hashCode();
			}

			@Override
			public String toString() {
				return this.key + "=" + this.getValue();
			}

		}

		private Stream<Map.Entry<IConceptRelationType, Collection<IConcept>>> entryCollectionsStream() {
			return keyStreamDistinct().map((a) -> new MapViewEntry(a));
		}

		private class MapViewEntrySet implements Set<Map.Entry<IConceptRelationType, Collection<IConcept>>> {

			@Override
			public int size() {
				return MapViewMap.this.size();
			}

			@Override
			public boolean isEmpty() {
				return MapViewMap.this.isEmpty();
			}

			@Override
			public boolean contains(Object o) {
				if (o instanceof Map.Entry e) {
					Collection<IConcept> col = MapViewMap.this.get(e.getKey());
					if (col != null) {
						return e.getValue().equals(col);
					}
				}
				return false;
			}

			@Override
			public Iterator<Entry<IConceptRelationType, Collection<IConcept>>> iterator() {
				return entryCollectionsStream().iterator();
			}

			@Override
			public Object[] toArray() {
				return entryCollectionsStream().toArray();
			}

			@Override
			public <T> T[] toArray(T[] a) {
				return entryCollectionsStream().toArray((x) -> a);
			}

			@Override
			public boolean add(Entry<IConceptRelationType, Collection<IConcept>> e) {
				return MapViewMap.this.put(e.getKey(), e.getValue()).isEmpty();
			}

			@Override
			public boolean remove(Object o) {
				if (o instanceof Map.Entry e) {
					return MapViewMap.this.remove(e.getKey(), e.getValue());
				}
				return false;
			}

			@Override
			public boolean containsAll(Collection<?> c) {
				for (Object o : c) {
					if (!this.contains(o)) {
						return false;
					}
				}
				return true;
			}

			@Override
			public boolean addAll(Collection<? extends Entry<IConceptRelationType, Collection<IConcept>>> c) {
				boolean chang = false;
				for (Entry<IConceptRelationType, Collection<IConcept>> en : c) {
					chang = this.add(en) || chang;
				}
				return chang;
			}

			@Override
			public boolean retainAll(Collection<?> c) {
				throw new UnimplementedException("idgaf");
			}

			@Override
			public boolean removeAll(Collection<?> c) {
				boolean chang = false;
				for (Object en : c) {
					chang = this.remove(en) || chang;
				}
				return chang;
			}

			@Override
			public void clear() {
				MapViewMap.this.clear();
			}

			@Override
			public boolean equals(Object obj) {
				if (obj == this)
					return true;
				if (obj instanceof Collection s) {
					return this.size() == s.size() && s.containsAll(this);
				}
				return false;
			}

			@Override
			public int hashCode() {
				return ConceptRelationsMap.this.hashCode();
			}

			@Override
			public String toString() {
				return StringUtils.formatAsSetFromStream(entryCollectionsStream());
			}

		}

	}

}
