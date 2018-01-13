package org.dol.framework.redis;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

public class StringRedisTemplateEXTest {

    @Test
    public void test() {

    }

    @Test
    public void testSetIfEqual() {

        StringRedisTemplateEX stringRedisTemplateEX = buildRedisTemplate();
        stringRedisTemplateEX.boundValueOps("hello").set("world");
        assertTrue(stringRedisTemplateEX.setIfEqual("hello", "world1", "world"));

    }

    private StringRedisTemplateEX buildRedisTemplate() {
        JedisConnectionFactory connectionFactory = buildConnectionFactory();
        StringRedisTemplateEX stringRedisTemplateEX = new StringRedisTemplateEX();
        stringRedisTemplateEX.setConnectionFactory(connectionFactory);
        stringRedisTemplateEX.afterPropertiesSet();
        return stringRedisTemplateEX;
    }

    private JedisConnectionFactory buildConnectionFactory() {
        JedisConnectionFactory connectionFactory = new JedisConnectionFactory();
        connectionFactory.setHostName("192.168.3.102");
        connectionFactory.setPort(6379);
        connectionFactory.setPassword("mcredispasSwd");
        connectionFactory.setUsePool(true);
        connectionFactory.afterPropertiesSet();
        return connectionFactory;
    }
}
