package things.physical_form.kinds;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import things.physical_form.channelsystems.IChannelCenter;
import things.physical_form.channelsystems.IChannelCenter.ChannelRole;
import things.physical_form.components.IComponentPart;
import things.physical_form.components.IPartAbility;
import things.physical_form.components.IPartStat;
import things.physical_form.material.IMaterial;
import things.physical_form.material.IShape;
import things.physical_form.visage.IVisagePart;
import things.spirit.ISpirit;

public class SingleComponent implements IComponentPart, IVisagePart {

	private String name;
	private UUID id;
	private IMaterial material;
	private int planes;
	private IShape shape;
	private Collection<IPartAbility> abilities;
	private Collection<IMaterial> embedded;
	private Set<IChannelCenter> autos;
	private Set<ChannelRole> roles;
	private Set<ISpirit> spirits;
	private Map<IPartStat<?>, Object> stats;

	public SingleComponent(String name, UUID id, IMaterial mat, IShape shape, int planes,
			Collection<IPartAbility> abilities, Map<IPartStat<?>, Object> stats,
			Collection<IMaterial> embeddedMaterials) {
		this.name = name;
		this.id = id;
		this.material = mat;
		this.shape = shape;
		this.planes = planes;
		this.abilities = new HashSet<>(abilities);
		this.embedded = new HashSet<>(embeddedMaterials);
		this.autos = abilities.stream()
				.filter((a) -> a instanceof IChannelCenter ? ((IChannelCenter) a).isAutomatic() : false)
				.map((a) -> (IChannelCenter) a).collect(Collectors.toSet());
		this.roles = abilities.stream().filter((a) -> a instanceof IChannelCenter)
				.map((a) -> ((IChannelCenter) a).getRole()).collect(Collectors.toSet());
		spirits = new HashSet<>();
		this.stats = stats;
	}

	@Override
	public UUID getID() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public float getRelativeSize() {
		return 1f;
	}

	@Override
	public boolean isHole() {
		return false;
	}

	@Override
	public IShape getShape() {
		return shape;
	}

	@Override
	public IMaterial getMaterial() {
		return material;
	}

	@Override
	public int detectionPlanes() {
		return planes;
	}

	@Override
	public Collection<IMaterial> embeddedMaterials() {
		return embedded;
	}

	@Override
	public void addEmbeddedMaterials(Collection<? extends IMaterial> mat) {
		embedded.addAll(mat);
	}

	@Override
	public int interactionPlanes() {
		return planes;
	}

	@Override
	public Collection<IPartAbility> getAbilities() {
		return abilities;
	}

	@Override
	public void addAbility(IPartAbility ability) {
		abilities.add(ability);
	}

	@Override
	public Collection<IChannelCenter> getChannelCenters(ChannelRole role) {
		return abilities.stream()
				.filter((a) -> a instanceof IChannelCenter ? ((IChannelCenter) a).getRole() == role : false)
				.map((a) -> (IChannelCenter) a).collect(Collectors.toSet());
	}

	@Override
	public Collection<IChannelCenter> getAutomaticChannelCenters() {
		return autos;
	}

	@Override
	public boolean hasControlCenter() {
		return this.roles.contains(ChannelRole.CONTROL);
	}

	@Override
	public boolean hasAutomaticChannelCenter() {
		return !autos.isEmpty();
	}

	@Override
	public Collection<ChannelRole> getChannelRoles() {
		return roles;
	}

	@Override
	public Collection<ISpirit> getTetheredSpirits() {
		return this.spirits;
	}

	@Override
	public boolean canAttachSpirit(ISpirit spirit) {
		return true;
	}

	@Override
	public void attachSpirit(ISpirit spirit) {
		this.spirits.add(spirit);
	}

	@Override
	public void removeSpirit(ISpirit toRemove) {
		this.spirits.remove(toRemove);
	}

	@Override
	public <E> E getStat(IPartStat<E> forStat) {
		return (E) stats.getOrDefault(forStat, forStat.getDefaultValue(this));
	}

}
