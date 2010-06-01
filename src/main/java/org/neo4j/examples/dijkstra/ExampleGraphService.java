package org.neo4j.examples.dijkstra;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.index.IndexService;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.util.GraphDatabaseLifecycle;

/**
 * Wrapper around Neo4j every node has a name. Nodes are created lazily when
 * relationships are created.
 * 
 * @author Anders Nawroth
 */
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

    /**
     * Create new or open existing DB.
     * 
     * @param storeDir location of DB files
     */
    public ExampleGraphService( final String storeDir )
    {
        graphDb = new EmbeddedGraphDatabase( storeDir );
        lifecycle = new GraphDatabaseLifecycle( graphDb );
        lifecycle.addLuceneIndexService();
        index = lifecycle.indexService();
    }

    /**
     * Create relationship between two nodes and set a property on the
     * relationship. Note that the propertyValue has to be a Java primitive or
     * String or an array of either Java primitives or Strings.
     * 
     * @param fromNodeName start node
     * @param toNodeName end node
     * @param propertyName
     * @param propertyValue
     */
    public void createRelationship( final String fromNodeName,
            final String toNodeName, final String propertyName,
            final Object propertyValue )
    {
        Transaction tx = graphDb.beginTx();

        try
        {
            // find/create nodes
            Node firstNode = findOrCreateNode( fromNodeName );
            Node secondNode = findOrCreateNode( toNodeName );

            // add relationship
            Relationship rel = firstNode.createRelationshipTo( secondNode,
                    MyDijkstraTypes.REL );
            rel.setProperty( propertyName, propertyValue );

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

    /**
     * Get a node by its name.
     * 
     * @param name
     * @return
     */
    public Node getNode( final String name )
    {
        return index.getSingleNode( NAME, name );
    }

    /**
     * Find a node or create a new node if it doesn't exist.
     * 
     * @param nodeName
     * @return
     */
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

    /**
     * Shutdown service.
     */
    public void shutdown()
    {
        lifecycle.manualShutdown();
    }
}
