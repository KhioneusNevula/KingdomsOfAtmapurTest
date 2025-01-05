package things.form;

import java.util.Collection;
import java.util.UUID;

import things.form.material.IMaterial;
import things.form.shape.IShape;
import things.form.visage.IVisage;
import things.stains.IStain;

/**
 * A generic interface for component parts in a physical oroganism as well as
 * "illusory" parts in the illusory visage of an organism
 * 
 * @author borah
 *
 */
public interface IPart extends Cloneable {

	/**
	 * Clone this part (does not need to change the UUID)
	 * 
	 * @return
	 */
	public IPart clone();

	/**
	 * Set the id of this part (and return it)
	 * 
	 * @return
	 */
	public IPart setID(UUID id);

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
	 * the size was changed. Will automatically call an update on contained spirits
	 * 
	 * @param size
	 */
	public void changeSize(float size, boolean callUpdate);

	/**
	 * Change the material of this part. Set callUpdate to true if you want to call
	 * an update on the parent soma. Will automatically call an update on contained
	 * spirits
	 * 
	 * @param material
	 * @param callUpdate
	 */
	public void changeMaterial(IMaterial material, boolean callUpdate);

	/**
	 * Change the shape of this part. Set callUpdate to true if you want to call an
	 * update on the parent soma. Will automatically call an update on contained
	 * spirits
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

	public IForm<?> getOwner();

	/**
	 * What planes this part can be sensed on
	 * 
	 * @return
	 */
	public int detectionPlanes();

	/**
	 * Get the owner of this part, cast as a visage
	 * 
	 * @return
	 */
	default IVisage<?> getVisageOwner() {
		return (IVisage<?>) getOwner();
	}

	public void setOwner(IForm<?> owner);

	/**
	 * add a stain to this part
	 * 
	 * @param stain
	 * @param callUpdate whether to notify the body that these stains have been
	 *                   removed
	 */
	public void addStain(IStain stain, boolean callUpdate);

	/**
	 * Remove a stain from this part
	 * 
	 * @param stain
	 * @param callUpdate whether to notify the body that these stains have been
	 *                   removed
	 */
	public void removeStain(IStain stain, boolean callUpdate);

	/**
	 * Remove all stains from this part
	 * 
	 * @param callUpdate whether to notify the body that these stains have been
	 *                   removed
	 */
	public void removeAllStains(boolean callUpdate);

	/**
	 * Return all stains on this part
	 * 
	 * @return
	 */
	public Collection<IStain> getStains();

}
