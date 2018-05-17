Java
```java
SimpleStatement statement = new SimpleStatement("select * from tbl").setKeyspace("ks");
session.execute(statement);
```
Python
```python
statement = SimpleStatement("select * from tbl", keyspace="ks")
session.execute(statement)
```

