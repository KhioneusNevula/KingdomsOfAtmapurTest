package thinker.helpers;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import com.google.common.collect.Streams;

import _utilities.StringUtils;
import _utilities.UnimplementedException;
import _utilities.couplets.Pair;
import _utilities.couplets.Triplet;
import thinker.concepts.IConcept;
import thinker.concepts.general_types.IConnectorConcept;
import thinker.concepts.general_types.IPropertyConcept;
import thinker.concepts.general_types.IValueConcept;
import thinker.concepts.profile.IProfile;
import thinker.concepts.relations.IConceptRelationType;
import thinker.concepts.relations.descriptive.PropertyRelationType;
import thinker.helpers.RelationsHelper.RelationValence;
import thinker.knowledge.IKnowledgeRepresentation;

/**
 * A map implementation representing the traits known about a given profile. Can
 * be configured to return positive and/or opposite relations when querying
 * 
 * @author borah
 *
 */
public class ProfilePropertyMap implements Map<IPropertyConcept, IValueConcept> {

	private IProfile focus;
	private IKnowledgeRepresentation base;
	private RelationValence valence;
	private ValueCol valuecol = new ValueCol();
	private EntrySet entryset = new EntrySet();
	private KeySet keyset = new KeySet();

	ProfilePropertyMap(IProfile focus, IKnowledgeRepresentation base, RelationValence valence) {
		this.focus = focus;
		this.base = base;
		this.valence = valence;
	}

	/**
	 * What positivity (positive, not, opposite) this queries
	 * 
	 * @return
	 */
	public RelationValence getValence() {
		return valence;
	}

	private boolean is(IConcept first, IConceptRelationType re, IConcept sec) {
		return valence.checkRelation(Triplet.of(first, re, sec), base);
	}

	/**
	 * Get the profile this map centers around
	 * 
	 * @return
	 */
	public IProfile getFocusProfile() {
		return focus;
	}

	@Override
	public int size() {
		return (int) Streams.stream(base.getConnectedConcepts(focus, PropertyRelationType.HAS_TRAIT))
				.filter((a) -> is(focus, PropertyRelationType.HAS_TRAIT, a)).count();
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public boolean containsKey(Object key) {
		if (!(key instanceof IConcept))
			return false;
		return is(focus, PropertyRelationType.HAS_TRAIT, (IConcept) key)
				|| (Streams.stream(base.getConnectedConcepts(focus, PropertyRelationType.HAS_TRAIT))
						.filter((a) -> is(focus, PropertyRelationType.HAS_TRAIT, a))
						.anyMatch((a) -> a instanceof IConnectorConcept cc && cc.isPropertyAndValue()
								&& is(cc, PropertyRelationType.HAS_TRAIT, (IConcept) key)));
	}

	@Override
	public boolean containsValue(Object value) {
		if (!(value instanceof IConcept))
			return false;
		return (Streams.stream(base.getConnectedConcepts(focus, PropertyRelationType.HAS_TRAIT))
				.filter((a) -> is(focus, PropertyRelationType.HAS_TRAIT, a))
				.anyMatch((a) -> a instanceof IConnectorConcept cc && cc.isPropertyAndValue()
						&& is(cc, PropertyRelationType.HAS_VALUE, (IConcept) value)));
	}

	/**
	 * Return true if one of the properties is mapped as given; also returns true if
	 * "value" is {@link IValueConcept#PRESENT} and this trait is attached with a
	 * positive relation or opposite relation (if this map includes opposite), or
	 * returns true if "value" is {@link IValueConcept#ABSENT} and this trait is NOT
	 * attached with a positive relation or opposite relation (if this map queries
	 * opposie)
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean contains(IPropertyConcept key, IValueConcept value) {
		if (value == IValueConcept.PRESENT) {
			return is(focus, PropertyRelationType.HAS_TRAIT, key);
		} else if (value == IValueConcept.ABSENT) {
			return !is(focus, PropertyRelationType.HAS_TRAIT, key);
		} else {
			return Streams.stream(base.getConnectedConcepts(focus, PropertyRelationType.HAS_TRAIT))
					.filter((a) -> is(focus, PropertyRelationType.HAS_TRAIT, a))
					.anyMatch((a) -> a instanceof IConnectorConcept cc && cc.isPropertyAndValue()
							&& is(cc, PropertyRelationType.HAS_TRAIT, key)
							&& is(cc, PropertyRelationType.HAS_VALUE, value));
		}
	}

	@Override
	public IValueConcept get(Object keyo) {
		if (keyo instanceof IConcept key) {
			if (is(focus, PropertyRelationType.HAS_TRAIT, key))
				return IValueConcept.PRESENT;
			return Streams.stream(base.getConnectedConcepts(focus, PropertyRelationType.HAS_TRAIT))
					.filter((a) -> is(focus, PropertyRelationType.HAS_TRAIT, a))
					.filter((a) -> a instanceof IConnectorConcept cc && cc.isPropertyAndValue()
							&& is(cc, PropertyRelationType.HAS_TRAIT, key))
					.flatMap((a) -> Streams.stream(base.getConnectedConcepts(a, PropertyRelationType.HAS_VALUE)))
					.filter((a) -> a instanceof IValueConcept).map((a) -> (IValueConcept) a).findAny().orElse(null);
		}
		return null;
	}

	@Override
	public IValueConcept put(IPropertyConcept key, IValueConcept value) {
		IValueConcept vc = this.get(key);
		if (!base.knowsConcept(key)) {
			base.learnConcept(key);
		}
		if (!base.knowsConcept(value)) {
			base.learnConcept(value);
		}
		if (value == IValueConcept.PRESENT) {
			valence.establishRelation(Triplet.of(focus, PropertyRelationType.HAS_TRAIT, key), base);
		} else if (value == IValueConcept.ABSENT) {
			valence.invertRelation(Triplet.of(focus, PropertyRelationType.HAS_TRAIT, key), base);
		} else {
			IConnectorConcept conne = IConnectorConcept.propertyAndValue();
			base.learnConcept(conne);
			valence.establishRelation(Triplet.of(focus, PropertyRelationType.HAS_TRAIT, conne), base);
			valence.establishRelation(Triplet.of(conne, PropertyRelationType.HAS_TRAIT, key), base);
			valence.establishRelation(Triplet.of(conne, PropertyRelationType.HAS_VALUE, value), base);
		}
		return vc;
	}

	@Override
	public IValueConcept remove(Object key) {
		if (key instanceof IConcept ckey) {
			if (is(focus, PropertyRelationType.HAS_TRAIT, ckey)) {
				valence.removeRelation(Triplet.of(focus, PropertyRelationType.HAS_TRAIT, ckey), base);
				return IValueConcept.PRESENT;
			} else {
				Pair<IConnectorConcept, IValueConcept> targa = Streams
						.stream(base.getConnectedConcepts(focus, PropertyRelationType.HAS_TRAIT))
						.filter(IConnectorConcept.class::isInstance).map(IConnectorConcept.class::cast)
						.filter(IConnectorConcept::isPropertyAndValue)
						.filter((a) -> base.hasAnyValenceRelation(a, PropertyRelationType.HAS_TRAIT, ckey))
						.flatMap((cc) -> Streams.stream(base.getConnectedConcepts(cc, PropertyRelationType.HAS_VALUE))
								.map((a) -> Pair.of(cc, a)))
						.filter((p) -> p.getSecond() instanceof IValueConcept)
						.map((a) -> Pair.of(a.getFirst(), (IValueConcept) a.getSecond())).findAny().orElse(null);
				if (targa == null) {
					return null;
				} else {
					valence.removeRelation(Triplet.of(focus, PropertyRelationType.HAS_TRAIT, targa.getFirst()), base);
					valence.removeRelation(Triplet.of(targa.getFirst(), PropertyRelationType.HAS_TRAIT, ckey), base);
					valence.removeRelation(
							Triplet.of(targa.getFirst(), PropertyRelationType.HAS_VALUE, targa.getSecond()), base);
					if (base.countConnectedConcepts(targa.getFirst()) == 0) {
						base.forgetConcept(targa.getFirst());
					}
					return targa.getSecond();
				}
			}
		}
		return null;
	}

	@Override
	public void putAll(Map<? extends IPropertyConcept, ? extends IValueConcept> m) {
		for (IPropertyConcept pc : m.keySet()) {
			this.put(pc, m.get(pc));
		}
	}

	@Override
	public void clear() {
		base.removeAllRelations(focus, PropertyRelationType.HAS_TRAIT);
	}

	@Override
	public Set<IPropertyConcept> keySet() {
		return keyset;

	}

	public Iterable<IPropertyConcept> keyIterable() {
		return () -> keyStream().iterator();
	}

	/**
	 * Return all properties as a stream
	 * 
	 * @return
	 */
	public Stream<IPropertyConcept> keyStream() {
		return Streams.stream(base.getConnectedConcepts(focus, PropertyRelationType.HAS_TRAIT))
				.filter((a) -> is(focus, PropertyRelationType.HAS_TRAIT, a))
				.flatMap((a) -> a instanceof IConnectorConcept icc
						? Streams.stream(base.getConnectedConcepts(icc, PropertyRelationType.HAS_TRAIT))
						: Optional.of(a).stream())
				.filter(IPropertyConcept.class::isInstance).map(IPropertyConcept.class::cast);
	}

	@Override
	public Collection<IValueConcept> values() {
		return valuecol;
	}

	public Iterable<IValueConcept> valueIterable() {
		return () -> valueStream().iterator();
	}

	/**
	 * Return all values as a stream
	 * 
	 * @return
	 */
	public Stream<IValueConcept> valueStream() {
		return Streams.stream(base.getConnectedConcepts(focus, PropertyRelationType.HAS_TRAIT))
				.filter((a) -> is(focus, PropertyRelationType.HAS_TRAIT, a))
				.flatMap((a) -> a instanceof IConnectorConcept icc
						? Streams.stream(base.getConnectedConcepts(icc, PropertyRelationType.HAS_VALUE))
						: Optional.empty().stream())
				.filter(IValueConcept.class::isInstance).map(IValueConcept.class::cast);
	}

	/**
	 * Return entries as stream
	 * 
	 * @return
	 */
	public Stream<Entry<IPropertyConcept, IValueConcept>> entryStream() {
		return Streams.stream(base.getConnectedConcepts(focus, PropertyRelationType.HAS_TRAIT))
				.filter((a) -> is(focus, PropertyRelationType.HAS_TRAIT,
						a))
				.flatMap((a) -> a instanceof IConnectorConcept
						? Streams.stream(base.getConnectedConcepts(a, PropertyRelationType.HAS_TRAIT))
								.filter(IPropertyConcept.class::isInstance).map(IPropertyConcept.class::cast)
								.flatMap((pr) -> Streams
										.stream(base.getConnectedConcepts(a, PropertyRelationType.HAS_VALUE))
										.filter(IValueConcept.class::isInstance).map(IValueConcept.class::cast)
										.map((b) -> Map.entry(pr, b)))
						: Optional.of(Map.entry((IPropertyConcept) a, IValueConcept.PRESENT)).stream());
	}

	public Iterable<Entry<IPropertyConcept, IValueConcept>> entryIterable() {
		return () -> entryStream().iterator();
	}

	@Override
	public Set<Entry<IPropertyConcept, IValueConcept>> entrySet() {
		return entryset;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj instanceof Map m) {
			return m.size() == this.size()
					&& this.keyStream().allMatch((o) -> m.containsKey(o) && m.get(o).equals(get(o)));
		}
		return false;
	}

	@Override
	public int hashCode() {
		return focus.hashCode() + this.base.hashCode() + this.valence.hashCode();
	}

	@Override
	public String toString() {
		return StringUtils.formatAsSetFromStream(this.entryStream());
	}

	private class EntrySet implements Set<Entry<IPropertyConcept, IValueConcept>> {

		@Override
		public int size() {
			return ProfilePropertyMap.this.size();
		}

		@Override
		public boolean isEmpty() {
			return ProfilePropertyMap.this.isEmpty();
		}

		@Override
		public boolean contains(Object o) {
			if (o instanceof Map.Entry entrya) {
				return containsKey(entrya.getKey()) && containsValue(entrya.getValue());
			}
			return false;
		}

		@Override
		public Iterator<Entry<IPropertyConcept, IValueConcept>> iterator() {
			return entryIterable().iterator();
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
		public boolean add(Map.Entry<IPropertyConcept, IValueConcept> e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(Object o) {
			if (o instanceof Map.Entry entry) {
				return ProfilePropertyMap.this.remove(entry.getKey(), entry.getValue());
			}
			return false;
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			for (Object x : c) {
				if (!contains(x))
					return false;
			}
			return true;
		}

		@Override
		public boolean addAll(Collection<? extends Map.Entry<IPropertyConcept, IValueConcept>> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			throw new UnimplementedException();
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			boolean a = false;
			for (Object x : c) {
				a = a || this.remove(x);
			}
			return a;
		}

		@Override
		public void clear() {
			ProfilePropertyMap.this.clear();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this)
				return true;
			if (obj instanceof Collection c) {
				return this.size() == c.size() && c.containsAll(this);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return ProfilePropertyMap.this.hashCode();
		}

		@Override
		public String toString() {
			return StringUtils.formatAsSetFromStream(entryStream());
		}

	}

	private class KeySet implements Set<IPropertyConcept> {

		@Override
		public int size() {
			return ProfilePropertyMap.this.size();
		}

		@Override
		public boolean isEmpty() {
			return ProfilePropertyMap.this.isEmpty();
		}

		@Override
		public boolean contains(Object o) {
			return containsKey(o);
		}

		@Override
		public Iterator<IPropertyConcept> iterator() {
			return keyIterable().iterator();
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
		public boolean add(IPropertyConcept e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(Object o) {
			return ProfilePropertyMap.this.remove(o) != null;
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			for (Object x : c) {
				if (!containsKey(x))
					return false;
			}
			return true;
		}

		@Override
		public boolean addAll(Collection<? extends IPropertyConcept> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			throw new UnimplementedException();
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			boolean a = false;
			for (Object x : c) {
				a = a || this.remove(x);
			}
			return a;
		}

		@Override
		public void clear() {
			ProfilePropertyMap.this.clear();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this)
				return true;
			if (obj instanceof Collection c) {
				return this.size() == c.size() && c.containsAll(this);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return ProfilePropertyMap.this.hashCode();
		}

		@Override
		public String toString() {
			return StringUtils.formatAsSetFromStream(keyStream());
		}

	}

	private class ValueCol implements Collection<IValueConcept> {

		@Override
		public int size() {
			return ProfilePropertyMap.this.size();
		}

		@Override
		public boolean isEmpty() {
			return ProfilePropertyMap.this.isEmpty();
		}

		@Override
		public boolean contains(Object o) {
			return containsValue(o);
		}

		@Override
		public Iterator<IValueConcept> iterator() {
			return valueIterable().iterator();
		}

		@Override
		public Object[] toArray() {
			return valueStream().toArray();
		}

		@Override
		public <T> T[] toArray(T[] a) {
			return valueStream().toArray(($) -> a);
		}

		@Override
		public boolean add(IValueConcept e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(Object o) {
			IPropertyConcept keya = null;
			for (IPropertyConcept p : keyIterable()) {
				if (ProfilePropertyMap.this.get(p).equals(o)) {
					keya = p;
					break;
				}
			}
			if (keya != null) {
				return ProfilePropertyMap.this.remove(keya) != null;
			}
			return false;
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			for (Object x : c) {
				if (!containsValue(x)) {
					return false;
				}
			}
			return true;
		}

		@Override
		public boolean addAll(Collection<? extends IValueConcept> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			boolean o = false;
			for (Object x : c) {
				o = o || remove(x);
			}
			return o;
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			throw new UnimplementedException();
		}

		@Override
		public void clear() {
			ProfilePropertyMap.this.clear();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this)
				return true;
			if (obj instanceof Collection c) {
				return this.size() == c.size() && c.containsAll(this);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return ProfilePropertyMap.this.hashCode();
		}

		@Override
		public String toString() {
			return StringUtils.formatAsSetFromStream(valueStream());
		}

	}

}
