package thinker.mind.clock;

public interface IClock {

	/**
	 * How many ticks per day-cycle of this clock
	 * 
	 * @return
	 */
	public int dayCycleLength();

	/**
	 * How many divisions per cycle; for example, for us that would be hours
	 * 
	 * @return
	 */
	public int divisionsPerDay();

	/**
	 * Ticks per division (hour); usually just the result of
	 * {@link #dayCycleLength()} / {@link #divisionsPerDay()}
	 * 
	 * @return
	 */
	public default int ticksPerDivision() {
		return dayCycleLength() / divisionsPerDay();
	}
}
