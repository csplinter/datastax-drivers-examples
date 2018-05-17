Define table schema
```
CREATE TABLE tbl (k int PRIMARY KEY, c int, d int);
```

Client prepares statement, returned known metadata is columns (k, c, d)
```java
PreparedStatement prepared = session.prepare("select * from tbl");
```

Schema is altered to add new column 'b'.
```
ALTER TABLE tbl ADD b text;
```
```java
Row row = session.execute(prepared.bind()).one();
```
print the value of column 'c' 

5.1 - Fails, driver thinks c is @ column 1 (k, c, d)

6.0 - Succeeds, because rows returned from DSE indicate metadata was changed and returns new metadata (k, b, c, d)
```java
System.out.println(row.getInt("c"));
```


