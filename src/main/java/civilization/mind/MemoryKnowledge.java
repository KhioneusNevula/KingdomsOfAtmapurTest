package civilization.mind;

import civilization.AbstractKnowledgeBase;
import civilization.social.concepts.profile.Profile;

public class MemoryKnowledge extends AbstractKnowledgeBase implements IMemoryKnowledge {

	public MemoryKnowledge(Profile selfProfile) {
		super(selfProfile);
	}

}
