package things.form.sensing.sensors;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;

import _utilities.collections.FilteredCollectionView;
import _utilities.collections.ImmutableSetView;
import _utilities.property.IProperty;
import things.actor.IActor;
import things.form.IPart;
import things.form.soma.component.IComponentPart;
import things.form.soma.stats.FloatPartStats;
import things.form.visage.ISensableProperty;
import things.form.visage.IVisage;
import things.form.visage.PartialVisage;
import thinker.knowledge.IKnowledgeMedium;
import thinker.mind.perception.PerceptibleProperties;

public class StandardSightSensor extends AbstractSensor {

	private final Set<ISensableProperty<?>> SIGHT_SENSABLES = new HashSet<>();

	private final Set<IProperty<?>> SIGHT_PERCEPTIBLES = new HashSet<>();

	StandardSightSensor(String name) {
		super(name, FloatPartStats.SIGHT_DISTANCE);
		SIGHT_PERCEPTIBLES.add(PerceptibleProperties.DIRECTION);
		SIGHT_PERCEPTIBLES.add(PerceptibleProperties.LOCATION);
		SIGHT_PERCEPTIBLES.add(PerceptibleProperties.DISTANCE);
	}

	@Override
	public Collection<ISensableProperty<?>> getSensablePropertiesCollection() {
		return SIGHT_SENSABLES;
	}

	@Override
	public ISensor registerSensableProperty(ISensableProperty<?> p) {
		SIGHT_SENSABLES.add(p);
		return this;
	}

	@Override
	public Collection<IKnowledgeMedium> senseKnowledge(IPart part, IComponentPart sensorPart, boolean foc) {
		Set<IKnowledgeMedium> utts = new HashSet<>();
		for (ISensableProperty<?> p : SIGHT_SENSABLES) {
			if (p.requiresFocus() && !foc)
				continue;
			IKnowledgeMedium utt = part.readKnowledge(p);
			if (utt != null) {
				utts.add(utt);
			}
		}
		return utts;
	}

	@Override
	public Map<ISensableProperty<?>, Object> senseProperties(IPart part, IComponentPart sensorPart, boolean isFocused) {
		return Maps.asMap(
				new FilteredCollectionView<>(SIGHT_SENSABLES,
						((p) -> p instanceof ISensableProperty<?>prop && (!prop.requiresFocus() || isFocused))),
				part::getSensableProperty);
	}

	@Override
	public Collection<IProperty<?>> getPerceptibleProperties(IComponentPart sensor) {
		return ImmutableSetView.from(SIGHT_PERCEPTIBLES);
	}

	@Override
	public Map<IProperty<?>, Object> getPerceptibleProperties(IVisage<?> forVis, IComponentPart sensor) {
		if (forVis.getOwner() != null && sensor.getOwner().getOwner() != null) {
			return Map.of(PerceptibleProperties.LOCATION, forVis.getOwner().getPosition(),
					PerceptibleProperties.DISTANCE, forVis.getOwner().distance(sensor.getOwner().getOwner()),
					PerceptibleProperties.DIRECTION,
					forVis.getOwner().getPosition().add(sensor.getOwner().getOwner().getPosition().invert()));
		}
		return Collections.emptyMap();
	}

	@Override
	public <T extends IPart> IVisage<T> sensableParts(IVisage<T> sensing, IActor sensingActor, IComponentPart sensor) {
		Set<T> visibles = new HashSet<>();
		for (T par : sensing.getCoverageGraph()) {
			if (sensing.getOverallCoverage(par).size() < 6) {
				visibles.add(par);
			}
		}
		return new PartialVisage<>(sensing).addSensableParts(visibles);
	}

	@Override
	public float getMaxDistance(IComponentPart sensor) {
		return sensor.getStat(FloatPartStats.SIGHT_DISTANCE);
	}

	@Override
	public String toString() {
		return "{<o>(" + this.name() + ")<o>}";
	}

}
