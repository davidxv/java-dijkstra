package org.neo4j.examples.dijkstra;

import org.neo4j.graphalgo.CostEvaluator;
import org.neo4j.graphalgo.path.Dijkstra;
import org.neo4j.graphalgo.path.WeightedPath;
import org.neo4j.graphalgo.util.DoubleEvaluator;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipExpander;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.index.IndexService;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.kernel.TraversalFactory;
import org.neo4j.util.GraphDatabaseLifecycle;

public class DijkstraService
{
    private enum MyDijkstraTypes implements RelationshipType
    {
        REL
    }

    public static final String NAME = "name";
    private static final String COST = "cost";
    private final EmbeddedGraphDatabase graphDb;
    private final IndexService index;
    private final GraphDatabaseLifecycle lifecycle;
    RelationshipExpander expander = TraversalFactory.expanderForTypes(
            MyDijkstraTypes.REL, Direction.BOTH );
    private final CostEvaluator<Double> costEvaluator = new DoubleEvaluator(
            COST );

    public DijkstraService()
    {
        graphDb = new EmbeddedGraphDatabase( "target/neo4j-db" );
        lifecycle = new GraphDatabaseLifecycle( graphDb );
        lifecycle.addLuceneIndexService();
        index = lifecycle.indexService();
    }

    public void shutdown()
    {
        lifecycle.manualShutdown();
    }

    public void makeEdge( final String firstNodeName,
            final String secondNodeName, final double cost )
    {
        Transaction tx = graphDb.beginTx();

        try
        {
            // find/create nodes
            Node firstNode = findOrCreateNode( firstNodeName );
            Node secondNode = findOrCreateNode( secondNodeName );

            // add relationship
            Relationship rel = firstNode.createRelationshipTo( secondNode,
                    MyDijkstraTypes.REL );
            rel.setProperty( COST, cost );

            tx.success();
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        finally
        {
            tx.finish();
        }
    }

    private Node findOrCreateNode( final String nodeName )
    {
        Node node = index.getSingleNode( NAME, nodeName );
        if ( node == null )
        {
            node = graphDb.createNode();
            node.setProperty( NAME, nodeName );
            index.index( node, NAME, node.getProperty( NAME ) );
        }
        return node;
    }

    public WeightedPath getDijkstra( final double startCost,
            final String startNodeName, final String endNodeName )
    {
        Node startNode = index.getSingleNode( NAME, startNodeName );
        Node endNode = index.getSingleNode( NAME, endNodeName );
        if ( startNode == null )
        {
            throw new IllegalArgumentException( "Start node not found." );
        }
        if ( endNode == null )
        {
            throw new IllegalArgumentException( "End node not found." );
        }
        Dijkstra dijkstra = new Dijkstra( expander, costEvaluator );
        return dijkstra.findSinglePath( startNode, endNode );
    }
}
