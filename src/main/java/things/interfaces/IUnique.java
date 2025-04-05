package things.interfaces;

import java.util.UUID;

public interface IUnique {

	/**
	 * Return the unique id of this unique object
	 * 
	 * @return
	 */
	public UUID getUUID();

	/** Return the "unique type" of this unique thing */
	public UniqueType getUniqueType();

}
