package thinker.concepts.general_types;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import thinker.concepts.general_types.IProfile.ProfileType;

public class LabelConcept implements ILabelConcept {

	private String name;
	private Set<ProfileType> matches;

	public LabelConcept(String name, Iterable<ProfileType> matches) {
		this.name = name;
		this.matches = ImmutableSet.copyOf(matches);
	}

	public LabelConcept(String name) {
		this.name = name;
		this.matches = Collections.emptySet();
	}

	@Override
	public ConceptType getConceptType() {
		return ConceptType.LABEL;
	}

	@Override
	public Collection<ProfileType> matchesProfileTypes() {
		return this.matches;
	}

	@Override
	public String getUnderlyingName() {
		return this.name + "_" + this.matchesProfileTypes();
	}

	@Override
	public String toString() {
		return "[[" + this.name
				+ (!this.matches.isEmpty() ? " (" + String.join(",",
						(Iterable<String>) () -> this.matchesProfileTypes().stream().map(Object::toString).iterator())
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
		if (obj instanceof ILabelConcept other) {
			return this.getPropertyName().equals(other.getPropertyName())
					&& this.matchesProfileTypes().equals(other.matchesProfileTypes());
		}
		return super.equals(obj);
	}

	@Override
	public boolean isIdentity() {
		return false;
	}

}
