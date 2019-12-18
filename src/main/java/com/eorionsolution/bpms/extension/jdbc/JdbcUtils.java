package com.eorionsolution.bpms.extension.jdbc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class JdbcUtils {
    private static final int batchSize = 100;
    private static Connection connection;
    private static PreparedStatement preparedStatement;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static Connection getConnection() throws SQLException {
        HikariConfig hikariConfig = new HikariConfig("/jdbc.properties");
        HikariDataSource dataSource = new HikariDataSource(hikariConfig);
        return dataSource.getConnection();
    }

    private static void closeAll() throws SQLException {
        if (preparedStatement != null) {
            preparedStatement.close();
        }
        if (connection != null) {
            connection.close();
        }
    }

    public static int batchInsert(String jsonData) {
        int result = 200;
        try {
            JdbcOperationInfo jdbcOperationInfo = objectMapper.readValue(jsonData, JdbcOperationInfo.class);
            Integer t = Optional.of(jdbcOperationInfo).map(JdbcOperationInfo::getType).orElse(-1);
            if (t != 0 || errorData(jdbcOperationInfo)) {
                return 401;
            }
            List<String> fields = new ArrayList<>(jdbcOperationInfo.getDetails().get(0).getInsert().keySet());
            String sql = constructInsertSQL(fields, jdbcOperationInfo.getTableName());
            connection = getConnection();
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(sql);
            int count = 0;

            List<SqlInfo> details = jdbcOperationInfo.getDetails();

            for (SqlInfo info : details) {
                Map<String, Object> insert = info.getInsert();
                for (int index = 0; index < fields.size(); index ++) {
                    preparedStatement.setObject(index + 1, insert.get(fields.get(index)));
                }
                preparedStatement.addBatch();
                if (++count % batchSize == 0) {
                    preparedStatement.executeBatch();
                    preparedStatement.clearBatch();
                }
            }

            preparedStatement.executeBatch();
            preparedStatement.clearBatch();
            connection.commit();

        } catch (Exception e) {
            e.printStackTrace();
            result = 402;
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                closeAll();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private static String constructInsertSQL(List<String> fields, String tableName) {
        String fs = String.join(",", fields);
        String values = fields.stream().map(s -> "?").collect(Collectors.joining(","));
        return "INSERT INTO "+ tableName + "(" + fs + ")" + "VALUES(" + values + ")";
    }

    public static int batchUpdate(String jsonData) {
        int result = 200;
        try {
            JdbcOperationInfo jdbcOperationInfo = objectMapper.readValue(jsonData, JdbcOperationInfo.class);
            Integer t = Optional.of(jdbcOperationInfo).map(JdbcOperationInfo::getType).orElse(-1);
            if (t != 1 || errorData(jdbcOperationInfo)) {
                return 401;
            }
            List<String> fields = new ArrayList<>(jdbcOperationInfo.getDetails().get(0).getUpdate().keySet());
            List<String> conditions = new ArrayList<>(jdbcOperationInfo.getDetails().get(0).getWhere().keySet());
            String sql = constructUpdateSql(fields, conditions);
            connection = getConnection();
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(sql);

            List<SqlInfo> details = jdbcOperationInfo.getDetails();

            int count = 0;
            for (SqlInfo info : details) {
                Map<String, Object> update = info.getUpdate();
                Map<String, Object> condition = info.getWhere();
                for (int index = 0; index < fields.size() + condition.size(); index ++) {
                    Object value;
                    if (index < fields.size()) {
                        value = update.get(fields.get(index));
                    } else {
                        value = condition.get(conditions.get(index - fields.size()));
                    }
                    preparedStatement.setObject(index + 1, value);
                }
                preparedStatement.addBatch();
                if (++count % batchSize == 0) {
                    preparedStatement.executeBatch();
                    preparedStatement.clearBatch();
                }
            }
            preparedStatement.executeBatch();
            preparedStatement.clearBatch();
            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
            result = 402;
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                closeAll();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private static String constructUpdateSql(List<String> fields, List<String> conditions) {
        fields.removeAll(conditions);
        String fs = String.join("=?,", fields) + "=?";
        String con = String.join("=? and ", conditions) + "=?";
        return "UPDATE PRODUCTION_PROCESS SET " + fs + " WHERE " + con;
    }

    private static boolean errorData(JdbcOperationInfo jdbcOperationInfo) {
        if (jdbcOperationInfo == null) {
            return true;
        }
        String tableName = jdbcOperationInfo.getTableName();
        if (tableName == null || tableName.trim().equals("")) {
            return true;
        }
        Integer type = jdbcOperationInfo.getType();
        if (type != 0 && type != 1) {
            return true;
        }
        List<SqlInfo> details = jdbcOperationInfo.getDetails();
        if (details == null || details.size() == 0) {
            return true;
        }

        boolean hasInserts = details.stream().map(SqlInfo::getInsert).noneMatch(info -> info == null || info.isEmpty());
        boolean hasUpdates = details.stream().map(SqlInfo::getUpdate).noneMatch(info -> info == null || info.isEmpty());
        boolean hasConditions = details.stream().map(SqlInfo::getWhere).noneMatch(info -> info == null || info.isEmpty());

        if (type == 0) {
            return !(hasInserts && !hasUpdates && !hasConditions && listMapHasSameKey(details.stream().map(SqlInfo::getInsert).collect(Collectors.toList())) ) ;
        }

        boolean updateHasSameKey = listMapHasSameKey(details.stream().map(SqlInfo::getUpdate).collect(Collectors.toList()));
        boolean conditionHasSamKey = listMapHasSameKey(details.stream().map(SqlInfo::getWhere).collect(Collectors.toList()));
        return !(hasUpdates && hasConditions && !hasInserts && updateHasSameKey && conditionHasSamKey);
    }

    private static boolean listMapHasSameKey(List<Map<String, Object>> list) {
        Set<String> keyStringSet = list.stream().map(Map::keySet).map(keys -> String.join(",", keys)).collect(Collectors.toSet());
        return keyStringSet.size() == 1;
    }

}
