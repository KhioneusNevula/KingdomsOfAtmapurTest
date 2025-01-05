package utilities.graph;

/**
 * Edge properties are assumed to be immutable. An edge property that is
 * nonimmutable may have unexpected behavior
 * 
 * @author borah
 *
 * @param <E>
 */
public class EdgeProperty<E> {
	private String name;
	private Class<E> type;

	public EdgeProperty(String name, Class<E> type) {
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public Class<E> getType() {
		return type;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof EdgeProperty ep) {
			return this.name.equals(ep.name) && this.type.equals(ep.type);
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return (name + type).hashCode();
	}

	@Override
	public String toString() {
		return "<" + name + ">";
	}
}