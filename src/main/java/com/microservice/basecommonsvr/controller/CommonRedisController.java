package com.microservice.basecommonsvr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.lilanz.microservice.common.entity.Result;
import com.microservice.basecommonsvr.service.CommonRedisService;

@RestController
public class CommonRedisController {

	@Autowired
	private CommonRedisService commonRedisService;
	
	/**
	 * 以hash的方式保存业务数据至redis
	 * @param jsonObject json对象
	 * @return 保存成功
	 */
	@PostMapping("/redisHashSave")
	private Result<?> redisHashSave(@RequestBody JSONObject jsonObject){
		String key = jsonObject.getString("key");
		Long timeout = jsonObject.getLong("timeout");
		JSONObject data = jsonObject.getJSONObject("data");
		if(timeout==null) {
			timeout = 3600l;
		}
		return commonRedisService.redisHashSave(key, timeout, data);
	}
	
	/**
	 * 删除redis中指定业务数据的hash键
	 * @param jsonObject json对象
	 * @return 删除成功
	 */
	@PostMapping("/redisHashDelete")
	private Result<?> redisHashDelete(@RequestBody JSONObject jsonObject){
		String key = jsonObject.getString("key");
		String data = jsonObject.getString("data");
		return commonRedisService.redisHashDelete(key, data);
	}
	
	/**
	 * redis查询指定业务数据的hash键
	 * @param jsonObject json对象
	 * @return 查询结果
	 */
	@PostMapping("/redisHashSearch")
	private Result<?> redisHashSearch(@RequestBody JSONObject jsonObject){
		String key = jsonObject.getString("key");
		String searchType = jsonObject.getString("searchType");
		String data = jsonObject.getString("data");
		return commonRedisService.redisHashSearch(key, searchType, data);
	}
	
	/**
	 * 判断某个键在redis中是否存在
	 * @param key 键
	 * @return 是否有该键
	 */
	@GetMapping("/redisKeySearch/{key}")
	private Result<?> redisKeySearch(@PathVariable(value = "key") String key){
		return commonRedisService.redisKeySearch(key);
	}
	
	/**
	 * 以list的方式保存业务数据至redis(右推)
	 * @param jsonObject json对象
	 * @return 保存成功
	 */
	@PostMapping("/redisListSave")
	private Result<?> redisListSave(@RequestBody JSONObject jsonObject){
		String key = jsonObject.getString("key");
		Long timeout = jsonObject.getLong("timeout");
		Object data;
		try {
			try {
				data = jsonObject.getJSONObject("data");
			}catch(Exception e) {
				data = jsonObject.getJSONArray("data");
			}
		} catch (Exception e) {
			data = null;
		}
		if(timeout==null) {
			timeout = 3600l;
		}
		return commonRedisService.redisListSave(key, timeout, data);
	}
	
	/**
	 * 用redis以list形式左出栈
	 * @param key key
	 * @return list的第一个key值
	 */
	@GetMapping("/redisListLPop/{key}")
	private Result<?> redisListLPop(@PathVariable(value = "key") String key){
		return commonRedisService.redisListLPop(key);
	}
	
	/**
	 * redis查询指定业务数据的list键
	 * @param key key
	 * @return 查询结果
	 */
	@GetMapping("/redisListFindAll/{key}")
	private Result<?> redisListFindAll(@PathVariable(value = "key") String key){
		return commonRedisService.redisListFindAll(key);
	}
	
	/**
	 * 移除redis中的存储数据
	 * @param key key
	 * @return 执行结果
	 */
	@PostMapping("/redisRemove")
	private Result<?> redisRemove(@RequestParam String key){
		return commonRedisService.redisRemove(key);
	}
}
