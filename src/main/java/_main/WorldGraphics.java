package _main;

import java.awt.event.KeyEvent;

import _sim.IRenderable;
import _sim.world.GameUniverse;
import processing.core.PApplet;
import processing.event.MouseEvent;

public class WorldGraphics extends PApplet {

	private GameUniverse world;
	private Display currentDisplay = Display.WORLD;
	private IRenderable currentScreen;
	private final float fps;
	public static final int BORDER = 30;
	private int planeNumber = 0; // TODO for now we will use the all-planes number
	// private Set<Pair<Line2D.Float, Integer>> showLines = new HashSet<>();
	// TODO allow interpreting the world using a culture as a 'lens,' i.e. using its
	// language and stuff like that

	public WorldGraphics(GameUniverse world, float fps) {
		this.fps = fps;
		this.world = world;
		this.randomSeed(world.rand().nextLong());

	}

	public GameUniverse getWorld() {
		return world;
	}

	public int getMaxWidth() {
		return this.displayWidth - BORDER;
	}

	public int getMaxHeight() {
		return this.displayHeight - BORDER;
	}

	public int width() {
		return this.currentScreen == null ? width : width / 2;
	}

	public int height() {
		return height;
		// return this.currentScreen == null ? height : height / 2;
	}

	@Override
	public void settings() {
		super.settings();
		size(world.getWidth() + 2 * BORDER, world.getHeight() + 2 * BORDER);

	}

	@Override
	public void setup() {
		super.setup();
		frameRate(fps);
		world.worldSetup();
		this.windowResizable(true);
	}

	/**
	 * Get the prime-product of planes being viewed
	 * 
	 * @return
	 */
	public int getPlaneNumber() {
		return planeNumber;
	}

	/**
	 * Set the prime-product of planes being viewed
	 * 
	 * @param planeNumber
	 */
	public void setPlaneNumber(int planeNumber) {
		this.planeNumber = planeNumber;
	}

	public void changeDisplay(IRenderable newScreen, Display newDisplay) {
		this.currentDisplay = newDisplay;
		this.currentScreen = newScreen;
		this.windowResize(2 * (world.getWidth() + 2 * BORDER), (world.getHeight() + 2 * BORDER));
	}

	public void returnToWorldDisplay() {
		this.currentDisplay = Display.WORLD;
		this.currentScreen = null;
		this.windowResize(world.getWidth() + 2 * BORDER, world.getHeight() + 2 * BORDER);

	}

	public float getFps() {
		return fps;
	}

	@Override
	public void keyPressed() {
		if (key == KeyEvent.VK_ESCAPE) {
			if (this.currentDisplay != Display.WORLD) {

			}
			key = 0;
		}
		super.keyPressed();
	}

	@Override
	public void keyPressed(processing.event.KeyEvent event) {
		int aMouseX = mouseX - BORDER;
		int aMouseY = mouseY - BORDER;
		synchronized (world) {
			if (event.getKeyCode() == KeyEvent.VK_W) {
			}
		}
		super.keyPressed(event);
	}

	@Override
	public void mouseClicked(MouseEvent event) {
		super.mouseClicked(event);

	}

	public static enum Display {
		WORLD, MIND, GROUP
	}

	@Override
	public void draw() {
		background(color(0, 100, 100));
		g.push();
		g.translate(BORDER, BORDER);
		world.tickWorlds(this);
		g.pop();
		if (this.currentScreen != null) {
			g.push();
			g.translate(width(), 0);
			this.currentScreen.draw(this);
			g.pop();
		}
		/**
		 * g.pushStyle(); g.stroke(Color.yellow.getRGB()); g.strokeWeight(5);
		 * Iterator<Pair<Line2D.Float, Integer>> lines = this.showLines.iterator();
		 * while (lines.hasNext()) { Pair<Line2D.Float, Integer> line = lines.next();
		 * line.setSecond(line.getSecond() - 1); g.line((float) line.getKey().getX1(),
		 * (float) line.getKey().getY1(), (float) line.getKey().getX2(), (float)
		 * line.getKey().getY2()); if (line.getSecond() <= 0) lines.remove(); }
		 * g.popStyle();
		 */

	}

}
