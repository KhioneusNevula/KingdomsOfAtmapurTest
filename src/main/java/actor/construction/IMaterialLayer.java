package actor.construction;

import java.util.Map;

import sim.physicality.PhysicalState;

public interface IMaterialLayer {

	public IMaterialLayerType getType();

	public PhysicalState getState();

	/**
	 * Throw an illegal argument exception if the new state is the same.<br>
	 * REMEMBER TO CALL {@link IPhysicalActorObject#updatePart(IComponentPart)}
	 * 
	 * @param state
	 */
	public void changeState(PhysicalState state);

	public Map<? extends IMaterialLayerType, ? extends IMaterialLayer> getSubLayers();

	/**
	 * Whether this particular layer of material is "usual," i.e. undamaged, etc
	 * 
	 * @return
	 */
	public boolean isUsual();

}
