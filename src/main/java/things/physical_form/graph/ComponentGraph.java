package things.physical_form.graph;

import com.google.common.collect.ImmutableMap;

import things.physical_form.IPart;
import utilities.graph.RelationGraph;

/**
 * A graph representing connections of different parts.
 * 
 * @author borah
 *
 */
public class ComponentGraph<P extends IPart> extends RelationGraph<P, IPartConnection> {

	/**
	 * 
	 * @param illusory whether this graph represents an illusory body
	 */
	public ComponentGraph() {
		/**
		 * TODO we will add things like channels and properties and idk
		 */
		super(ImmutableMap.of());
	}
}
