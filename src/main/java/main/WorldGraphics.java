package main;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.geom.Line2D;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import actor.Actor;
import actor.construction.physical.IComponentPart;
import actor.construction.physical.IMaterialLayer;
import actor.construction.properties.SenseProperty.BasicColor;
import actor.types.FoodActor;
import biology.systems.ESystem;
import processing.core.PApplet;
import processing.event.MouseEvent;
import sim.interfaces.IDynamicsObject.Force;
import sim.interfaces.IRenderable;
import sim.physicality.PhysicalState;
import utilities.Pair;

public class WorldGraphics extends PApplet {

	private GameUniverse world;
	private Display currentDisplay = Display.WORLD;
	private IRenderable currentScreen;
	private final float fps;
	public static final int BORDER = 30;
	private Set<Pair<Line2D.Float, Integer>> showLines = new HashSet<>();
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
		if (event.getKeyCode() == KeyEvent.VK_W) {
		} else if (event.getKeyCode() == KeyEvent.VK_SPACE) {
		} else if (event.getKeyCode() == KeyEvent.VK_I) {
		} else if (event.getKeyCode() == KeyEvent.VK_F) { // spawn food
			world.spawnActor(new FoodActor(world.currentTile, "food" + world.getActors().size(), mouseX - BORDER,
					mouseY - BORDER, 10, 5f, 1f)
							.setColor(BasicColor.values()[world.rand().nextInt(BasicColor.values().length)]),
					true);
		} else if (event.getKeyCode() == KeyEvent.VK_T) {
		} else if (event.getKeyCode() == KeyEvent.VK_R) {
		} else if (event.getKeyCode() == KeyEvent.VK_X) { // strike/damage

			Actor l = world.getActors().stream().filter((a) -> a.pointInHitbox(mouseX - BORDER, mouseY - BORDER))
					.findAny().orElse(null);
			if (l != null) {
				IComponentPart part = l.getPhysical().getOutermostParts().iterator().next();
				float angle = this.random(0, (float) (2 * Math.PI));
				Force strikeForce = Force.fromAngleInRadians(angle, this.random(5, 100));
				this.showLines.add(Pair.of(new Line2D.Float(mouseX, mouseY, mouseX + strikeForce.getXForce(),
						mouseY + strikeForce.getYForce()), 30));
				System.out.printf("Strike " + l + " on " + part + " of weight " + l.getWorld().getWeight(l)
						+ "N with force " + strikeForce + " at %.2f°", Math.toDegrees(angle));
				System.out.println();
				l.applyForce(strikeForce, false);
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

		Actor l = world.getActors().stream().filter((a) -> a.pointInHitbox(mouseX - BORDER, mouseY - BORDER)).findAny()
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
		g.pushStyle();
		g.stroke(Color.yellow.getRGB());
		g.strokeWeight(5);
		Iterator<Pair<Line2D.Float, Integer>> lines = this.showLines.iterator();
		while (lines.hasNext()) {
			Pair<Line2D.Float, Integer> line = lines.next();
			line.setSecond(line.getSecond() - 1);
			g.line((float) line.getKey().getX1(), (float) line.getKey().getY1(), (float) line.getKey().getX2(),
					(float) line.getKey().getY2());
			if (line.getSecond() <= 0)
				lines.remove();
		}
		g.popStyle();

	}

}
