package main;

import java.awt.event.KeyEvent;

import actor.Actor;
import actor.construction.IComponentPart;
import actor.construction.IMaterialLayer;
import actor.types.FoodActor;
import biology.anatomy.SenseProperty.BasicColor;
import biology.systems.ESystem;
import processing.core.PApplet;
import processing.event.MouseEvent;
import sim.interfaces.IRenderable;
import sim.physicality.PhysicalState;

public class WorldGraphics extends PApplet {

	private GameRunner world;
	private Display currentDisplay = Display.WORLD;
	private IRenderable currentScreen;
	private final float fps;
	public static final int BORDER = 30;
	// TODO allow interpreting the world using a culture as a 'lens,' i.e. using its
	// language and stuff like that

	public WorldGraphics(GameRunner world, float fps) {
		this.fps = fps;
		this.world = world;
		this.randomSeed(world.rand().nextLong());

	}

	public GameRunner getWorld() {
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
		if (event.getKeyCode() == KeyEvent.VK_W) {
		} else if (event.getKeyCode() == KeyEvent.VK_SPACE) {
		} else if (event.getKeyCode() == KeyEvent.VK_I) {
		} else if (event.getKeyCode() == KeyEvent.VK_F) { // spawn food
			world.spawnActor(new FoodActor(world.currentWorld, "food" + world.getActors().size(), mouseX - BORDER,
					mouseY - BORDER, 10, 5f)
							.setColor(BasicColor.values()[world.rand().nextInt(BasicColor.values().length)]));
		} else if (event.getKeyCode() == KeyEvent.VK_T) {
		} else if (event.getKeyCode() == KeyEvent.VK_R) {
		} else if (event.getKeyCode() == KeyEvent.VK_X) { // strike/damage

			Actor l = world.getActors().stream()
					.filter((a) -> a.distance(mouseX - BORDER, mouseY - BORDER) <= a.getRadius() + 5).findAny()
					.orElse(null);
			if (l != null) {
				IComponentPart part = l.getPhysical().getOutermostParts().values().iterator().next();
				System.out.println("Strike " + l + " at " + part);
				for (IMaterialLayer mat : part.getMaterials().values()) {
					if (!mat.getState().gone()) {
						mat.changeState(PhysicalState.GONE);
						break;
					}
				}
				l.getPhysical().updatePart(part);
			}
		}
		super.keyPressed(event);
	}

	@Override
	public void mouseClicked(MouseEvent event) {
		super.mouseClicked(event);

		Actor l = world.getActors().stream()
				.filter((a) -> a.distance(event.getX() - BORDER, event.getY() - BORDER) <= a.getRadius() + 5).findAny()
				.orElse(null);
		if (l != null) {
			System.out.print(l.report());

			for (ESystem sys : l.getSystems()) {
				System.out.print(";" + sys.report());
			}
			System.out.println();
		}
	}

	public static enum Display {
		WORLD, MIND, GROUP
	}

	@Override
	public void draw() {
		background(color(0, 100, 100));
		world.worldTick();
		world.draw(this);
		if (this.currentScreen == null) {
		} else {
			g.pushMatrix();
			g.translate(width(), 0);
			this.currentScreen.draw(this);
			g.popMatrix();
		}

	}

}
