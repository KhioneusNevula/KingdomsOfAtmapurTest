package things.form.kinds.multipart;

import java.util.List;

import com.google.common.collect.ImmutableList;

import _utilities.graph.IModifiableRelationGraph;
import things.form.channelsystems.IChannelSystem;
import things.form.graph.connections.CoverageType;
import things.form.graph.connections.IPartConnection;
import things.form.kinds.BasicKindProperties;
import things.form.kinds.IKind;
import things.form.kinds.settings.IKindSettings;
import things.form.soma.ISoma;
import things.form.soma.component.IComponentPart;

public abstract class MultipartKind implements IKind {

	private String name;
	private float averageMass;
	private float averageSize;
	protected List<IChannelSystem> systems;

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
		this.systems = ImmutableList.copyOf(channelSystems);
	}

	/**
	 * Add systems to this Kind. Keep in mind that order matters!
	 * 
	 * @param channelSystems
	 * @return
	 */
	protected MultipartKind sys(IChannelSystem... channelSystems) {
		systems = ImmutableList.<IChannelSystem>builder().addAll(systems).add(channelSystems).build();
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
	protected void addSystemsWithoutPopulating(MultipartSoma createdSoma, IKindSettings settings) {
		for (IChannelSystem system : this.systems) {
			createdSoma.addChannelSystem(system, false); // so that all channel systems know that the other systems
															// exist
		}
	}

	@Override
	public ISoma generate(IKindSettings settings) {
		IModifiableRelationGraph<IComponentPart, IPartConnection> parts = this.makePartGraph(settings);
		MultipartSoma soma = new MultipartSoma(parts, this.makeCoverageGraph(parts, settings),
				settings.getOrDefault(BasicKindProperties.SIZE, averageSize),
				settings.getOrDefault(BasicKindProperties.MASS, averageMass), this.identifyCenter(parts, settings));
		soma.setKind(this);
		addSystemsWithoutPopulating(soma, settings);
		for (IChannelSystem system : soma.getChannelSystems()) {
			soma.addChannelSystem(system, true);
		}
		return soma;
	}

	@Override
	public String toString() {
		return "|>" + this.name() + "<|";
	}

}
