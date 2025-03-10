package things.form.graph.connections;

import _utilities.graph.IInvertibleRelationType;
import _utilities.property.IProperty;

public interface IPartConnection extends IInvertibleRelationType {

	public static final IProperty<Float> CONNECTION_INTEGRITY = IProperty.make("connection_integrity", float.class, 1f);

	@Override
	public IPartConnection invert();

	/**
	 * Whether this kind of part connection can be broken by damage
	 * 
	 * @return
	 */
	public boolean severable();

	/**
	 * Whether this connection is an attachment, i.e. it is a physical material
	 * connection and not something else
	 * 
	 * @return
	 */
	public default boolean isAttachment() {
		return this == PartConnection.JOINED || this == PartConnection.MERGED;
	}
}
