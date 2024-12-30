package things.physical_form;

import java.util.UUID;

import things.physical_form.material.IMaterial;
import things.physical_form.material.IShape;

/**
 * A generic interface for component parts in a physical oroganism as well as
 * "illusory" parts in the illusory visage of an organism
 * 
 * @author borah
 *
 */
public interface IPart {

	/**
	 * The ID of this part to distinguish it from others for internal purposes
	 * 
	 * @return
	 */
	public UUID getID();

	/**
	 * The name of this part with respect to other parts
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * The size of this part relative to the thing it is part of
	 * 
	 * @return
	 */
	public float getRelativeSize();

	// TODO mass, stored heat, electricity, material

	/**
	 * If this part is actually a hole
	 * 
	 * @return
	 */
	public boolean isHole();

	/**
	 * The shape of this part
	 * 
	 * @return
	 */
	public IShape getShape();

	/**
	 * The primary material of this part
	 * 
	 * @return
	 */
	public IMaterial getMaterial();

}
