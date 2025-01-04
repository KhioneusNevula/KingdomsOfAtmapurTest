package sim.world;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import sim.MapLayer;
import things.blocks.IBlockState;
import things.blocks.basic.BasicBlock;
import things.blocks.fluid.BasicFluidBlock;
import utilities.IProperty;

/**
 * Class for general properties that can be assigned to worlds
 * 
 * @author borah
 *
 */
public class WorldProperty {

	private WorldProperty() {
	}

	/**
	 * Acceleration of gravity in a world
	 */
	public static final IProperty<Float> GRAVITY = IProperty.make("gravity", float.class, 10f);
	/**
	 * The standard default blocks for each map layer (which permits blocks)
	 */
	public static final IProperty<Map<MapLayer, ? extends IBlockState>> LAYER_BLOCKS = IProperty.make("layer_blocks",
			Map.class,
			ImmutableMap.of(MapLayer.LOWEST, BasicBlock.STONE.getDefaultState(), MapLayer.FLOOR,
					BasicBlock.STONE.getDefaultState(), MapLayer.STANDARD_LAYER, BasicFluidBlock.AIR.getDefaultState(),
					MapLayer.ROOF, BasicFluidBlock.AIR.getDefaultState()));

}
