package _graphics;

import java.util.Map;

/** Interface to render parts of the mind as a series of lists */
public interface IMindListRenderableInterface {

	/**
	 * Returns renderable qualities of this part of the mind as a list of topics and
	 * values
	 */
	public Iterable<Map.Entry<String, ? extends Iterable>> getRenderables();
}
