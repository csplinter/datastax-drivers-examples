# At the time of this blog post, dse_graph only supports gremlinpython version 3.2.x
# This script was tested using gremlinpython version 3.2.6

from dse.cluster import Cluster, GraphExecutionProfile, EXEC_PROFILE_GRAPH_DEFAULT, EXEC_PROFILE_GRAPH_SYSTEM_DEFAULT
from dse.graph import GraphOptions
from dse_graph import DseGraph
from gremlin_python.process.graph_traversal import __
from gremlin_python.structure.graph import Vertex

graph_name = 'modern'
ep_schema = GraphExecutionProfile(graph_options=GraphOptions(graph_name=graph_name))
ep = DseGraph.create_execution_profile(graph_name)

cluster = Cluster(execution_profiles={'schema': ep_schema, EXEC_PROFILE_GRAPH_DEFAULT: ep})
session = cluster.connect()

# Define schema
session.execute_graph("system.graph(name).create()", { 'name': graph_name }, execution_profile = EXEC_PROFILE_GRAPH_SYSTEM_DEFAULT)
session.execute_graph("schema.propertyKey('neighborhood').Bigint().create()", execution_profile = 'schema')
session.execute_graph("schema.propertyKey('name').Text().create()", execution_profile = 'schema')
session.execute_graph("schema.propertyKey('age').Bigint().create()", execution_profile = 'schema')
session.execute_graph("schema.propertyKey('weight').Float().create()", execution_profile = 'schema')
session.execute_graph("schema.vertexLabel('person').partitionKey('neighborhood').clusteringKey('name').properties('age').create()", execution_profile = 'schema')
session.execute_graph("schema.edgeLabel('knows').properties('weight').connection('person', 'person').create()", execution_profile = 'schema')

# Execute batch
batch = DseGraph.batch()
batch.add(__.addV('person').property('neighborhood', 0).property('name', 'bob').property('age', 23))
batch.add(__.addV('person').property('neighborhood', 0).property('name', 'alice').property('age', 21))
batch.add(__.addE('knows')
        .from_(Vertex({ 'neighborhood': 0, 'name': 'bob', '~label' : 'person' }))
        .to(Vertex({ 'neighborhood': 0, 'name': 'alice', '~label' : 'person' }))
        .property('weight', 2.3))
session.execute_graph(batch.as_graph_statement())
cluster.close()

