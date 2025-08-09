package thinker.mind.perception;

import java.util.Collection;
import java.util.Map.Entry;

import _graphics.IMindListRenderableInterface;

import java.util.Set;
import java.util.UUID;

import _utilities.couplets.Pair;
import _utilities.property.IProperty;
import things.form.channelsystems.IChannelNeed;
import things.form.sensing.sensors.ISensor;
import things.form.soma.component.IComponentPart;
import things.form.visage.IVisage;
import thinker.knowledge.IKnowledgeMedium;
import thinker.mind.perception.sensation.ISensation;
import thinker.mind.util.IBeingAccess;

/**
 * Sensory perception of the world by this entity. This gets periodically
 * updated, but not all info in it is updated and may instead be generated
 * on-demand.
 */
public interface IPerception extends IMindListRenderableInterface {

	@Override
	default Iterable<Entry<String, ? extends Iterable>> getRenderables() {
		return Set.of(
				Pair.of("NEEDS",
						() -> getKnownNeeds().stream().map((s) -> Pair.of("need", s, "level", getLevelOfNeed(s)))
								.iterator()),
				Pair.of("SENSATIONS", () -> getKnownSensations().stream()
						.map((s) -> Pair.of("sensation", s, "level", getSensation(s))).iterator()));
	}

	/** Adds a blockage to this Perception for the given Perceptor. */
	public void addBlockage(IPerceptor blockage);

	/** Returns all Perceptors that are blocked */
	public Collection<IPerceptor> getBlockages();

	/** Removes a blockage from this perceptor */
	public void removeBlockage(IPerceptor blockage);

	/** Returns if this perceptor is blocked */
	public default boolean isBlocked(IPerceptor perceptor) {
		return getBlockages().contains(perceptor);
	}

	/** Returns the kinds of {@link ISensation}s that can be felt */
	public Collection<ISensation> getKnownSensations();

	/** Returns the amount of a certain {@link ISensation} that is felt */
	public float getSensation(ISensation forAffect);

	/** Get all needs that were sensed i nthe last update */
	public Collection<IChannelNeed> getKnownNeeds();

	/** Returns the amount of need of this need */
	public float getLevelOfNeed(IChannelNeed need);

	/**
	 * Returns all linguistic information that can be sensed from the given entity,
	 * with the given focus value
	 */
	public Collection<IKnowledgeMedium> getSensedKnowledge(UUID fromEntity, ISensor forSensor, boolean focused,
			IBeingAccess info);

	/**
	 * Returns the visage sensable for the given entity, which is probably partial.
	 */
	public IVisage<?> getVisageFor(UUID entity, IBeingAccess info);

	/** Returns all available perceptible properties of the given entity. */
	public Collection<IProperty<?>> getPerceptibleProperties(UUID entity, IBeingAccess info);

	/**
	 * Returns this property of the given sensed entity, if it is available. Null if
	 * the property is not available.
	 */
	public <T> T getPerceptibleProperty(UUID entity, IProperty<T> property, IBeingAccess info);

	/** Get all available sensors in this body */
	public Collection<ISensor> getAvailableSensors();

	/** Get all available sensors in the body, based on last update */
	public Collection<IComponentPart> getAvailableSensorParts();

	/**
	 * Update what is sensed.
	 * 
	 */
	public void update(IBeingAccess info);

}
