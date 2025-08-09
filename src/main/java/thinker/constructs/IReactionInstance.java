package thinker.constructs;

import java.util.Collection;

import things.form.soma.ISoma;

/** A single instance of a process of constructing something (using a Recipe) */
public interface IReactionInstance {

	/** What ingredients were used in this reaction */
	public Collection<ISoma> ingredientsUsed();

	/** Returns what objects were utilized as a work station for this process */
	public Collection<ISoma> workStationsUsed();
}
