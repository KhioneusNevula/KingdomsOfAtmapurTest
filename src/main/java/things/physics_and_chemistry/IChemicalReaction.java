package things.physics_and_chemistry;

import java.util.Collection;

import com.google.common.collect.Multimap;

import things.form.channelsystems.IResource;
import things.form.soma.ISoma;
import things.form.soma.component.IComponentPart;

/**
 * Represents a "chemical reaction" between different resources, i.e. materials,
 * energy, and so on.
 * 
 * @author borah
 *
 */
public interface IChemicalReaction {

	/**
	 * The set of resources (by name) that act as reagents. These should be names of
	 * channel resources
	 * 
	 * @return
	 */
	public Collection<String> reagents();

	/**
	 * The minimum requisite value of the tested resource needed for this reaction;
	 * return null if there is no minimum
	 * 
	 * @param <E>
	 * @param resource
	 * @return
	 */
	public <E extends Comparable<E>> E minimumRequisiteAmount(IResource<E> resource);

	/**
	 * The maximum requisite value of the tested resource needed for this reaction;
	 * return null if there is no maximum
	 * 
	 * @param <E>
	 * @param resource
	 * @return
	 */
	public <E extends Comparable<E>> E maximumRequisiteAmount(IResource<E> resource);

	/**
	 * The exact requisite value of the tested resource needed for this reaction;
	 * return null if there is a range instead
	 * 
	 * @param <E>
	 * @param resource
	 * @return
	 */
	public <E extends Comparable<E>> E exactRequisiteAmount(IResource<E> resource);

	/**
	 * The minimum temperature required for this reaction, or null if there is no
	 * minimum
	 * 
	 * @return
	 */
	public Float getMinimumTemperature();

	/**
	 * The maximum temperature required for this reaction, or null if there is no
	 * maximum
	 * 
	 * @return
	 */
	public Float getMaximumTemperature();

	/**
	 * Perform the reaction on the given somas' component parts, at the given
	 * temperature
	 * 
	 * @param reagents
	 * @param temperature
	 */
	public void react(Multimap<ISoma, IComponentPart> reagents, float temperature);

}
