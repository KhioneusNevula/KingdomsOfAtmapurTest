package things.biology.genes;

import java.util.Collection;
import java.util.Collections;

import things.form.kinds.IKind;
import things.form.kinds.settings.IKindSettings;

/**
 * A Genome's main traits are that it returns a unique representational string
 * of traits, and keeps track of what kind of organism it is
 * 
 * @author borah
 *
 */
public interface IGenome {

	/**
	 * A genome which is not specific to any entity
	 */
	public static final IGenome NONE = new IGenome() {

		@Override
		public String getUniqueGenestring() {
			return "";
		}

		@Override
		public IKindSettings getSettings() {
			return IKindSettings.NONE;
		}

		@Override
		public IKind getKind() {
			return IKind.MISCELLANEOUS;
		}

		@Override
		public Collection<IGene<?>> getGenes() {
			return Collections.emptySet();
		}

		@Override
		public <E> E getGene(IGene<E> gene) {
			return gene.defaultValue();
		}
	};

	/**
	 * Return the kind of this organism
	 * 
	 * @return
	 */
	public IKind getKind();

	/**
	 * Returns a unique string representing its genes, of the form
	 * [geneName]L.[geneName]L. and so on
	 * 
	 * @return
	 */
	public String getUniqueGenestring();

	/**
	 * Returns all genes in this genome
	 * 
	 * @return
	 */
	public Collection<IGene<?>> getGenes();

	/**
	 * Return a gene of this genome
	 * 
	 * @param <E>
	 * @param gene
	 * @return
	 */
	public <E> E getGene(IGene<E> gene);

	/**
	 * Return the kindsettings this genecode uses to generate an organism
	 * 
	 * @return
	 */
	public IKindSettings getSettings();

}
