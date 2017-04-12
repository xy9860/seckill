package com.xy9860.seckill.dao;

import org.apache.ibatis.annotations.Param;

import com.xy9860.seckill.entity.SuccessKilled;

public interface SuccessKilledDao {
	public int insertSuccessKilled(@Param("seckillId")long seckillId,@Param("userPhone")long userPhone);
	public SuccessKilled queryByid(@Param("seckillId")long seckillId,@Param("userPhone")long userPhone);
}
