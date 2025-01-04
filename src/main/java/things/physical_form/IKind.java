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
	 * The kind for things which are generated in a custom fashion and therefore do
	 * not have a distinctive species. Use this for unique beings and objects, for
	 * example
	 */
	public static final IKind MISCELLANEOUS = new IKind() {

		@Override
		public String name() {
			return "_miscellaneous";
		}

		@Override
		public ISoma<? extends IComponentPart> generate(IKindSettings settings) {
			throw new UnsupportedOperationException("cannot generate body for miscellaneous kind");
		}
	};

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
	public ISoma<? extends IComponentPart> generate(IKindSettings settings);
}
