package things.physical_form.graph;

import utilities.graph.IInvertibleRelationType;

public interface IPartConnection extends IInvertibleRelationType {

	/**
	 * Whether this kind of part connection can be broken by damage
	 * 
	 * @return
	 */
	public boolean severable();
}
