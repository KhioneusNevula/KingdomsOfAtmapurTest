package thinker.concepts.relations.actional;

/**
 * The type of relation
 * 
 * @author borah
 *
 */
public enum EventCategory {
	/** Goal satisfaction */
	SATISFACTION,
	/** Create something */
	GENERATION,
	/** Changing the connective relationship between two things */
	CONNECTION,
	/** Change the position or spatial relation of something */
	POSITIONING,
	/** Destroy something */
	DESTRUCTION,
	/** Change the traits or value of something */
	TRANSFORMATION,
	/** Answers some question */
	ANSWERING,
	/** Something else ig */
	OTHER,
	/** Not an applicable event type */
	N_A
}