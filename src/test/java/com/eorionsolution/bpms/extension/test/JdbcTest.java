package com.eorionsolution.bpms.extension.test;

import com.eorionsolution.bpms.extension.jdbc.JdbcUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class JdbcTest {
    @Test
    @Ignore
    public void testInsert() {
       String jsonString = "{\n" +
               "  \"tableName\":\"PRODUCTION_PROCESS\",\n" +
               "  \"type\":1,\n" +
               "  \"details\":\n" +
               "  [\n" +
               "    {\n" +
               "      \"update\": {\"ORDER_QTY_1\": 122, \"TYPE_\": \"AAA\"},\n" +
               "      \"where\":{\"SUP_PROC_INST_\":\"11\",\"CODE_\":\"2\"}\n" +
               "    },\n" +
               "    {\n" +
               "      \"update\": {\"ORDER_QTY_\": 2, \"TYPE_\": \"BBB\"},\n" +
               "      \"where\":{\"SUP_PROC_INST_\":\"11\",\"CODE_\":\"1\"}\n" +
               "    }\n" +
               "  ]\n" +
               "\n" +
               "}";
       assertEquals(401, JdbcUtils.batchUpdate(jsonString));
    }

    @Test
    public void testFlatMap() {
        List<List<String>> list = new ArrayList<>();
        list.add(Arrays.asList("1", "2", "3"));
        list.add(Arrays.asList("4", "5", "6"));
        list.add(Arrays.asList("7", "8", "9"));
        List<String> result = list.parallelStream().flatMap(List::stream).filter(it -> !it.equals("5")).collect(Collectors.toList());
        System.out.println(result);
    }
}
