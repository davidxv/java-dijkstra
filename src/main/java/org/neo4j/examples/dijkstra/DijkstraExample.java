package org.neo4j.examples.dijkstra;

import org.neo4j.graphalgo.PathFinder;
import org.neo4j.graphalgo.path.Dijkstra;
import org.neo4j.graphalgo.path.WeightedPath;
import org.neo4j.graphalgo.util.DoubleEvaluator;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipExpander;
import org.neo4j.kernel.TraversalFactory;

public class DijkstraExample
{
    private final ExampleGraphService graph;

    private static final String COST = "cost";
    private static final RelationshipExpander expander;
    private static final DoubleEvaluator costEvaluator;
    private static final PathFinder<WeightedPath> dijkstraPathFinder;

    static
    {
        expander = TraversalFactory.expanderForTypes(
                ExampleGraphService.MyDijkstraTypes.REL, Direction.BOTH );
        costEvaluator = new DoubleEvaluator( COST );
        dijkstraPathFinder = new Dijkstra( expander, costEvaluator );
    }

    public DijkstraExample()
    {
        graph = new ExampleGraphService();
    }

    private void createGraph()
    {
        graph.makeEdge( "s", "c", COST, 7 );
        graph.makeEdge( "c", "e", COST, 7 );
        graph.makeEdge( "s", "a", COST, 2 );
        graph.makeEdge( "a", "b", COST, 7 );
        graph.makeEdge( "b", "e", COST, 2 );
    }

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

    private void shutdown()
    {
        graph.shutdown();
    }

    public static void main( final String[] args )
    {
        DijkstraExample de = new DijkstraExample();
        de.createGraph();
        de.runDijkstraPathFinder();
        de.shutdown();
    }
}
