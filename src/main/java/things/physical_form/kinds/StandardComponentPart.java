package things.physical_form.kinds;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import sim.Plane;
import things.IMultipart;
import things.physical_form.IPart;
import things.physical_form.IPart.IComponentVisagePart;
import things.physical_form.ISoma;
import things.physical_form.IVisage;
import things.physical_form.channelsystems.IChannelCenter;
import things.physical_form.channelsystems.IChannelCenter.ChannelRole;
import things.physical_form.components.IPartAbility;
import things.physical_form.components.IPartStat;
import things.physical_form.material.IMaterial;
import things.physical_form.material.IShape;
import things.spirit.ISpirit;

public class StandardComponentPart implements IComponentVisagePart {

	private String name;
	private UUID id;
	private IMaterial material;
	private int planes;
	private float size;
	private IShape shape;
	private Collection<IPartAbility> abilities;
	private Collection<IMaterial> embedded;
	private Set<IChannelCenter> autos;
	private Set<ChannelRole> roles;
	private Set<ISpirit> spirits;
	private Map<IPartStat<?>, Object> stats;
	private IMultipart<? super StandardComponentPart> owner;

	public StandardComponentPart(String name, UUID id, IMaterial mat, IShape shape, float size, int planes,
			Collection<? extends IPartAbility> abilities, Map<? extends IPartStat<?>, ? extends Object> stats,
			Collection<? extends IMaterial> embeddedMaterials) {
		this.name = name;
		this.id = id;
		this.size = size;
		this.material = mat;
		this.shape = shape;
		this.planes = planes;
		this.abilities = new HashSet<>(abilities);
		this.embedded = new HashSet<>(embeddedMaterials);
		this.autos = abilities.stream()
				.filter((a) -> a instanceof IChannelCenter ? ((IChannelCenter) a).isAutomatic() : false)
				.map((a) -> (IChannelCenter) a).collect(Collectors.toSet());
		this.roles = this.abilities.stream().filter((a) -> a instanceof IChannelCenter)
				.map((a) -> ((IChannelCenter) a).getRole()).collect(Collectors.toSet());
		spirits = new HashSet<>();
		this.stats = new HashMap<>(stats);
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
		return size;
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
	public int interactionPlanes() {
		return planes;
	}

	@Override
	public Collection<IPartAbility> getAbilities() {
		return abilities;
	}

	@Override
	public void addAbility(IPartAbility ability, boolean callUpdate) {
		abilities.add(ability);
		if (ability instanceof IChannelCenter cc) {
			if (cc.isAutomatic()) {
				this.autos = new HashSet<>(this.autos);
				autos.add(cc);
				autos = autos.stream().collect(Collectors.toUnmodifiableSet());
			}
			this.roles = new HashSet<>(this.roles);
			roles.add(cc.getRole());
			roles = roles.stream().collect(Collectors.toUnmodifiableSet());
		}
		if (callUpdate && this.owner instanceof ISoma<?>soma) {
			soma.onPartAbilitiesChange(this, Collections.singleton(ability));
		}

	}

	@Override
	public void addEmbeddedMaterials(Collection<? extends IMaterial> mat, boolean callUpdate) {
		embedded.addAll(mat);
		if (callUpdate && this.owner instanceof ISoma<?>soma) {
			soma.onPartEmbeddedMaterialsChanged(this, new HashSet<>(mat));
		}
	}

	@Override
	public void changeMaterial(IMaterial material, boolean callUpdate) {
		IMaterial fmat = this.material;
		this.material = material;
		if (callUpdate && this.owner instanceof IMultipart<?>soma) {
			soma.onPartMaterialChange(this, fmat);
		}
	}

	@Override
	public void changeSize(float size, boolean callUpdate) {
		float fmat = this.size;
		this.size = size;
		if (callUpdate && this.owner instanceof IMultipart<?>soma) {
			soma.onPartSizeChange(this, fmat);
		}
	}

	@Override
	public void changeShape(IShape shape, boolean callUpdate) {
		IShape fmat = this.shape;
		this.shape = shape;
		if (callUpdate && this.owner instanceof IMultipart<?>soma) {
			soma.onPartShapeChange(this, fmat);
		}
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

	@Override
	public <E> void changeStat(IPartStat<E> stat, E newValue, boolean callUpdate) {
		Object formerVal = stats.put(stat, newValue);
		if (callUpdate && this.owner instanceof ISoma<?>soma) {
			soma.onPartStatChange(this, stat, formerVal);
		}
	}

	@Override
	public Collection<? extends IPartStat<?>> getStats() {
		return this.stats.keySet();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IPart part) {
			return this.id.equals(part.getID());
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return this.id.hashCode();
	}

	@Override
	public String toString() {
		return "{{\"" + this.name + "\", [" + this.material.name() + "," + this.shape.name() + "] }}";
	}

	@Override
	public String componentReport() {
		return "{name=" + name + ",mat=" + this.material + ",shape=" + this.shape
				+ (planes != 1 ? ",planes=" + Plane.separate(planes) : "")
				+ (abilities.isEmpty() ? "" : ",abs=" + abilities) + (spirits.isEmpty() ? "" : ",spirits=" + spirits)
				+ (stats.isEmpty() ? "" : ",stats=" + stats) + (embedded.isEmpty() ? "" : ",embedded=" + embedded)
				+ "}";
	}

	@Override
	public IMultipart<?> getOwner() {
		return owner;
	}

	@Override
	public void setOwner(ISoma<?> soma) {
		this.owner = (IMultipart<? super StandardComponentPart>) soma;
	}

	@Override
	public void setOwner(IVisage<?> owner) {
		this.owner = (IMultipart<? super StandardComponentPart>) owner;
	}

}
