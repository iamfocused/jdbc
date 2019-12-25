package com.eorionsolution.bpms.extension.test;

import com.eorionsolution.bpms.extension.jdbc.JdbcUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;


public class JdbcTest {
    @Test
    @Ignore
    public void testInsert() {
       String jsonString = "{\n" +
               "    \"tableName\":\"DP_LEAVE_RECORDS_\",\n" +
               "    \"type\":1,\n" +
               "    \"details\":[\n" +
               "        {\n" +
               "            \"update\":{\n" +
               "                \"ENABLE_\":true" +
               "            },\n" +
               "            \"where\":{\n" +
               "            \t\"PROC_INST_ID_\":\"524807\", \"ENABLE_\": false, \"SN_\":\"100011\"\n" +
               "            }\n" +
               "        }\n" +
               "    ]\n" +
               "}";
//        JdbcUtils.batchInsert(jsonString);
        JdbcUtils.batchUpdate(jsonString);
    }

    @Test
    public void testFlatMap() {
        List<List<String>> list = new ArrayList<>();
        list.add(Arrays.asList("1", "2", "3"));
        list.add(Arrays.asList("4", "5", "6"));
        list.add(Arrays.asList("7", "8", "9"));
        List<String> result = list.parallelStream().flatMap(List::stream).filter(it -> !it.equals("5")).collect(Collectors.toList());
    }
}
