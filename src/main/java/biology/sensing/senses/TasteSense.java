package biology.sensing.senses;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import actor.Actor;
import actor.IUniqueEntity;
import actor.construction.physical.IComponentPart;
import actor.construction.physical.IMaterialLayer;
import biology.sensing.AbstractSense;
import biology.sensing.stats.BasicSenseStats;

public class TasteSense extends AbstractSense {

	public TasteSense(String name) {
		super(name, BasicSenseStats.EXISTENCE_PLANES);
	}

	@Override
	public Stream<IUniqueEntity> getSensedVisages(Actor sensingActor, IComponentPart usingPart) {
		return Collections.<IUniqueEntity>emptySet().stream(); // TODO sense if something is touching tongue ig
	}

	@Override
	public Map<? extends IComponentPart, ? extends Collection<? extends IMaterialLayer>> getSensableParts(
			Actor sensingActor, IComponentPart usingPart, Actor forEntity) {
		Multimap<IComponentPart, IMaterialLayer> map = MultimapBuilder.hashKeys().arrayListValues().build();
		for (IComponentPart part : forEntity.getPhysical().getOutermostParts()) {

			for (IMaterialLayer materi : part.getMaterials().values()) {
				if (materi.getState().gone())
					continue;
				map.put(part, materi);
				if (materi.getState().isSolid())
					break;
			}
		}
		return map.asMap();
	}

}
