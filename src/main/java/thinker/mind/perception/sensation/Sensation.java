package thinker.mind.perception.sensation;

import things.interfaces.UniqueType;
import thinker.concepts.IConcept;
import thinker.concepts.application.IConceptApplier;
import thinker.concepts.application.ISensationApplier;

/** The main sensations */
public enum Sensation implements ISensation {
	/** The feeling of physical discomfort (due to the sensation of touch) */
	DISCOMFORT(false, true),
	/** The feeling of physical comfort (due to the sensation of touch) */
	COMFORT(true, false),
	/**
	 * Pleasure, i.e. well yk. Not sure if I want to keep this lmfao I don't want to
	 * make a sex simulator
	 */
	PLEASURE(true, false),
	/** Pain, i.e. the feeling of damage */
	PAIN(false, true),
	/**
	 * Normal heat sensation, i.e. indicating a temperature above the average
	 * internal temperature but not an unbearable temperatre
	 */
	HEAT(false, true),
	/**
	 * Normal cold sensation, i.e. indicating a temperature below the average but
	 * not an unbearable temperature
	 */
	COLD(false, true),
	/**
	 * Extreme heat sensation, i.e. indicating a temperature so high that it causes
	 * an inability to think
	 */
	EXTREME_HEAT(false, true),
	/**
	 * Extreme cold sensation, i.e. indicating a temperature so low that it causes
	 * an inability to think
	 */
	EXTREME_COLD(false, true);

	private boolean pref;
	private boolean disf;
	private SensitivityStat sensitivityStat;

	private Sensation(boolean isPref, boolean isDisf) {// , boolean isIntr, Object... aff) {
		this.pref = isPref;
		this.disf = isDisf;
		/*
		 * 
		 * this.intr = isIntr; for (Object ob : aff) { if (ob instanceof Map.Entry pair)
		 * { if (aD.containsKey(pair.getKey()) || aI.containsKey(pair.getKey())) throw
		 * new IllegalArgumentException(); if (pair.getValue() instanceof Boolean b) {
		 * if (b) { aD.put((IAffect) pair.getKey(), 1f); } else { aI.put((IAffect)
		 * pair.getKey(), 1f); } } else if (pair.getValue() instanceof Float f) { if (f
		 * < 0) { aD.put((IAffect) pair.getKey(), -f); } else if (f > 0) {
		 * aI.put((IAffect) pair.getKey(), f); } else { throw new
		 * IllegalArgumentException(); } } else { throw new IllegalArgumentException();
		 * } } else if (ob instanceof IAffect iaf) { if (aD.containsKey(iaf) ||
		 * aI.containsKey(iaf)) throw new IllegalArgumentException(); aI.put(iaf, 1f); }
		 * else if (ob instanceof Table.Cell trip) { if
		 * (aD.containsKey(trip.getRowKey()) || aI.containsKey(trip.getRowKey())) throw
		 * new IllegalArgumentException(); if ((Float) trip.getValue() <= 0) throw new
		 * IllegalArgumentException(); if ((Boolean) trip.getColumnKey()) {
		 * aD.put((IAffect) trip.getRowKey(), (Float) trip.getValue()); } else {
		 * aI.put((IAffect) trip.getRowKey(), (Float) trip.getValue()); } } else { throw
		 * new IllegalArgumentException(Arrays.toString(aff)); } }
		 * 
		 */
		this.sensitivityStat = new SensitivityStat(this);
	}

	@Override
	public String getName() {
		return name();
	}

	@Override
	public boolean isPreferred() {
		return this.pref;
	}

	@Override
	public boolean isDisfavored() {
		return this.disf;
	}

	@Override
	public SensitivityStat getSensitivityStat() {
		return sensitivityStat;
	}

	private SensationApplier applier = new SensationApplier();

	@Override
	public <T extends IConceptApplier> T getApplier() {
		return (T) applier;
	}

	private class SensationApplier implements ISensationApplier {

		@Override
		public boolean applies(Object forThing, IConcept checker) {
			return forThing.equals(Sensation.this);
		}

		@Override
		public Sensation getPhysicalSensation() {
			return Sensation.this;
		}

		@Override
		public UniqueType forType() {
			return UniqueType.N_A;
		}

		@Override
		public String toString() {
			return this.getClass().getSimpleName() + "(" + Sensation.this + ")";
		}
	}

}
