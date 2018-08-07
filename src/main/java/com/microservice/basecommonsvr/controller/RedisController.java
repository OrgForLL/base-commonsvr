package com.microservice.basecommonsvr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lilanz.microservice.common.entity.Result;
import com.microservice.basecommonsvr.service.ReqRecordService;

@RestController
public class RedisController {

	@Autowired
	private ReqRecordService reqRecordService;
	
	/**
	 * 根据入参保存请求记录
	 * @param key 最外层key
	 * @param appName 服务名
	 * @param reqid 请求id
	 * @return 请求记录对象
	 */
	@PostMapping("/redisInsertInter")
	private Result<?> redisInsertInter(@RequestParam String appName,@RequestParam String reqid){
		return reqRecordService.redisInsertInter(appName, reqid);
	}
	
	/**
	 * 根据入参更新redis请求记录
	 * @param appName 服务名
	 * @param reqid 请求id
	 * @param tid 业务id
	 * @param status 状态
	 * @return 请求记录对象
	 */
	@PostMapping("/redisUpdateInter")
	private Result<?> redisUpdateInter(@RequestParam String appName,@RequestParam String reqid,@RequestParam int tid,@RequestParam boolean status){
		return reqRecordService.redisUpdateInter(appName, reqid, tid,status);
	}
	
	@GetMapping("/myinfo")
	private String Myinfo() {
		return "Redis";
	}
}
