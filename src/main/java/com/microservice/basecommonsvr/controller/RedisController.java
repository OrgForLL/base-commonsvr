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
	
	@PostMapping("/redisInsertInter")
	private Result<?> redisInsertInter(@RequestParam String appName,@RequestParam String reqid,@RequestParam int tid){
		return reqRecordService.redisInsertInter(appName, reqid, tid);
	}
	
	@PostMapping("/redisUpdateInter")
	private Result<?> redisUpdateInter(@RequestParam String appName,@RequestParam String reqid,@RequestParam int tid,@RequestParam boolean status){
		return reqRecordService.redisUpdateInter(appName, reqid, tid,status);
	}
	
	@GetMapping("/myinfo")
	private String Myinfo() {
		return "Redis";
	}
}
