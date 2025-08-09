package _graphics;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.PrimitiveIterator.OfInt;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import com.google.common.base.Functions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import _sim.plane.Plane;
import thinker.mind.will.IThinkerWill;
import thinker.mind.will.thoughts.IThought;

public class ThoughtsRenderer implements IBoxyRenderer {

	private BiMap<IThought, Rectangle> relationBoxes = HashBiMap.create();
	private IThinkerWill willRef;
	private Random rand;
	private String title;
	private int boxcount;
	private static final int standard_boxcount = 30;
	private float box_border = 10, boxes_startX = 30, boxes_startY = 30, textSize = 15;
	private Supplier<Boolean> eraseGraphCondition = () -> false;
	private Function<String, String> titleUpdater = Functions.identity();
	private Predicate<Object> pred;

	public ThoughtsRenderer(String title, IThinkerWill graph, Random rand) {
		this.willRef = graph;
		this.boxcount = graph.focusedThoughtsCap() * 5;
		this.rand = rand;
		this.title = title;
		if (boxcount > standard_boxcount) {
			box_border = (box_border * standard_boxcount / boxcount);
			boxes_startX = (boxes_startX * standard_boxcount / boxcount);
			boxes_startY = (boxes_startY * standard_boxcount / boxcount);
			textSize = (textSize * standard_boxcount / boxcount);
		}
	}

	/** Set a condition to stop displaying this graph */
	public ThoughtsRenderer setEraseGraphCondition(Supplier<Boolean> eraseGraphCondition) {
		this.eraseGraphCondition = eraseGraphCondition;
		return this;
	}

	/**
	 * Set a condition to keep updating this renderer's title (based on the previous
	 * title as argument)
	 */
	public ThoughtsRenderer setTitleUpdater(Function<String, String> titleUpdater) {
		this.titleUpdater = titleUpdater;
		return this;
	}

	/** Whether this graph should stop displaying and just show NULL. */
	public boolean shouldEraseThoughts() {
		return eraseGraphCondition.get();
	}

	public String updateTitle() {
		return titleUpdater.apply(title);
	}

	@Override
	public boolean canRender() {
		return true;
	}

	@Override
	public int visibilityPlanes() {
		return Plane.OMNIPLANE.getPrime();
	}

	public BiMap<IThought, Rectangle> getBoxMap() {
		return relationBoxes;
	}

	@Override
	public void limitVisibleBoxes(Predicate<Object> allowed) {
		this.pred = allowed;
	}

	private Rectangle createBoxForNode(String boxText, WorldGraphics g, float border, float startX, float startY,
			float textHeight) {
		double textWidth = g.textWidth(boxText);
		int attempts = 0;
		int width = (int) (textWidth + border + 0.5);
		int height = (int) (textHeight + border + 0.5);
		float ubX = g.width() - width - border;
		float ubY = g.height() - height - border;
		if (startX >= ubX || startY >= ubY)
			return null;
		IntStream xis = rand.ints((int) startX, (int) ubX).distinct();
		IntStream yis = rand.ints((int) startY, (int) ubY).distinct();
		OfInt xIter = xis.iterator();
		OfInt yIter = yis.iterator();

		for (; attempts < Math.min(200, Math.min(ubX - startX, ubY - startY)); attempts++) {
			int x = xIter.nextInt();
			int y = yIter.nextInt();
			Rectangle rect = new Rectangle(x, y, width, height);
			boolean canMake = true;
			for (Rectangle ra : this.relationBoxes.values()) {
				if (ra.intersects(rect)) {
					canMake = false;
					break;
				}
			}
			if (canMake) {
				return rect;
			}
		}
		return null;
	}

	private void renderThoughtBox(Rectangle box, WorldGraphics g, String boxText, float border, float textSize,
			Color color) {
		g.fill(g.color((int) (color.getRed()), (int) (color.getGreen()), (int) (color.getBlue())));
		g.rectMode(WorldGraphics.CORNER);
		g.rect((int) box.getMinX(), (int) box.getMinY(), (int) box.getWidth(), (int) box.getHeight());
		g.fill(g.color(0));
		g.text(boxText, (int) box.getMinX() + border / 2, (int) box.getMaxY() - border / 2);
	}

	private void renderNodes(IThinkerWill graph, WorldGraphics g, float border, float startX, float startY, float textSize,
			Color color) {

		for (IThought node : graph.getThoughts()) {
			if (pred != null && !pred.test(node))
				continue;
			g.textSize(textSize);
			final float textHeight = (g.textAscent() + g.textDescent() + 0.5f);
			Rectangle box = this.relationBoxes.get(node);
			String boxText = node.toString();
			if (box == null) {
				box = this.createBoxForNode(boxText, g, border, startX, startY, textHeight);
				if (box != null) {
					relationBoxes.put(node, box);
				}
			}
			if (box != null) {
				this.renderThoughtBox(box, g, boxText, border, textSize, color);
			} else {
				// System.err.println("Failed to show thought box for " + thought);
				break;
			}

		}
	}

	private void renderThoughts(WorldGraphics g, IThinkerWill graph) {
		this.renderNodes(graph, g, box_border, boxes_startX, boxes_startY, textSize, Color.green);

	}

	@Override
	public void draw(WorldGraphics g) {
		boolean erase = shouldEraseThoughts();

		if (erase || this.willRef == null) {
			this.willRef = null;
			g.text("(NULL)", g.width() / 2 - g.textWidth("(NULL)") / 2, g.textAscent() + 10);
		} else {
			this.title = updateTitle();
			g.text(title, g.width() / 2 - g.textWidth(title) / 2, g.textAscent() + 10);
			this.renderThoughts(g, this.willRef);
		}

	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " \"" + this.title + "\" (" + this.willRef + ")";
	}

}
