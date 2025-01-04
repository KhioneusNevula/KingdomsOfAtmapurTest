package things.physical_form;

import java.util.UUID;

import things.IMultipart;
import things.physical_form.components.IComponentPart;
import things.physical_form.material.IMaterial;
import things.physical_form.material.IShape;
import things.physical_form.visage.IVisagePart;

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
	 * The size of this part relative to the body
	 * 
	 * @return
	 */
	public float getRelativeSize();

	/**
	 * Modify the size of this part; use "callUpdate" to notify the parent part that
	 * the size was changed
	 * 
	 * @param size
	 */
	public void changeSize(float size, boolean callUpdate);

	/**
	 * Change the material of this part. Set callUpdate to true if you want to call
	 * an update on the parent soma
	 * 
	 * @param material
	 * @param callUpdate
	 */
	public void changeMaterial(IMaterial material, boolean callUpdate);

	/**
	 * Change the shape of this part. Set callUpdate to true if you want to call an
	 * update on the parent soma
	 * 
	 * @param material
	 * @param callUpdate
	 */
	public void changeShape(IShape shape, boolean callUpdate);

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

	public IMultipart<?> getOwner();

	public static interface IComponentVisagePart extends IComponentPart, IVisagePart {

	}

}
