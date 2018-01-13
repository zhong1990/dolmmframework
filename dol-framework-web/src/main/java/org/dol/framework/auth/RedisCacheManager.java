package org.dol.framework.auth;
/// **
// *
// */
// package org.dol.rbac.auth;
//
// import java.util.Collection;
// import java.util.Set;
//
// import org.apache.shiro.cache.AbstractCacheManager;
// import org.apache.shiro.cache.Cache;
// import org.apache.shiro.cache.CacheException;
// import org.apache.shiro.cache.MapCache;
//
// import org.dol.framework.redis.StringRedisTemplateEX;
//
/// **
// * TODO.
// *
// * @author dolphin
// * @date 2017年4月13日 下午4:19:45
// */
// public class RedisCacheManager extends AbstractCacheManager {
//
// /*
// * (non-Javadoc)
// * @see
// * org.apache.shiro.cache.AbstractCacheManager#createCache(java.lang.String)
// */
// @Override
// protected Cache createCache(String name) throws CacheException {
// return new RedisCache(null, name);
// }
//
// static class RedisCache implements Cache<String, String> {
//
// private String name;
// private StringRedisTemplateEX redisTemplate;
//
// public RedisCache(StringRedisTemplateEX redisTemplate, String name) {
// this.name = name;
// this.redisTemplate = redisTemplate;
// }
//
// /*
// * (non-Javadoc)
// * @see org.apache.shiro.cache.Cache#get(java.lang.Object)
// */
// @Override
// public String get(String key) throws CacheException {
//
// return redisTemplate.
// }
//
// /*
// * (non-Javadoc)
// * @see org.apache.shiro.cache.Cache#put(java.lang.Object,
// * java.lang.Object)
// */
// @Override
// public V put(K key, V value) throws CacheException {
// // TODO Auto-generated method stub
// return null;
// }
//
// /*
// * (non-Javadoc)
// * @see org.apache.shiro.cache.Cache#remove(java.lang.Object)
// */
// @Override
// public V remove(K key) throws CacheException {
// // TODO Auto-generated method stub
// return null;
// }
//
// /*
// * (non-Javadoc)
// * @see org.apache.shiro.cache.Cache#clear()
// */
// @Override
// public void clear() throws CacheException {
// // TODO Auto-generated method stub
//
// }
//
// /*
// * (non-Javadoc)
// * @see org.apache.shiro.cache.Cache#size()
// */
// @Override
// public int size() {
// // TODO Auto-generated method stub
// return 0;
// }
//
// /*
// * (non-Javadoc)
// * @see org.apache.shiro.cache.Cache#keys()
// */
// @Override
// public Set<K> keys() {
// // TODO Auto-generated method stub
// return null;
// }
//
// /*
// * (non-Javadoc)
// * @see org.apache.shiro.cache.Cache#values()
// */
// @Override
// public Collection<V> values() {
// // TODO Auto-generated method stub
// return null;
// }
//
// }
//
// }
