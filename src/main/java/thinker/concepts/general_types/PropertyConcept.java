package thinker.concepts.general_types;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import things.interfaces.UniqueType;

public class PropertyConcept implements IPropertyConcept {

	private String name;
	private Set<UniqueType> matches;
	private boolean enume;

	public PropertyConcept(String name, Iterable<UniqueType> matches, boolean enume) {
		this.name = name;
		this.matches = ImmutableSet.copyOf(matches);
		this.enume = enume;
	}

	public PropertyConcept(String name) {
		this.name = name;
		this.matches = Collections.emptySet();
	}

	@Override
	public ConceptType getConceptType() {
		return ConceptType.PROPERTY;
	}

	@Override
	public Collection<UniqueType> getDescriptiveTypes() {
		return this.matches;
	}

	@Override
	public boolean isEnumerable() {
		return enume;
	}

	@Override
	public String getUnderlyingName() {
		return this.name + "_" + this.getDescriptiveTypes();
	}

	@Override
	public String toString() {
		return "[[" + this.name
				+ (!this.matches.isEmpty() ? " (" + String.join(",",
						(Iterable<String>) () -> this.getDescriptiveTypes().stream().map(Object::toString).iterator())
						+ ")" : "")
				+ "]]";
	}

	@Override
	public String getPropertyName() {
		return this.name;
	}

	@Override
	public Boolean defaultValue() {
		return false;
	}

	@Override
	public Class<? super Boolean> getType() {
		return boolean.class;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IPropertyConcept other) {
			return this.getPropertyName().equals(other.getPropertyName())
					&& this.getDescriptiveTypes().equals(other.getDescriptiveTypes());
		}
		return super.equals(obj);
	}

	@Override
	public boolean isIdentity() {
		return false;
	}

}
