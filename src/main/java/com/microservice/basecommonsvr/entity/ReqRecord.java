package com.microservice.basecommonsvr.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 请求记录实体
 * @author cjj
 */
public class ReqRecord implements Serializable {

	private static final long serialVersionUID = 1L;

	//请求id
	private String reqid;
	//应用id
	private String appid;
	//请求时间
	private Date reqtime;
	//业务id
	private int tid;
	//处理状态
	private boolean status;

	public String getReqid() {
		return reqid;
	}

	public void setReqid(String reqid) {
		this.reqid = reqid;
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public Date getReqtime() {
		return reqtime;
	}

	public void setReqtime(Date reqtime) {
		this.reqtime = reqtime;
	}

	public int getTid() {
		return tid;
	}

	public void setTid(int tid) {
		this.tid = tid;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
