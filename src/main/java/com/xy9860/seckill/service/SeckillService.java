package com.xy9860.seckill.service;

import java.util.List;

import com.xy9860.seckill.dto.Exposer;
import com.xy9860.seckill.dto.SeckillExecution;
import com.xy9860.seckill.entity.Seckill;
import com.xy9860.seckill.exception.RepeatKillException;
import com.xy9860.seckill.exception.SeckillCloseException;
import com.xy9860.seckill.exception.SeckillException;

/**
 * 站在使用者的角度设计接口
 * 方法定义粒度(明确方法功能),参数,返回类型(类型要友好)
 * */
public interface SeckillService {
	public List<Seckill> getSeckillList();
	public Seckill getSeckillById(long seckillId);
	/**
	 * 秒杀开启时 返回秒杀的接口地址
	 * 否则输出系统时间和秒杀时间
	 * 
	 * 这样的需求使用DTO层进行控制
	 * */
	public Exposer exportSeckillUrl(long seckillId);
	public SeckillExecution executeSeckill(long seckillId,long userPhone,String md5)
			throws SeckillException,SeckillCloseException,RepeatKillException;
	public SeckillExecution executeSeckillProcedure(long seckillId,long userPhone,String md5);
}
