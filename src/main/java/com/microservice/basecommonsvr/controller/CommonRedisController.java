package com.microservice.basecommonsvr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
	 * @param jsonObject
	 * @return 查询结果
	 */
	@PostMapping("/redisHashSearch")
	private Result<?> redisHashSearch(@RequestBody JSONObject jsonObject){
		String key = jsonObject.getString("key");
		String searchType = jsonObject.getString("searchType");
		String data = jsonObject.getString("data");
		return commonRedisService.redisHashSearch(key, searchType, data);
	}
}
