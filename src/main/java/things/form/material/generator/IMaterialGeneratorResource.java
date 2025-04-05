package things.form.material.generator;

import things.biology.genes.IGenomeEncoding;
import things.biology.kinds.OrganicKindProperties;
import things.form.channelsystems.IResource;
import things.form.kinds.settings.IKindSettings;
import things.form.material.IMaterial;
import things.form.material.generator.PropertyMapperMaterialGeneratorResource.MaterialGeneratorBuilder;
import things.form.material.property.MaterialProperty;

/**
 * A resource which represents a material-generator, generating materials from a
 * given {@link IKindSettings}. This is usually used for materials that have a
 * genetic encoding that means it has to be uniquely generated for every entity.
 * All normal materials implement this as well, but do NOT use
 * {@link IKindSettings} to generate and instead just return their unaltered
 * self when generating.
 * 
 * @author borah
 *
 */
public interface IMaterialGeneratorResource extends IResource<Float> {

	/** Build a material generator resource with the given base material and name */
	public static MaterialGeneratorBuilder builder(IMaterial base, String name) {
		return new MaterialGeneratorBuilder(base, name);
	}

	/**
	 * Build a material generator resource with the given base material and name
	 * that encodes a genome, i.e. it maps {@link MaterialProperty#GENETICS} to
	 * {@link OrganicKindProperties#GENOME}
	 */
	public static MaterialGeneratorBuilder geneticEncodedMaterial(IMaterial base, String name) {
		return new MaterialGeneratorBuilder(base, name).addMapping(MaterialProperty.GENETICS,
				OrganicKindProperties.GENOME);
	}

	/**
	 * Creates a version of the base material of this resource using the given
	 * {@link IKindSettings}. May return this material in and of itself, if it this
	 * material is not designed to be a generator
	 * 
	 * @param genome
	 * @return
	 */
	public IMaterial generateMaterialFromSettings(IKindSettings settings);

	/**
	 * Whether this {@link IMaterialGeneratorResource} changes what it generates
	 * based on settings or not
	 */
	public boolean isGenerator();

	/**
	 * Returns true if the given material appears to be either the base material of
	 * this generator, or something created from this generator
	 */
	public boolean isBasisOf(IMaterial other);

}
