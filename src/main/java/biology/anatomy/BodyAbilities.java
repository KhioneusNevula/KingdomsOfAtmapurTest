package biology.anatomy;

import actor.construction.physical.IPartAbility;

public enum BodyAbilities implements IPartAbility {
	SEE, SMELL, HEAR, TASTE,
	/**
	 * typically a property of the brain, indicating a being can have a soul (mind)
	 */
	HAVE_SOUL,
	/** pump blood, usually */
	PUMP_LIFE_ESSENCE,
	/** typically a property of wombs */
	GESTATE,
	/** e.g. ovaries store eggs */
	STORE_EGGS,
	/** where eggs or offspring emerge */
	GIVE_BIRTH,
	/** puts sperm in eggs */
	FERTILIZE,
	/** e.g. testicles store seed */
	STORE_SEED,
	/** e.g. a tail is prehensile */
	PREHENSILE, GRASP, WALK, SPEAK, EAT, CAST_POWER, FLY;

	@Override
	public String getName() {
		return name();
	}
}