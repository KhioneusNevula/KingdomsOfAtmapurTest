package _sim.world;

import things.form.kinds.IKind;

/** Data about a world map when it is unloaded TODO */
public interface IMapData {

	/** Returns the filepath where this stores data */
	public String getFilepath();

	/** Return the number of objects of the given kind that were on the map */
	public int getNumberOfObjects(IKind ofKind);

	/** set the number of objects of the given kind that were on the map */
	public void setNumberOfObjects(IKind ofKind, int num);

}
