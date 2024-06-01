package biology.anatomy;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import actor.construction.IComponentPart;

public class BodyPart implements IComponentPart {
	UUID id;
	IBodyPartType type;
	private Map<ITissueLayerType, Tissue> tissue;
	BodyPart parent;
	private Multimap<String, BodyPart> subParts;
	private BodyPart surrounding;
	private Multimap<String, BodyPart> surroundeds;
	private Map<SenseProperty<?>, Object> sensables;
	private boolean usual = true;
	private Float nutrition;
	private int nutritionTypes = 1;
	private boolean gone;
	private Tissue mainTissue;

	public BodyPart(IBodyPartType type, Map<String, IBodyPartType> partTypes,
			Map<String, ITissueLayerType> tissueTypes) {
		this.type = type;
		this.id = UUID.randomUUID();
		if (!type.tissueTags().isEmpty()) {
			tissue = new TreeMap<>(Comparator.reverseOrder());
			for (String str : type.tissueTags()) {
				ITissueLayerType layer = tissueTypes.get(str);
				if (layer == null)
					throw new IllegalStateException("tissue key " + str
							+ " does not correspond to any tissue type; error while building body part "
							+ type.getName());
				try {
					Tissue tiss = new Tissue(layer, tissueTypes);
					tissue.put(layer, tiss);
					if (type.tissueTags().size() == 1) {
						mainTissue = tiss;
					}
					if (nutritionTypes % layer.getNutritionTypes() != 0) {
						nutritionTypes *= layer.getNutritionTypes();
					}
				} catch (Exception e) {
					throw new RuntimeException("exception while building " + type.getName() + ": " + e.getMessage());
				}
			}
		}

	}

	public IBodyPartType getType() {
		return type;
	}

	public UUID getId() {
		return id;
	}

	@Override
	public Map<ITissueLayerType, Tissue> getMaterials() {
		return tissue == null ? Map.of() : tissue;
	}

	@Override
	public BodyPart getParent() {
		return parent;
	}

	@Override
	public Multimap<String, BodyPart> getChildParts() {
		return subParts == null ? ImmutableSetMultimap.of() : subParts;
	}

	@Override
	public Multimap<String, BodyPart> getSurroundeds() {
		return surroundeds == null ? ImmutableSetMultimap.of() : surroundeds;
	}

	@Override
	public BodyPart getSurrounding() {
		return surrounding;
	}

	@Override
	public float getNutrition() {
		return this.nutrition == null ? this.type.defaultNutritionContent() : this.nutrition;
	}

	public BodyPart setNutrition(float nutrition) {
		this.nutrition = nutrition;
		return this;
	}

	@Override
	public int nutritionTypes() {
		return this.nutritionTypes;
	}

	/**
	 * Set parent to the body part with this Name and ID
	 * 
	 * @param parent
	 */
	protected BodyPart setParent(BodyPart parent) {
		this.parent = parent;
		return this;
	}

	protected void setSurrounding(BodyPart surrounding) {
		this.surrounding = surrounding;
	}

	protected BodyPart addSurrounded(BodyPart surrounded) {
		if (surroundeds == null)
			surroundeds = MultimapBuilder.treeKeys().hashSetValues().build();
		this.surroundeds.put(surrounded.type.getName(), surrounded);
		return this;
	}

	protected BodyPart addChild(BodyPart child) {
		if (subParts == null)
			subParts = MultimapBuilder.treeKeys().hashSetValues().build();
		this.subParts.put(child.type.getName(), child);
		return this;
	}

	protected BodyPart setSensableProperty(SenseProperty<?> prop, Object value) {
		if (this.sensables == null)
			sensables = new TreeMap<>();
		sensables.put(prop, value);
		return this;
	}

	@Override
	public <T> T getProperty(SenseProperty<T> property, boolean ignoreType) {
		T obj = sensables == null ? null : (T) sensables.get(property);
		if (obj == null && !ignoreType)
			return this.type.getTrait(property);
		return obj;
	}

	@Override
	public <T> void changeProperty(SenseProperty<T> property, T value) {
		this.setSensableProperty(property, value);
	}

	@Override
	public Collection<SenseProperty<?>> getSensableProperties() {
		return sensables == null ? Set.of() : sensables.keySet();
	}

	@Override
	public boolean isUnusual() {
		return usual;
	}

	@Override
	public String report() {
		return this.type.getName() + (gone ? "#" : (usual ? "" : "*"))
				+ (this.parent == null ? "" : "{p:" + this.parent.type.getName() + "}") + "," + this.tissue.keySet()
				+ "," + this.sensables + "}";
	}

	@Override
	public String toString() {
		return this.type.getName() + (gone ? "#" : (usual ? "" : "*")) + "{"
				+ (this.parent == null ? "" : this.parent.type.getName()) + "}";

	}

	@Override
	public boolean checkIfUsual() {
		usual = true;
		gone = true;
		if (tissue != null) {
			for (Tissue tiss : tissue.values()) {
				if (!tiss.isUsual()) {
					usual = false;
				}
				if (!tiss.getState().gone()) {
					gone = false;
				}
			}
		}
		return usual;
	}

	@Override
	public boolean isGone() {
		return this.gone;
	}

	@Override
	public Tissue getMainMaterial() {
		return mainTissue;
	}

	@Override
	public boolean hasOneMaterial() {
		return mainTissue != null;
	}

}