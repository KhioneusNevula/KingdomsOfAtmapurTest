package things.form;

import java.util.Collection;
import java.util.UUID;

import things.form.material.IMaterial;
import things.form.shape.IShape;
import things.form.soma.component.IComponentPart;
import things.form.visage.ISensableProperty;
import things.form.visage.IVisage;
import things.interfaces.IUnique;
import things.interfaces.UniqueType;
import things.stains.IStain;
import thinker.knowledge.IKnowledgeMedium;

/**
 * A generic interface for component parts in a physical oroganism as well as
 * "illusory" parts in the illusory visage of an organism
 * 
 * @author borah
 *
 */
public interface IPart extends Cloneable, IUnique {

	/**
	 * Creates a "dummy part", i.e. a single-use part whose sole purpose is to be
	 * used to find a part in a hash map. If any method other than {@link #equals},
	 * {@link #hashCode}, {@link #toString()}, or {@link #getName()} is called on
	 * this, throw an exception
	 * 
	 * @param id
	 * @return
	 */
	public static IPart dummy(UUID id) {
		return new DummyPart(id);
	}

	/** Whether this part is a dummy */
	public default boolean isDummy() {
		return this instanceof DummyPart;
	}

	/**
	 * Clone this part (does not need to change the UUID)
	 * 
	 * @return
	 */
	public IPart clone();

	@Override
	default UniqueType getUniqueType() {
		return UniqueType.PART;
	}

	/**
	 * Set the id of this part (and return it)
	 * 
	 * @return
	 */
	public IPart setUUID(UUID id);

	/**
	 * Sets this part to have language emitted from it/written on it, conveyed using
	 * the given property. I.e., the property's "data" as the way in which the
	 * language is detected.
	 */
	public void writeKnowledge(IKnowledgeMedium utterance, ISensableProperty<?> property);

	/**
	 * Gets a text written on this part, if any, conveyed using the given property.
	 * I.e., the property's "data" is he way in which the language is detected
	 */
	public IKnowledgeMedium readKnowledge(ISensableProperty<?> property);

	/**
	 * Return all properties which have some sensable language associated with them
	 */
	public Collection<ISensableProperty<?>> getAllPropertiesWithSensableLanguage();

	/**
	 * The ID of this part to distinguish it from others for internal purposes
	 * 
	 * @return
	 */
	public UUID getUUID();

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
	 * the size was changed. Will NOT automatically call an update on contained
	 * spirits
	 * 
	 * @param size
	 */
	public void changeSize(float size, boolean callUpdate);

	/**
	 * Change the material of this part. Set callUpdate to true if you want to call
	 * an update on the parent soma. Will NOT automatically call an update on
	 * contained spirits
	 * 
	 * @param material
	 * @param callUpdate
	 */
	public void changeMaterial(IMaterial material, boolean callUpdate);

	/**
	 * Change the shape of this part. Set callUpdate to true if you want to call an
	 * update on the parent soma. Will NOT automatically call an update on contained
	 * spirits
	 * 
	 * @param material
	 * @param callUpdate
	 */
	public void changeShape(IShape shape, boolean callUpdate);

	// TODO mass, stored heat, electricity, material
	// TODO sensable properties

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
	 * Sets the form that this part Actually belongs to, i.e. when it is being held
	 */
	public void setTrueOwner(IForm<?> so);

	/**
	 * add a stain to this part
	 * 
	 * @param stain
	 * @param callUpdate whether to notify the body that these stains have been
	 *                   added
	 */
	public void addStain(IStain stain, boolean callUpdate);

	/**
	 * Remove a stain from this part
	 * 
	 * @param stain
	 * @param callUpdate whether to notify the body that these stains have been
	 *                   removed
	 */
	public void removeStain(IMaterial stain, boolean callUpdate);

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

	/**
	 * Get a sensable property of this Part based on its internal state
	 * 
	 * @param <@Override T>
	 * @param prop
	 * @return
	 */
	public <T> T getSensableProperty(ISensableProperty<T> prop);

	/**
	 * 
	 * @author borah
	 *
	 */
	class DummyPart implements IPart {

		private UUID uuid;

		protected DummyPart(UUID id) {
			this.uuid = id;
		}

		@Override
		public DummyPart setUUID(UUID id) {
			this.uuid = id;
			return this;
		}

		@Override
		public UUID getUUID() {
			return uuid;
		}

		@Override
		public int hashCode() {
			return uuid.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj instanceof IComponentPart ico) {
				return this.uuid.equals(ico.getUUID());
			}
			return super.equals(obj);
		}

		@Override
		public String toString() {
			return "dummy(" + this.uuid + ")";
		}

		@Override
		public String getName() {
			return toString();
		}

		@Override
		public Collection<ISensableProperty<?>> getAllPropertiesWithSensableLanguage() {
			throw new UnsupportedOperationException();
		}

		@Override
		public IKnowledgeMedium readKnowledge(ISensableProperty<?> property) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void writeKnowledge(IKnowledgeMedium utterance, ISensableProperty<?> property) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setTrueOwner(IForm<?> so) {
			throw new UnsupportedOperationException();
		}

		@Override
		public <T> T getSensableProperty(ISensableProperty<T> prop) {
			throw new UnsupportedOperationException();
		}

		@Override
		public float getRelativeSize() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void changeSize(float size, boolean callUpdate) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void changeMaterial(IMaterial material, boolean callUpdate) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void changeShape(IShape shape, boolean callUpdate) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean isHole() {
			throw new UnsupportedOperationException();
		}

		@Override
		public IShape getShape() {
			throw new UnsupportedOperationException();
		}

		@Override
		public IMaterial getMaterial() {
			throw new UnsupportedOperationException();
		}

		@Override
		public IForm<?> getOwner() {
			throw new UnsupportedOperationException();
		}

		@Override
		public int detectionPlanes() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setOwner(IForm<?> owner) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void addStain(IStain stain, boolean callUpdate) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void removeStain(IMaterial stain, boolean callUpdate) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void removeAllStains(boolean callUpdate) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Collection<IStain> getStains() {
			throw new UnsupportedOperationException();
		}

		@Override
		public IComponentPart clone() {
			throw new UnsupportedOperationException();
		}

	}

}
