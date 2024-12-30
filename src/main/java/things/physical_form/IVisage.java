package things.physical_form;

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

}
