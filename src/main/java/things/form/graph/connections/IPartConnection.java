package things.form.graph.connections;

import utilities.graph.IInvertibleRelationType;

public interface IPartConnection extends IInvertibleRelationType {

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
