package thinker.constructs;

import java.util.Collection;

import things.form.kinds.IKind;

/**
 * Concept similar to a {@link IKind} but for a structure composed of multiple
 * things TODO
 * 
 * @author borah
 *
 */
public interface IStructureType {

	/**
	 * What Kinds this structure can contain, e.g. a house may contain a furnace,
	 * which may be its own Kind
	 */
	public Collection<IKind> containedKinds();
}
