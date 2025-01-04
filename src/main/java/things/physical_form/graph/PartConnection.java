package things.physical_form.graph;

import java.util.Collection;
import java.util.Set;

public enum PartConnection implements IPartConnection {
	/**
	 * indicates two things are joined by a severable connection
	 */
	JOINED,
	/**
	 * indicating two things are inseparably connected, e.g. your pupil can't really
	 * be severed from your eyeball in a meaningful way
	 */
	MERGED,
	/**
	 * indicates two things are not joined but instead one is exerting pressure on
	 * the other
	 */
	HOLDING,
	/** inverse of {@link #HOLDING} */
	HELD_BY(HOLDING),
	/** indicates one thing is draped over the other as clothing or whatever */
	WEARING,
	/** inverse of {@link #WEARING} */
	WORN(WEARING);

	private PartConnection reverse;

	private static Collection<PartConnection> attachments = Set.of(JOINED, MERGED);
	private static Collection<PartConnection> valuesl = Set.of(values());

	private PartConnection(PartConnection reverse) {
		this.reverse = reverse;
		reverse.reverse = this;
	}

	private PartConnection() {
		reverse = this;
	}

	@Override
	public PartConnection invert() {
		return reverse;
	}

	@Override
	public boolean bidirectional() {
		return reverse == this;
	}

	@Override
	public boolean severable() {
		return this != MERGED;
	}

	public static Collection<PartConnection> attachments() {
		return attachments;
	}

	public static Collection<PartConnection> valuesCollection() {
		return valuesl;
	}

}