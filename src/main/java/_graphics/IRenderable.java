package _graphics;

public interface IRenderable {

	public boolean canRender();

	/**
	 * What planes this can be rendered on
	 * 
	 * @return
	 */
	public int visibilityPlanes();

	public void draw(WorldGraphics g);
}
