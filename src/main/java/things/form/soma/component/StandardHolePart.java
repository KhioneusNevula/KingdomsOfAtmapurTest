package things.form.soma.component;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import things.form.material.IMaterial;
import things.form.shape.IShape;
import things.form.soma.abilities.IPartAbility;
import things.form.soma.stats.IPartStat;

public class StandardHolePart extends StandardComponentPart {

	public StandardHolePart(String name, UUID id, IShape shape, float size, int planes,
			Collection<? extends IPartAbility> abilities, Map<? extends IPartStat<?>, ? extends Object> stats,
			Collection<? extends IMaterial> embeddedMaterials) {
		super(name, id, IMaterial.NONE, shape, size, planes, abilities, stats, embeddedMaterials);
	}

	@Override
	public boolean isHole() {
		return true;
	}

	@Override
	public String toString() {
		return "((\"" + this.getName() + "\" [hole]))";
	}

}
