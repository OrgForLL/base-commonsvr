package com.microservice.basecommonsvr.mapper;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.DefaultStringRedisConnection;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.microservice.basecommonsvr.mapper.RedisDBHelper;

/**
 * 接口的简单实现 
 * @author cjj
 * @param <HK> hashkey
 * @param <T> entity
 */
@Repository
@SuppressWarnings("unused")
public class RedisDBHelper<HK, T> {
	// 在构造器中获取redisTemplate实例, key(not hashKey)
	// 默认使用String类型
	private RedisTemplate<String, T> redisTemplate;
	// 在构造器中通过redisTemplate的工厂方法实例化操作对象
	private HashOperations<String, HK, T> hashOperations;
	private ListOperations<String, T> listOperations;
	private ZSetOperations<String, T> zSetOperations;
	private SetOperations<String, T> setOperations;
	private ValueOperations<String, T> valueOperations;

	// IDEA虽然报错,但是依然可以注入成功, 实例化操作对象后就可以直接调用方法操作Redis数据库
	@Autowired
	public RedisDBHelper(RedisTemplate<String, T> redisTemplate) {
		RedisSerializer<?> stringSerializer = new StringRedisSerializer();
	    redisTemplate.setKeySerializer(stringSerializer);
	    redisTemplate.setValueSerializer(stringSerializer);
	    redisTemplate.setHashKeySerializer(stringSerializer);
	    redisTemplate.setHashValueSerializer(stringSerializer);
	    this.redisTemplate = redisTemplate;
	    
		this.hashOperations = redisTemplate.opsForHash();
		this.listOperations = redisTemplate.opsForList();
		this.zSetOperations = redisTemplate.opsForZSet();
		this.setOperations = redisTemplate.opsForSet();
		this.valueOperations = redisTemplate.opsForValue();
	}
	
	public void setDatabase(int dbIndex) {
		redisTemplate.getConnectionFactory().getConnection().select(dbIndex);
		hashOperations =  redisTemplate.opsForHash();
	}
	
	public Set<String> getKeys(String pattern){
		return redisTemplate.keys(pattern);
	}
	
	public boolean hasKey(String key) {
		return redisTemplate.hasKey(key);
	}
	
	public T valueGet(String key) {
		return valueOperations.get(key);
	}
	
	public void valueSave(String key,T value) {
		valueOperations.set(key, value);
	}
	
	public boolean hashHasKey(String key,Object hashKey) {
		return hashOperations.hasKey(key, hashKey);
	}
	
	public void hashPut(String key, HK hashKey, T domain) {
		hashOperations.put(key, hashKey, domain);
	}
	
	public Set<HK> hashFindAllKey(String key) {
		return hashOperations.keys(key);
	}
	
	public List<T> hashFindAllValue(String key){
		return hashOperations.values(key);
	}

	public Map<HK, T> hashFindAll(String key) {
		return hashOperations.entries(key);
	}

	public T hashGet(String key, HK hashKey) {
		return hashOperations.get(key, hashKey);
	}

	public void hashRemove(String key, HK hashKey) {
		hashOperations.delete(key, hashKey);
	}

	
	public Long listPush(String key, T domain) {
		return listOperations.rightPush(key, domain);
	}
	
	public Long listUnshift(String key, T domain) {
		return listOperations.leftPush(key, domain);
	}

	public List<T> listFindAll(String key) {
		if (!redisTemplate.hasKey(key)) {
			return null;
		}
		return listOperations.range(key, 0, listOperations.size(key));
	}

	public T listLPop(String key) {
		return listOperations.leftPop(key);
	}

	public void remove(String key) {
		redisTemplate.delete(key);
	}

	public boolean expire(String key, long timeout, TimeUnit timeUnit) {
		return redisTemplate.expire(key, timeout, timeUnit);
	}

}