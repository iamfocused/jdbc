package com.eorionsolution.bpms.extension.jdbc;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
@Data
public class JdbcOperationInfo implements Serializable {
    private String tableName;
    private Integer type;//0:insert 1:update
    private List<SqlInfo> details;
}
