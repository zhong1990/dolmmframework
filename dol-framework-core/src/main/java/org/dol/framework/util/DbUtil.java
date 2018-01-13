package org.dol.framework.util;

import java.sql.*;
import java.util.*;

/**
 * Created by dolphin on 2017/9/12.
 */
public class DbUtil {

    private static final String MYSQL_URL_TEMPLATE = "jdbc:mysql://%s:%d/%s?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&allowMultiQueries=true";
    private static final String MYSQL_DRIVER_CLASS_NAME = "com.mysql.jdbc.Driver";

    private String url;
    private String userName;
    private String password;
    private Integer batchSize = 100;

    public DbUtil(String driverClass, String url, String username, String password) throws ClassNotFoundException {
        this(driverClass, url, username, password, null);
    }

    public DbUtil(String driverClass, String url, String username, String password, Integer batchSize) throws ClassNotFoundException {
        Class.forName(driverClass);
        this.url = url;
        this.userName = username;
        this.password = password;
        if (batchSize != null) {
            this.batchSize = batchSize;
        }
    }

    public static DbUtil createMySqlDbUtil(String host,
                                           Integer port,
                                           String defaultDatabase,
                                           String user,
                                           String password) throws ClassNotFoundException {
        String url = String.format(MYSQL_URL_TEMPLATE, host, port, defaultDatabase);
        return new DbUtil(MYSQL_DRIVER_CLASS_NAME, url, user, password);
    }

    public Map<String, Object> selectOne(String sql, Object... parameters) throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement preparedStatement = buildStatement(sql, connection, parameters);
            ResultSet resultSet = preparedStatement.executeQuery();
            int columnCount = resultSet.getMetaData().getColumnCount();
            if (resultSet.next()) {
                return getRow(resultSet, columnCount);
            }
            return null;
        } finally {
            quietClose(connection);
        }
    }

    private Map<String, Object> getRow(ResultSet resultSet, int columnCount) throws SQLException {
        Map<String, Object> row = new HashMap<>(columnCount);
        for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
            Object object = resultSet.getObject(columnIndex);
            String columnName = resultSet.getMetaData().getColumnName(columnIndex);
            row.put(columnName, object);
        }
        return row;
    }

    public List<Map<String, Object>> selectList(String sql, Object... parameters) throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement preparedStatement = buildStatement(sql, connection, parameters);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Map<String, Object>> rowList = new ArrayList<>(100);
            int columnCount = resultSet.getMetaData().getColumnCount();
            while (resultSet.next()) {
                Map<String, Object> row = getRow(resultSet, columnCount);
                rowList.add(row);
            }
            return rowList;
        } finally {
            quietClose(connection);
        }
    }

    public int insert(String sql, Object... parameters) throws SQLException {
        return update(sql, parameters);
    }

    public int delete(String sql, Object... parameters) throws SQLException {
        return update(sql, parameters);
    }

    public int update(String sql, Object... parameters) throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement preparedStatement = buildStatement(sql, connection, parameters);
            return preparedStatement.executeUpdate();
        } finally {
            quietClose(connection);
        }
    }

    public int[] executeBatch(String sql, Collection<Object[]> parameters) throws SQLException {
        if (empty(parameters)) {
            throw new IllegalArgumentException("参数 parameters 不能为空");
        }
        int[] effectCount = new int[parameters.size()];
        int index = 0;
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            if (notEmpty(parameters)) {
                int j = 0;
                for (Object[] parameter : parameters) {
                    j++;
                    for (int i = 0; i < parameter.length; i++) {
                        preparedStatement.setObject(i + 1, parameter[i]);
                    }
                    preparedStatement.addBatch();
                    if (j % batchSize == 0) {
                        int[] counts = preparedStatement.executeBatch();
                        for (int count : counts) {
                            effectCount[index++] = count;
                        }
                        preparedStatement.clearBatch();
                    }
                }
                preparedStatement.executeBatch();
            }
            connection.commit();
            return effectCount;
        } catch (SQLException ex) {
            rollback(connection);
            throw ex;
        } finally {
            quietClose(connection);
        }
    }

    private boolean empty(Collection<Object[]> parameters) {
        return parameters == null || parameters.size() == 0;
    }

    private void rollback(Connection connection) throws SQLException {
        if (connection != null) {
            try {
                connection.rollback();
            } catch (Exception ex) {

            }
        }
    }

    private boolean notEmpty(Collection<Object[]> parameters) {
        return parameters != null && parameters.size() > 0;
    }

    private PreparedStatement buildStatement(String sql, Connection connection, Object[] parameters) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        if (parameters != null) {
            for (int i = 0; i < parameters.length; i++) {
                preparedStatement.setObject(i + 1, parameters[i]);
            }
        }
        return preparedStatement;
    }

    private void quietClose(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (Exception ignore) {

            }
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, userName, password);
    }
}
