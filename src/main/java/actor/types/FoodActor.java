package actor.types;

import java.util.Set;

import actor.Actor;
import actor.construction.IPhysicalActorObject;
import actor.construction.IVisage;
import actor.construction.simple.SimpleActorPhysicalObject;
import actor.construction.simple.SimpleActorType;
import actor.construction.simple.SimpleMaterialLayer;
import actor.construction.simple.SimpleMaterialType;
import actor.construction.simple.SimpleMultilayerPart;
import actor.construction.simple.SimplePartType;
import biology.anatomy.SenseProperty;
import biology.anatomy.SenseProperty.IColor;
import sim.WorldDimension;

public class FoodActor extends Actor {

	public static final SimplePartType FOOD_PART_TYPE = SimplePartType.builder("food_chunk", 1).setDefaultNutrition(5f)
			.build();

	public static final SimpleActorType FOOD_TYPE = SimpleActorType.builder("food").setPartType(FOOD_PART_TYPE).build();

	private SimpleActorPhysicalObject physical;

	public FoodActor(WorldDimension world, String name, int startX, int startY, int radius, Float nutrition) {
		super(world, name, FOOD_TYPE, startX, startY, radius);
		physical = new SimpleActorPhysicalObject(this,
				new SimpleMultilayerPart(FOOD_PART_TYPE, getUUID(),
						Set.of(new SimpleMaterialLayer(SimpleMaterialType.VEGGIE_MATERIAL),
								new SimpleMaterialLayer(SimpleMaterialType.MEATY_MATERIAL))).setNutrition(nutrition));
	}

	public FoodActor setColor(IColor color) {
		this.setOptionalColor(color.getColor().getRGB());
		this.physical.mainComponent().changeProperty(SenseProperty.COLOR, color);
		return this;
	}

	@Override
	public IVisage getVisage() {
		return physical;
	}

	@Override
	public IPhysicalActorObject getPhysical() {
		return physical;
	}

}
