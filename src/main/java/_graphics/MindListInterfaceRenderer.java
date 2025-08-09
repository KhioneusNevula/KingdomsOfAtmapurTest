package _graphics;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.google.common.base.Functions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Streams;

import _sim.plane.Plane;

public class MindListInterfaceRenderer implements IBoxyRenderer {

	private BiMap<Object, Rectangle> relationBoxes = HashBiMap.create();
	private IMindListRenderableInterface mindRef;
	private String title;
	private int boxcount;
	private static final int standard_boxcount = 30;
	private float box_border = 10, boxes_startX = 30, boxes_startY = 30, textSize = 15;
	private Supplier<Boolean> eraseGraphCondition = () -> false;
	private Function<String, String> titleUpdater = Functions.identity();
	private Predicate<Object> bpred;

	public MindListInterfaceRenderer(String title, IMindListRenderableInterface graph) {
		this.mindRef = graph;
		this.boxcount = (int) (Streams.stream(graph.getRenderables()).flatMap((x) -> Streams.stream(x.getValue()))
				.count()) * 2;
		this.title = title;
		if (boxcount > standard_boxcount) {
			box_border = (box_border * standard_boxcount / boxcount);
			boxes_startX = (boxes_startX * standard_boxcount / boxcount);
			boxes_startY = (boxes_startY * standard_boxcount / boxcount);
			textSize = (textSize * standard_boxcount / boxcount);
		}
	}

	@Override
	public void limitVisibleBoxes(Predicate<Object> allowed) {
		this.bpred = allowed;
	}

	/** Set a condition to stop displaying this graph */
	public MindListInterfaceRenderer setEraseGraphCondition(Supplier<Boolean> eraseGraphCondition) {
		this.eraseGraphCondition = eraseGraphCondition;
		return this;
	}

	/**
	 * Set a condition to keep updating this renderer's title (based on the previous
	 * title as argument)
	 */
	public MindListInterfaceRenderer setTitleUpdater(Function<String, String> titleUpdater) {
		this.titleUpdater = titleUpdater;
		return this;
	}

	/** Whether this graph should stop displaying and just show NULL. */
	public boolean shouldEraseGraph() {
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

	public BiMap<Object, Rectangle> getBoxMap() {
		return relationBoxes;
	}

	private Rectangle createBoxForNode(String boxText, WorldGraphics g, float border, float startX, float startY,
			float textHeight) {
		double textWidth = g.textWidth(boxText);
		int width = (int) (textWidth + border + 0.5);
		int height = (int) (textHeight + border + 0.5);
		float ubX = g.width() - width - border;
		float ubY = g.height() - height - border;
		if (startX >= ubX || startY >= ubY)
			return null;

		Rectangle rect = new Rectangle((int) startX, (int) startY, width, height);

		return rect;
	}

	private void renderBox(Rectangle box, WorldGraphics g, String boxText, float border, float textSize, Color color) {
		g.fill(g.color((int) (color.getRed()), (int) (color.getGreen()), (int) (color.getBlue())));
		g.rectMode(WorldGraphics.CORNER);
		g.textAlign(WorldGraphics.BOTTOM);
		g.rect((int) box.getMinX(), (int) box.getMinY(), (int) box.getWidth(), (int) box.getHeight());
		g.fill(g.color(0));
		double textY = box.getMaxY();
		if (boxText.contains("\n")) {
			textY -= boxText.split("\n").length * (textSize + (border / 2));
		}
		g.text(boxText, (int) box.getMinX() + border / 2, (int) textY - border / 2);
	}

	private void renderNodes(IMindListRenderableInterface graph, WorldGraphics g, float border, float startX,
			float startY, float textSize, Color color, Color tColor) {
		float prevY = 0;
		float rX = startX;
		for (Entry<String, ? extends Iterable> keyiterator : graph.getRenderables()) {
			g.textSize(textSize);
			final float textHeight = (g.textAscent() + g.textDescent() + 0.5f);

			String keystring = keyiterator.getKey().toString();
			float adj_text_height = textHeight * (keystring.contains("\n") ? keystring.split("\n").length + 1 : 1);

			Rectangle titleBox = this.createBoxForNode(keystring, g, border, rX, startY, adj_text_height);
			if (titleBox == null) {
				startY = prevY;
				rX = startX;
				titleBox = this.createBoxForNode(keystring, g, border, rX, startY, adj_text_height); // try again
			}
			if (titleBox != null) {
				this.renderBox(titleBox, g, keystring, border, textSize, tColor);
				float maxBoxWidth = 0;
				float maxBoxHeight = 0;

				float rY = startY + 2 * adj_text_height;

				Iterable displayables = keyiterator.getValue();

				for (Object node : displayables) {
					if (bpred != null && !bpred.test(node))
						continue;
					Rectangle box = this.relationBoxes.get(node);
					String boxText = node.toString();
					adj_text_height = textHeight * (boxText.contains("\n") ? boxText.split("\n").length + 1 : 1);
					if (box == null) {
						box = this.createBoxForNode(boxText, g, border, rX, rY, adj_text_height);
						if (box != null) {
							relationBoxes.put(node, box);
						}
					}
					if (box != null) {
						maxBoxWidth = Math.max(maxBoxWidth, box.width);
						maxBoxHeight = Math.max(maxBoxHeight, box.height);

						this.renderBox(box, g, boxText, border, textSize, color);
						rY += 1.2 * box.height;
					}

				}
				rX += 1.2 * maxBoxWidth;
				prevY = Math.max(prevY, rY);
			}
		}
	}

	private void renderPercColumns(WorldGraphics g, IMindListRenderableInterface graph) {
		this.renderNodes(graph, g, box_border, boxes_startX, boxes_startY, textSize, Color.green, Color.cyan);

	}

	@Override
	public void draw(WorldGraphics g) {
		boolean erase = shouldEraseGraph();

		if (erase || this.mindRef == null) {
			this.mindRef = null;
			g.text("(NULL)", g.width() / 2 - g.textWidth("(NULL)") / 2, g.textAscent() + 10);
		} else {
			this.title = updateTitle();
			g.text(title, g.width() / 2 - g.textWidth(title) / 2, g.textAscent() + 10);
			this.renderPercColumns(g, this.mindRef);
		}

	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " \"" + this.title + "\" (" + this.mindRef + ")";
	}

}
