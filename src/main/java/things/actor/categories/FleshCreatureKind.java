package things.actor.categories;

import java.awt.Color;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;
import java.util.function.BiFunction;

import _sim.GameUniverse;
import _utilities.collections.ImmutableCollection;
import _utilities.graph.IModifiableRelationGraph;
import metaphysics.spirit.ISpirit;
import party.kind.IKindCollective;
import party.kind.SentientKindCollective;
import party.kind.spawning.IKindSpawningContext;
import things.biology.kinds.OrganicKindProperties;
import things.form.channelsystems.IChannelSystem;
import things.form.channelsystems.blood.BloodChannelSystem;
import things.form.channelsystems.energy.EnergyChannelSystem;
import things.form.channelsystems.signal.SignalChannelSystem;
import things.form.graph.connections.IPartConnection;
import things.form.kinds.multipart.MultipartKind;
import things.form.kinds.settings.IKindSettings;
import things.form.material.Material;
import things.form.material.generator.IMaterialGeneratorResource;
import things.form.material.property.MaterialProperty;
import things.form.soma.ISoma;
import things.form.soma.component.IComponentPart;
import things.interfaces.UniqueType;
import thinker.actions.IActionConcept;
import thinker.concepts.profile.IProfile;
import thinker.concepts.profile.Profile;
import thinker.concepts.relations.descriptive.PropertyRelationType;
import thinker.concepts.relations.descriptive.ProfileInterrelationType;
import thinker.knowledge.base.IKnowledgeBase;
import thinker.knowledge.base.section.SectionKnowledgeBase;

/**
 * All creatures of this nature have an intangible body part called a life_core
 * which is responsible for converting stuff into life energy
 * 
 * @author borah
 *
 */
public abstract class FleshCreatureKind extends MultipartKind {

	public static final String LIFE_CORE_NAME = "lifeCore";
	public static final String BODY_NAME = "body";
	public static final String LIFE_SYSTEM_NAME = "life";
	public static final String SIGNAL_SYSTEM_NAME = "nerve";
	public static final String SIGNAL_SYSTEM_BRAIN_NAME = "brain";
	public static final String BLOOD_SYSTEM_NAME = "blood";
	public static final String BLOOD_SYSTEM_HEART_NAME = "heart";

	public static final IMaterialGeneratorResource NERVE_TISSUE_GEN = Material.GENERIC_TISSUE.buildCopy("nerve_tissue")
			.prop(MaterialProperty.COLOR, Color.pink).geneticGenerator().buildGenerator();
	public static final IMaterialGeneratorResource BLOOD_VESSEL_TISSUE_GEN = Material.GENERIC_TISSUE
			.buildCopy("blood_vessel_tissue").prop(MaterialProperty.COLOR, Color.yellow).geneticGenerator()
			.buildGenerator();
	public static final IMaterialGeneratorResource COLORED_BLOOD_GEN = Material.BLOOD.buildCopy("blood")
			.geneticGenerator().addMapping(MaterialProperty.COLOR, OrganicKindProperties.BLOOD_COLOR).buildGenerator();

	public static final EnergyChannelSystem LIFE_ENERGY_SYSTEM = new EnergyChannelSystem(LIFE_SYSTEM_NAME,
			LIFE_CORE_NAME, 1f);
	public static final BloodChannelSystem BLOOD_SYSTEM = new BloodChannelSystem(BLOOD_SYSTEM_NAME, COLORED_BLOOD_GEN,
			BLOOD_VESSEL_TISSUE_GEN, BLOOD_SYSTEM_HEART_NAME, LIFE_CORE_NAME);

	private SignalChannelSystem signalSys;
	private Collection<IActionConcept> doableActions;
	private IKindSpawningContext spawnContext = IKindSpawningContext.NEVER_SPAWN;

	/**
	 * 
	 * @param name           name of this creature kind
	 * @param averageMass    average mass of this creature kind
	 * @param averageSize    average size of this creature kind
	 * @param spiritGen      a function that generates a spirit for this creature's
	 *                       nervous system, or null if not relevant
	 * @param channelSystems
	 */
	public FleshCreatureKind(String name, float averageMass, float averageSize,
			BiFunction<ISoma, IComponentPart, ISpirit> spiritGen, IChannelSystem... channelSystems) {
		super(name, averageMass, averageSize, LIFE_ENERGY_SYSTEM, BLOOD_SYSTEM);
		this.signalSys = new SignalChannelSystem(SIGNAL_SYSTEM_NAME, NERVE_TISSUE_GEN, SIGNAL_SYSTEM_BRAIN_NAME,
				spiritGen);
		this.sys(signalSys);
		this.sys(channelSystems);
		this.doableActions = new HashSet<>();
	}

	public FleshCreatureKind setSpawnContext(IKindSpawningContext spawnContext) {
		this.spawnContext = spawnContext;
		return this;
	}

	/** Return actions members of this kind can do by default */
	public Collection<IActionConcept> getDoableActions() {
		return ImmutableCollection.from(doableActions);
	}

	public FleshCreatureKind addDoableActions(Iterable<IActionConcept> actos) {
		for (IActionConcept act : actos) {
			this.doableActions.add(act);
		}
		return this;
	}

	@Override
	protected IComponentPart identifyCenter(IModifiableRelationGraph<IComponentPart, IPartConnection> graph,
			IKindSettings settings) {
		return graph.stream().filter((a) -> a.getName().equals(BODY_NAME)).findFirst().orElseThrow();
	}

	protected void fillOutKnowledge(IKindCollective collective, GameUniverse universe) {
		IKnowledgeBase knowledge = collective.getKnowledge();
		knowledge.learnConcept(collective.getProfile());
		knowledge.learnConcept(collective.getFormTypeProfile());
		collective.maybeGetFigureTypeProfile().ifPresentOrElse((figure) -> {
			knowledge.learnConcept(figure);
			knowledge.addConfidentRelation(collective.getProfile(), ProfileInterrelationType.HAS_MEMBER, figure);
			knowledge.addConfidentRelation(figure, ProfileInterrelationType.HAS_BODY,
					collective.getFormTypeProfile());
		}, () -> {
			knowledge.addConfidentRelation(collective.getProfile(), ProfileInterrelationType.HAS_BODY,
					collective.getFormTypeProfile());
		});
		this.doableActions.forEach((action) -> {
			knowledge.learnConcept(action);
			knowledge.addConfidentRelation(collective.getProfile(), PropertyRelationType.HAS_ABILITY_TO, action);
			IActionConcept.incorporateIntoKnowledgeBase(action, knowledge);
		});

	}

	@Override
	public IKindCollective generateCollective(GameUniverse forUniverse) {
		UUID cid = UUID.randomUUID();
		IProfile cprof = new Profile(cid, UniqueType.COLLECTIVE).setIdentifierName(this.name());
		SectionKnowledgeBase know = new SectionKnowledgeBase(cprof, forUniverse.getNoosphere());
		SentientKindCollective collective = new SentientKindCollective(cprof, this, spawnContext, know);
		this.fillOutKnowledge(collective, forUniverse);
		return collective;
	}

}
