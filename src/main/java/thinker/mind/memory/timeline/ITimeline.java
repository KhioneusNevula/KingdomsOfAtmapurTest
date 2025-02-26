package thinker.mind.memory.timeline;

import thinker.concepts.IConcept;

/**
 * Stores a series of weakly-referenced "nodes" that can be used in a graph to
 * represent points in time; removes all these nodes when they become
 * phantom-reachable
 * 
 * @author borah
 *
 */
public interface ITimeline {

	/**
	 * A timeline node is stored weakly in a timeline, such that it can be used in a
	 * graph. Once it is no longer in use, it is deleted the next time an operation
	 * is performed on the Timeline
	 * 
	 * @author borah
	 *
	 */
	public interface ITimelineNode extends Comparable<ITimelineNode>, IConcept {
		/**
		 * The ticks when this node of time happened
		 * 
		 * @return
		 */
		public long getTicks();
	}

	/**
	 * Return a timeline node at a given point in time (creating the node on the fly
	 * as needed)
	 * 
	 * @param atTicks
	 * @return
	 */
	public ITimelineNode getNode(long atTicks);

	/**
	 * The timelinie node before this one
	 * 
	 * @param current
	 * @return
	 */
	public ITimelineNode predecessor(ITimelineNode current);

	/**
	 * The timeline node after this one
	 * 
	 * @param current
	 * @return
	 */
	public ITimelineNode successor(ITimelineNode current);
}
