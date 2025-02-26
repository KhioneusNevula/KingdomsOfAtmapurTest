package things.form.channelsystems;

import things.biology.genes.IGenome;
import things.form.material.IMaterial;

/**
 * A resource which represents a material that has a genetic encoding
 * 
 * @author borah
 *
 */
public interface IGeneticMaterialResource extends IResource<Float> {

	/**
	 * Creates a genome-encoded version of the material from this resource using the
	 * given genome
	 * 
	 * @param genome
	 * @return
	 */
	public IMaterial createMaterialFromGenome(IGenome genome);

}
