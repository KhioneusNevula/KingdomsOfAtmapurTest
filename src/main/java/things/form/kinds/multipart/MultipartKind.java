package things.form.kinds.multipart;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;

import _utilities.graph.IModifiableRelationGraph;
import things.actor.IActor;
import things.form.channelsystems.IChannelSystem;
import things.form.graph.connections.CoverageType;
import things.form.graph.connections.IPartConnection;
import things.form.kinds.BasicKindProperties;
import things.form.kinds.IKind;
import things.form.kinds.settings.IKindSettings;
import things.form.soma.IPartDestroyedCondition;
import things.form.soma.ISoma;
import things.form.soma.MultipartSoma;
import things.form.soma.component.IComponentPart;

public abstract class MultipartKind implements IKind {

	private String name;
	private float averageMass;
	private float averageSize;
	protected Map<String, IChannelSystem> systems;

	/**
	 * The name, average mass, and average size of this entity. Also channel
	 * systems. Keep in mind that the order you add channel systems indicates the
	 * order in which they are applied to the body
	 * 
	 * @param name
	 * @param averageMass
	 * @param averageSize
	 * @param channelSystems
	 */
	public MultipartKind(String name, float averageMass, float averageSize, IChannelSystem... channelSystems) {
		this.name = name;
		this.averageMass = averageMass;
		this.averageSize = averageSize;
		this.systems = ImmutableMap.copyOf(
				Arrays.stream(channelSystems).collect(Collectors.toMap(IChannelSystem::name, Functions.identity())));
	}

	@Override
	public Collection<IChannelSystem> getGeneratableChannelSystems() {
		return systems.values();
	}

	@Override
	public <T extends IChannelSystem> T getChannelSystemByName(String name) {
		return (T) systems.get(name);
	}

	/**
	 * Add systems to this Kind. Keep in mind that order matters!
	 * 
	 * @param channelSystems
	 * @return
	 */
	protected MultipartKind sys(IChannelSystem... channelSystems) {
		systems = ImmutableMap.<String, IChannelSystem>builder().putAll(systems)
				.putAll((Iterable<Map.Entry<String, IChannelSystem>>) () -> Arrays.stream(channelSystems)
						.map((cs) -> Map.entry(cs.name(), cs)).iterator())
				.build();
		return this;
	}

	@Override
	public String name() {
		return name;
	}

	public float getAverageMass() {
		return averageMass;
	}

	public float getAverageSize() {
		return averageSize;
	}

	@Override
	public final boolean isDisembodied() {
		return false;
	}

	/**
	 * Create a part graph
	 * 
	 * @param settings
	 * @return
	 */
	protected abstract IModifiableRelationGraph<IComponentPart, IPartConnection> makePartGraph(IKindSettings settings);

	/**
	 * Create a coverage graph
	 * 
	 * @param partGraph
	 * @param settings
	 * @return
	 */
	protected abstract IModifiableRelationGraph<IComponentPart, CoverageType> makeCoverageGraph(
			IModifiableRelationGraph<IComponentPart, IPartConnection> partGraph, IKindSettings settings);

	/**
	 * Return the center part, given the graph of the body
	 * 
	 * @param graph
	 * @param settings
	 * @return
	 */
	protected abstract IComponentPart identifyCenter(IModifiableRelationGraph<IComponentPart, IPartConnection> graph,
			IKindSettings settings);

	/**
	 * Add systems without populating the soma; overriide this method if you want to
	 * alsoo add special channel systems like blood systems with specific genetic
	 * material or whatever
	 */
	protected void addSystemsWithoutPopulating(MultipartSoma createdSoma, IKindSettings settings, IActor aFor) {
		for (IChannelSystem system : this.systems.values()) {
			createdSoma.addChannelSystem(system, settings); // so that all channel systems know that the
															// other systems
			// exist
		}
	}

	@Override
	public ISoma generateSoma(IKindSettings settings, IActor actorFor) {
		IModifiableRelationGraph<IComponentPart, IPartConnection> parts = this.makePartGraph(settings);
		MultipartSoma soma = new MultipartSoma(parts, this.makeCoverageGraph(parts, settings),
				settings.getOrDefault(BasicKindProperties.SIZE, averageSize),
				settings.getOrDefault(BasicKindProperties.MASS, averageMass), this.identifyCenter(parts, settings))
						.setDestructionCondition(settings.getSetting(BasicKindProperties.DESTRUCTION_CONDITION));
		soma.setKind(this);
		soma.setCreationSettings(settings);
		addSystemsWithoutPopulating(soma, settings, actorFor);
		return soma;
	}

	@Override
	public String toString() {
		return "|>" + this.name() + "<|";
	}

}
