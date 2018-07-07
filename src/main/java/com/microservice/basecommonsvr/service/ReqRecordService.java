package com.microservice.basecommonsvr.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.lilanz.microservice.common.entity.Result;
import com.lilanz.microservice.common.tools.ResultUtil;
import com.microservice.basecommonsvr.entity.ReqRecord;
import com.microservice.basecommonsvr.mapper.RedisDBHelper;

/**
 * 请求记录服务层
 * @author cjj
 */
@Service
public class ReqRecordService {
	@Autowired
	RedisDBHelper<String, Object> redisDBHelper;

	/** 
	 * 储存一个ReqRecord到一个Map结构中
	 * @param key 最外层key
	 * @param appName 应用名
	 * @param reqRecord 请求记录
	 */
	public void putReqRecord(String appName,ReqRecord reqRecord) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.FFF");
		redisDBHelper.hashPut(appName + reqRecord.getReqid(),"reqid" ,reqRecord.getReqid());
		redisDBHelper.hashPut(appName + reqRecord.getReqid(),"appid" ,reqRecord.getAppid());
		redisDBHelper.hashPut(appName + reqRecord.getReqid(),"reqtime" ,sdf.format(reqRecord.getReqtime()));
		redisDBHelper.hashPut(appName + reqRecord.getReqid(),"tid" ,reqRecord.getTid()+"");
		redisDBHelper.hashPut(appName + reqRecord.getReqid(),"status" ,reqRecord.isStatus()+"");
		//一小时过期
		redisDBHelper.expire(appName + reqRecord.getReqid(), 1, TimeUnit.HOURS);
	}
	
	/**
	 * 查找指定key下的hashKey是否存在,若存在则判断是否在未处理的合理范围
	 * @param key 最外层key
	 * @param hashKey
	 * @return
	 */
	public int findReqRecord(String key) {
		if(!redisDBHelper.hasKey(key)) {
			return 0;
		}else {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.FFF");
			try {
				Date newDate = sdf.parse(redisDBHelper.hashGet(key, "reqtime").toString());
				if(redisDBHelper.hashGet(key, "status").toString().equals("true")) {
					return 1;
				}else if((new Date().getTime() - newDate.getTime())/1000>5) {
					return 3;
				}else {
					return 2;
				}
			} catch (ParseException e) {
				e.printStackTrace();
				return 2;
			}
		}
	}
	
	/**
	 * 根据入参参数判断是否存在 
	 * 不存在：插入redis记录 返回{errcode:0,errmsg:'',data:{reqid:false,tid:0}} 该请求能处理
	 * 存在：已处理返回：返回{errcode:0,errmsg:'',data:{reqid:true,tid:@tid}} 该请求已处理可以直接返回结构
	 * 未处理并在合理范围内返回{errcode:0,errmsg:'',data:{reqid:false,tid:0}} 该请求能处理 
	 * 未处理并在合理范围外返回{errcode:0,errmsg:'',data:{reqid:true,tid:0}}该请求暂不能处理
	 * @param key 最外层key
	 * @param appName 服务名
	 * @param reqid 请求id
	 * @return
	 */
	public Result<?> redisInsertInter(String appName,String reqid){
		if(findReqRecord(appName + reqid)==0) {
			ReqRecord reqRecord = new ReqRecord();
			reqRecord.setAppid(appName);
			reqRecord.setReqid(reqid);
			reqRecord.setReqtime(new Date());
			reqRecord.setTid(0);
			reqRecord.setStatus(false);
			putReqRecord(appName, reqRecord);
			JSONObject jo = new JSONObject();
			jo.put("reqid", false);
			jo.put("tid", 0);
			return ResultUtil.success(jo);
		}else if(findReqRecord(appName + reqid)==1) {
			JSONObject jo = new JSONObject();
			jo.put("reqid", true);
			jo.put("tid", redisDBHelper.hashGet(appName+reqid, "tid"));
			return ResultUtil.success(jo);
		}else if(findReqRecord(appName + reqid)==2){
			JSONObject jo = new JSONObject();
			jo.put("reqid", false);
			jo.put("tid", 0);
			return ResultUtil.success(jo);
		}else {
			JSONObject jo = new JSONObject();
			jo.put("reqid", true);
			jo.put("tid", 0);
			return ResultUtil.success(jo);
		}
	}

	public Result<?> redisUpdateInter(String appName, String reqid, int tid, boolean status) {
		if(redisDBHelper.hashHasKey(appName+reqid, "status")) {
			redisDBHelper.hashPut(appName+reqid, "status", status+"");
			redisDBHelper.hashPut(appName+reqid, "tid", tid+"");
			JSONObject jo = new JSONObject();
			jo.put("status", status);
			return ResultUtil.success(jo);
		}else {
			return ResultUtil.error(100, "指定key下的status不存在");
		}
	}
}
