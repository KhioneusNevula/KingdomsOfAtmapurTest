package _sim.world;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import _sim.GameUniverse;
import things.form.kinds.IKind;

public class MapData implements IMapData {

	private MapTile tile;
	private GameUniverse uni;
	private String path;
	private Map<IKind, Integer> objCountsOnLastSave = new HashMap<>();

	protected String generateFilepath() {
		return Path
				.of(uni.getSaveFolder(),
						tile.getDimension().getId() + "_"
								+ (tile.isContiguous() ? tile.getRow() + "_" + tile.getCol() : tile.getName()))
				.toString();
	}

	public MapData(MapTile mt, GameUniverse gu) {
		this.tile = mt;
		this.uni = gu;
		this.path = generateFilepath();
	}

	@Override
	public String getFilepath() {
		return path;
	}

	@Override
	public int getNumberOfObjects(IKind ofKind) {
		return this.objCountsOnLastSave.getOrDefault(ofKind, 0);
	}

	@Override
	public void setNumberOfObjects(IKind ofKind, int num) {
		this.objCountsOnLastSave.put(ofKind, num);
	}

}
