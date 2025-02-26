package utilities.graph;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.PrimitiveIterator.OfInt;
import java.util.Random;
import java.util.stream.IntStream;

import _main.WorldGraphics;
import _sim.IRenderable;
import _sim.plane.Plane;
import utilities.couplets.Triplet;

public class GraphRenderer implements IRenderable {

	private Map<Object, Rectangle> relationBoxes = new HashMap<>();
	private WeakReference<IRelationGraph<?, ? extends IInvertibleRelationType>> graphRef;
	private Random rand;
	private String title;
	private int boxcount;
	private static final int standard_boxcount = 30;
	private float box_border = 10, boxes_startX = 30, boxes_startY = 30, textSize = 15;
	private Collection<? extends IInvertibleRelationType> renderableEdges = Collections.emptySet();

	public GraphRenderer(String title, IRelationGraph<?, ? extends IInvertibleRelationType> graph, Random rand) {
		this.graphRef = new WeakReference<>(graph);
		this.boxcount = graph.size();
		this.rand = rand;
		this.title = title;
		if (boxcount > standard_boxcount) {
			box_border = (box_border * standard_boxcount / boxcount);
			boxes_startX = (boxes_startX * standard_boxcount / boxcount);
			boxes_startY = (boxes_startY * standard_boxcount / boxcount);
			textSize = (textSize * standard_boxcount / boxcount);
		}
	}

	public Collection<? extends IInvertibleRelationType> getRenderableEdges() {
		return renderableEdges;
	}

	public GraphRenderer setRenderableEdges(Collection<? extends IInvertibleRelationType> renderableEdges) {
		this.renderableEdges = renderableEdges;
		return this;
	}

	@Override
	public boolean canRender() {
		return graphRef.get() != null;
	}

	@Override
	public int visibilityPlanes() {
		return Plane.OMNIPLANE.getPrime();
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

	private Point[] getConnectionPoints(Rectangle r, boolean includeCenter) {
		int[] xs = { (int) r.getCenterX(), r.x, (int) r.getMaxX() };
		int[] ys = { (int) r.getCenterY(), r.y, (int) r.getMaxY() };
		Point[] list = new Point[8 + (includeCenter ? 1 : 0)];
		int i = 0;
		for (int xi = 0; xi < xs.length; xi++) {
			for (int yi = 0; yi < ys.length; yi++) {
				if (!includeCenter && (xi == 0 && 0 == yi))
					continue;
				list[i] = new Point(xs[xi], ys[yi]);
				i++;
			}
		}
		return list;
	}

	private void renderRelationMapBox(Rectangle box, WorldGraphics g, String boxText, float border, float textSize,
			Color color) {
		g.fill(g.color((int) (color.getRed()), (int) (color.getGreen()), (int) (color.getBlue())));
		g.rectMode(WorldGraphics.CORNER);
		g.rect((int) box.getMinX(), (int) box.getMinY(), (int) box.getWidth(), (int) box.getHeight());
		g.fill(g.color(0));
		g.text(boxText, (int) box.getMinX() + border / 2, (int) box.getMaxY() - border / 2);
	}

	private void renderRelationMapConnection(Triplet<?, ? extends IInvertibleRelationType, ?> edge, String edgeString,
			WorldGraphics g, float textSize, Color color) {

		Rectangle left = relationBoxes.get(edge.getFirst());
		Rectangle right = relationBoxes.get(edge.getThird());
		if (left == null || right == null)
			return;

		Point[] points = null;
		double distance = -1;
		Point[] childPoints = getConnectionPoints(left, false);
		Point[] parentPoints = getConnectionPoints(right, false);
		for (Point point : childPoints) {
			for (Point pPoint : parentPoints) {
				double d = point.distance(pPoint);
				if (points == null || d < distance) {
					points = new Point[] { point, pPoint };
					distance = d;
				}
			}
		}
		g.stroke(g.color(color.getRGB()));
		g.line(points[0].x, points[0].y, points[1].x, points[1].y);
		g.textSize(textSize);
		g.fill(g.color(color.getRGB()));
		g.ellipseMode(WorldGraphics.CENTER);
		g.circle(points[1].x, points[1].y, 10);
		g.fill(g.color(Color.white.getRGB()));

		float w = g.textWidth(edgeString);
		g.text(edgeString, (points[0].x + points[1].x) / 2 - w / 2, (points[0].y + points[1].y) / 2);

	}

	private void renderEdges(IRelationGraph graph, WorldGraphics g, float textSize, Color color) {
		for (Triplet<?, ? extends IInvertibleRelationType, ?> edge : (Iterable<Triplet<?, ? extends IInvertibleRelationType, ?>>) () -> graph
				.edgeIterator(this.renderableEdges)) {
			this.renderRelationMapConnection(edge,
					((IRelationGraph) graph).edgeToString(edge.getFirst(), edge.getSecond(), edge.getThird(), false), g,
					textSize, color);
		}
	}

	private void renderNodes(IRelationGraph<?, ? extends IInvertibleRelationType> graph, WorldGraphics g, float border,
			float startX, float startY, float textSize, Color color) {

		for (Object node : graph) {
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
				this.renderRelationMapBox(box, g, boxText, border, textSize, color);
			} else {
				// System.err.println("Failed to show thought box for " + thought);
				break;
			}

		}
	}

	private void renderRelationsGraph(WorldGraphics g, IRelationGraph<?, ? extends IInvertibleRelationType> graph) {

		this.renderEdges(graph, g, textSize, Color.red);
		this.renderNodes(graph, g, box_border, boxes_startX, boxes_startY, textSize, Color.green);

	}

	@Override
	public void draw(WorldGraphics g) {

		g.text(title, g.width() / 2 - g.textWidth(title) / 2, g.textAscent() + 10);
		if (this.graphRef.get() != null) {
			this.renderRelationsGraph(g, this.graphRef.get());
		} else {
			this.title = "(NULL)";
		}
	}

}
