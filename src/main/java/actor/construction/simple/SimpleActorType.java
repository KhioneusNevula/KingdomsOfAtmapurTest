package actor.construction.simple;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import actor.construction.IBlueprintTemplate;
import actor.construction.IComponentType;

/**
 * Use this for entities that have virtually zero individuality among them and
 * only one relevant part.
 * 
 * @author borah
 *
 */
public class SimpleActorType implements IBlueprintTemplate {

	private String name;

	private IComponentType partType;

	private Map<String, IComponentType> ctSingleton;

	private SimpleActorType(String name) {
		this.name = name;
	}

	public static Builder builder(String name) {
		return Builder.start(name);
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public float averageUniqueness() {
		return 0;
	}

	@Override
	public String toString() {
		return "ActorType(" + name + ")";
	}

	@Override
	public String getUniqueName() {
		return "actortype_" + name;
	}

	@Override
	public int hashCode() {
		return getUniqueName().hashCode();
	}

	@Override
	public Map<String, ? extends IComponentType> partTypes() {
		return ctSingleton;
	}

	@Override
	public boolean hasSinglePartType() {
		return true;
	}

	@Override
	public IComponentType mainComponent() {
		return this.partType;
	}

	public static class Builder {
		private SimpleActorType at;

		private static Builder start(String name) {
			Builder b = new Builder();
			b.at = new SimpleActorType(name);
			return b;
		}

		public Builder setPartType(IComponentType ct) {
			at.partType = ct;
			at.ctSingleton = ImmutableMap.of(ct.getName(), ct);
			return this;
		}

		public SimpleActorType build() {
			SimpleActorType lo = at;
			at = null;
			return lo;
		}
	}

}
