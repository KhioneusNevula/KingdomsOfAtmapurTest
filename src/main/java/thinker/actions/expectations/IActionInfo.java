package thinker.actions.expectations;

import java.util.Optional;

import party.util.IGroupAccess;
import party.util.IAgentAccess;
import thinker.concepts.general_types.IActionPatternConcept;
import thinker.knowledge.IKnowledgeRepresentation;
import thinker.mind.util.IBeingAccess;

/**
 * Info given to an action for execution
 * 
 * @author borah
 *
 */
public interface IActionInfo {

	/**
	 * The pattern used for this action, optionally
	 * 
	 * @return
	 */
	public Optional<IActionPatternConcept> maybePattern();

	/**
	 * An <em>expectation</em> to be satisfied; i.e. one that has only event-based
	 * relations in it. May be null if this action is being checked in some context
	 * where it is not relevant info?
	 */
	public Optional<IKnowledgeRepresentation> maybeIntention();

	/** What {@link IAgentAccess} this action has access to */
	public IAgentAccess accessInfo();

	/**
	 * Return {@link #accessInfo()} as a {@link IBeingAccess} if it is; othrwise
	 * nothing
	 * 
	 * @return
	 */
	public default Optional<IBeingAccess> maybeBeingInfo() {
		return Optional.of(accessInfo()).filter(IBeingAccess.class::isInstance).map(IBeingAccess.class::cast);
	}

	/**
	 * Return {@link #accessInfo()} as a {@link IGroupAccess} if it is;
	 * othrwise nothing
	 * 
	 * @return
	 */
	public default Optional<IGroupAccess> maybeCollectiveInfo() {
		return Optional.of(accessInfo()).filter(IGroupAccess.class::isInstance).map(IGroupAccess.class::cast);
	}

	/**
	 * Create one of these using an {@link IAgentAccess} to represent the actor
	 * 
	 * @param par
	 * @param re
	 * @param pat
	 * @return
	 */
	public static IActionInfo create(IAgentAccess par) {
		return new ActionInfoImpl(par, null, null);
	}

	/**
	 * Create one of these using an {@link IAgentAccess} to represent the actor and
	 * a {@link IActionPatternConcept} to idnicate what variant of the action is
	 * being performed
	 * 
	 * @param par
	 * @param re
	 * @param pat
	 * @return
	 */
	public static IActionInfo create(IAgentAccess par, IActionPatternConcept pat) {
		return new ActionInfoImpl(par, null, pat);
	}

	/**
	 * Create one of these using an {@link IAgentAccess} for the actor, a
	 * {@link IKnowledgeRepresentation} to represent the intention the action is
	 * directed to, and a {@link IActionPatternConcept} for what variant of the
	 * action is performed
	 * 
	 * @param par
	 * @param re
	 * @param pat
	 * @return
	 */
	public static IActionInfo create(IAgentAccess par, IKnowledgeRepresentation re, IActionPatternConcept pat) {
		return new ActionInfoImpl(par, re, pat);
	}

	/**
	 * Create one of these using an {@link IAgentAccess} for the actor, and a
	 * {@link IKnowledgeRepresentation} for the intention of the action
	 * 
	 * @param par
	 * @param re
	 * @param pat
	 * @return
	 */
	public static IActionInfo create(IAgentAccess par, IKnowledgeRepresentation re) {
		return new ActionInfoImpl(par, re, null);
	}

	class ActionInfoImpl implements IActionInfo {

		private IAgentAccess pa;
		private IKnowledgeRepresentation re;
		private IActionPatternConcept pat;

		private ActionInfoImpl(IAgentAccess par, IKnowledgeRepresentation re, IActionPatternConcept pat) {
			this.pa = par;
			this.re = re;
			this.pat = pat;
		}

		@Override
		public Optional<IKnowledgeRepresentation> maybeIntention() {
			return Optional.ofNullable(re);
		}

		@Override
		public IAgentAccess accessInfo() {
			return pa;
		}

		@Override
		public Optional<IActionPatternConcept> maybePattern() {
			return Optional.ofNullable(pat);
		}

	}
}
