package hkust.edu.visualneo.utils.backend;

import hkust.edu.visualneo.utils.frontend.Vertex;
import org.neo4j.driver.Value;

import java.util.*;
import java.util.stream.Collectors;

public class Node extends Entity {

    final Set<Relation> relations = new TreeSet<>();

    public Node(long id, Vertex vertex) {
        this(id, vertex.getLabel(), vertex.getProp());
    }

    public Node(long id, String label, Map<String, Value> properties) {
        super(id, label, properties);
    }

    public boolean hasRelation() {
        return !relations.isEmpty();
    }

    public int relationCount() {
        return relations.size();
    }

    public Set<Node> neighbors() {
        return relations
                .stream()
                .map(relation -> relation.other(this))
                .collect(Collectors.toCollection(TreeSet::new));
    }

    public Set<Relation> relationsWith(Node other) {
        if (other == null)
            return Collections.emptySet();

        return relations
                .stream()
                .filter(relation -> other.equals(relation.other(this)))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    void attach(Relation relation) {
        relations.add(relation);
    }

    //    // Two distinct node are indistinguishable in the neighborhood of this node
    //    // iff they and the relations between them and this node can match the same node
    //    ArrayList<Pair<Node>> getDuplicateNeighborPairs() {
    //        HashSet<Node> dups = new HashSet<>();
    //        ArrayList<ArrayList<Node>> dupSets = new ArrayList<>();
    //        for (int i = 0; i < relations.size(); ++i) {
    //            Relation outerRelation = relations.get(i);
    //            Node outerNode = outerRelation.other(this);
    //            if (dups.contains(outerNode))
    //                continue;
    //            ArrayList<Node> dupSet = new ArrayList<>();
    //            dupSet.add(outerNode);
    //            for (int j = i + 1; j < relations.size(); ++ j) {
    //                Relation innerRelation = relations.get(j);
    //                Node innerNode = innerRelation.other(this);
    //                if (!outerRelation.resembles(innerRelation))
    //                    continue;
    //                if (dups.contains(innerNode) || !outerNode.resembles(innerNode))
    //                    continue;
    //                dups.add(innerNode);
    //                dupSet.add(innerNode);
    //            }
    //            dupSets.add(dupSet);
    //        }
    //
    //        ArrayList<Pair<Node>> dupPairs = new ArrayList<>();
    //        dupSets.forEach(dupSet -> {
    //            for (int i = 0; i < dupSet.size(); ++i)
    //                for (int j = i + 1; j < dupSet.size(); ++j)
    //                    dupPairs.add(Pair.ordered(dupSet.get(i), dupSet.get(j)));
    //        });
    //
    //        return dupPairs;
    //    }

    // Check whether two distinct nodes can match the same node
    // This method assumes the other node is non-null
    @Override
    boolean resembles(Entity other) {
        if (!(other instanceof Node))
            return false;

        if (this == other)
            return false;
        
        return super.resembles(other);
    }

    @Override
    public String toString() {
        return 'n' + super.toString();
    }

    @Override
    public Map<Object, Object> expand() {
        Map<Object, Object> expansion = super.expand();
        expansion.put("Relations", relations.stream().map(Relation::toString).toList());
        return expansion;
    }
}
