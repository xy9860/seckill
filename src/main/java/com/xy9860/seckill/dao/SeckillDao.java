package com.xy9860.seckill.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.xy9860.seckill.entity.Seckill;

public interface SeckillDao {
	public int reduceNumber(@Param("seckillId")long seckillId,@Param("killTime")Date killTime);
	public Seckill queryByid(long seckillId);
	public List<Seckill> queryAll(@Param("offset")int offset,@Param("limit")int limit);
	public void killByProcedure(Map<String, Object> paramMap);
}
