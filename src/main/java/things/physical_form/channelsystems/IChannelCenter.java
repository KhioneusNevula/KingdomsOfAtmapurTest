package things.physical_form.channelsystems;

import things.interfaces.IStain;
import things.interfaces.IThing;
import things.physical_form.ISoma;
import things.physical_form.components.IComponentPart;
import things.physical_form.components.IPartAbility;
import utilities.Pair;

/**
 * A descriptor of a part that plays a specific role in a channel system
 * 
 * @author borah
 *
 */
public interface IChannelCenter extends IPartAbility {

	public static enum ChannelRole {
		/** This body part creates something, e.g. bones create blood */
		CREATION,
		/** this body part that intakes something, e.g. the mouth intakes food */
		INTAKE,
		/**
		 * a body part responsible for distributing something, e.g. the heart
		 * distributes blood
		 */
		DISTRIBUTION,
		/** a body part responsible for changing one channeled quantity into another */
		TRANSFORM,
		/**
		 * a body part responsible for creating a Thing based on whatever the
		 * channelsystem conveys
		 */
		INSTANTIATE,
		/** A body part which controls other parts, i.e. a brain */
		CONTROL
	}

	/**
	 * The role of this ChannelCenter
	 * 
	 * @return
	 */
	public ChannelRole getRole();

	/**
	 * What channel system this is part of
	 * 
	 * @return
	 */
	public IChannelSystem getSystem();

	/**
	 * Whether this component can currently run an operation; checked by the Control
	 * center. Also checked FOR the Control center, since the Control center may not
	 * be able to run in some instances
	 * 
	 * @param body
	 * @param part
	 * @param ticks
	 * @return
	 */
	public boolean canTick(ISoma<?> body, IComponentPart part, long ticks);

	/**
	 * Whether this part runs automatic behaviors without needing explicit
	 * instructions from a controller. Irrelevant for CONTROL parts, which already
	 * are required to run automatically
	 */
	default boolean isAutomatic() {
		return false;
	}

	/**
	 * Run an automatic operation on this part, e.g. an automatic heartbeat, or a
	 * controlcycle of a brain
	 * 
	 * @param body
	 * @param part
	 * @param ticks
	 */
	default void automaticTick(ISoma<?> body, IComponentPart part, long ticks) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Run one control cycle on this part, which is presumably a Control center
	 * 
	 * @param body
	 * @param part
	 * @param tick
	 */
	public void controlTick(ISoma<?> body, IComponentPart part, long tick);

	/**
	 * Whether this part can intake this item
	 * 
	 * @param body
	 * @param part
	 * @param consumable
	 * @param ticks
	 * @return
	 */
	default boolean canIntake(ISoma<?> body, IComponentPart part, IThing consumable) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Whether this part can intake this item/substance
	 * 
	 * @param body
	 * @param part
	 * @param consumable
	 * @param ticks
	 * @return
	 */
	default boolean canIntake(ISoma<?> body, IComponentPart part, IStain consumable) {
		throw new UnsupportedOperationException();

	}

	/**
	 * accept a Thing and turn it into a resource or return null if not possible
	 * 
	 * @param body
	 * @param part
	 * @param consumable
	 * @return
	 */
	default Pair<IChannelResource<?>, ?> intake(ISoma<?> body, IComponentPart part, IThing consumable) {
		throw new UnsupportedOperationException();
	}

	/**
	 * accept a Stain and turn it into a resource or return null if not possible
	 * 
	 * @param body
	 * @param part
	 * @param consumable
	 * @return
	 */
	default Pair<IChannelResource<?>, ?> intake(ISoma<?> body, IComponentPart part, IStain consumable) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Create an instance of something using a resource or return null if not
	 * possible
	 */
	default IThing instantiateThing(ISoma<?> body, IComponentPart part, IChannelResource<?> res, Object instance) {
		throw new UnsupportedOperationException();
	}

	/** Create a stain using a resource or return null if not possible */
	default IStain instantiateStain(ISoma<?> body, IComponentPart part, IChannelResource<?> res, Object instance) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Create the appropriate resource given that this has a CREATE role, or retuern
	 * null if not possible
	 * 
	 * @param body
	 * @param part
	 * @return
	 */
	default Pair<IChannelResource<?>, ?> create(ISoma<?> body, IComponentPart part) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Transform the contained channel resource into another, given that this has a
	 * TRANSFORM role, or return null if not possible.
	 * 
	 * @param res
	 * @param instance
	 * @return
	 */
	default Pair<IChannelResource<?>, ?> transform(ISoma<?> body, IComponentPart part, IChannelResource<?> res,
			Object instance) {
		throw new UnsupportedOperationException();
	}

}
