package com.xy9860.seckill.dto;
//数据传输层
//封装秒杀执行后的结果

import com.xy9860.seckill.entity.SuccessKilled;
import com.xy9860.seckill.enums.SeckillStatEnum;

public class SeckillExecution {
	private long seckillId;
	private int state;
	private String stateInfo;
	//秒杀成功对象
	private SuccessKilled successKilled;
	
	public SeckillExecution() {
		super();
	}
	public SeckillExecution(long seckillId, SeckillStatEnum stateEnum) {
		super();
		this.seckillId = seckillId;
		this.state = stateEnum.getState();
		this.stateInfo = stateEnum.getStateInfo();
	}
	public SeckillExecution(long seckillId, SeckillStatEnum stateEnum, SuccessKilled successKilled) {
		super();
		this.seckillId = seckillId;
		this.state = stateEnum.getState();
		this.stateInfo = stateEnum.getStateInfo();
		this.successKilled = successKilled;
	}
	public long getSeckillId() {
		return seckillId;
	}
	public void setSeckillId(long seckillId) {
		this.seckillId = seckillId;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public String getStateInfo() {
		return stateInfo;
	}
	public void setStateInfo(String stateInfo) {
		this.stateInfo = stateInfo;
	}
	public SuccessKilled getSuccessKilled() {
		return successKilled;
	}
	public void setSuccessKilled(SuccessKilled successKilled) {
		this.successKilled = successKilled;
	}
	@Override
	public String toString() {
		return "SeckillExecution [seckillId=" + seckillId + ", state=" + state + ", stateInfo=" + stateInfo
				+ ", successKilled=" + successKilled + "]";
	}
}
