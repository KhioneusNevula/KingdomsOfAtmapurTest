package thinker.knowledge.timeline;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.TreeMap;

public class Timeline implements ITimeline {

	private ReferenceQueue<TimelineNode> nodeQueue = new ReferenceQueue<>();
	private TreeMap<Long, TNodeRef> nodes = new TreeMap<>();

	public Timeline() {
	}

	private void expunge() {
		for (Object x; (x = nodeQueue.poll()) != null;) {
			TNodeRef ref = (TNodeRef) x;
			nodes.remove(ref.ticks);
		}
	}

	@Override
	public ITimelineNode getNode(long atTicks) {
		expunge();
		if (nodes.containsKey(atTicks))
			return nodes.get(atTicks).get();
		TimelineNode node = new TimelineNode(atTicks);
		nodes.put(atTicks, new TNodeRef(node, nodeQueue));
		return node;
	}

	@Override
	public ITimelineNode predecessor(ITimelineNode current) {
		expunge();
		return nodes.lowerEntry(current.getHistoricalTick()).getValue().get();
	}

	@Override
	public ITimelineNode successor(ITimelineNode current) {
		expunge();
		return nodes.higherEntry(current.getHistoricalTick()).getValue().get();
	}

	private static class TNodeRef extends WeakReference<TimelineNode> {

		private long ticks;

		public TNodeRef(TimelineNode referent, ReferenceQueue<? super TimelineNode> q) {
			super(referent, q);
			ticks = referent.ticks;
		}

	}

	private static class TimelineNode implements ITimelineNode {

		private long ticks;

		private TimelineNode(long ticks) {
			this.ticks = ticks;

		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof ITimelineNode node) {
				return this.ticks == node.getHistoricalTick();
			}
			return super.equals(obj);
		}

		@Override
		public int hashCode() {
			return Long.hashCode(ticks);
		}

		@Override
		public int compareTo(ITimelineNode o) {
			return Long.compare(ticks, o.getHistoricalTick());
		}

		@Override
		public long getHistoricalTick() {
			return ticks;
		}

		@Override
		public ConceptType getConceptType() {
			return ConceptType.TIMELINE_POINT;
		}

		@Override
		public String getUnderlyingName() {
			return "timenode_" + ticks;
		}

		@Override
		public String toString() {
			return "time(" + ticks + ")";
		}

	}

}
