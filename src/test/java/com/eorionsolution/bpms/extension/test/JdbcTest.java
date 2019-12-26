package com.eorionsolution.bpms.extension.test;

import com.eorionsolution.bpms.extension.jdbc.JdbcUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;


public class JdbcTest {
    @Test
    @Ignore
    public void testInsert() throws InterruptedException {
//        for (int i = 0; i < 10; i++) {
//            new Thread(this::tttt).start();
//            tttt();
//        }
//        Thread.sleep(1000 * 10);
//        tttt();
    }

//    public void tttt(){
//        for (int i=0; i < 300; i ++) {
//            String jsonString = "{\n" +
//                    "    \"tableName\":\"DP_LEAVE_RECORDS_\",\n" +
//                    "    \"type\":0,\n" +
//                    "    \"details\":[\n" +
//                    "        {\n" +
//                    "            \"insert\":{\n" +
//                    "                \"SN_\":\"100011\",\n" +
//                    "                \"LEAVE_TYPE_\":\"事假Personal Leave\",\n" +
//                    "                \"LEAVE_DAYS_\": 1.00,\n" +
//                    "                \"PROC_INST_ID_\":\"" + UUID.randomUUID().toString() + "\",\n" +
//                    "                \"ENABLE_\":true,\n" +
//                    "                \"CREATE_TIME_\":\"2019-12-12\"\n" +
//                    "            }\n" +
//                    "        }\n" +
//                    "    ]\n" +
//                    "}";
//            int i1 = JdbcUtils.batchInsert(jsonString);
//            if (i1 != 200) {
//                int index = i;
//                System.out.println(index);
//            }
////        JdbcUtils.batchUpdate(jsonString);
//        }    }

    @Test
    public void testFlatMap() {
        List<List<String>> list = new ArrayList<>();
        list.add(Arrays.asList("1", "2", "3"));
        list.add(Arrays.asList("4", "5", "6"));
        list.add(Arrays.asList("7", "8", "9"));
        List<String> result = list.parallelStream().flatMap(List::stream).filter(it -> !it.equals("5")).collect(Collectors.toList());
    }
}
