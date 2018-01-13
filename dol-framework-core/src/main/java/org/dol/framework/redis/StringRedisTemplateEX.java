/**
 * xf9-framework-core
 * RedisTemplateEx.java
 * org.dol.framework
 * TODO
 *
 * @author dolphin
 * @date 2016年3月18日 下午3:15:11
 * @Copyright 2016, 唯创国际 幸福9号 All Rights Reserved.
 * @version 1.0
 */

package org.dol.framework.redis;

import com.alibaba.fastjson.JSON;
import org.dol.framework.util.StringUtil;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * ClassName:RedisTemplateEx <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2016年3月18日 下午3:15:11 <br/>
 *
 * @author dolphin
 * @version 1.0
 * @since JDK 1.7
 * @see
 */
public class StringRedisTemplateEX extends StringRedisTemplate {

    private static final RedisScript<String> SET_IF_ABSENT_SCRIPT = new DefaultRedisScript<String>(
            "return redis.call('SET',KEYS[1],ARGV[1],'PX',ARGV[2],'NX')",
            String.class);

    private static final RedisScript<String> SET_IF_EQUAL_SCRIPT_WITH_TIME_OUT = new DefaultRedisScript<String>(
            "if redis.call('GET',KEYS[1]) == ARGV[2] then "
                    + "redis.call('SET',KEYS[1],ARGV[1],'PX',ARGV[3]) "
                    + "return 'OK' "
                    + "end "
                    + "return 'FALSE'",
            String.class);

    private static final RedisScript<String> SET_IF_EQUAL_SCRIPT = new DefaultRedisScript<String>(
            "if redis.call('GET',KEYS[1]) == ARGV[2] then "
                    + "redis.call('SET',KEYS[1],ARGV[1]) "
                    + "return 'OK' "
                    + "end "
                    + "return 'FALSE'",
            String.class);

    private static final RedisScript<String> DEL_IF_EQUAL_SCRIPT = new DefaultRedisScript<String>(
            "if redis.call('GET',KEYS[1]) == ARGV[1] then "
                    + "redis.call('DEL',KEYS[1]) "
                    + "return 'OK' "
                    + "end "
                    + "return 'FALSE'",
            String.class);

    private static final RedisScript<String> HASH_UPDATE_IF_EQUAL_SCRIPT = new DefaultRedisScript<String>(
            "if redis.call('HGET',KEYS[1],ARGV[1]) == ARGV[3] then "
                    + "redis.call('HSET',KEYS[1],ARGV[1],ARGV[2]) "
                    + "return 'OK' "
                    + "end "
                    + "return 'FALSE'",

            String.class);

    private static final RedisScript<String> HASH_SET_IF_ABSENT_SCRIPT = new DefaultRedisScript<String>(
            "if redis.call('HEXISTS',KEYS[1],ARGV[1]) == 0 then "
                    + "redis.call('HSET',KEYS[1],ARGV[1],ARGV[2]) "
                    + "return 'OK' "
                    + "end "
                    + "return 'FALSE'",
            String.class);

    private static final RedisScript<String> HASH_DEL_IF_EQUAL_SCRIPT = new DefaultRedisScript<String>(
            "if redis.call('HGET',KEYS[1],ARGV[1]) == ARGV[2] then "
                    + "redis.call('HDEL',KEYS[1],ARGV[1]) "
                    + "return 'OK' "
                    + "end "
                    + "return 'FALSE'",
            String.class);

    private static final RedisScript<String> MULTI_SET_IF_ABSENT_SCRIPT = new DefaultRedisScript<String>(
            "local n = table.getn(KEYS) "
                    + "for i=1,n do "
                    + " if not redis.call('SET',KEYS[i],ARGV[1],'PX',ARGV[2],'NX') then "
                    + "  for j = i-1,1,-1 do "
                    + "    redis.call('DEL',KEYS[j]) "
                    + "  end "
                    + "  return 'FALSE' "
                    + "  elseif i == n then "
                    + "     return 'OK' "
                    + "  end "
                    + "end ",
            String.class);

    private static final RedisScript<String> MULTI_DEL_IF_EQUAL_SCRIPT = new DefaultRedisScript<String>(
            "local n = table.getn(KEYS) "
                    + "for i=1,n do "
                    + " if redis.call('GET',KEYS[i]) == ARGV[1] then "
                    + "   redis.call('DEL',KEYS[i]) "
                    + " end "
                    + "end "
                    + "return 'OK' ",
            String.class);

    private static final String SUCCESS_FLAG = "OK";

    public static StringRedisTemplateEX buildRedisTemplate(
            String hostName,
            int port) {
        return buildRedisTemplate(hostName, port, null, 0, true);
    }

    public static StringRedisTemplateEX buildRedisTemplate(
            String hostName,
            int port,
            String password) {
        return buildRedisTemplate(hostName, port, password, 0, true);
    }

    public static StringRedisTemplateEX buildRedisTemplate(
            String hostName,
            int port,
            String password,
            int database,
            boolean userPool) {
        JedisConnectionFactory redisConnectionFactory = new JedisConnectionFactory();
        redisConnectionFactory.setHostName(hostName);
        redisConnectionFactory.setPassword(password);
        redisConnectionFactory.setPort(port);
        redisConnectionFactory.setDatabase(database);
        redisConnectionFactory.setUsePool(userPool);
        redisConnectionFactory.afterPropertiesSet();
        StringRedisTemplateEX stringRedisTemplateEX = new StringRedisTemplateEX();
        stringRedisTemplateEX.setConnectionFactory(redisConnectionFactory);
        stringRedisTemplateEX.afterPropertiesSet();
        return stringRedisTemplateEX;
    }

    public static void main(String[] args) {
        StringRedisTemplateEX redisTemplateEX = buildRedisTemplate("aliyun.dol.com", 6379, "b840fc02d524045429941cc15f59e41cb7be6c52", 1, true);
        // System.out.println(redisTemplateEX.setIfAbsent("hello", "world", 30L,
        // TimeUnit.SECONDS));
        // System.out.println(redisTemplateEX.setIfEqual("hello", "world3",
        // "world2", 30L, TimeUnit.SECONDS));

        System.out.println(redisTemplateEX.setIfAbsent(Arrays.asList("cc", "aa", "bb"), "world", 30L, TimeUnit.SECONDS));

        System.out.println(redisTemplateEX.deleteIfEqual(Arrays.asList("cc", "aa", "bb"), "world"));
    }

    public boolean deleteIfEqual(final List<String> keys, final String expectValue) {
        String result = null;
        if (keys.size() == 1) {
            result = this.execute(
                    DEL_IF_EQUAL_SCRIPT,
                    keys,
                    expectValue);
        } else {
            result = this.execute(
                    MULTI_DEL_IF_EQUAL_SCRIPT,
                    keys,
                    expectValue);
        }
        return isSuccess(result);
    }

    public boolean deleteIfEqual(final String key, final String expectValue) {
        return deleteIfEqual(toList(key), expectValue);
    }

    public <T> T getObject(final String key, final Class<T> clazz) {
        String json = opsForValue().get(key);
        return StringUtil.isBlank(json) ? null : JSON.parseObject(json, clazz);
    }

    public <T> List<T> getObjectList(final Collection<String> keys, final Class<T> clazz) {
        List<String> jsonList = opsForValue().multiGet(keys);
        List<T> list = new ArrayList<T>(jsonList.size());
        for (String json : jsonList) {
            if (StringUtil.isNotBlank(json)) {
                list.add(JSON.parseObject(json, clazz));
            }
        }
        return list;
    }

    /**
     * 如果filed的值等与expectValue删除指定field.
     *
     * @param keys
     * @param filed
     * @param expectValue
     * @return
     */
    public boolean hDeleteIfEqual(List<String> keys, String filed, String expectValue) {

        String result = this.execute(
                HASH_DEL_IF_EQUAL_SCRIPT,
                keys,
                filed,
                expectValue);

        return isSuccess(result);
    }

    public boolean hDeleteIfEqual(String key, String filed, String expectValue) {
        return hDeleteIfEqual(Arrays.asList(key), filed, expectValue);
    }

    public boolean hSetIfAbsent(List<String> keys, String filed, String value) {
        String result = this.execute(
                HASH_SET_IF_ABSENT_SCRIPT,
                keys,
                filed,
                value);
        return isSuccess(result);
    }

    public boolean hSetIfAbsent(String key, String filed, String value) {
        return hSetIfAbsent(Arrays.asList(key), filed, value);
    }

    public boolean hUpdateIfEqual(List<String> keys, String filed, String value, String expectValue) {

        String result = this.execute(
                HASH_UPDATE_IF_EQUAL_SCRIPT,
                keys,
                filed,
                value,
                expectValue);

        return isSuccess(result);
    }

    public boolean hUpdateIfEqual(
            final String key,
            final String filed,
            final String value,
            final String expectValue) {
        return hUpdateIfEqual(toList(key), filed, value, expectValue);
    }

    /**
     * 参照方法名.
     *
     * @param key
     * @param value
     *
     */
    public void set(String key, String value) {
        this.boundValueOps(key).set(value);
    }

    /**
     * 参照方法名.
     *
     * @param keys
     * @param value
     * @param timeOutMs 过期时间，单位毫秒
     * @return
     */
    public boolean setIfAbsent(final List<String> keys, final String value, final String timeOutMs) {
        String result = null;
        if (keys.size() > 1) {
            result = this.execute(
                    MULTI_SET_IF_ABSENT_SCRIPT,
                    keys,
                    value,
                    timeOutMs);
        } else {
            result = this.execute(
                    SET_IF_ABSENT_SCRIPT,
                    keys,
                    value,
                    timeOutMs);
        }
        return isSuccess(result);
    }

    /**
     * 参照方法名.
     *
     * @param result
     * @return
     */
    private boolean isSuccess(String result) {
        return SUCCESS_FLAG.equals(result);
    }

    public boolean setIfAbsent(
            final String key,
            final String value,
            final long timeOut) {
        return setIfAbsent(toList(key), value, String.valueOf(TimeUnit.SECONDS.toMillis(timeOut)));
    }

    public boolean setIfAbsent(
            final String key,
            final String value,
            final long timeOut,
            final TimeUnit timeUnit) {
        return setIfAbsent(toList(key), value, String.valueOf(timeUnit.toMillis(timeOut)));
    }

    public boolean setIfAbsent(
            final List<String> keys,
            final String value,
            final long timeOut,
            final TimeUnit timeUnit) {
        return setIfAbsent(keys, value, String.valueOf(timeUnit.toMillis(timeOut)));
    }

    public boolean setIfEqual(
            final String key,
            final String newValue,
            final String expectedValue) {

        String result = this.execute(
                SET_IF_EQUAL_SCRIPT,
                toList(key),
                newValue,
                expectedValue);
        return isSuccess(result);
    }

    public boolean setIfEqual(
            final String key,
            final String newValue,
            final String expectedValue,
            final Long timeout,
            final TimeUnit timeUnit) {

        String result = this.execute(
                SET_IF_EQUAL_SCRIPT_WITH_TIME_OUT,
                toList(key),
                newValue,
                expectedValue,
                String.valueOf(timeUnit.toMillis(timeout)));
        return isSuccess(result);
    }

    protected List<String> toList(String key) {
        return Arrays.asList(key);
    }

}
