package org.dol.framework.util;


import org.dol.framework.Table;

import java.sql.*;
import java.util.*;

/**
 * Created by dolphin on 2017/9/12.
 */
public class DbUtil {

    private static final String MYSQL_URL_TEMPLATE = "jdbc:mysql://%s:%d/%s?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&allowMultiQueries=true&useSSL=false";
    private static final String MYSQL_DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";

    private String url;
    private String userName;
    private String password;
    private Integer batchSize = 2000;

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

    /*   public static void main(String[] args) throws ClassNotFoundException, SQLException {
           DbUtil dbUtil = createMySqlDbUtil("192.168.3.71", 3306, "user_center", "admin", "ser4w2e10p");
          *//* String sql = "" +
                "select card_no from uc_card limit 1;" +
                "update uc_card set status=1 where id=-1;" +
                "select user_type from uc_user_card limit 1;";
        List<Object> lists = dbUtil.execute(sql);
        System.out.println(JSON.toJSONString(lists));*//*

     *//* List<Object> execute = dbUtil.execute("call uc_proc_test(?)", "2014140000001");
        System.out.println(JSON.toJSONString(execute));*//*

        System.out.println(JSON.toJSONString(dbUtil.selectManyList("call uc_proc_test(?)", "2014140000001")));
    }
*/
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
            return getRowList(resultSet);
        } finally {
            quietClose(connection);
        }
    }

    public List<Object> execute(String sql, Object... parameters) throws SQLException {
        Connection connection = null;
        List<Object> lists = new ArrayList<>(5);
        try {
            connection = getConnection();
            PreparedStatement preparedStatement = buildStatement(sql, connection, parameters);
            preparedStatement.execute();
            do {
                ResultSet resultSet = preparedStatement.getResultSet();
                if (resultSet != null) {
                    lists.add(getRowList(resultSet));
                } else {
                    lists.add(preparedStatement.getUpdateCount());
                }
            } while (preparedStatement.getMoreResults() || preparedStatement.getUpdateCount() >= 0);
            return lists;
        } finally {
            quietClose(connection);
        }
    }

    public List<List<Map<String, Object>>> selectManyList(String sql, Object... parameters) throws SQLException {
        Connection connection = null;
        List<List<Map<String, Object>>> lists = new ArrayList<List<Map<String, Object>>>();
        try {
            connection = getConnection();
            PreparedStatement preparedStatement = buildStatement(sql, connection, parameters);
            preparedStatement.execute();
            do {
                ResultSet resultSet = preparedStatement.getResultSet();
                if (resultSet != null) {
                    List<Map<String, Object>> rowList = getRowList(resultSet);
                    lists.add(rowList);
                }
            } while (preparedStatement.getMoreResults() || preparedStatement.getUpdateCount() >= 0);
            return lists;
        } finally {
            quietClose(connection);
        }
    }

    private List<Map<String, Object>> getRowList(ResultSet resultSet) throws SQLException {
        List<Map<String, Object>> rowList = new ArrayList<>();
        int columnCount = resultSet.getMetaData().getColumnCount();
        while (resultSet.next()) {
            Map<String, Object> row = getRow(resultSet, columnCount);
            rowList.add(row);
        }
        return rowList;
    }

    public int executeNonQuery(String sql, Object... parameters) throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement preparedStatement = buildStatement(sql, connection, parameters);
            return preparedStatement.executeUpdate();
        } finally {
            quietClose(connection);
        }
    }

    public Object insert(String sql, Object[] parameters, boolean returnGeneratedKeys) throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement preparedStatement = buildStatement(sql, connection, parameters, returnGeneratedKeys);
            int result = preparedStatement.executeUpdate();
            if (returnGeneratedKeys) {
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
            return result;
        } finally {
            quietClose(connection);
        }
    }

    public List<Object> batchInsert(String sql,
                                    Collection<Object[]> parameters,
                                    boolean returnGeneratedKeys) throws SQLException {
        return executeBatch(sql, parameters, returnGeneratedKeys);
    }

    public List<Object> executeBatch(String sql,
                                     Collection<Object[]> parameters) throws SQLException {
        return executeBatch(sql, parameters, false);
    }

    private List<Object> executeBatch(String sql,
                                      Collection<Object[]> parameters,
                                      boolean returnGeneratedKeys) throws SQLException {
        if (empty(parameters)) {
            throw new IllegalArgumentException("参数 parameters 不能为空");
        }
        /*  int[] effectCount = new int[parameters.size()];*/
        List<Object> results = new ArrayList<>(parameters.size());
        int index = 0;
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = getPreparedStatement(sql, connection, returnGeneratedKeys);

            int j = 0;
            for (Object[] parameter : parameters) {
                j++;
                for (int i = 0; i < parameter.length; i++) {
                    preparedStatement.setObject(i + 1, parameter[i]);
                }
                preparedStatement.addBatch();
                if (j % batchSize == 0) {
                    execute(returnGeneratedKeys, results, preparedStatement);
                    preparedStatement.clearBatch();
                }
            }
            execute(returnGeneratedKeys, results, preparedStatement);
            connection.commit();
            return results;
        } catch (SQLException ex) {
            rollback(connection);
            throw ex;
        } finally {
            quietClose(connection);
        }
    }

    private void execute(boolean returnGeneratedKeys, List<Object> keys, PreparedStatement preparedStatement) throws SQLException {
        int[] counts = preparedStatement.executeBatch();
        if (returnGeneratedKeys) {
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            while (generatedKeys.next()) {
                keys.add(generatedKeys.getObject(1));
            }
        } else {
            for (int count : counts) {
                keys.add(count);
            }
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

    private PreparedStatement buildStatement(String sql,
                                             Connection connection,
                                             Object[] parameters) throws SQLException {
        return buildStatement(sql, connection, parameters, false);

    }

    private PreparedStatement buildStatement(String sql,
                                             Connection connection,
                                             Object[] parameters,
                                             boolean returnGeneratedKeys
    ) throws SQLException {
        PreparedStatement preparedStatement = getPreparedStatement(sql, connection, returnGeneratedKeys);

        if (parameters != null) {
            for (int i = 0; i < parameters.length; i++) {
                preparedStatement.setObject(i + 1, parameters[i]);
            }
        }
        return preparedStatement;
    }

    private PreparedStatement getPreparedStatement(String sql, Connection connection, boolean returnGeneratedKeys) throws SQLException {
        PreparedStatement preparedStatement;
        if (returnGeneratedKeys) {
            preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        } else {
            preparedStatement = connection.prepareStatement(sql);
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

    public Object insert(String sql, Object[] parameters) throws SQLException {
        return insert(sql, parameters, false);
    }

    public List<Object> batchInsert(String sql, List<Object[]> parameters) throws SQLException {
        return batchInsert(sql, parameters, false);
    }

    public List<Object> selectValueList(String sql, Object... parameters) throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement preparedStatement = buildStatement(sql, connection, parameters);
            ResultSet resultSet = preparedStatement.executeQuery();
            return getRowValueList(resultSet);
        } finally {
            quietClose(connection);
        }
    }


    private List<Object> getRowValueList(ResultSet resultSet) throws SQLException {
        List<Object> valueList = new ArrayList<>();
        while (resultSet.next()) {
            Object object = resultSet.getObject(1);
            valueList.add(object);
        }
        return valueList;
    }

    public Table selectTable(String sql, Object... parameters) throws SQLException {

        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement preparedStatement = buildStatement(sql, connection, parameters);
            ResultSet resultSet = preparedStatement.executeQuery();
            return getTable(resultSet);

        } finally {
            quietClose(connection);
        }
    }

    private Table getTable(ResultSet resultSet) throws SQLException {
        int columnCount = resultSet.getMetaData().getColumnCount();
        List<String> columnNames = new ArrayList<>(columnCount);
        for (int i = 1; i <= columnCount; i++) {
            columnNames.add(resultSet.getMetaData().getColumnName(i));
        }
        Table table = new Table(columnNames);
        while (resultSet.next()) {
            Object[] row = new Object[columnCount];
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                row[columnIndex - 1] = resultSet.getObject(columnIndex);
            }
            table.addRow(row);
        }
        return table;
    }

    public int update(String sql, Object... parameters) throws SQLException {
        return this.executeNonQuery(sql, parameters);
    }

    public List<Object> batchUpdate(String sql, List<Object[]> parameters) throws SQLException {
        return this.executeBatch(sql, parameters);
    }
}
