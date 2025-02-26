package things.actor.categories;

import java.awt.Color;

import things.biology.kinds.OrganicKindProperties;
import things.form.channelsystems.IChannelSystem;
import things.form.channelsystems.blood.BloodChannelSystem;
import things.form.channelsystems.energy.EnergyChannelSystem;
import things.form.channelsystems.signal.SignalChannelSystem;
import things.form.graph.connections.IPartConnection;
import things.form.kinds.multipart.MultipartKind;
import things.form.kinds.multipart.MultipartSoma;
import things.form.kinds.settings.IKindSettings;
import things.form.material.IMaterial;
import things.form.material.Material;
import things.form.material.property.MaterialProperty;
import things.form.soma.component.IComponentPart;
import utilities.graph.IModifiableRelationGraph;

/**
 * All creatures of this nature have an intangible body part called a life_core
 * which is responsible for converting stuff into life energy
 * 
 * @author borah
 *
 */
public abstract class FleshCreatureKind extends MultipartKind {

	public FleshCreatureKind(String name, float averageMass, float averageSize, IChannelSystem... channelSystems) {
		super(name, averageMass, averageSize);

		this.sys(channelSystems);
	}

	/**
	 * Create a generic nerve tissue material from the given settings
	 * 
	 * @param settings
	 * @return
	 */
	protected IMaterial makeNerveMaterial(IKindSettings settings) {
		return Material.GENERIC_TISSUE.buildCopy("nerve_tissue")
				.prop(MaterialProperty.GENETICS, settings.getSetting(OrganicKindProperties.GENOME))
				.prop(MaterialProperty.COLOR, Color.pink).build();
	}

	/**
	 * Create a blood vessel material from the given settings
	 * 
	 * @param settings
	 * @return
	 */
	protected IMaterial bloodVesselMaterial(IKindSettings settings) {
		return Material.GENERIC_TISSUE.buildCopy("blood_vessel_tissue")
				.prop(MaterialProperty.GENETICS, settings.getSetting(OrganicKindProperties.GENOME))
				.prop(MaterialProperty.COLOR, Color.yellow).build();
	}

	/**
	 * Create a blood material from the given settings (only using color)
	 * 
	 * @param settings
	 * @return
	 */
	protected IMaterial makeBloodMaterial(IKindSettings settings) {
		return Material.BLOOD.buildCopy("blood")
				.prop(MaterialProperty.COLOR, settings.getSetting(OrganicKindProperties.BLOOD_COLOR)).build();
	}

	@Override
	protected IComponentPart identifyCenter(IModifiableRelationGraph<IComponentPart, IPartConnection> graph,
			IKindSettings settings) {
		return graph.stream().filter((a) -> a.getName().equals("body")).findFirst().orElseThrow();
	}

	@Override
	protected void addSystemsWithoutPopulating(MultipartSoma createdSoma, IKindSettings set) {
		createdSoma.addChannelSystem(new EnergyChannelSystem("life", "life_core", 1f), false);
		createdSoma.addChannelSystem(new BloodChannelSystem("blood", this.makeBloodMaterial(set),
				this.bloodVesselMaterial(set), "heart", "life_core"), false);
		createdSoma.addChannelSystem(new SignalChannelSystem("nerve", this.makeNerveMaterial(set), "brain"), false);
		super.addSystemsWithoutPopulating(createdSoma, set);
	}

}
