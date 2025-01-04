package things.physical_form.kinds.single;

import java.awt.Color;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import things.physical_form.IKind;
import things.physical_form.IKindSettings;
import things.physical_form.channelsystems.IChannelSystem;
import things.physical_form.components.IPartAbility;
import things.physical_form.components.IPartDestructionCondition;
import things.physical_form.components.IPartStat;
import things.physical_form.kinds.BasicKindProperty;
import things.physical_form.kinds.StandardComponentPart;
import things.physical_form.material.IMaterial;
import things.physical_form.material.IShape;

public class SingleComponentKind implements IKind {

	private String name;

	public SingleComponentKind(String name) {
		this.name = name;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public SingleComponentSoma<StandardComponentPart> generate(IKindSettings settings) {
		String name = settings.getSetting(BasicKindProperty.SINGLE_PART_NAME);
		int planes = settings.getSetting(BasicKindProperty.PLANES);
		IShape shape = settings.getSetting(BasicKindProperty.SHAPE);
		IMaterial mat = settings.getSetting(BasicKindProperty.MATERIAL);
		float size = settings.getSetting(BasicKindProperty.SIZE);
		float mass = settings.getSetting(BasicKindProperty.MASS);
		Set<? extends IPartAbility> abs = settings.getSetting(BasicKindProperty.SINGLE_PART_ABILITIES);
		Map<? extends IPartStat<?>, ? extends Object> stats = settings.getSetting(BasicKindProperty.SINGLE_PART_STATS);
		Set<? extends IMaterial> emb = settings.getSetting(BasicKindProperty.SINGLE_PART_EMBEDDED_MATERIALS);
		Set<? extends IChannelSystem> systems = settings.getSetting(BasicKindProperty.CHANNEL_SYSTEMS);
		Color color = settings.getSetting(BasicKindProperty.COLOR);
		IPartDestructionCondition descon = settings.getSetting(BasicKindProperty.DESTRUCTION_CONDITION);
		UUID id = UUID.randomUUID();
		StandardComponentPart component = new StandardComponentPart(name, id, mat, shape, 1f, planes, abs, stats, emb);
		SingleComponentSoma<StandardComponentPart> soma = new SingleComponentSoma<StandardComponentPart>(component,
				size, mass, systems, color, descon);
		return soma;
	}

	@Override
	public String toString() {
		return "|<" + this.name + ">|";
	}

}
