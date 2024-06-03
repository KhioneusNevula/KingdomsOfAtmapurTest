package civilization.social.concepts;

/**
 * A kind of knowledge; e.g. a recognized creature type is a type of concept.
 * Concepts are expected to be "self-contained," i.e. they don't depend on
 * references to multiple external objects, and they should have stable equality
 * checks and consistent hashcodes. As such, typically concepts are expected to
 * be <em> immutable </em>
 * 
 * @author borah
 *
 */
public interface IConcept {

	/**
	 * The specific unique name of this concept;
	 * 
	 * @return
	 */
	public String getUniqueName();
}
