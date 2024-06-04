package biology.sensing.senses;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Streams;

import actor.Actor;
import actor.IUniqueEntity;
import actor.construction.physical.IComponentPart;
import actor.construction.physical.IMaterialLayer;
import actor.construction.properties.SenseProperty;
import biology.sensing.AbstractSense;
import biology.sensing.stats.BasicSenseStats;
import phenomenon.IPhenomenon;
import sim.interfaces.IPhysicalEntity;
import sim.physicality.IInteractability;

/**
 * Sight sense checks
 * 
 * @author borah
 *
 */
public class SightSense extends AbstractSense {

	public SightSense(String name) {
		super(name, BasicSenseStats.DISTANCE, BasicSenseStats.EXISTENCE_PLANES, BasicSenseStats.DARKVISION,
				BasicSenseStats.CLOUD_PENETRATION);
	}

	@Override
	public Stream<IUniqueEntity> getSensedVisages(Actor sensingActor, IComponentPart usingPart) {
		int distance = usingPart.getAbilityStat(this, BasicSenseStats.DISTANCE);
		Collection<IInteractability> planes = usingPart.getAbilityStat(this, BasicSenseStats.EXISTENCE_PLANES);
		// TODO darkvision, cloud penetration
		Stream<Actor> actorstream = sensingActor.getWorld().getActors().stream()
				.filter((a) -> a.distance(sensingActor) < distance
						&& IInteractability.interactibleWith(a.getVisage().sensabilityMode(this), planes))
				.sorted((a, b) -> (int) (a.distance(sensingActor) - b.distance(sensingActor))); // actors
		Stream<IPhenomenon> phenstream = sensingActor.getWorld().getPhenomena().stream()
				.filter((a) -> a.isPhysical()
						&& IInteractability.interactibleWith(a.getVisage().sensabilityMode(this), planes))
				.map((a) -> (IPhysicalEntity) a).filter((a) -> a.distance(sensingActor) < distance)
				.sorted((a, b) -> (int) (a.distance(sensingActor) - b.distance(sensingActor)))
				.map((a) -> (IPhenomenon) a); // physical phenomena
		Stream<IPhenomenon> wophenstream = sensingActor.getWorld().getPhenomena().stream()
				.filter((a) -> !a.isPhysical()
						&& IInteractability.interactibleWith(a.getVisage().sensabilityMode(this), planes))
				.map((a) -> (IPhenomenon) a); // world phenomena

		return Streams.concat(actorstream, phenstream, wophenstream);
	}

	@Override
	public Map<? extends IComponentPart, ? extends Collection<? extends IMaterialLayer>> getSensableParts(
			Actor sensingActor, IComponentPart usingPart, Actor forEntity) {
		Multimap<IComponentPart, IMaterialLayer> map = MultimapBuilder.hashKeys().arrayListValues().build();
		for (IComponentPart part : forEntity.getPhysical().getOutermostParts()) {
			float additiveTransparency = 0.0f;
			for (IMaterialLayer materi : part.getMaterials().values()) {
				if (materi.getState().gone())
					continue;
				map.put(part, materi);
				additiveTransparency += materi.getProperty(SenseProperty.OPACITY, true).getValue();
				map.put(part, materi);
				if (additiveTransparency >= 1f) {
					break;
				}

			}
		}
		return map.asMap();
	}

}
