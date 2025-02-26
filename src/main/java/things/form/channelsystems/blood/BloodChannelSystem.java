package things.form.channelsystems.blood;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import things.form.channelsystems.GeneticMaterialResource;
import things.form.channelsystems.IChannel;
import things.form.channelsystems.IChannelCenter;
import things.form.channelsystems.IChannelCenter.ChannelRole;
import things.form.channelsystems.IChannelSystem;
import things.form.graph.connections.IPartConnection;
import things.form.graph.connections.PartConnection;
import things.form.material.IMaterial;
import things.form.material.property.MaterialProperty;
import things.form.soma.ISoma;
import things.form.soma.component.IComponentPart;
import utilities.couplets.Triplet;

public class BloodChannelSystem implements IChannelSystem {

	private String name;
	private GeneticMaterialResource blood;
	private BloodVesselChannel channel;
	private String heartPart;
	private String bloodGen;
	private HeartChannelCenter heartType;

	/**
	 * 
	 * @param name
	 * @param blood               the material to base blood off
	 * @param bloodVesselMaterial if this is null, then simply have no blood vessels
	 * @param heartPart           the part to give the heart-ability to
	 * @param bloodGen            the part that generates blood
	 */
	public BloodChannelSystem(String name, IMaterial blood, IMaterial bloodVesselMaterial, String heartPart,
			String bloodGen) {
		this.name = name;
		this.blood = new GeneticMaterialResource(name + "_blood", blood);
		this.channel = new BloodVesselChannel(name + "_vessel", bloodVesselMaterial, blood, this);
		this.heartPart = heartPart;
		this.bloodGen = bloodGen;
		this.heartType = new HeartChannelCenter(name + "_heart", this);
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public Collection<GeneticMaterialResource> getChannelResources() {
		return Collections.singleton(this.blood);
	}

	@Override
	public ChannelType getType() {
		return ChannelType.MATERIAL;
	}

	@Override
	public Collection<IChannelCenter> getCenterTypes() {
		return Collections.singleton(heartType);
	}

	@Override
	public Collection<IChannelCenter> getCenterTypes(ChannelRole role) {
		switch (role) {
		case DISTRIBUTION:
			return getCenterTypes();

		default:
			return Collections.emptySet();
		}
	}

	/**
	 * Gets the resource used as blood
	 * 
	 * @return
	 */
	public GeneticMaterialResource getBloodMaterial() {
		return blood;
	}

	@Override
	public Collection<IChannel> getChannelConnectionTypes() {

		return Collections.singleton(channel);
	}

	@Override
	public String toString() {
		return "sys{~" + this.name + "~}";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof IChannelSystem ics) {
			return this.name.equals(ics.name()) && ics.getType() == ChannelType.MATERIAL
					&& Collections.singleton(this.blood).equals(ics.getChannelResources())
					&& Collections.singleton(this.heartType).equals(ics.getCenterTypes())
					&& Collections.singleton(this.channel).equals(ics.getChannelConnectionTypes());
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return name.hashCode() + heartType.hashCode() + channel.hashCode() + this.blood.hashCode();
	}

	@Override
	public Collection<? extends IComponentPart> populateBody(ISoma body) {
		Collection<IComponentPart> brains = body.getPartsByName(heartPart);
		Collection<IComponentPart> bone = body.getPartsByName(bloodGen);
		if (bone.isEmpty()) {
			throw new IllegalStateException();
		}
		IMaterial bloodmat = blood.createMaterialFromGenome(
				bone.stream().findAny().get().getMaterial().getProperty(MaterialProperty.GENETICS));
		for (IComponentPart brain : brains) {
			brain.addAbility(heartType, true);
			for (Triplet<IComponentPart, IPartConnection, IComponentPart> edge : (Iterable<Triplet<IComponentPart, IPartConnection, IComponentPart>>) () -> body
					.getRepresentationGraph()
					.edgeTraversalIteratorBFS(brain, Set.copyOf(PartConnection.attachments()), (a, b) -> true)) {
				body.addChannel(edge.getFirst(), channel, edge.getThird(), true);
				edge.getThird().addEmbeddedMaterials(channel.getVectorMaterials(), true);
				edge.getThird().addEmbeddedMaterials(Collections.singleton(bloodmat), true);
			}

		}
		return brains;
	}

	@Override
	public void onBodyUpdate(ISoma body, IComponentPart updated) {
		// TODO update channel system

	}

	@Override
	public boolean onBodyLoss(ISoma body, IComponentPart lost) {
		// TODO update when the body loses a part
		return true;
	}

	@Override
	public void onBodyNew(ISoma body, IComponentPart gained, IPartConnection connection, IComponentPart to,
			boolean isNew) {
		if (to.embeddedMaterials().containsAll(channel.getVectorMaterials())) {
			body.addChannel(to, channel, gained, false);
		}
	}

}
