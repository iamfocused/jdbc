# JDBC Helper JAR

## Usage

### Build
```shell 
mvn clean compile assembly:single
```    

### API 

```java
import com.eorionsolution.bpms.extension.jdbc.JdbcUtils;
//insert
JdbcUtils.batchInsert(inserJson)

//update
JdbsUtils.batchUpdate(updateJson)
```    

insert json    
```
{
  "tableName":"tableName",
  "type":0,
  "details":
  [
    {
      "insert": {"field1":field1Value,"field2": field2Value}
    },
    {
      "insert": {"field1":field1Value,"field2": field2Value}
    }
  ]

}
```    
update json
```
{
  "tableName":"tableName",
  "type":1,
  "details":
  [
    {
      "update": {"field1": field1Value, "filed2": field2Value},
      "where":{"condition1": condition1Value, "condition2": condition2Value}
    },
    {
      "update": {"field1": field1Value, "filed2": field2Value},
      "where":{"condition1": condition1Value, "condition2": condition2Value}
    }
  ]

}
```