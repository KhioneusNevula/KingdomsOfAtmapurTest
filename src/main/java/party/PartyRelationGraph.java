package party;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Streams;

import _utilities.collections.ImmutableCollection;
import _utilities.graph.RelationGraph;
import _utilities.property.IProperty;
import party.collective.ICollective;
import party.relations.types.IPartyRelationType;
import thinker.concepts.IConcept;
import thinker.knowledge.base.individual.IIndividualKnowledgeBase;

/**
 * A relation graph which encodes relations between parties and sometimes
 * changes info (e.g. parent lists) of parties which experience a change in
 * their info content
 * 
 * @author borah
 *
 */
public class PartyRelationGraph extends RelationGraph<IParty, IPartyRelationType> {

	public static final IProperty<Set<IConcept>> RESOURCES = IProperty.make("resources", Set.class, new HashSet<>());
	private Map<UUID, ICollective> collectives;
	private Map<UUID, IParty> allParties;

	public PartyRelationGraph() {
		super(ImmutableSet.of(RESOURCES));
		this.collectives = new HashMap<>();
		this.allParties = new HashMap<>();
	}

	/**
	 * Returns the resources sent from this party to the other
	 * 
	 * @param party
	 * @param relation
	 * @param party2
	 * @param writable set this to true if you want to be able to modify the set
	 *                 that is returned
	 * @return
	 */
	public Collection<IConcept> getSentResources(IParty party, IPartyRelationType relation, IParty party2,
			boolean writable) {
		if (writable) {
			return this.getProperty(party, relation, party2, RESOURCES, true);
		} else {
			return ImmutableCollection.from(this.getProperty(party, relation, party2, RESOURCES, false));
		}
	}

	public Collection<ICollective> getCollectives() {
		return collectives.values();
	}

	/**
	 * Get a Collective by UUID
	 * 
	 * @param byID
	 * @return
	 */
	public ICollective getCollective(UUID byID) {
		return collectives.get(byID);
	}

	/**
	 * Get a party by UUID
	 * 
	 * @param byID
	 * @return
	 */
	public IParty getParty(UUID byID) {
		return allParties.get(byID);
	}

	/**
	 * Return whether a specific UUID has a party associated with it
	 * 
	 * @param forID
	 * @return
	 */
	public boolean partyExists(UUID forID) {
		return allParties.containsKey(forID);
	}

	@Override
	public boolean add(IParty e) {
		if (e instanceof ICollective) {
			this.collectives.put(e.getUUID(), e.asCollective());
		} else {
			this.allParties.put(e.getUUID(), e);
		}
		return super.add(e);
	}

	@Override
	public boolean addAll(Collection<? extends IParty> c) {
		c.stream().filter((ca) -> ca instanceof ICollective)
				.forEach((pa) -> collectives.put(pa.getUUID(), pa.asCollective()));
		c.forEach((x) -> allParties.put(x.getUUID(), x));
		return super.addAll(c);
	}

	@Override
	public void removeBareNodes() {
		for (IParty next : this.getBareNodes()) {
			if (next instanceof ICollective) {
				collectives.remove(next.getUUID());
			}
			allParties.remove(next.getUUID());
		}

		super.removeBareNodes();
	}

	@Override
	public boolean remove(Object o) {
		if (o instanceof ICollective ic) {
			this.collectives.remove(ic.getUUID(), ic);
		}
		if (o instanceof IParty ip) {
			this.allParties.remove(ip.getUUID(), ip);
		}
		return super.remove(o);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		c.stream().filter((p) -> p instanceof ICollective).map(IParty.class::cast)
				.forEach((pa) -> collectives.remove(pa.getUUID(), pa.asCollective()));

		c.stream().filter((p) -> p instanceof IParty).map(IParty.class::cast)
				.forEach((x) -> allParties.remove(x.getUUID(), x));
		return super.removeAll(c);
	}

	@Override
	public void set(Object node, IParty newNode) {
		if (node instanceof ICollective c) {
			this.collectives.remove(c.getUUID(), c);
		}
		if (node instanceof IParty p) {
			this.allParties.remove(p.getUUID(), p);
		}
		if (newNode instanceof ICollective) {
			this.collectives.put(newNode.getUUID(), newNode.asCollective());
		}
		if (newNode instanceof IParty) {
			this.allParties.put(newNode.getUUID(), newNode);
		}
		super.set(node, newNode);
	}

	protected void removeParent(IParty forP, IParty par) {
		if (forP.getKnowledge() instanceof IIndividualKnowledgeBase ikb)
			ikb.removeParents(Collections.singleton(par.getKnowledge()));
	}

	@Override
	protected IInvertibleEdge<IParty, IPartyRelationType> removeConnection(INode<IParty, IPartyRelationType> node,
			IPartyRelationType type, INode<IParty, IPartyRelationType> other) {
		IInvertibleEdge<IParty, IPartyRelationType> out = super.removeConnection(node, type, other);
		if (type.isKnowledgeParent())
			this.removeParent(out.getEnd().getValue(), out.getStart().getValue());
		else if (type.hasKnowledgeParent())
			this.removeParent(out.getStart().getValue(), out.getEnd().getValue());
		return out;
	}

	@Override
	protected void disconnectEnds(Iterable<? extends IInvertibleEdge<IParty, IPartyRelationType>> edges) {

		super.disconnectEnds(edges);
		Streams.stream(edges).filter((edge) -> edge.getType().isKnowledgeParent())
				.forEach((edge) -> removeParent(edge.getEnd().getValue(), edge.getStart().getValue()));
		Streams.stream(edges).filter((edge) -> edge.getType().hasKnowledgeParent())
				.forEach((edge) -> removeParent(edge.getStart().getValue(), edge.getEnd().getValue()));
	}

}
