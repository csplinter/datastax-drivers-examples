Setup
```
cqlsh> create keyspace ks with replication = {'class': 'NetworkTopologyStrategy', 'Solr': 1};
cqlsh> create table ks.tbl (key int primary key, value text); 
cqlsh> insert into ks.tbl (key, value) values (0, 'val_0');
cqlsh> create search index on ks.tbl with columns value;
cqlsh> select * from ks.tbl where value='val_0';

 key | solr_query | value
-----+------------+-------
   0 |       null | val_0

(1 rows)
```

Java - QueryBuilder
```java
import static com.datastax.driver.core.querybuilder.QueryBuilder.*;
DseCluster cluster = getCluster();
DseSession session = cluster.connect();
Statement selectAll = select().all().from("ks", "tbl").where(eq("value", "val_0"));
Statement selectLike = select().all().from("ks", "tbl").where(like("value", "val%"));
Statement selectNotNull = select().all().from("ks", "tbl").where(notNull("value"));
Statement selectNotEqual = select().all().from("ks", "tbl").where(ne("value", "val_1"));
ResultSet resultAll = session.execute(selectAll);
ResultSet resultLike = session.execute(selectLike);
ResultSet resultNotNull = session.execute(selectNotEqual);
ResultSet resultNotEqual = session.execute(selectNotEqual);

System.out.println("selectAll Statement = " + selectAll.toString());
System.out.println("selectAll ResultSet = " +resultAll.one());
System.out.println();
System.out.println("selectLike Statement = " + selectLike.toString());
System.out.println("selectLike ResultSet = " +resultLike.one());
System.out.println();
System.out.println("selectNotNull Statement = " + selectNotNull.toString());
System.out.println("selectNotNull ResultSet = " + resultNotNull.one());
System.out.println();
System.out.println("selectNotEqual Statement = " + selectNotEqual.toString());
System.out.println("selectNotEqual ResultSet = " + resultNotEqual.one());
System.out.println();
```

Output ( note - The NULL column in the result set is the solr_query column in ks.tbl )
```
selectAll Statement = SELECT * FROM ks.tbl WHERE value='val_0';
selectAll ResultSet = Row[0, NULL, val_0]

selectLike Statement = SELECT * FROM ks.tbl WHERE value LIKE 'val%';
selectLike ResultSet = Row[0, NULL, val_0]

selectNotNull Statement = SELECT * FROM ks.tbl WHERE value IS NOT NULL;
selectNotNull ResultSet = Row[0, NULL, val_0]

selectNotEqual Statement = SELECT * FROM ks.tbl WHERE value!='val_1';
selectNotEqual ResultSet = Row[0, NULL, val_0]
```

