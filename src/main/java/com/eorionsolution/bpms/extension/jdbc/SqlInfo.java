package com.eorionsolution.bpms.extension.jdbc;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class SqlInfo implements Serializable {
    private Map<String, Object> insert;
    private Map<String, Object> update;
    private Map<String, Object> where;
}

