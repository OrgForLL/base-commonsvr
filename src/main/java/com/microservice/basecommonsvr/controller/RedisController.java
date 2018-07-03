package com.microservice.basecommonsvr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lilanz.microservice.common.entity.Result;
import com.microservice.basecommonsvr.service.ReqRecordService;

@RestController
public class RedisController {

	@Autowired
	private ReqRecordService reqRecordService;
	
	@PostMapping("/redisInsertInter")
	private Result<?> redisInsertInter(String appName,String reqid,int tid){
		return reqRecordService.redisInsertInter(appName, reqid, tid);
	}
	
	@PostMapping("/redisUpdateInter")
	private Result<?> redisUpdateInter(String appName,String reqid,int tid,boolean status){
		return reqRecordService.redisUpdateInter(appName, reqid, tid,status);
	}
}
