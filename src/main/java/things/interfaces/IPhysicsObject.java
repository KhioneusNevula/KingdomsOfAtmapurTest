package things.interfaces;

import sim.IVector;

/**
 * An object with physics behaviors
 * 
 * @author borah
 *
 */
public interface IPhysicsObject extends IMovableObject {

	/**
	 * The mass of this object in kg
	 * 
	 * @return
	 */
	public float mass();

	/**
	 * Return friction magnitue on this object given the local coefficient of
	 * friction
	 * 
	 * @param coefficient
	 * @return
	 */
	public default float friction(float coefficient, float gravity) {
		return coefficient * normalForce(gravity);
	}

	/**
	 * The magnitude of normal force on this object; used to calculate friction.
	 * Calculated as just the gravity on this object
	 * 
	 * @return
	 */
	public default float normalForce(float gravity) {
		return gravity * mass();
	}

	/**
	 * Apply a force to this object (in newtons)
	 * 
	 * @param force
	 */
	public default void applyForce(IVector force) {
		this.accelerate(force.withXY(force.getUnadjustedX() / mass(), force.getUnadjustedY() / mass()));
	}

	/**
	 * Accelerate this object using the givn vector
	 * 
	 * @param byAmount
	 */
	public void accelerate(IVector acceleration);

	/**
	 * Get the current velocity of this object
	 * 
	 * @return
	 */
	public IVector velocity();

	/**
	 * Return the momentum of this object
	 * 
	 * @return
	 */
	public default IVector momentum() {
		return IVector.of(velocity().getUnadjustedX() * mass(), velocity().getUnadjustedY() * mass());
	}

	/**
	 * Return the kinetic energy of this object
	 * 
	 * @return
	 */
	public default double kineticEnergy() {
		return 0.5 * mass() * velocity().mag() * velocity().mag();
	}
}
