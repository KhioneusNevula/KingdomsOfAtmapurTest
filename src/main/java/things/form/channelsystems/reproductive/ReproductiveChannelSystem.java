package things.form.channelsystems.reproductive;

import java.util.Collection;

import _sim.world.GameMap;
import things.form.channelsystems.IChannel;
import things.form.channelsystems.IChannelCenter;
import things.form.channelsystems.IChannelCenter.ChannelRole;
import things.form.channelsystems.IChannelSystem;
import things.form.channelsystems.IResource;
import things.form.graph.connections.IPartConnection;
import things.form.kinds.settings.IKindSettings;
import things.form.soma.ISoma;
import things.form.soma.component.IComponentPart;

public class ReproductiveChannelSystem implements IChannelSystem {

	private String name;

	public ReproductiveChannelSystem(String name) {
		this.name = name;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public ChannelType getType() {
		return ChannelType.OTHER;
	}

	@Override
	public Collection<IChannelCenter> getCenterTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<IChannelCenter> getCenterTypes(ChannelRole role) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<IChannel> getChannelConnectionTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<? extends IResource<?>> getChannelResources() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<? extends IComponentPart> populateBody(ISoma body, IKindSettings set, GameMap world) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onBodyUpdate(ISoma body, IComponentPart updated) {

	}

	@Override
	public boolean onBodyLoss(ISoma body, IComponentPart lost) {
		return true;
	}

	@Override
	public void onBodyNew(ISoma body, IComponentPart gained, IPartConnection connect, IComponentPart to,
			boolean isNew) {

	}

}
