/**
 * dol-framework-core
 * HashMapEx.java
 * org.dol.framework.util
 * TODO
 *
 * @author dolphin
 * @date 2016年9月23日 上午10:20:26
 * @Copyright 2016, 唯创国际 幸福9号 All Rights Reserved.
 * @version 1.0
 */

package org.dol.framework.util;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * ClassName:HashMapEx <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2016年9月23日 上午10:20:26 <br/>
 *
 * @author dolphin
 * @version 1.0
 * @param <K>
 * @param <V>
 * @since JDK 1.7
 * @see
 */
public class HashMap2<K, V> extends HashMap<K, V> {

    private static final long serialVersionUID = 1L;

    // Type safe getters
    // -------------------------------------------------------------------------

    /**
     * Gets from a Map in a null-safe manner.
     *
     * @param <K>
     *            the key type
     * @param <V>
     *            the value type
     *
     *
     * @param key
     *            the key to look up
     * @return the value in the Map, <code>null</code> if null map input
     */
    public V getObject(final K key) {
        return this.get(key);
    }

    /**
     * Gets a String from a Map in a null-safe manner.
     * <p>
     * The String is obtained via <code>toString</code>.
     *
     * @param <K>
     *            the key type
     *
     *
     * @param key
     *            the key to look up
     * @return the value in the Map as a String, <code>null</code> if null map
     *         input
     */
    public String getString(final K key) {
        return MapUtil.getString(this, key);
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
     * @param <K>
     *            the key type
     *
     *
     * @param key
     *            the key to look up
     * @return the value in the Map as a Boolean, <code>null</code> if null map
     *         input
     */
    public Boolean getBoolean(final K key) {
        return MapUtil.getBoolean(this, key);
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
     * @param <K>
     *            the key type
     *
     *
     * @param key
     *            the key to look up
     * @return the value in the Map as a Number, <code>null</code> if null map
     *         input
     */
    public Number getNumber(final K key) {
        return MapUtil.getNumber(this, key);
    }

    /**
     * Gets a Byte from a Map in a null-safe manner.
     * <p>
     * The Byte is obtained from the results of {@link #getNumber(Map, Object)}.
     *
     * @param <K>
     *            the key type
     *
     *
     * @param key
     *            the key to look up
     * @return the value in the Map as a Byte, <code>null</code> if null map
     *         input
     */
    public Byte getByte(final K key) {
        return MapUtil.getByte(this, key);
    }

    /**
     * Gets a Short from a Map in a null-safe manner.
     * <p>
     * The Short is obtained from the results of {@link #getNumber(Map, Object)}.
     *
     * @param <K>
     *            the key type
     *
     *
     * @param key
     *            the key to look up
     * @return the value in the Map as a Short, <code>null</code> if null map
     *         input
     */
    public Short getShort(final K key) {
        return MapUtil.getShort(this, key);
    }

    /**
     * Gets a Integer from a Map in a null-safe manner.
     * <p>
     * The Integer is obtained from the results of
     * {@link #getNumber(Map, Object)}.
     *
     * @param <K>
     *            the key type
     *
     *
     * @param key
     *            the key to look up
     * @return the value in the Map as a Integer, <code>null</code> if null map
     *         input
     */
    public Integer getInteger(final K key) {
        return MapUtil.getInteger(this, key);
    }

    /**
     * Gets a Long from a Map in a null-safe manner.
     * <p>
     * The Long is obtained from the results of {@link #getNumber(Map, Object)}.
     *
     * @param <K>
     *            the key type
     *
     *
     * @param key
     *            the key to look up
     * @return the value in the Map as a Long, <code>null</code> if null map
     *         input
     */
    public Long getLong(final K key) {
        return MapUtil.getLong(this, key);
    }

    /**
     * Gets a Float from a Map in a null-safe manner.
     * <p>
     * The Float is obtained from the results of {@link #getNumber(Map, Object)}.
     *
     * @param <K>
     *            the key type
     *
     *
     * @param key
     *            the key to look up
     * @return the value in the Map as a Float, <code>null</code> if null map
     *         input
     */
    public Float getFloat(final K key) {
        return MapUtil.getFloat(this, key);
    }

    /**
     * Gets a Double from a Map in a null-safe manner.
     * <p>
     * The Double is obtained from the results of
     * {@link #getNumber(Map, Object)}.
     *
     * @param <K>
     *            the key type
     *
     *
     * @param key
     *            the key to look up
     * @return the value in the Map as a Double, <code>null</code> if null map
     *         input
     */
    public Double getDouble(final K key) {
        return MapUtil.getDouble(this, key);
    }

    /**
     * Gets a Map from a Map in a null-safe manner.
     * <p>
     * If the value returned from the specified map is not a Map then
     * <code>null</code> is returned.
     *
     * @param <K>
     *            the key type
     *
     *
     * @param key
     *            the key to look up
     * @return the value in the Map as a Map, <code>null</code> if null map
     *         input
     */
    public Map<?, ?> getMap(final K key) {
        return MapUtil.getMap(this, key);
    }

    // Type safe getters with default values
    // -------------------------------------------------------------------------

    /**
     * Looks up the given key in the given map, converting null into the given
     * default value.
     *
     * @param <K>
     *            the key type
     * @param <V>
     *            the value type
     *
     *            the map whose value to look up
     * @param key
     *            the key of the value to look up in that map
     * @param defaultValue
     *            what to return if the value is null
     * @return the value in the map, or defaultValue if the original value is
     *         null or the map is null
     */
    public V getObject(final K key, final V defaultValue) {
        return MapUtil.getObject(this, key, defaultValue);
    }

    /**
     * Looks up the given key in the given map, converting the result into a
     * string, using the default value if the the conversion fails.
     *
     * @param <K>
     *            the key type
     *
     *            the map whose value to look up
     * @param key
     *            the key of the value to look up in that map
     * @param defaultValue
     *            what to return if the value is null or if the conversion fails
     * @return the value in the map as a string, or defaultValue if the original
     *         value is null, the map is null or the string conversion fails
     */
    public String getString(final K key, final String defaultValue) {
        return MapUtil.getString(this, key, defaultValue);
    }

    /**
     * Looks up the given key in the given map, converting the result into a
     * boolean, using the default value if the the conversion fails.
     *
     * @param <K>
     *            the key type
     *
     *            the map whose value to look up
     * @param key
     *            the key of the value to look up in that map
     * @param defaultValue
     *            what to return if the value is null or if the conversion fails
     * @return the value in the map as a boolean, or defaultValue if the
     *         original value is null, the map is null or the boolean conversion
     *         fails
     */
    public Boolean getBoolean(final K key, final Boolean defaultValue) {
        return MapUtil.getBoolean(this, key, defaultValue);
    }

    /**
     * Looks up the given key in the given map, converting the result into a
     * number, using the default value if the the conversion fails.
     *
     * @param <K>
     *            the key type
     *
     *            the map whose value to look up
     * @param key
     *            the key of the value to look up in that map
     * @param defaultValue
     *            what to return if the value is null or if the conversion fails
     * @return the value in the map as a number, or defaultValue if the original
     *         value is null, the map is null or the number conversion fails
     */
    public Number getNumber(final K key, final Number defaultValue) {
        return MapUtil.getNumber(this, key, defaultValue);
    }

    /**
     * Looks up the given key in the given map, converting the result into a
     * byte, using the default value if the the conversion fails.
     *
     * @param <K>
     *            the key type
     *
     *            the map whose value to look up
     * @param key
     *            the key of the value to look up in that map
     * @param defaultValue
     *            what to return if the value is null or if the conversion fails
     * @return the value in the map as a number, or defaultValue if the original
     *         value is null, the map is null or the number conversion fails
     */
    public Byte getByte(final K key, final Byte defaultValue) {
        return MapUtil.getByte(this, key, defaultValue);
    }

    /**
     * Looks up the given key in the given map, converting the result into a
     * short, using the default value if the the conversion fails.
     *
     * @param <K>
     *            the key type
     *
     *            the map whose value to look up
     * @param key
     *            the key of the value to look up in that map
     * @param defaultValue
     *            what to return if the value is null or if the conversion fails
     * @return the value in the map as a number, or defaultValue if the original
     *         value is null, the map is null or the number conversion fails
     */
    public Short getShort(final K key, final Short defaultValue) {
        return MapUtil.getShort(this, key, defaultValue);
    }

    /**
     * Looks up the given key in the given map, converting the result into an
     * integer, using the default value if the the conversion fails.
     *
     * @param <K>
     *            the key type
     *
     *            the map whose value to look up
     * @param key
     *            the key of the value to look up in that map
     * @param defaultValue
     *            what to return if the value is null or if the conversion fails
     * @return the value in the map as a number, or defaultValue if the original
     *         value is null, the map is null or the number conversion fails
     */
    public Integer getInteger(final K key, final Integer defaultValue) {
        return MapUtil.getInteger(this, key, defaultValue);
    }

    /**
     * Looks up the given key in the given map, converting the result into a
     * long, using the default value if the the conversion fails.
     *
     * @param <K>
     *            the key type
     *
     *            the map whose value to look up
     * @param key
     *            the key of the value to look up in that map
     * @param defaultValue
     *            what to return if the value is null or if the conversion fails
     * @return the value in the map as a number, or defaultValue if the original
     *         value is null, the map is null or the number conversion fails
     */
    public Long getLong(final K key, final Long defaultValue) {
        return MapUtil.getLong(this, key, defaultValue);
    }

    /**
     * Looks up the given key in the given map, converting the result into a
     * float, using the default value if the the conversion fails.
     *
     * @param <K>
     *            the key type
     *
     *            the map whose value to look up
     * @param key
     *            the key of the value to look up in that map
     * @param defaultValue
     *            what to return if the value is null or if the conversion fails
     * @return the value in the map as a number, or defaultValue if the original
     *         value is null, the map is null or the number conversion fails
     */
    public Float getFloat(final K key, final Float defaultValue) {
        return MapUtil.getFloat(this, key, defaultValue);
    }

    /**
     * Looks up the given key in the given map, converting the result into a
     * double, using the default value if the the conversion fails.
     *
     * @param <K>
     *            the key type
     *
     *            the map whose value to look up
     * @param key
     *            the key of the value to look up in that map
     * @param defaultValue
     *            what to return if the value is null or if the conversion fails
     * @return the value in the map as a number, or defaultValue if the original
     *         value is null, the map is null or the number conversion fails
     */
    public Double getDouble(final K key, final Double defaultValue) {
        return MapUtil.getDouble(this, key, defaultValue);
    }

    /**
     * Looks up the given key in the given map, converting the result into a
     * map, using the default value if the the conversion fails.
     *
     * @param <K>
     *            the key type
     *
     *            the map whose value to look up
     * @param key
     *            the key of the value to look up in that map
     * @param defaultValue
     *            what to return if the value is null or if the conversion fails
     * @return the value in the map as a number, or defaultValue if the original
     *         value is null, the map is null or the map conversion fails
     */
    public Map<?, ?> getMap(final K key, final Map<?, ?> defaultValue) {
        return MapUtil.getMap(this, key, defaultValue);
    }

    // Type safe primitive getters
    // -------------------------------------------------------------------------

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
     * @param <K>
     *            the key type
     *
     *
     * @param key
     *            the key to look up
     * @return the value in the Map as a Boolean, <code>false</code> if null map
     *         input
     */
    public boolean getBooleanValue(final K key) {
        return MapUtil.getBooleanValue(this, key);
    }

    /**
     * Gets a byte from a Map in a null-safe manner.
     * <p>
     * The byte is obtained from the results of {@link #getNumber(Map, Object)}.
     *
     * @param <K>
     *            the key type
     *
     *
     * @param key
     *            the key to look up
     * @return the value in the Map as a byte, <code>0</code> if null map input
     */
    public byte getByteValue(final K key) {
        return MapUtil.getByteValue(this, key);
    }

    /**
     * Gets a short from a Map in a null-safe manner.
     * <p>
     * The short is obtained from the results of {@link #getNumber(Map, Object)}.
     *
     * @param <K>
     *            the key type
     *
     *
     * @param key
     *            the key to look up
     * @return the value in the Map as a short, <code>0</code> if null map input
     */
    public short getShortValue(final K key) {
        return MapUtil.getShortValue(this, key);
    }

    /**
     * Gets an int from a Map in a null-safe manner.
     * <p>
     * The int is obtained from the results of {@link #getNumber(Map, Object)}.
     *
     * @param <K>
     *            the key type
     *
     *
     * @param key
     *            the key to look up
     * @return the value in the Map as an int, <code>0</code> if null map input
     */
    public int getIntValue(final K key) {
        return MapUtil.getIntValue(this, key);
    }

    /**
     * Gets a long from a Map in a null-safe manner.
     * <p>
     * The long is obtained from the results of {@link #getNumber(Map, Object)}.
     *
     * @param <K>
     *            the key type
     *
     *
     * @param key
     *            the key to look up
     * @return the value in the Map as a long, <code>0L</code> if null map input
     */
    public long getLongValue(final K key) {
        return MapUtil.getLongValue(this, key);
    }

    /**
     * Gets a float from a Map in a null-safe manner.
     * <p>
     * The float is obtained from the results of {@link #getNumber(Map, Object)}.
     *
     * @param <K>
     *            the key type
     *
     *
     * @param key
     *            the key to look up
     * @return the value in the Map as a float, <code>0.0F</code> if null map
     *         input
     */
    public float getFloatValue(final K key) {
        return MapUtil.getFloatValue(this, key);
    }

    /**
     * Gets a double from a Map in a null-safe manner.
     * <p>
     * The double is obtained from the results of
     * {@link #getNumber(Map, Object)}.
     *
     * @param <K>
     *            the key type
     *
     *
     * @param key
     *            the key to look up
     * @return the value in the Map as a double, <code>0.0</code> if null map
     *         input
     */
    public double getDoubleValue(final K key) {
        return MapUtil.getDoubleValue(this, key);
    }

    // Type safe primitive getters with default values
    // -------------------------------------------------------------------------

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
     * @param <K>
     *            the key type
     *
     *
     * @param key
     *            the key to look up
     * @param defaultValue
     *            return if the value is null or if the conversion fails
     * @return the value in the Map as a Boolean, <code>defaultValue</code> if
     *         null map input
     */
    public boolean getBooleanValue(final K key, final boolean defaultValue) {
        return MapUtil.getBooleanValue(this, key, defaultValue);
    }

    /**
     * Gets a byte from a Map in a null-safe manner, using the default value if
     * the the conversion fails.
     * <p>
     * The byte is obtained from the results of {@link #getNumber(Map, Object)}.
     *
     * @param <K>
     *            the key type
     *
     *
     * @param key
     *            the key to look up
     * @param defaultValue
     *            return if the value is null or if the conversion fails
     * @return the value in the Map as a byte, <code>defaultValue</code> if null
     *         map input
     */
    public byte getByteValue(final K key, final byte defaultValue) {
        return MapUtil.getByteValue(this, key, defaultValue);
    }

    /**
     * Gets a short from a Map in a null-safe manner, using the default value if
     * the the conversion fails.
     * <p>
     * The short is obtained from the results of {@link #getNumber(Map, Object)}.
     *
     * @param <K>
     *            the key type
     *
     *
     * @param key
     *            the key to look up
     * @param defaultValue
     *            return if the value is null or if the conversion fails
     * @return the value in the Map as a short, <code>defaultValue</code> if
     *         null map input
     */
    public short getShortValue(final K key, final short defaultValue) {
        return MapUtil.getShortValue(this, key, defaultValue);
    }

    /**
     * Gets an int from a Map in a null-safe manner, using the default value if
     * the the conversion fails.
     * <p>
     * The int is obtained from the results of {@link #getNumber(Map, Object)}.
     *
     * @param <K>
     *            the key type
     *
     *
     * @param key
     *            the key to look up
     * @param defaultValue
     *            return if the value is null or if the conversion fails
     * @return the value in the Map as an int, <code>defaultValue</code> if null
     *         map input
     */
    public int getIntValue(final K key, final int defaultValue) {
        return MapUtil.getIntValue(this, key, defaultValue);
    }

    /**
     * Gets a long from a Map in a null-safe manner, using the default value if
     * the the conversion fails.
     * <p>
     * The long is obtained from the results of {@link #getNumber(Map, Object)}.
     *
     * @param <K>
     *            the key type
     *
     *
     * @param key
     *            the key to look up
     * @param defaultValue
     *            return if the value is null or if the conversion fails
     * @return the value in the Map as a long, <code>defaultValue</code> if null
     *         map input
     */
    public long getLongValue(final K key, final long defaultValue) {
        return MapUtil.getLongValue(this, key, defaultValue);
    }

    /**
     * Gets a float from a Map in a null-safe manner, using the default value if
     * the the conversion fails.
     * <p>
     * The float is obtained from the results of {@link #getNumber(Map, Object)}.
     *
     * @param <K>
     *            the key type
     *
     *
     * @param key
     *            the key to look up
     * @param defaultValue
     *            return if the value is null or if the conversion fails
     * @return the value in the Map as a float, <code>defaultValue</code> if
     *         null map input
     */
    public float getFloatValue(final K key, final float defaultValue) {
        return MapUtil.getFloatValue(this, key, defaultValue);
    }

    /**
     * Gets a double from a Map in a null-safe manner, using the default value
     * if the the conversion fails.
     * <p>
     * The double is obtained from the results of
     * {@link #getNumber(Map, Object)}.
     *
     * @param <K>
     *            the key type
     *
     *
     * @param key
     *            the key to look up
     * @param defaultValue
     *            return if the value is null or if the conversion fails
     * @return the value in the Map as a double, <code>defaultValue</code> if
     *         null map input
     */
    public double getDoubleValue(final K key, final double defaultValue) {
        return MapUtil.getDoubleValue(this, key, defaultValue);
    }

}
