package things.blocks;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;

import _sim.MapLayer;
import _sim.vectors.IVector;

/**
 * TODO store temperature?
 * 
 * @author borah
 *
 */
public class BlockMap {

	private IBlockState[] blockmap;
	private EnumMap<MapLayer, IBlockState> defaultBlocks;
	private int w;
	private int h;

	public BlockMap(int w, int h, IBlockState defaultBlock) {
		this(w, h, Maps.asMap(Set.copyOf(MapLayer.getBlockLayers()), (a) -> defaultBlock));
	}

	public BlockMap(int w, int h, Map<MapLayer, ? extends IBlockState> defaultBlocks) {
		this.w = w;
		this.h = h;
		this.blockmap = new IBlockState[MapLayer.numBlockLayers() * w * h];
		this.defaultBlocks = new EnumMap<>(defaultBlocks);
		for (MapLayer layer : MapLayer.getBlockLayers()) {
			if (!defaultBlocks.containsKey(layer)) {
				throw new IllegalArgumentException("Not all appropriate layers have default blocks: at least " + layer);
			}
		}

	}

	/**
	 * map index from coordinates and layer
	 * 
	 * @param x
	 * @param y
	 * @param layer
	 * @return
	 */
	private int index(int x, int y, MapLayer layer) {
		if (x < 0 || x >= w || y < 0 || y >= h || layer == null || !layer.isBlockLayer())
			throw new IllegalArgumentException("X:" + x + " [0," + w + ") Y:" + y + " [0," + h + ") L:" + layer);
		return layer.blockLayerOrdinal() * w * h + y * w + x;
	}

	/**
	 * Get the default block of this layer of the map
	 * 
	 * @param forLayer
	 * @return
	 */
	public IBlockState getDefaultBlock(MapLayer forLayer) {
		return defaultBlocks.get(forLayer);
	}

	public IBlockState getBlock(int x, int y, MapLayer layer) {
		IBlockState b = blockmap[index(x, y, layer)];
		return b == null ? defaultBlocks.get(layer) : b;
	}

	public IBlockState getBlock(IVector pos) {
		return getBlock(pos.getX(), pos.getY(), pos.getLayer());
	}

	/**
	 * Change the block at a given position
	 * 
	 * @param x
	 * @param y
	 * @param layer
	 * @param block
	 * @return
	 */
	public IBlockState setBlock(int x, int y, MapLayer layer, IBlockState block) {
		IBlockState b = blockmap[index(x, y, layer)];
		blockmap[index(x, y, layer)] = block;
		return b;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(blockmap);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof IBlockState[]ls) {
			return Arrays.equals(this.blockmap, ls);
		} else if (obj instanceof BlockMap mp) {
			return Arrays.equals(this.blockmap, mp.blockmap);
		}
		return super.equals(obj);
	}

	@Override
	public String toString() {
		return "BlockMap[w=" + w + ",h=" + h + "defaults=" + defaultBlocks + "]";
	}

}
