package things.physics_and_chemistry;

import _sim.RelativeSide;
import _sim.vectors.IVector;
import things.form.soma.component.IComponentPart;
import things.interfaces.IMovableObject;

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
	 * Apply a force of the given type on the given side without specifying an
	 * origin (e.g. a magical force or something similar)
	 * 
	 * @param at           the component part the force is directed at
	 * @param connection   the connection between parts the force is directed at, if
	 *                     any; may be null. Only used for Slice; should not be used
	 *                     for others
	 * @param force
	 * @param types
	 * @param acrossPlanes what planes the force is applied across
	 * @return the result of the force's application
	 */
	public ForceResult applyForce(IComponentPart at, IComponentPart connection, IVector force, ForceType type,
			RelativeSide onSide, int acrossPlanes);

	/**
	 * Apply a force using the given component part, of the given type
	 * 
	 * @param at          the component part the force is directed at
	 * @param connection  the connection between parts the force is directed at, if
	 *                    any; may be null. Only used for Slice; should not be used
	 *                    for others
	 * @param force
	 * @param type
	 * @param generatedBy the part that applies the force
	 * @return what the result of applying the force was
	 */
	public ForceResult applyForce(IComponentPart at, IComponentPart connection, IVector force, ForceType type,
			IComponentPart generatedBy);

	/**
	 * Apply a force to this object (in newtons).
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
