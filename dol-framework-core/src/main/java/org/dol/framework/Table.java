package org.dol.framework;

import com.alibaba.fastjson.util.TypeUtils;

import java.util.*;

/**
 * Created by dolphin on 2017/10/30.
 */
public class Table implements Iterable<Table.Row> {

    private Map<String, Integer> columnIndex;
    private List<String> columnNames;
    private List<Row> rows;

    public Table(List<String> columnNames) {
        this(columnNames, 20);
    }

    public Table(List<String> columnNames, int rowCount) {
        this.columnNames = columnNames;
        this.columnIndex = new HashMap<>(columnNames.size());
        int index = 0;
        for (String columnName : columnNames) {
            this.columnIndex.put(columnName, index++);
        }
        rows = new ArrayList<>(rowCount);
        /*this.rows = rows;*/
    }

    public Object get(int rowIndex, int columnIndex) {
        return rows.get(rowIndex).get(columnIndex);
    }

    public List<String> columnNames() {
        return columnNames;
    }

    public List<Row> rows() {
        return rows;
    }


    public Object get(int rowIndex, String columnName) {
        return get(rowIndex, columnIndex.get(columnName));
    }

    private int getColumnIndex(String columnName) {
        return columnIndex.get(columnName);
    }

    public void addRow(Object[] row) {
        rows.add(new Row(row));
    }

    @Override
    public Iterator<Row> iterator() {
        return rows.iterator();
    }

    public class Row {

        private Object[] data;

        private Row(Object[] data) {
            this.data = data;
        }

        public Object get(int columnIndex) {
            return data[columnIndex];
        }

        public Object get(String columnName) {
            return data[getColumnIndex(columnName)];
        }

        public String getString(String columnName) {
            return TypeUtils.castToString(get(columnName));
        }

        public Long getLong(String columnName) {
            return TypeUtils.castToLong(get(columnName));
        }

        public Double getDouble(String columnName) {
            return TypeUtils.castToDouble(get(columnName));
        }

        public Byte getByte(String columnName) {
            return TypeUtils.castToByte(get(columnName));
        }

        public byte[] getByte2(String columnName) {
            return TypeUtils.castToBytes(get(columnName));
        }

        public Boolean getBoolean(String columnName) {
            return TypeUtils.castToBoolean(get(columnName));
        }

        public Float getFloat(String columnName) {
            return TypeUtils.castToFloat(get(columnName));
        }

        public Integer getInteger(String columnName) {
            return TypeUtils.castToInt(get(columnName));
        }


        public String getString(int index) {
            return TypeUtils.castToString(get(index));
        }

        public Long getLong(int index) {
            return TypeUtils.castToLong(get(index));
        }

        public Double getDouble(int index) {
            return TypeUtils.castToDouble(get(index));
        }

        public Byte getByte(int index) {
            return TypeUtils.castToByte(get(index));
        }

        public byte[] getByte2(int index) {
            return TypeUtils.castToBytes(get(index));
        }

        public Boolean getBoolean(int index) {
            return TypeUtils.castToBoolean(get(index));
        }

        public Float getFloat(int index) {
            return TypeUtils.castToFloat(get(index));
        }

        public Integer getInteger(int index) {
            return TypeUtils.castToInt(get(index));
        }
    }

}

