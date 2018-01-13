package org.dol.framework.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class MapUtil {

    public static String map2QueryString(Map<String, Object> map) {
        if (map == null || map.size() == 0) {
            return StringUtil.EMPTY_STRING;
        }
        StringBuilder sbBuilder = new StringBuilder();
        Set<Entry<String, Object>> entries = map.entrySet();
        for (Entry<String, Object> entry : entries) {
            Object val = entry.getValue();
            if (val == null) {
                sbBuilder.append(entry.getKey() + "=&");
            } else {
                sbBuilder.append(entry.getKey() + "=" + StringUtil.encode(val) + "&");
            }
        }
        return sbBuilder.substring(0, sbBuilder.length() - 1);
    }

    public static void cleanMap(Map<String, Object> map) {
        Set<Entry<String, Object>> entries = map.entrySet();
        for (Entry<String, Object> entry : entries) {
            if (entry.getValue() == null) {
                continue;
            }
            if (entry.getValue() instanceof Number) {
                Number number = (Number) entry.getValue();
                if (number.intValue() == number.floatValue()) {
                    entry.setValue(number.intValue());
                }
            }
        }
    }

    public static Map<String, Object> queryString2Map(String queryString) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (StringUtil.isBlank(queryString)) {
            return map;
        }
        String[] items = queryString.split("\\&");
        for (String item : items) {

            int index = item.indexOf('=');

            if (index > 0) {
                String key = item.substring(0, index);
                if (StringUtil.isBlank(key)) {
                    continue;
                }
                String value = item.substring(index + 1);
                map.put(key, value);
            }
        }
        return map;
    }

    public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException {
        // String result =
        // "signMethod=MD5&rspCode=00&fileName=105290053114851_20161017.zip&rspDesc=成功&fileContent=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&signature=27be98400a63d6aec705d3a9088af26e";
        // Map<String, Object> map = queryString2Map(result);
        // for (String key : map.keySet()) {
        // System.out.println(MapUtil.getString(map, key));
        // }

        Map<String, Object> map1 = MapUtil.buildMap(
                "userName", "dolphin",
                "sex", "1",
                "age", "1",
                "height", "12",
                "aa", "12.1",
                "bb", "12.1",
                "cc", "12.1",
                "xx", 1);
        Man user = new Man();
        map2Object(map1, Man.class);
        System.out.println(JSON.toJSONString(user));
    }

    /**
     * 合并两个 Map List，两个List中的Map如果key为uniqueKey的值相同，则合并两个Map为一个Map
     *
     * @param mapList1
     * @param mapList2
     * @param uniqueKey
     * @param overwrite
     * @return List<Map<K,V>>
     * @throws @author dolphin
     * @date 2015年12月22日 下午4:56:10
     * @since JDK 1.7
     */
    public static <K, V> List<Map<K, V>> mergeAndCombine(List<Map<K, V>> mapList1, List<Map<K, V>> mapList2, K uniqueKey, boolean overwrite) {

        if (mapList1.isEmpty()) {
            return new ArrayList<Map<K, V>>(mapList2);
        }
        if (mapList2.isEmpty()) {
            return new ArrayList<Map<K, V>>(mapList1);
        }

        List<Map<K, V>> returnMapList = new ArrayList<Map<K, V>>(mapList1);
        List<Map<K, V>> copyMapList = new ArrayList<Map<K, V>>(mapList2);

        for (Map<K, V> map : returnMapList) {
            V keyValue = map.get(uniqueKey);
            Map<K, V> findMap = null;
            for (Map<K, V> map2 : copyMapList) {
                if (keyValue.equals(map2.get(uniqueKey))) {
                    findMap = map2;
                    merge(map, map2, overwrite);
                    break;
                }
            }
            if (findMap != null) {
                copyMapList.remove(findMap);
            }
        }
        returnMapList.addAll(copyMapList);
        return returnMapList;
    }

    /**
     * 根据uniqueKey, 把2个list中Map 的Key为uniqueKey的的值合并成一个新的List<Map<K,V>>，
     *
     * @param mapList1
     * @param mapList2
     * @param uniqueKey
     * @param overwrite
     * @return List<Map<K,V>>
     * @author dolphin
     * @date 2015年12月16日 下午1:39:11
     * @since JDK 1.7
     */
    public static <K, V> List<Map<K, V>> merge(List<Map<K, V>> mapList1, List<Map<K, V>> mapList2, K uniqueKey, boolean overwrite) {

        if (mapList1.isEmpty() || mapList2.isEmpty()) {
            return new ArrayList<Map<K, V>>(0);
        }
        List<Map<K, V>> mergeMapList = null;
        List<Map<K, V>> copyMapList = null;

        if (mapList1.size() < mapList2.size()) {
            mergeMapList = new ArrayList<Map<K, V>>(mapList1);
            copyMapList = new ArrayList<Map<K, V>>(mapList2);
        } else {
            mergeMapList = new ArrayList<Map<K, V>>(mapList2);
            copyMapList = new ArrayList<Map<K, V>>(mapList1);
        }

        int i = 0;
        while (i < mergeMapList.size()) {
            Map<K, V> map1 = mergeMapList.get(i);
            V keyValue = map1.get(uniqueKey);
            Map<K, V> findMap2 = null;
            for (Map<K, V> map2 : copyMapList) {
                V keyValue2 = map2.get(uniqueKey);
                if (keyValue2 != null && keyValue.equals(keyValue2)) {
                    findMap2 = map2;
                    MapUtil.merge(map1, map2, overwrite);
                    break;
                }
            }
            if (findMap2 == null) {
                mergeMapList.remove(map1);
            } else {
                i++;
                copyMapList.remove(findMap2);
            }
        }
        return mergeMapList;
    }

    public static <K, V> List<String> getStringValueList(List<Map<K, V>> mapList, K keyName, boolean removeDuplicate) {
        return getValueList(mapList, keyName, removeDuplicate);

    }

    @SuppressWarnings("unchecked")
    public static <K, V, FV> List<FV> getValueList(List<Map<K, V>> mapList, K keyName, boolean removeDuplicate) {
        if (removeDuplicate) {
            Set<FV> keyList = new HashSet<FV>(mapList.size());
            for (Map<K, V> map : mapList) {
                keyList.add((FV) map.get(keyName));
            }
            List<FV> valueList = new ArrayList<FV>(keyList);
            return valueList;
        } else {
            List<FV> valueList = new ArrayList<FV>(mapList.size());
            for (Map<K, V> map : mapList) {
                valueList.add((FV) map.get(keyName));
            }
            return valueList;
        }

    }

    public static <K, V> void merge(Map<K, V> map1, Map<K, V> map2, boolean overwrite) {
        Set<Entry<K, V>> entries = map2.entrySet();
        if (overwrite) {
            for (Entry<K, V> entry : entries) {
                map1.put(entry.getKey(), entry.getValue());
            }
        } else {
            for (Entry<K, V> entry : entries) {
                if (!map1.containsKey(entry.getKey())) {
                    map1.put(entry.getKey(), entry.getValue());
                }
            }
        }
    }

    public static <K, V> Map<K, V> copyFrom(Map<K, V> sourceMap) {
        Map<K, V> copyMap = new HashMap<K, V>(sourceMap);
        // copyMap.putAll(sourceMap);
        return copyMap;
    }

    public static <K, V> List<Map<K, V>> getNotInSecondMapList(List<Map<K, V>> mapList1, List<Map<K, V>> mapList2, K uniqueKeyName) {

        List<Map<K, V>> returnMapList = new ArrayList<Map<K, V>>(mapList1);
        for (Map<K, V> map2 : mapList2) {
            V v = map2.get(uniqueKeyName);
            for (Map<K, V> map1 : returnMapList) {
                if (v.equals(map1.get(uniqueKeyName))) {
                    returnMapList.remove(map1);
                    break;
                }
            }
            if (returnMapList.isEmpty()) {
                break;
            }
        }
        return returnMapList;
    }

    public static <K, V> Map<V, Map<K, V>> list2Map(List<Map<K, V>> mapList, String valueKeyName) {

        Map<V, Map<K, V>> returnMap = new HashMap<V, Map<K, V>>();
        for (Map<K, V> map : mapList) {
            returnMap.put(map.get(valueKeyName), map);
        }
        return returnMap;
    }

    public static <K, V> Map<V, List<Map<K, V>>> group(List<Map<K, V>> mapList, String groupKeyName) {

        Map<V, List<Map<K, V>>> returnGroupList = new HashMap<V, List<Map<K, V>>>();
        for (Map<K, V> map : mapList) {
            V value = map.get(groupKeyName);
            if (!returnGroupList.containsKey(value)) {
                returnGroupList.put(value, new ArrayList<Map<K, V>>());
            }
            returnGroupList.get(value).add(map);
        }
        return returnGroupList;
    }

    public static boolean isNotNullAndEmpty(Map<?, ?> map) {

        return !isNullOrEmpty(map);
    }

    public static <T> boolean isNullOrEmpty(Map<?, ?> map) {

        return map == null || map.isEmpty();
    }

    public static Map<String, Object> object2Map(Object object, boolean ignoreNull, boolean ignoreEmpty) {
        Map<String, Object> map = new HashMap<String, Object>();
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object value = field.get(object);

                if (ignoreNull && value == null) {
                    continue;
                }

                if (ignoreEmpty && (value == null || value.equals(StringUtil.EMPTY_STRING))) {
                    continue;
                }
                map.put(field.getName(), value);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    public static Map<String, Object> object2Map(Object object) {
        return object2Map(object, true, false);
    }

    public static Map<String, Object> xml2Map(String responseXml) throws ParserConfigurationException, SAXException, IOException {
        // 这里用Dom的方式解析回包的最主要目的是防止API新增回包字段
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        ByteArrayInputStream is = new ByteArrayInputStream(responseXml.getBytes());
        try {
            Document document = builder.parse(is);
            NodeList allNodes = document.getFirstChild().getChildNodes();
            return getSubMap(allNodes);
        } finally {
            is.close();
        }

    }

    private static Map<String, Object> getSubMap(NodeList allNodes) {
        Map<String, Object> map = new HashMap<String, Object>();
        int countLength = allNodes.getLength();
        for (int i = 0; i < countLength; i++) {
            Node node = allNodes.item(i);
            if (node instanceof Element) {
                if (node.getChildNodes().getLength() > 0 && node.getFirstChild() instanceof Element) {
                    map.put(node.getNodeName(), getSubMap(node.getChildNodes()));
                } else {
                    map.put(node.getNodeName(), node.getTextContent());
                }
            }
        }
        return map;
    }

    /**
     * Gets from a Map in a null-safe manner.
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param map the map to use
     * @param key the key to look up
     * @return the value in the Map, <code>null</code> if null map input
     */
    public static <K, V> V getObject(final Map<? super K, V> map, final K key) {
        if (map != null) {
            return map.get(key);
        }
        return null;
    }

    /**
     * Gets a String from a Map in a null-safe manner.
     * <p>
     * The String is obtained via <code>toString</code>.
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @return the value in the Map as a String, <code>null</code> if null map
     * input
     */
    public static <K> String getString(final Map<? super K, ?> map, final K key) {
        if (map != null) {
            final Object answer = map.get(key);
            if (answer != null) {
                return answer.toString();
            }
        }
        return null;
    }

    // Type safe getters
    // -------------------------------------------------------------------------

    public static <K> String getStringValue(final Map<? super K, ?> map, final K key) {
        return getString(map, key);
    }

    /**
     * Gets a Boolean from a Map in a null-safe manner.
     * <p>
     * If the value is a <code>Boolean</code> it is returned directly. If the
     * value is a <code>String</code> and it equals 'true' ignoring case then
     * <code>true</code> is returned, otherwise <code>false</code>. If the value
     * is a <code>Number</code> an integer zero value returns <code>false</code>
     * and non-zero returns <code>true</code>. Otherwise, <code>null</code> is
     * returned.
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @return the value in the Map as a Boolean, <code>null</code> if null map
     * input
     */
    public static <K> Boolean getBoolean(final Map<? super K, ?> map, final K key) {
        if (map != null) {
            final Object answer = map.get(key);
            if (answer != null) {
                if (answer instanceof Boolean) {
                    return (Boolean) answer;
                }
                if (answer instanceof String) {
                    return Boolean.valueOf((String) answer);
                }
                if (answer instanceof Number) {
                    final Number n = (Number) answer;
                    return n.intValue() != 0 ? Boolean.TRUE : Boolean.FALSE;
                }
            }
        }
        return Boolean.FALSE;
    }

    /**
     * Gets a Number from a Map in a null-safe manner.
     * <p>
     * If the value is a <code>Number</code> it is returned directly. If the
     * value is a <code>String</code> it is converted using
     * {@link NumberFormat#parse(String)} on the system default formatter
     * returning <code>null</code> if the conversion fails. Otherwise,
     * <code>null</code> is returned.
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @return the value in the Map as a Number, <code>null</code> if null map
     * input
     */
    public static <K> Number getNumber(final Map<? super K, ?> map, final K key) {
        if (map != null) {
            final Object answer = map.get(key);
            if (answer != null) {
                if (answer instanceof Number) {
                    return (Number) answer;
                }
                if (answer instanceof Boolean) {
                    return ((Boolean) answer) ? 1 : 0;
                }
                if (answer instanceof String) {
                    try {
                        final String text = (String) answer;
                        return NumberFormat.getInstance().parse(text);
                    } catch (final ParseException e) { // NOPMD
                        // failure means null is returned
                    }
                }
            }
        }
        return null;
    }

    /**
     * Gets a Byte from a Map in a null-safe manner.
     * <p>
     * The Byte is obtained from the results of {@link #getNumber(Map, Object)}.
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @return the value in the Map as a Byte, <code>null</code> if null map
     * input
     */
    public static <K> Byte getByte(final Map<? super K, ?> map, final K key) {
        final Number answer = getNumber(map, key);
        if (answer == null) {
            return null;
        }
        if (answer instanceof Byte) {
            return (Byte) answer;
        }
        return Byte.valueOf(answer.byteValue());
    }

    /**
     * Gets a Short from a Map in a null-safe manner.
     * <p>
     * The Short is obtained from the results of {@link #getNumber(Map, Object)}.
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @return the value in the Map as a Short, <code>null</code> if null map
     * input
     */
    public static <K> Short getShort(final Map<? super K, ?> map, final K key) {
        final Number answer = getNumber(map, key);
        if (answer == null) {
            return null;
        }
        if (answer instanceof Short) {
            return (Short) answer;
        }
        return Short.valueOf(answer.shortValue());
    }

    /**
     * Gets a Integer from a Map in a null-safe manner.
     * <p>
     * The Integer is obtained from the results of
     * {@link #getNumber(Map, Object)}.
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @return the value in the Map as a Integer, <code>null</code> if null map
     * input
     */
    public static <K> Integer getInteger(final Map<? super K, ?> map, final K key) {
        final Number answer = getNumber(map, key);
        if (answer == null) {
            return null;
        }
        if (answer instanceof Integer) {
            return (Integer) answer;
        }
        return Integer.valueOf(answer.intValue());
    }

    /**
     * Gets a Long from a Map in a null-safe manner.
     * <p>
     * The Long is obtained from the results of {@link #getNumber(Map, Object)}.
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @return the value in the Map as a Long, <code>null</code> if null map
     * input
     */
    public static <K> Long getLong(final Map<? super K, ?> map, final K key) {
        final Number answer = getNumber(map, key);
        if (answer == null) {
            return null;
        }
        if (answer instanceof Long) {
            return (Long) answer;
        }
        return Long.valueOf(answer.longValue());
    }

    /**
     * Gets a Float from a Map in a null-safe manner.
     * <p>
     * The Float is obtained from the results of {@link #getNumber(Map, Object)}.
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @return the value in the Map as a Float, <code>null</code> if null map
     * input
     */
    public static <K> Float getFloat(final Map<? super K, ?> map, final K key) {
        final Number answer = getNumber(map, key);
        if (answer == null) {
            return null;
        }
        if (answer instanceof Float) {
            return (Float) answer;
        }
        return Float.valueOf(answer.floatValue());
    }

    /**
     * Gets a Double from a Map in a null-safe manner.
     * <p>
     * The Double is obtained from the results of
     * {@link #getNumber(Map, Object)}.
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @return the value in the Map as a Double, <code>null</code> if null map
     * input
     */
    public static <K> Double getDouble(final Map<? super K, ?> map, final K key) {
        final Number answer = getNumber(map, key);
        if (answer == null) {
            return null;
        }
        if (answer instanceof Double) {
            return (Double) answer;
        }
        return Double.valueOf(answer.doubleValue());
    }

    /**
     * Gets a Map from a Map in a null-safe manner.
     * <p>
     * If the value returned from the specified map is not a Map then
     * <code>null</code> is returned.
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @return the value in the Map as a Map, <code>null</code> if null map
     * input
     */
    public static <K> Map<?, ?> getMap(final Map<? super K, ?> map, final K key) {
        if (map != null) {
            final Object answer = map.get(key);
            if (answer != null && answer instanceof Map) {
                return (Map<?, ?>) answer;
            }
        }
        return null;
    }

    /**
     * Looks up the given key in the given map, converting null into the given
     * default value.
     *
     * @param <K>          the key type
     * @param <V>          the value type
     * @param map          the map whose value to look up
     * @param key          the key of the value to look up in that map
     * @param defaultValue what to return if the value is null
     * @return the value in the map, or defaultValue if the original value is
     * null or the map is null
     */
    public static <K, V> V getObject(final Map<K, V> map, final K key, final V defaultValue) {
        if (map != null) {
            final V answer = map.get(key);
            if (answer != null) {
                return answer;
            }
        }
        return defaultValue;
    }

    /**
     * Looks up the given key in the given map, converting the result into a
     * string, using the default value if the the conversion fails.
     *
     * @param <K>          the key type
     * @param map          the map whose value to look up
     * @param key          the key of the value to look up in that map
     * @param defaultValue what to return if the value is null or if the conversion fails
     * @return the value in the map as a string, or defaultValue if the original
     * value is null, the map is null or the string conversion fails
     */
    public static <K> String getString(final Map<? super K, ?> map, final K key, final String defaultValue) {
        String answer = getString(map, key);
        if (answer == null) {
            answer = defaultValue;
        }
        return answer;
    }

    // Type safe getters with default values
    // -------------------------------------------------------------------------

    /**
     * Looks up the given key in the given map, converting the result into a
     * boolean, using the default value if the the conversion fails.
     *
     * @param <K>          the key type
     * @param map          the map whose value to look up
     * @param key          the key of the value to look up in that map
     * @param defaultValue what to return if the value is null or if the conversion fails
     * @return the value in the map as a boolean, or defaultValue if the
     * original value is null, the map is null or the boolean conversion
     * fails
     */
    public static <K> Boolean getBoolean(final Map<? super K, ?> map, final K key, final Boolean defaultValue) {
        Boolean answer = getBoolean(map, key);
        if (answer == null) {
            answer = defaultValue;
        }
        return answer;
    }

    /**
     * Looks up the given key in the given map, converting the result into a
     * number, using the default value if the the conversion fails.
     *
     * @param <K>          the key type
     * @param map          the map whose value to look up
     * @param key          the key of the value to look up in that map
     * @param defaultValue what to return if the value is null or if the conversion fails
     * @return the value in the map as a number, or defaultValue if the original
     * value is null, the map is null or the number conversion fails
     */
    public static <K> Number getNumber(final Map<? super K, ?> map, final K key, final Number defaultValue) {
        Number answer = getNumber(map, key);
        if (answer == null) {
            answer = defaultValue;
        }
        return answer;
    }

    /**
     * Looks up the given key in the given map, converting the result into a
     * byte, using the default value if the the conversion fails.
     *
     * @param <K>          the key type
     * @param map          the map whose value to look up
     * @param key          the key of the value to look up in that map
     * @param defaultValue what to return if the value is null or if the conversion fails
     * @return the value in the map as a number, or defaultValue if the original
     * value is null, the map is null or the number conversion fails
     */
    public static <K> Byte getByte(final Map<? super K, ?> map, final K key, final Byte defaultValue) {
        Byte answer = getByte(map, key);
        if (answer == null) {
            answer = defaultValue;
        }
        return answer;
    }

    /**
     * Looks up the given key in the given map, converting the result into a
     * short, using the default value if the the conversion fails.
     *
     * @param <K>          the key type
     * @param map          the map whose value to look up
     * @param key          the key of the value to look up in that map
     * @param defaultValue what to return if the value is null or if the conversion fails
     * @return the value in the map as a number, or defaultValue if the original
     * value is null, the map is null or the number conversion fails
     */
    public static <K> Short getShort(final Map<? super K, ?> map, final K key, final Short defaultValue) {
        Short answer = getShort(map, key);
        if (answer == null) {
            answer = defaultValue;
        }
        return answer;
    }

    /**
     * Looks up the given key in the given map, converting the result into an
     * integer, using the default value if the the conversion fails.
     *
     * @param <K>          the key type
     * @param map          the map whose value to look up
     * @param key          the key of the value to look up in that map
     * @param defaultValue what to return if the value is null or if the conversion fails
     * @return the value in the map as a number, or defaultValue if the original
     * value is null, the map is null or the number conversion fails
     */
    public static <K> Integer getInteger(final Map<? super K, ?> map, final K key, final Integer defaultValue) {
        Integer answer = getInteger(map, key);
        if (answer == null) {
            answer = defaultValue;
        }
        return answer;
    }

    /**
     * Looks up the given key in the given map, converting the result into a
     * long, using the default value if the the conversion fails.
     *
     * @param <K>          the key type
     * @param map          the map whose value to look up
     * @param key          the key of the value to look up in that map
     * @param defaultValue what to return if the value is null or if the conversion fails
     * @return the value in the map as a number, or defaultValue if the original
     * value is null, the map is null or the number conversion fails
     */
    public static <K> Long getLong(final Map<? super K, ?> map, final K key, final Long defaultValue) {
        Long answer = getLong(map, key);
        if (answer == null) {
            answer = defaultValue;
        }
        return answer;
    }

    /**
     * Looks up the given key in the given map, converting the result into a
     * float, using the default value if the the conversion fails.
     *
     * @param <K>          the key type
     * @param map          the map whose value to look up
     * @param key          the key of the value to look up in that map
     * @param defaultValue what to return if the value is null or if the conversion fails
     * @return the value in the map as a number, or defaultValue if the original
     * value is null, the map is null or the number conversion fails
     */
    public static <K> Float getFloat(final Map<? super K, ?> map, final K key, final Float defaultValue) {
        Float answer = getFloat(map, key);
        if (answer == null) {
            answer = defaultValue;
        }
        return answer;
    }

    /**
     * Looks up the given key in the given map, converting the result into a
     * double, using the default value if the the conversion fails.
     *
     * @param <K>          the key type
     * @param map          the map whose value to look up
     * @param key          the key of the value to look up in that map
     * @param defaultValue what to return if the value is null or if the conversion fails
     * @return the value in the map as a number, or defaultValue if the original
     * value is null, the map is null or the number conversion fails
     */
    public static <K> Double getDouble(final Map<? super K, ?> map, final K key, final Double defaultValue) {
        Double answer = getDouble(map, key);
        if (answer == null) {
            answer = defaultValue;
        }
        return answer;
    }

    /**
     * Looks up the given key in the given map, converting the result into a
     * map, using the default value if the the conversion fails.
     *
     * @param <K>          the key type
     * @param map          the map whose value to look up
     * @param key          the key of the value to look up in that map
     * @param defaultValue what to return if the value is null or if the conversion fails
     * @return the value in the map as a number, or defaultValue if the original
     * value is null, the map is null or the map conversion fails
     */
    public static <K> Map<?, ?> getMap(final Map<? super K, ?> map, final K key, final Map<?, ?> defaultValue) {
        Map<?, ?> answer = getMap(map, key);
        if (answer == null) {
            answer = defaultValue;
        }
        return answer;
    }

    /**
     * Gets a boolean from a Map in a null-safe manner.
     * <p>
     * If the value is a <code>Boolean</code> its value is returned. If the
     * value is a <code>String</code> and it equals 'true' ignoring case then
     * <code>true</code> is returned, otherwise <code>false</code>. If the value
     * is a <code>Number</code> an integer zero value returns <code>false</code>
     * and non-zero returns <code>true</code>. Otherwise, <code>false</code> is
     * returned.
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @return the value in the Map as a Boolean, <code>false</code> if null map
     * input
     */
    public static <K> boolean getBooleanValue(final Map<? super K, ?> map, final K key) {
        return Boolean.TRUE.equals(getBoolean(map, key));
    }

    /**
     * Gets a byte from a Map in a null-safe manner.
     * <p>
     * The byte is obtained from the results of {@link #getNumber(Map, Object)}.
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @return the value in the Map as a byte, <code>0</code> if null map input
     */
    public static <K> byte getByteValue(final Map<? super K, ?> map, final K key) {
        final Byte byteObject = getByte(map, key);
        if (byteObject == null) {
            return 0;
        }
        return byteObject.byteValue();
    }

    // Type safe primitive getters
    // -------------------------------------------------------------------------

    /**
     * Gets a short from a Map in a null-safe manner.
     * <p>
     * The short is obtained from the results of {@link #getNumber(Map, Object)}.
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @return the value in the Map as a short, <code>0</code> if null map input
     */
    public static <K> short getShortValue(final Map<? super K, ?> map, final K key) {
        final Short shortObject = getShort(map, key);
        if (shortObject == null) {
            return 0;
        }
        return shortObject.shortValue();
    }

    /**
     * Gets an int from a Map in a null-safe manner.
     * <p>
     * The int is obtained from the results of {@link #getNumber(Map, Object)}.
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @return the value in the Map as an int, <code>0</code> if null map input
     */
    public static <K> int getIntValue(final Map<? super K, ?> map, final K key) {
        final Integer integerObject = getInteger(map, key);
        if (integerObject == null) {
            return 0;
        }
        return integerObject.intValue();
    }

    /**
     * Gets a long from a Map in a null-safe manner.
     * <p>
     * The long is obtained from the results of {@link #getNumber(Map, Object)}.
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @return the value in the Map as a long, <code>0L</code> if null map input
     */
    public static <K> long getLongValue(final Map<? super K, ?> map, final K key) {
        final Long longObject = getLong(map, key);
        if (longObject == null) {
            return 0L;
        }
        return longObject.longValue();
    }

    /**
     * Gets a float from a Map in a null-safe manner.
     * <p>
     * The float is obtained from the results of {@link #getNumber(Map, Object)}.
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @return the value in the Map as a float, <code>0.0F</code> if null map
     * input
     */
    public static <K> float getFloatValue(final Map<? super K, ?> map, final K key) {
        final Float floatObject = getFloat(map, key);
        if (floatObject == null) {
            return 0f;
        }
        return floatObject.floatValue();
    }

    /**
     * Gets a double from a Map in a null-safe manner.
     * <p>
     * The double is obtained from the results of
     * {@link #getNumber(Map, Object)}.
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @return the value in the Map as a double, <code>0.0</code> if null map
     * input
     */
    public static <K> double getDoubleValue(final Map<? super K, ?> map, final K key) {
        final Double doubleObject = getDouble(map, key);
        if (doubleObject == null) {
            return 0d;
        }
        return doubleObject.doubleValue();
    }

    /**
     * Gets a boolean from a Map in a null-safe manner, using the default value
     * if the the conversion fails.
     * <p>
     * If the value is a <code>Boolean</code> its value is returned. If the
     * value is a <code>String</code> and it equals 'true' ignoring case then
     * <code>true</code> is returned, otherwise <code>false</code>. If the value
     * is a <code>Number</code> an integer zero value returns <code>false</code>
     * and non-zero returns <code>true</code>. Otherwise,
     * <code>defaultValue</code> is returned.
     *
     * @param <K>          the key type
     * @param map          the map to use
     * @param key          the key to look up
     * @param defaultValue return if the value is null or if the conversion fails
     * @return the value in the Map as a Boolean, <code>defaultValue</code> if
     * null map input
     */
    public static <K> boolean getBooleanValue(final Map<? super K, ?> map, final K key, final boolean defaultValue) {
        final Boolean booleanObject = getBoolean(map, key);
        if (booleanObject == null) {
            return defaultValue;
        }
        return booleanObject.booleanValue();
    }

    /**
     * Gets a byte from a Map in a null-safe manner, using the default value if
     * the the conversion fails.
     * <p>
     * The byte is obtained from the results of {@link #getNumber(Map, Object)}.
     *
     * @param <K>          the key type
     * @param map          the map to use
     * @param key          the key to look up
     * @param defaultValue return if the value is null or if the conversion fails
     * @return the value in the Map as a byte, <code>defaultValue</code> if null
     * map input
     */
    public static <K> byte getByteValue(final Map<? super K, ?> map, final K key, final byte defaultValue) {
        final Byte byteObject = getByte(map, key);
        if (byteObject == null) {
            return defaultValue;
        }
        return byteObject.byteValue();
    }

    // Type safe primitive getters with default values
    // -------------------------------------------------------------------------

    /**
     * Gets a short from a Map in a null-safe manner, using the default value if
     * the the conversion fails.
     * <p>
     * The short is obtained from the results of {@link #getNumber(Map, Object)}.
     *
     * @param <K>          the key type
     * @param map          the map to use
     * @param key          the key to look up
     * @param defaultValue return if the value is null or if the conversion fails
     * @return the value in the Map as a short, <code>defaultValue</code> if
     * null map input
     */
    public static <K> short getShortValue(final Map<? super K, ?> map, final K key, final short defaultValue) {
        final Short shortObject = getShort(map, key);
        if (shortObject == null) {
            return defaultValue;
        }
        return shortObject.shortValue();
    }

    /**
     * Gets an int from a Map in a null-safe manner, using the default value if
     * the the conversion fails.
     * <p>
     * The int is obtained from the results of {@link #getNumber(Map, Object)}.
     *
     * @param <K>          the key type
     * @param map          the map to use
     * @param key          the key to look up
     * @param defaultValue return if the value is null or if the conversion fails
     * @return the value in the Map as an int, <code>defaultValue</code> if null
     * map input
     */
    public static <K> int getIntValue(final Map<? super K, ?> map, final K key, final int defaultValue) {
        final Integer integerObject = getInteger(map, key);
        if (integerObject == null) {
            return defaultValue;
        }
        return integerObject.intValue();
    }

    /**
     * Gets a long from a Map in a null-safe manner, using the default value if
     * the the conversion fails.
     * <p>
     * The long is obtained from the results of {@link #getNumber(Map, Object)}.
     *
     * @param <K>          the key type
     * @param map          the map to use
     * @param key          the key to look up
     * @param defaultValue return if the value is null or if the conversion fails
     * @return the value in the Map as a long, <code>defaultValue</code> if null
     * map input
     */
    public static <K> long getLongValue(final Map<? super K, ?> map, final K key, final long defaultValue) {
        final Long longObject = getLong(map, key);
        if (longObject == null) {
            return defaultValue;
        }
        return longObject.longValue();
    }

    /**
     * Gets a float from a Map in a null-safe manner, using the default value if
     * the the conversion fails.
     * <p>
     * The float is obtained from the results of {@link #getNumber(Map, Object)}.
     *
     * @param <K>          the key type
     * @param map          the map to use
     * @param key          the key to look up
     * @param defaultValue return if the value is null or if the conversion fails
     * @return the value in the Map as a float, <code>defaultValue</code> if
     * null map input
     */
    public static <K> float getFloatValue(final Map<? super K, ?> map, final K key, final float defaultValue) {
        final Float floatObject = getFloat(map, key);
        if (floatObject == null) {
            return defaultValue;
        }
        return floatObject.floatValue();
    }

    /**
     * Gets a double from a Map in a null-safe manner, using the default value
     * if the the conversion fails.
     * <p>
     * The double is obtained from the results of
     * {@link #getNumber(Map, Object)}.
     *
     * @param <K>          the key type
     * @param map          the map to use
     * @param key          the key to look up
     * @param defaultValue return if the value is null or if the conversion fails
     * @return the value in the Map as a double, <code>defaultValue</code> if
     * null map input
     */
    public static <K> double getDoubleValue(final Map<? super K, ?> map, final K key, final double defaultValue) {
        final Double doubleObject = getDouble(map, key);
        if (doubleObject == null) {
            return defaultValue;
        }
        return doubleObject.doubleValue();
    }

    /**
     * Gets a new Properties object initialised with the values from a Map. A
     * null input will return an empty properties object.
     * <p>
     * A Properties object may only store non-null keys and values, thus if the
     * provided map contains either a key or value which is {@code null}, a
     * {@link NullPointerException} will be thrown.
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param map the map to convert to a Properties object
     * @return the properties object
     * @throws NullPointerException if a key or value in the provided map is {@code null}
     */
    public static <K, V> Properties toProperties(final Map<K, V> map) {
        final Properties answer = new Properties();
        if (map != null) {
            for (final Entry<K, V> entry2 : map.entrySet()) {
                final Map.Entry<?, ?> entry = entry2;
                final Object key = entry.getKey();
                final Object value = entry.getValue();
                answer.put(key, value);
            }
        }
        return answer;
    }

    /**
     * Creates a new HashMap using data copied from a ResourceBundle.
     *
     * @param resourceBundle the resource bundle to convert, may not be null
     * @return the hashmap containing the data
     * @throws NullPointerException if the bundle is null
     */
    public static Map<String, Object> toMap(final ResourceBundle resourceBundle) {
        final Enumeration<String> enumeration = resourceBundle.getKeys();
        final Map<String, Object> map = new HashMap<String, Object>();

        while (enumeration.hasMoreElements()) {
            final String key = enumeration.nextElement();
            final Object value = resourceBundle.getObject(key);
            map.put(key, value);
        }

        return map;
    }

    // Conversion methods
    // -------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> buildMap(Object... params) {
        if (params == null) {
            return new HashMap<K, V>();
        }
        Map<K, V> map = new HashMap<K, V>(params.length / 2);
        for (int i = 0; i < params.length; i += 2) {
            map.put((K) params[i], (V) params[i + 1]);
        }
        return map;
    }

    public static <K> BigDecimal getBigDecimal(final Map<? super K, ?> map, final K key) {
        final Number answer = getNumber(map, key);
        if (answer == null) {
            return null;
        }
        if (answer instanceof BigDecimal) {
            return (BigDecimal) answer;
        }
        return BigDecimal.valueOf(answer.doubleValue());
    }

    public static <K, V> void retain(final Map<K, V> map1, final Map<K, V> map2) {
        Set<K> keys = new HashSet<K>(map1.keySet());
        for (K key : keys) {
            if (map2.containsKey(key)) {
                continue;
            }
            map1.remove(key);
        }
    }

    public static <K, V> boolean equal(final Map<K, V> map1, final Map<K, V> map2) {
        Set<Entry<K, V>> entries = map1.entrySet();
        for (Entry<K, V> entry : entries) {
            V map1Value = entry.getValue();
            V map2Value = map2.get(entry.getKey());
            boolean isEqual = map1Value == null ? map2Value == null : map1Value.equals(map2Value);
            if (!isEqual) {
                return false;
            }
        }
        return false;
    }

    public static <T> T map2Object(Map<String, Object> map, Class<T> clazz) {
        if (map instanceof JSONObject) {
            return JSON.toJavaObject((JSONObject) map, clazz);
        } else {
            JSONObject jsonObject = new JSONObject(map);
            return JSON.toJavaObject(jsonObject, clazz);
        }
    }

    static class Man extends User {
        private String xx;

        /**
         * @return the xx
         */
        public String getXx() {
            return xx;
        }

        /**
         * @param xx the xx to set
         */
        public void setXx(String xx) {
            this.xx = xx;
        }
    }

    static class User {
        private String userName;
        private Boolean sex;
        private Byte age;
        private Integer height;
        private Long aa;
        private Float bb;
        private Double cc;
        private Short dd;

        /**
         * @return the userName
         */
        public String getUserName() {
            return userName;
        }

        /**
         * @param userName the userName to set
         */
        public void setUserName(String userName) {
            this.userName = userName;
        }

        /**
         * @return the sex
         */
        public Boolean getSex() {
            return sex;
        }

        /**
         * @param sex the sex to set
         */
        public void setSex(Boolean sex) {
            this.sex = sex;
        }

        /**
         * @return the age
         */
        public Byte getAge() {
            return age;
        }

        /**
         * @param age the age to set
         */
        public void setAge(Byte age) {
            this.age = age;
        }

        /**
         * @return the height
         */
        public Integer getHeight() {
            return height;
        }

        /**
         * @param height the height to set
         */
        public void setHeight(Integer height) {
            this.height = height;
        }

        /**
         * @return the aa
         */
        public Long getAa() {
            return aa;
        }

        /**
         * @param aa the aa to set
         */
        public void setAa(Long aa) {
            this.aa = aa;
        }

        /**
         * @return the bb
         */
        public Float getBb() {
            return bb;
        }

        /**
         * @param bb the bb to set
         */
        public void setBb(Float bb) {
            this.bb = bb;
        }

        /**
         * @return the cc
         */
        public Double getCc() {
            return cc;
        }

        /**
         * @param cc the cc to set
         */
        public void setCc(Double cc) {
            this.cc = cc;
        }

        /**
         * @return the dd
         */
        public Short getDd() {
            return dd;
        }

        /**
         * @param dd the dd to set
         */
        public void setDd(Short dd) {
            this.dd = dd;
        }
    }

}
