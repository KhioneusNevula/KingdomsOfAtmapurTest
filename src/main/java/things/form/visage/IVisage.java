package things.form.visage;

import _main.WorldGraphics;
import _sim.IRenderable;
import things.form.IForm;
import things.form.IPart;

/**
 * Representation of an appearance as a graph.
 * 
 * @author borah
 *
 * @param <P>
 */
public interface IVisage<P extends IPart> extends IForm<P>, IRenderable {

	/**
	 * Draw this actor at 0, 0. It will be translated and scaled to the correct
	 * position by the WorldGraphics
	 */
	@Override
	void draw(WorldGraphics g);

	/**
	 * Return a string reporting the different parts of this visage
	 * 
	 * @return
	 */
	String visageReport();
}
