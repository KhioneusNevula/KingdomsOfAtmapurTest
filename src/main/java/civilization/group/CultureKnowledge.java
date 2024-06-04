package civilization.group;

import civilization.AbstractKnowledgeBase;
import civilization.social.concepts.profile.Profile;

public class CultureKnowledge extends AbstractKnowledgeBase implements ICultureKnowledge {

	public CultureKnowledge(Profile selfProfile) {
		super(selfProfile);
	}

	public CultureKnowledge setParent(ICultureKnowledge parent) {
		this.parent = parent;
		return this;
	}

	public void setStatic(boolean isStatic) {
		this.isStatic = isStatic;
	}

	@Override
	public boolean isHybrid() {
		return false;
	}

}
