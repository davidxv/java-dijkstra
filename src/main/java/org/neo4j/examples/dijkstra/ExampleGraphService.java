package org.neo4j.examples.dijkstra;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.index.IndexService;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.util.GraphDatabaseLifecycle;

public class ExampleGraphService
{
    public enum MyDijkstraTypes implements RelationshipType
    {
        REL
    }

    public static final String NAME = "name";

    private final EmbeddedGraphDatabase graphDb;
    private final GraphDatabaseLifecycle lifecycle;
    private final IndexService index;

    public ExampleGraphService()
    {
        graphDb = new EmbeddedGraphDatabase( "target/neo4j-db" );
        lifecycle = new GraphDatabaseLifecycle( graphDb );
        lifecycle.addLuceneIndexService();
        index = lifecycle.indexService();
    }

    public void makeEdge( final String firstNodeName,
            final String secondNodeName, final String propertyName,
            final double cost )
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
            rel.setProperty( propertyName, cost );

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

    public Node getNode( final String name )
    {
        return index.getSingleNode( NAME, name );
    }

    private Node findOrCreateNode( final String nodeName )
    {
        Node node = getNode( nodeName );
        if ( node == null )
        {
            node = graphDb.createNode();
            node.setProperty( NAME, nodeName );
            index.index( node, NAME, node.getProperty( NAME ) );
        }
        return node;
    }

    public void shutdown()
    {
        lifecycle.manualShutdown();
    }
}
