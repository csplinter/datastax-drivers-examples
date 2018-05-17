Enable NodeSync on user-defined set of tables from command line
```
nodesync enable \*; // enable on all tables
nodesync enable -k foo \*; // enable on all tables of keyspace 'foo'.
nodesync enable foo.t1 bar.t2; // enable on tables foo.t1 and bar.t2
```

Java
```java
tblMetadata.getOptions().getNodeSync();
```
example format: {enabled=true}

Python
```python
tbl_metadata.options['nodesync']
```
example format: {u'enabled': u'true'}


