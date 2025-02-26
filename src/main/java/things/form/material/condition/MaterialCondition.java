package things.form.material.condition;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import things.form.material.IMaterial;
import things.form.material.property.IMaterialProperty;

public class MaterialCondition implements IMaterialCondition {

	Multimap<IMaterialProperty<?>, Object> propertiesCollectioned;

	MaterialCondition() {
		this.propertiesCollectioned = MultimapBuilder.hashKeys().hashSetValues().build();
	}

	@Override
	public boolean acceptsMaterial(IMaterial mat) {
		for (Map.Entry<IMaterialProperty<?>, Collection<Object>> proa : propertiesCollectioned.asMap().entrySet()) {
			if (!proa.getValue().contains(mat.getProperty(proa.getKey()))) {
				return false;
			}
		}
		return true;
	}

	@Override
	public Collection<IMaterialProperty<?>> getCheckedProperties() {
		return propertiesCollectioned.keySet();
	}

	@Override
	public <E> Collection<E> getAllowedValues(IMaterialProperty<E> forProp) {
		return (Collection<E>) propertiesCollectioned.get(forProp);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IMaterialCondition iec) {

			if (!this.getCheckedProperties().equals(iec.getCheckedProperties()))
				return false;
			for (IMaterialProperty<?> pro : this.propertiesCollectioned.keySet()) {
				if (!this.propertiesCollectioned.get(pro).equals(iec.getAllowedValues(pro))) {
					return false;
				}
			}
			return true;
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return this.propertiesCollectioned.hashCode();
	}

	@Override
	public String toString() {
		return "EatingCondition" + this.propertiesCollectioned;
	}

}
