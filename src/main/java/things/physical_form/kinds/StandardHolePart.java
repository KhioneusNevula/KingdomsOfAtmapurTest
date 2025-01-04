package things.physical_form.kinds;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import things.physical_form.components.IPartAbility;
import things.physical_form.components.IPartStat;
import things.physical_form.material.IMaterial;
import things.physical_form.material.IShape;

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

}
