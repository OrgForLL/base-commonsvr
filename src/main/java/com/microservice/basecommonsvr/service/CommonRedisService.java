package com.microservice.basecommonsvr.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.lilanz.microservice.common.entity.Result;
import com.lilanz.microservice.common.tools.ResultUtil;
import com.microservice.basecommonsvr.mapper.RedisDBHelper;

@Service
public class CommonRedisService {
	@Autowired
	RedisDBHelper<String, Object> redisDBHelper;

	private final Logger logger = LogManager.getLogger(CommonRedisService.class);
	
	/**
	 * 以hash的方式保存业务数据至redis
	 * @param key 大key
	 * @param timeout 超时时间
	 * @param data 业务数据
	 * @return 保存成功
	 */
	public Result<?> redisHashSave(String key,Long timeout,JSONObject data){
		try {
			Set<String> keySet = data.keySet();
			for(String hashKey:keySet) {
				redisDBHelper.hashPut(key, hashKey, data.get(hashKey).toString());
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.FFF");
			redisDBHelper.hashPut(key, "createTime", sdf.format(new Date()));
			if(null != timeout)
				redisDBHelper.expire(key, timeout, TimeUnit.SECONDS);
			return ResultUtil.success("保存成功！");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(" 用redis以hash的方式保存业务数据\n redisHashSave\n"+e);
			return ResultUtil.error(100, e.getMessage());
		}
	}
	
	/**
	 * 删除redis中指定业务数据的hash键
	 * @param key 大key
	 * @param data 业务数据
	 * @return 删除成功
	 */
	public Result<?> redisHashDelete(String key,String data){
		try {
			//指定key存在
			if(redisDBHelper.hasKey(key)) {
				Set<String> keySet;
				//如果data为空则删除该key下所有的hashkey
				if(data==null||data.isEmpty()) {
					keySet = redisDBHelper.hashFindAllKey(key);
				}else {
					keySet = new HashSet<String>();
					String [] keys = data.split(",");
					for(int i=0;i<keys.length;i++) {
						keySet.add(keys[i]);
					}
				}
				for(String hashKey:keySet) {
					if(redisDBHelper.hashHasKey(key, hashKey)) {
						redisDBHelper.hashRemove(key, hashKey);
					}
				}
				//如果其他键都被删完了只剩下创建时间、那么也把创建时间删除
				if(redisDBHelper.hashFindAllKey(key).size()==1&&redisDBHelper.hashHasKey(key, "createTime")) {
					redisDBHelper.hashRemove(key, "createTime");
				}
				return ResultUtil.success("删除成功！");
			}else {
				logger.error(" 用redis删除指定业务数据的hash键\n redisHashDelete\n 指定redis下暂无指定key的数据");
				return ResultUtil.error(100, "指定redis下暂无指定key的数据");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(" 删除redis中的hash存储数据\n redisHashDelete\n"+e);
			return ResultUtil.error(100, e.getMessage());
		}
	}

	/**
	 * redis查询指定业务数据的hash键
	 * @param key 大key
	 * @param searchType 搜索类型
	 * @param data 搜索键
	 * @return 查询结果
	 */
	public Result<?> redisHashSearch(String key,String searchType,String data){
		try {
			//指定key存在
			if(redisDBHelper.hasKey(key)) {
				Object result = null;
				switch(searchType.toLowerCase()) {
				//查所有的key和value
				case "all":
					result = redisDBHelper.hashFindAll(key);
					break;
				//查所有的hashkey
				case "keys":
					result = redisDBHelper.hashFindAllKey(key);
					break;
				//查所有的hashkey的value
				case "values":
					result = redisDBHelper.hashFindAllValue(key);
					break;
				//查某个hashkey的value
				case "hashkey":
					if(redisDBHelper.hashHasKey(key, data)) {
						result = redisDBHelper.hashGet(key, data);
					}else {
						logger.error(" 查询redis中的hash存储数据\n redisHashSearch\n 指定redis的指定key下无指定的hashkey数据");
						return ResultUtil.error(100, "指定redis的指定key下无指定的hashkey数据"); 
					}
					break;
				//查些hashKey下的value
				case "hashkeys":
					String [] hashKeys = data.split(",");
					List<Object>list = new ArrayList<Object>();
					for(String hashKey:hashKeys) {
						if(redisDBHelper.hashHasKey(key, hashKey)) {
							list.add(redisDBHelper.hashGet(key, hashKey));
						}else {
							logger.error(" 查询redis中的hash存储数据\n redisHashSearch\n 指定redis的指定key下无指定的hashkey:"+hashKey+"的数据");
							return ResultUtil.error(100, "指定redis的指定key下无指定的hashkey:"+hashKey+"的数据"); 
						}
					}
					result = list;
					break;
				default:
					logger.error(" 查询redis中的hash存储数据\n redisHashSearch\n 搜索类型格式有误");
					return ResultUtil.error(100, "搜索类型格式有误"); 
				}
				return ResultUtil.success(result);
			}else {
				logger.error(" 查询redis中的hash存储数据\n redisHashSearch\n 不存在指定的key");
				return ResultUtil.error(100, "指定redis下暂无指定key的数据");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(" 查询redis指定业务数据的hash键\n redisHashSearch\n"+e);
			return ResultUtil.error(100, e.getMessage());
		}
	}

	/**
	 * 以list的方式保存业务数据至redis(右推)
	 * @param key 大key
	 * @param timeout 超时时间
	 * @param data 业务数据
	 * @return 保存成功
	 */
	public Result<?> redisListSave(String key,Long timeout,Object data){
		if(null == data) {
			logger.error(" 用redis以list的方式保存业务数据\n redisListSave\n 转换json数据异常");
			return ResultUtil.error(100, "转换json数据异常");
		}
		try {
			JSONObject.DEFFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
			redisDBHelper.listPush(key, JSONObject.toJSON(data).toString());
			if(null != timeout)
				redisDBHelper.expire(key, timeout, TimeUnit.SECONDS);
			return ResultUtil.success("保存成功！");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(" 用redis以list的方式保存业务数据\n redisListSave\n"+e);
			return ResultUtil.error(100, e.getMessage());
		}
	}
	
	/**
	 * 用redis以list形式左出栈
	 * @param key key
	 * @return list的第一个key值
	 */
	public Result<?> redisListLPop(String key) {
		try {
			return ResultUtil.success(redisDBHelper.listLPop(key));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(" 用redis以list形式左出栈\n redisListLPop\n"+e);
			return ResultUtil.error(100, e.getMessage());
		}
	}
	
	/**
	 * redis查询指定业务数据的list键
	 * @param key key
	 * @return 查询结果
	 */
	public Result<?> redisListFindAll(String key){
		List<?> list = redisDBHelper.listFindAll(key);
		if(null == list) {
			logger.error(" 查询redis中的list存储数据\n redisListFindAll\n 指定redis的指定key下无指定的key数据");
			return ResultUtil.error(100, "查询redis中的list存储数据为空");
		}else {
			return ResultUtil.success(list);
		}
	}
	
	/**
	 * 移除redis中的存储数据
	 * @param key key
	 * @return 执行结果
	 */
	public Result<?> redisRemove(String key){
		if(!redisDBHelper.hasKey(key)) {
			logger.error(" 移除redis中的存储数据\n redisRemove\n redis的指定key下无数据");
			return ResultUtil.error(100, "redis中不存在该key");
		}else {
			try {
				redisDBHelper.remove(key);
				return ResultUtil.success("删除redis下的key成功");
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(" 移除redis中的存储数据\n redisRemove\n 移除redis下的指定key异常");
				return ResultUtil.error(100, "移除redis下的指定key异常");
			}
		}
	}
	
	/**
	 * 保存redis的key
	 * @param key 键
	 * @param value 值
	 * @return 保存结果
	 */
	public Result<?> redisValueSave(String key,Long timeout,Object value){
		try {
			redisDBHelper.valueSave(key, value);
			if(null != timeout)
				redisDBHelper.expire(key, timeout, TimeUnit.SECONDS);
			return ResultUtil.success("保存成功");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(" 移除redis中的存储数据\n redisValueSet\n 保存redis的指定key异常");
			return ResultUtil.error(100, "保存redis的指定key异常");
		}
	}
	
	/**
	 * 获取redis中的存储数据
	 * @param key 键
	 * @return 值
	 */
	public Result<?> redisValueGet(String key){
		Object value = redisDBHelper.valueGet(key);
		if(value == null) {
			logger.error(" 获取redis中的存储数据\n redisValueGet\n redis的指定key下无数据");
			return ResultUtil.error(100, "redis中不存在该key的数据");
		}else {
			return ResultUtil.success(value);
		}
	}
	
	/**
	 * 判断某个键在redis中是否存在
	 * @param key 键
	 * @return 是否有该键
	 */
	public Result<?> redisKeySearch(String key){
		JSONObject jo = new JSONObject();
		jo.put("isHasKey", redisDBHelper.hasKey(key));
		return ResultUtil.success(jo);
	}
}
