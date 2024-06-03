package metaphysical.soul;

import java.util.UUID;

import actor.construction.physical.IComponentPart;
import biology.anatomy.BodyPart;
import biology.anatomy.Tissue;
import civilization.mind.IMind;

public class HumanSoul extends SapientSoul {

	private boolean wantsToDie;

	public HumanSoul(UUID id, IMind mind) {
		super(id, mind);
	}

	@Override
	public void onPartUpdate(IComponentPart part) {
		if (part instanceof BodyPart bpart) {
			for (Tissue tissue : bpart.getMaterials().values()) {
				if (tissue.getType().getName().equals("gray_matter")) {
					if (tissue.getState().isDamaged() || !tissue.getState().isSolid()) {
						this.wantsToDie = true;
					}
				} else {
					// TODO brain damage effects? memory issues, balance issues, etc
				}
			}

		}
	}

	@Override
	public boolean shouldRemove(long worldTick) {
		return wantsToDie;
	}

}
