package actor.construction.simple;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import actor.construction.IComponentPart;
import actor.construction.IComponentType;
import actor.construction.IMaterialLayer;
import actor.construction.IMaterialLayerType;
import actor.construction.NutritionType;
import biology.anatomy.SenseProperty;

/**
 * A basic part for a basic entity: one materrial, etc
 * 
 * @author borah
 *
 */
public class SimplePart implements IComponentPart {

	UUID id;
	IComponentType type;
	private Map<IMaterialLayerType, IMaterialLayer> materialmap;
	private IMaterialLayer material;
	IComponentPart parent;
	private Multimap<String, IComponentPart> subParts;
	private IComponentPart surrounding;
	private Multimap<String, IComponentPart> surroundeds;
	private Map<SenseProperty<?>, Object> sensables;
	private boolean usual = true;
	private Float nutrition;

	public SimplePart(IComponentType type, UUID id, IMaterialLayer material) {
		this.type = type;
		this.id = id;
		sensables = new HashMap<>();

		this.material = material;
		this.materialmap = ImmutableMap.of(material.getType(), material);
		for (SenseProperty<?> sep : material.getType().getSensableProperties()) {
			sensables.put(sep, material.getType().getTrait(sep));
		}
	}

	public SimplePart addProperties(Map.Entry<SenseProperty<?>, Object>... props) {
		for (Map.Entry<SenseProperty<?>, Object> prop : props) {
			sensables.put(prop.getKey(), prop.getValue());
		}
		return this;
	}

	@Override
	public <T> void changeProperty(SenseProperty<T> property, T value) {
		sensables.put(property, value);
	}

	public IComponentType getType() {
		return type;
	}

	public UUID getId() {
		return id;
	}

	@Override
	public Map<IMaterialLayerType, IMaterialLayer> getMaterials() {
		return materialmap;
	}

	@Override
	public IComponentPart getParent() {
		return parent;
	}

	@Override
	public Multimap<String, IComponentPart> getChildParts() {
		return subParts == null ? ImmutableSetMultimap.of() : subParts;
	}

	@Override
	public Multimap<String, IComponentPart> getSurroundeds() {
		return surroundeds == null ? ImmutableSetMultimap.of() : surroundeds;
	}

	@Override
	public IComponentPart getSurrounding() {
		return surrounding;
	}

	/**
	 * Set parent to the body part with this Name and ID
	 * 
	 * @param parent
	 */
	public SimplePart setParent(IComponentPart parent) {
		this.parent = parent;
		return this;
	}

	public SimplePart setNutrition(float nutrition) {
		this.nutrition = nutrition;
		return this;
	}

	@Override
	public float getNutrition() {
		return this.nutrition == null ? this.type.defaultNutritionContent() : nutrition;
	}

	@Override
	public int nutritionTypes() {
		return material.getType().getNutritionTypes();
	}

	public void setSurrounding(IComponentPart surrounding) {
		this.surrounding = surrounding;
	}

	public SimplePart addSurrounded(IComponentPart surrounded) {
		if (surroundeds == null)
			surroundeds = MultimapBuilder.treeKeys().hashSetValues().build();
		this.surroundeds.put(surrounded.getType().getName(), surrounded);
		return this;
	}

	public SimplePart addChild(IComponentPart child) {
		if (subParts == null)
			subParts = MultimapBuilder.treeKeys().hashSetValues().build();
		this.subParts.put(child.getType().getName(), child);
		return this;
	}

	@Override
	public <T> T getProperty(SenseProperty<T> property, boolean ignoreType) {
		T obj = sensables == null ? null : (T) sensables.get(property);
		if (obj == null && !ignoreType)
			return this.getType().getTrait(property);
		return obj;
	}

	@Override
	public Collection<SenseProperty<?>> getSensableProperties() {
		return sensables == null ? Set.of() : sensables.keySet();
	}

	@Override
	public boolean isUnusual() {
		return !usual;
	}

	@Override
	public IMaterialLayer getMainMaterial() {
		return material;
	}

	@Override
	public boolean hasOneMaterial() {
		return true;
	}

	@Override
	public boolean isGone() {
		return this.material.getState().gone();
	}

	@Override
	public String toString() {
		return this.type.getName() + (this.isGone() ? "#" : (usual ? "" : "*")) + "{"
				+ (this.parent == null ? "" : this.parent.getType().getName()) + "}";

	}

	@Override
	public String report() {
		return "{" + this.type.getName() + (this.isGone() ? "#" : (usual ? "" : "*"))
				+ (this.parent == null ? "" : "{p:" + this.parent.getType().getName() + "}") + ",{"
				+ this.material.getType() + "}" + ",foodval:" + this.nutrition + "+foodtypes:"
				+ NutritionType.decomposeCombinedValue(this.nutritionTypes(), true) + "}," + this.sensables.values()
				+ "}";
	}

	@Override
	public boolean checkIfUsual() {
		usual = true;
		if (material != null && !material.isUsual()) {
			usual = false;
		}
		return usual;
	}

}
