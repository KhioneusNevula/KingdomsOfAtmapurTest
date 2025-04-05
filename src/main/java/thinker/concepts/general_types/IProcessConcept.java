package thinker.concepts.general_types;

import java.util.UUID;

import thinker.concepts.IConcept;

/**
 * A single concept storing a mental process to find later in memory
 * 
 * @author borah
 *
 */
public interface IProcessConcept extends IConcept {

	public UUID getUUID();
}
