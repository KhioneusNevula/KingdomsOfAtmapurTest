package things.physical_form;

import _main.WorldGraphics;
import sim.IRenderable;
import things.IMultipart;
import things.physical_form.visage.IVisagePart;

/**
 * Representation of an appearance as a graph.
 * 
 * @author borah
 *
 * @param <P>
 */
public interface IVisage<P extends IVisagePart> extends IMultipart<P>, IRenderable {

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
