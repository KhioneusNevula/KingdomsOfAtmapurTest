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
public class ComponentGraph extends RelationGraph<IPart, PartConnection> {

	private boolean illusory;

	/**
	 * 
	 * @param illusory whether this graph represents an illusory body
	 */
	public ComponentGraph(boolean illusory) {
		/**
		 * TODO we will add things like channels and properties and idk
		 */
		super(ImmutableMap.of());
		this.illusory = illusory;
	}
}
