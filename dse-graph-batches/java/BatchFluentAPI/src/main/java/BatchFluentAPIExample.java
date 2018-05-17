import com.datastax.driver.dse.DseCluster;
import com.datastax.driver.dse.DseSession;
import com.datastax.dse.graph.api.DseGraph;
import com.datastax.driver.dse.graph.GraphResultSet;
import com.datastax.dse.graph.api.TraversalBatch;

import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.addE;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.addV;

import com.google.common.collect.ImmutableMap;
import org.apache.tinkerpop.gremlin.structure.util.detached.DetachedVertex;

public class BatchFluentAPIExample {

    public static void main(String[] args){

        DseCluster cluster = DseCluster.builder().addContactPoints("127.0.0.1").build();
        DseSession session = cluster.connect();

        GraphResultSet rs = session.executeGraph("system.graph(\"modern\").exists()");
        if (rs.one().asString().equals("false")) {
            session.executeGraph("system.graph(\"modern\").create()");
        }

        // Define schema
        session.getCluster().getConfiguration().getGraphOptions().setGraphName("modern");
        session.executeGraph("schema.propertyKey(\"neighborhood\").Bigint().create()");
        session.executeGraph("schema.propertyKey(\"name\").Text().create()");
        session.executeGraph("schema.propertyKey(\"age\").Bigint().create()");
        session.executeGraph("schema.propertyKey(\"weight\").Float().create()");
        session.executeGraph("schema.vertexLabel(\"person\").partitionKey(\"neighborhood\").clusteringKey(\"name\").properties(\"age\").create()");
        session.executeGraph("schema.edgeLabel(\"knows\").properties(\"weight\").connection(\"person\", \"person\").create()");


        // Execute Batch
        TraversalBatch batch = DseGraph.batch();

        batch.add(addV("person").property("neighborhood", 0).property("name", "bob").property("age", 23));
        batch.add(addV("person").property("neighborhood", 0).property("name", "alice").property("age", 21));
        batch.add(addE("knows")
                .from(DetachedVertex.build().setId(ImmutableMap.of("neighborhood", 0, "name", "bob", "~label", "person")).setLabel("person").create())
                .to(DetachedVertex.build().setId(ImmutableMap.of("neighborhood", 0, "name", "alice", "~label", "person")).setLabel("person").create())
                .property("weight", 2.3f));

        session.executeGraph(batch.asGraphStatement());
        cluster.close();
    }
}


