package _sim;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

/**
 * What layer of the world this item interacts with (so-called "Z position")
 * 
 * @author borah
 *
 */
public enum MapLayer {
	/**
	 * The lowest layer of the map, cannot be passed
	 */
	LOWEST(true),
	/**
	 * Part of the ground layer
	 */
	FLOOR(true),
	/**
	 * Layer on which walls usually are and which most things move in
	 */
	STANDARD_LAYER(true),
	/**
	 * Layer which usually blocks the sky, so to speak
	 */
	ROOF(true),
	/**
	 * Layer above roof, where flying things are. No blocks allowed here
	 */
	FLYING,
	/**
	 * Layer of (unrendered?) celestial phenomena like the sun?
	 */
	SKY,
	/**
	 * Layer of things that don't fit into one of the categories above
	 */
	OTHER;

	private static List<MapLayer> BLOCK_LAYERS;

	private int blockLayerOrdinal = -1;
	private boolean isBlockLayer;

	private MapLayer() {
	}

	private MapLayer(boolean is) {
		this.isBlockLayer = is;
	}

	static {
		BLOCK_LAYERS = new ArrayList<>();
		for (MapLayer layer : values()) {
			if (layer.isBlockLayer) {
				layer.blockLayerOrdinal = BLOCK_LAYERS.size();
				BLOCK_LAYERS.add(layer);
			}
		}
		BLOCK_LAYERS = ImmutableList.copyOf(BLOCK_LAYERS);
	}

	/**
	 * If this layer can have blocks
	 * 
	 * @return
	 */
	public boolean isBlockLayer() {
		return isBlockLayer;
	}

	/**
	 * The index of this block layer
	 * 
	 * @return
	 */
	public int blockLayerOrdinal() {
		return blockLayerOrdinal;
	}

	/**
	 * Return all layers that can have blocks
	 * 
	 * @return
	 */
	public static List<MapLayer> getBlockLayers() {
		return BLOCK_LAYERS;
	}

	/**
	 * The numberr of block layers
	 * 
	 * @return
	 */
	public static int numBlockLayers() {
		return BLOCK_LAYERS.size();
	}

	/**
	 * Return the block layer at this index
	 * 
	 * @param ordinal
	 * @return
	 */
	public static MapLayer getBlockLayer(int ordinal) {
		return BLOCK_LAYERS.get(ordinal);
	}
}
