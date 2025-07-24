package _graphics;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.BiMap;

import _sim.GameUniverse;
import _sim.RelativeSide;
import _sim.plane.Plane;
import _sim.vectors.IVector;
import _utilities.couplets.Pair;
import _utilities.couplets.Triplet;
import _utilities.graph.IRelationGraph;
import metaphysics.soul.ISoul;
import metaphysics.spirit.ISpirit;
import processing.core.PApplet;
import processing.event.MouseEvent;
import things.actor.IActor;
import things.form.soma.component.IComponentPart;
import things.physics_and_chemistry.ForceResult;
import things.physics_and_chemistry.ForceType;
import thinker.concepts.IConcept;
import thinker.concepts.relations.descriptive.ProfileInterrelationType;
import thinker.concepts.relations.descriptive.PropertyRelationType;
import thinker.mind.perception.sensation.Sensation;

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
		System.out.println("Changing display to " + newDisplay + "[" + newScreen + "]");
		this.currentDisplay = newDisplay;
		this.currentScreen = newScreen;
		this.windowResize(2 * (world.getWidth() + 2 * BORDER), (world.getHeight() + 2 * BORDER));
	}

	public void returnToWorldDisplay() {
		System.out.println("Resetting display to WORLD");
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
				this.returnToWorldDisplay();
			}
			key = 0;
		}
		super.keyPressed();
	}

	/** Returns mouse position X within the world map */
	private float getWorldMouseX() {
		return (float) (mouseX - BORDER) / world.getMainMap().getBlockRenderSize();
	}

	/** Returns mouse position Y within the world map */
	private float getWorldMouseY() {
		return (float) (mouseY - BORDER) / world.getMainMap().getBlockRenderSize();
	}

	/** Returns mouse position vector within the world map */
	private IVector getWorldMouseVector() {
		return IVector.of(getWorldMouseX(), getWorldMouseY());
	}

	@Override
	public void keyPressed(processing.event.KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.VK_N) {
			this.changeDisplay(new GraphRenderer("Noosphere", this.world.getNoosphere().getUnmappedConceptGraphView(),
					IConcept.EXISTENCE, this.world.rand()), Display.KNOWLEDGE);
		} else if (event.getKeyCode() == KeyEvent.VK_X) {
			IActor ac = world.getMainMap().getActors().stream()
					.filter((a) -> IVector.distance(getWorldMouseVector(), a.getPosition()) <= 1)
					.filter((a) -> !a.getBody().getAllTetheredSpirits().isEmpty()).findAny().orElse(null);
			if (ac != null) {

				System.out.println("Killing " + ac);
				Set<ISpirit> set = new HashSet<>(ac.getBody().getAllTetheredSpirits());
				set.forEach((spir) -> ac.getBody().killSpirit(spir));

			}
		} else if (event.getKeyCode() == KeyEvent.VK_B) {
			IActor ac = world.getMainMap().getActors().stream()
					.filter((a) -> IVector.distance(getWorldMouseVector(), a.getPosition()) <= 1).findAny()
					.orElse(null);
			if (ac != null) {

				System.out.println("Body:" + ac.getBody().somaReport());

			}
		} else if (event.isAltDown() && Character.isDigit(event.getKey())) {
			System.out.println("Pressed alt+" + event.getKey());
			if (this.currentScreen instanceof GraphRenderer gr) {
				if (gr.getRenderableEdges().isEmpty()) {
					gr.setRenderableEdges(Set.of(ProfileInterrelationType.IS, PropertyRelationType.HAS_TRAIT,
							PropertyRelationType.VALUE_OF));
				} else {
					gr.setRenderableEdges(
							gr.getGraph().getEdgeTypes().stream().filter((e) -> !gr.getRenderableEdges().contains(e))
									.limit(Long.parseLong(event.getKey() + "")).collect(Collectors.toSet()));
				}
			}
		} else if (Character.isDigit(event.getKey())) {
			System.out.println("Pressed just " + event.getKey());
			if (this.currentScreen instanceof GraphRenderer graphreneo) {
				GraphRenderer graphren = graphreneo.fundCopy();
				Rectangle centerm = null;
				for (Rectangle r : graphreneo.getBoxMap().values()) {
					System.out.println("Comparing " + r + " to " + (mouseX - width()) + " ," + mouseY);
					if (r.contains(mouseX - width(), mouseY)) {
						centerm = r;
						break;
					}
				}
				if (centerm != null) {

					Object node = graphreneo.getBoxMap().inverse().get(centerm);
					Set allowed = (Set) ((IRelationGraph) graphren.getGraph()).traverseBFS(node, null, (a) -> {
					}, (a, b) -> true).<Object>stream()
							.limit(event.getKey() == '0' ? Long.MAX_VALUE : Long.parseLong("" + event.getKey()))
							.collect(Collectors.toSet());
					graphren.limitVisibleBoxes((p) -> allowed.contains(p));
					graphren.setCentral(node);
					((BiMap) graphren.getBoxMap()).put(node, centerm);
					this.currentScreen = graphren;
				}
			}
		} else if (event.getKeyCode() == KeyEvent.VK_G) {
			this.changeDisplay(new GraphRenderer("Parties", this.world.getPartyRelations(), null, this.world.rand()),
					Display.SOCIAL);
		} else if (event.getKeyCode() == KeyEvent.VK_K) {

			IActor ac = world.getMainMap().getActors().stream()
					.filter((a) -> IVector.distance(getWorldMouseVector(), a.getPosition()) <= 1)
					.filter((a) -> !a.getBody().getAllTetheredSpirits().isEmpty()).findAny().orElse(null);
			if (ac != null) {
				ISpirit spir = ac.getBody().getAllTetheredSpirits().stream().findAny().get();
				System.out.println(spir.report());
				System.out.println("BodyHealth:" + ac.getBody().getPartGraph().stream()
						.map((x) -> Pair.of("part", x, "health", spir.determineHealthOfPart(x, ac.getBody())))
						.collect(Collectors.toSet()));
				this.changeDisplay(
						new GraphRenderer("", spir.getKnowledgeGraph(), spir.getProfile(), this.world.rand())
								.setEraseGraphCondition(spir::isRemoved)
								.setTitleUpdater((title) -> "Knowledge (" + ac.getProfile() + ")"
										+ (ac.getBody().getAllTetheredSpirits().contains(spir) ? "" : " (untethered)")),
						Display.KNOWLEDGE);

			}

		} else if (event.getKeyCode() == KeyEvent.VK_T) {

			IActor ac = world.getMainMap().getActors().stream()
					.filter((a) -> IVector.distance(getWorldMouseVector(), a.getPosition()) <= 1)
					.filter((a) -> !a.getBody().getAllTetheredSpirits().isEmpty()).findAny().orElse(null);
			if (ac != null) {
				ISoul spir = ac.getBody().getAllTetheredSpirits().stream().filter((a) -> a instanceof ISoul)
						.map((a) -> (ISoul) a).findAny().orElse(null);
				if (spir != null) {

					System.out.println(spir.report());
					System.out.println("BodyHealth:" + ac.getBody().getPartGraph().stream()
							.map((x) -> Triplet.of("part", x, "health", spir.determineHealthOfPart(x, ac.getBody()),
									"painsense", x.getStat(Sensation.PAIN.getSensitivityStat())))
							.collect(Collectors.toSet()));
					this.changeDisplay(
							new ThoughtsRenderer("", spir.getWill(),
									this.world.rand())
											.setEraseGraphCondition(
													spir::isRemoved)
											.setTitleUpdater((title) -> "Thoughts (" + ac.getProfile() + ")"
													+ (ac.getBody().getAllTetheredSpirits().contains(spir) ? ""
															: " (untethered)")),
							Display.THOUGHTS);
				}

			}

		} else if (event.getKeyCode() == KeyEvent.VK_E) {

			IActor ac = world.getMainMap().getActors().stream()
					.filter((a) -> IVector.distance(getWorldMouseVector(), a.getPosition()) <= 1)
					.filter((a) -> !a.getBody().getAllTetheredSpirits().isEmpty()).findAny().orElse(null);
			if (ac != null) {
				ISoul spir = ac.getBody().getAllTetheredSpirits().stream().filter((a) -> a instanceof ISoul)
						.map((a) -> (ISoul) a).findAny().orElse(null);
				if (spir != null) {

					System.out.println(spir.report());
					System.out.println("BodyHealth:" + ac.getBody().getPartGraph().stream()
							.map((x) -> Triplet.of("part", x, "health", spir.determineHealthOfPart(x, ac.getBody()),
									"painsense", x.getStat(Sensation.PAIN.getSensitivityStat())))
							.collect(Collectors.toSet()));
					this.changeDisplay(
							new MindListInterfaceRenderer("",
									spir.getPerception())
											.setEraseGraphCondition(
													spir::isRemoved)
											.setTitleUpdater((title) -> "Perception (" + ac.getProfile() + ")"
													+ (ac.getBody().getAllTetheredSpirits().contains(spir) ? ""
															: " (untethered)")),
							Display.PERCEPTION);
				}

			}

		} else if (event.getKeyCode() == KeyEvent.VK_P) {

			IActor ac = world.getMainMap().getActors().stream()
					.filter((a) -> IVector.distance(getWorldMouseVector(), a.getPosition()) <= 1)
					.filter((a) -> !a.getBody().getAllTetheredSpirits().isEmpty()).findAny().orElse(null);
			if (ac != null) {
				ISoul spir = ac.getBody().getAllTetheredSpirits().stream().filter((a) -> a instanceof ISoul)
						.map((a) -> (ISoul) a).findAny().orElse(null);
				if (spir != null) {

					System.out.println(spir.report());
					System.out.println("BodyHealth:" + ac.getBody().getPartGraph().stream()
							.map((x) -> Triplet.of("part", x, "health", spir.determineHealthOfPart(x, ac.getBody()),
									"painsense", x.getStat(Sensation.PAIN.getSensitivityStat())))
							.collect(Collectors.toSet()));
					this.changeDisplay(
							new MindListInterfaceRenderer("",
									spir.getPersonality())
											.setEraseGraphCondition(
													spir::isRemoved)
											.setTitleUpdater((title) -> "Personality (" + ac.getProfile() + ")"
													+ (ac.getBody().getAllTetheredSpirits().contains(spir) ? ""
															: " (untethered)")),
							Display.PERSONALITY);
				}

			}

		} else if (event.getKeyCode() == KeyEvent.VK_F) {

			IActor ac = world.getMainMap().getActors().stream()
					.filter((a) -> IVector.distance(getWorldMouseVector(), a.getPosition()) <= 1)
					.filter((a) -> !a.getBody().getAllTetheredSpirits().isEmpty()).findAny().orElse(null);
			if (ac != null) {
				ISoul spir = ac.getBody().getAllTetheredSpirits().stream().filter((a) -> a instanceof ISoul)
						.map((a) -> (ISoul) a).findAny().orElse(null);
				if (spir != null) {

					System.out.println(spir.report());
					System.out.println("BodyHealth:" + ac.getBody().getPartGraph().stream()
							.map((x) -> Triplet.of("part", x, "health", spir.determineHealthOfPart(x, ac.getBody()),
									"painsense", x.getStat(Sensation.PAIN.getSensitivityStat())))
							.collect(Collectors.toSet()));
					this.changeDisplay(
							new MindListInterfaceRenderer("",
									spir.getKnowledge())
											.setEraseGraphCondition(
													spir::isRemoved)
											.setTitleUpdater((title) -> "Feelings (" + ac.getProfile() + ")"
													+ (ac.getBody().getAllTetheredSpirits().contains(spir) ? ""
															: " (untethered)")),
							Display.PERSONALITY);
				}

			}

		}

		super.keyPressed(event);
	}

	private long mpressStart = 0;
	private IVector mStartPos = null;
	private IVector mStartWorldPos = null;

	@Override
	public void mousePressed() {
		if (mpressStart == 0) {
			mpressStart = System.currentTimeMillis();
		}
		if (mStartWorldPos == null) {
			mStartPos = IVector.of(mouseX, mouseY);
			mStartWorldPos = getWorldMouseVector();
		} else {
		}
		super.mousePressed();
	}

	@Override
	public void mouseReleased() {
		IVector aMouseVec = getWorldMouseVector();
		IVector sMouseVec = mStartWorldPos;
		if (mStartWorldPos != null) {
			IVector genForce = aMouseVec.add(sMouseVec.invert());
			System.out.println("F: " + genForce);
			long timespan = System.currentTimeMillis() - mpressStart;
			mpressStart = 0;
			mStartWorldPos = null;
			mStartPos = null;
			float fortz = timespan / 1000f * 50;
			Set<IActor> acs = world.getMainMap().getActors().stream()
					.filter((a) -> IVector.distance(aMouseVec, a.getPosition()) <= 1).collect(Collectors.toSet());
			for (IActor actor : acs) {
				world.getMainMap().queueAction(() -> {
					List<IComponentPart> abpSet = actor.getBody().getContiguousParts().stream()
							.limit((int) Math.ceil(timespan / 1000f)).collect(Collectors.toList());

					System.out.println("FORCES:" + fortz + "N, " + actor.getKind() + "->" + abpSet);
					System.out.println("RESULTS:" + abpSet.stream().map((abp) -> {
						if (!actor.getBody().getPartGraph().contains(abp)) {
							return ForceResult.INVALID;
						}
						System.out.println("\t>Apply to " + abp.componentReport());
						ForceResult res = actor.applyForce(abp, null, genForce.withMagnitude(fortz), ForceType.BLUNT,
								RelativeSide.LEFT, Plane.PHYSICAL.getPrime());
						return res;
					}).collect(Collectors.toList()));
					;
				});
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent event) {
		super.mouseClicked(event);

	}

	public static enum Display {
		/** standard world display */
		WORLD,
		/** display of a graph of thoughts */
		THOUGHTS,
		/** display of a graph of knowledge/memories */
		KNOWLEDGE,
		/** display of party and group relations */
		SOCIAL,
		/** display of a single entity's body represetnation */
		BODY,
		/** display of a list view of perception */
		PERCEPTION,
		/** a list view of personality traits */
		PERSONALITY,
		/** a list view of feelings */
		FEELINGS
	}

	@Override
	public void draw() {
		background(color(0, 100, 100));
		g.push();
		g.translate(BORDER, BORDER);
		world.tickWorlds(this);
		g.pop();
		if (this.currentScreen != null) {
			g.pushStyle();
			g.pushMatrix();
			g.translate(width(), 0);
			this.currentScreen.draw(this);
			g.popStyle();
			g.popMatrix();
		}
		g.pushStyle();
		fill(Color.yellow.getRGB());
		strokeWeight(5f);
		if (mStartPos != null)
			line((float) mStartPos.getUnadjustedX(), (float) mStartPos.getUnadjustedY(), mouseX, mouseY);
		g.popStyle();
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
