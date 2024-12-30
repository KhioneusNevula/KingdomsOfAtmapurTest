package things.physical_form;

import things.physical_form.components.IComponentPart;

/**
 * A Kind is a blueprint/template which something is generated from, and also
 * the category it falls into; it can be a Species or just an object class
 * 
 * @author borah
 *
 */
public interface IKind {

	/**
	 * The name of this Kind
	 * 
	 * @return
	 */
	public String name();

	/**
	 * Generate a Soma for something of this Kind from the given settings
	 * 
	 * @param <R>
	 * @param settings
	 * @return
	 */
	public <R extends IComponentPart> ISoma<R> generate(IKindSettings settings);
}
