package phenomenon;

import java.util.Random;
import java.util.UUID;

import sim.WorldDimension;

/**
 * TODO someday do phenomenon
 * 
 * @author borah
 *
 */
public abstract class Phenomenon implements IPhenomenon {

	private WorldDimension world;
	private UUID id = UUID.randomUUID();
	private IPhenomenonType type;
	protected int maxLifeTime = -1;
	protected int lifetime = -1;

	public Phenomenon(IPhenomenonType type) {
		this.type = type;
	}

	/**
	 * Use this to end the phenomenon's life at a specific time
	 * 
	 * @param seconds
	 */
	protected void setLifeTimer(int seconds) {
		this.maxLifeTime = seconds;
		this.lifetime = 0;
	}

	@Override
	public WorldDimension getWorld() {
		return world;
	}

	@Override
	public UUID getUUID() {
		return id;
	}

	@Override
	public IPhenomenonType type() {
		return type;
	}

	@Override
	public IPhenomenonType getObjectType() {
		return type;
	}

	@Override
	public Random rand() {
		return this.world.getRand();
	}

	@Override
	public void tick() {
		if (this.maxLifeTime > 0) {
			lifetime++;
		}
	}

	@Override
	public boolean isComplete() {
		return lifetime >= maxLifeTime;
	}
}
