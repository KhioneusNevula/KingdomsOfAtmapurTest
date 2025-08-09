package _graphics;

import java.awt.Rectangle;
import java.util.function.Predicate;

import com.google.common.collect.BiMap;

public interface IBoxyRenderer extends IRenderable {

	/** A {@link BiMap} of boxes associated to their appropriate concepts */
	public BiMap<?, Rectangle> getBoxMap();

	/** Limits visible boxes to those matching this predicate */
	public void limitVisibleBoxes(Predicate<Object> allowed);

}
