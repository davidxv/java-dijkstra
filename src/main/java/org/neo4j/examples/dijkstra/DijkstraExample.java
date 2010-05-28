package org.neo4j.examples.dijkstra;

import org.neo4j.graphalgo.path.WeightedPath;
import org.neo4j.graphdb.Node;

public class DijkstraExample
{
    public static void main( final String[] args )
    {
        DijkstraService graph = new DijkstraService();

        graph.makeEdge( "s", "c", 7 );
        graph.makeEdge( "c", "e", 7 );
        graph.makeEdge( "s", "a", 2 );
        graph.makeEdge( "a", "b", 7 );
        graph.makeEdge( "b", "e", 2 );

        WeightedPath path = graph.getDijkstra( 0.0, "s", "e" );
        for ( Node node : path.nodes() )
        {
            System.out.println( node.getProperty( DijkstraService.NAME ) );
        }

        graph.shutdown();
    }
}
