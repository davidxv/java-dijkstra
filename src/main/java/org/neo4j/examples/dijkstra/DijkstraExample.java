package org.neo4j.examples.dijkstra;

import org.neo4j.graphalgo.CommonEvaluators;
import org.neo4j.graphalgo.CostEvaluator;
import org.neo4j.graphalgo.GraphAlgoFactory;
import org.neo4j.graphalgo.PathFinder;
import org.neo4j.graphalgo.WeightedPath;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipExpander;
import org.neo4j.kernel.Traversal;

/**
 * Simple example of how to find the cheapest path between two nodes in a graph.
 * 
 * @author Anders Nawroth
 */
public class DijkstraExample
{
    private final ExampleGraphService graph;

    private static final String COST = "cost";
    private static final RelationshipExpander expander;
    private static final CostEvaluator<Double> costEvaluator;
    private static final PathFinder<WeightedPath> dijkstraPathFinder;

    static
    {
        // set up path finder
        expander = Traversal.expanderForTypes(
                ExampleGraphService.MyDijkstraTypes.REL, Direction.BOTH );
        costEvaluator = CommonEvaluators.doubleCostEvaluator( COST );
        dijkstraPathFinder = GraphAlgoFactory.dijkstra( expander, costEvaluator );
    }

    public DijkstraExample()
    {
        graph = new ExampleGraphService( "target/neo4j-db" );
    }

    /**
     * Create our example graph.
     */
    private void createGraph()
    {
        graph.createRelationship( "s", "c", COST, 7d );
        graph.createRelationship( "c", "e", COST, 7d );
        graph.createRelationship( "s", "a", COST, 2d );
        graph.createRelationship( "a", "b", COST, 7d );
        graph.createRelationship( "b", "e", COST, 2d );
    }

    /**
     * Find the path.
     */
    private void runDijkstraPathFinder()
    {
        Node start = graph.getNode( "s" );
        Node end = graph.getNode( "e" );
        WeightedPath path = dijkstraPathFinder.findSinglePath( start, end );
        for ( Node node : path.nodes() )
        {
            System.out.println( node.getProperty( ExampleGraphService.NAME ) );
        }
    }

    /**
     * Shutdown the graphdb.
     */
    private void shutdown()
    {
        graph.shutdown();
    }

    /**
     * Execute the example.
     * 
     * @param args
     */
    public static void main( final String[] args )
    {
        DijkstraExample de = new DijkstraExample();
        de.createGraph();
        de.runDijkstraPathFinder();
        de.shutdown();
    }
}
