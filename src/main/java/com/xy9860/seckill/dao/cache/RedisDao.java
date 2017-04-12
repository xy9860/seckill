package com.xy9860.seckill.dao.cache;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.xy9860.seckill.entity.Seckill;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisDao {
	
	private final JedisPool jedisPool;
	
	private RuntimeSchema<Seckill> schema=RuntimeSchema.createFrom(Seckill.class);
	
	public RedisDao(String ip,int port){
		jedisPool=new JedisPool(ip,port);
	}
	
	public Seckill getSeckill(long seckillId){
		//readis 操作逻辑
		try {
			Jedis jedis=jedisPool.getResource();
			try {
				String key="seckill:"+seckillId;
				//redis没有实现内部序列化操作
				//readis返回的数据都是字节数组 
				//建议使用自定义的序列化方式 protostuff 注意操作的必须是一个实体(get set 方法)
				byte[] bytes=jedis.get(key.getBytes());
				if (bytes!=null) {//反序列化
					Seckill seckill=schema.newMessage();
					ProtobufIOUtil.mergeFrom(bytes, seckill, schema);
					return seckill;
				}
			}finally{
				if (jedis!=null) {
					jedis.close();
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return null;
	}
	
	public String  putSeckill(Seckill seckill){
		try {
			Jedis jedis=jedisPool.getResource();
			try {
				String key="seckill:"+seckill.getSeckillId();
				int timeout=60*60;
				//redis没有实现内部序列化操作
				//readis返回的数据都是字节数组 
				//建议使用自定义的序列化方式 protostuff 注意操作的必须是一个实体(get set 方法)
				byte[] bytes=ProtobufIOUtil.toByteArray(seckill, schema,
						LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));//最后这个参数是缓存器
				
				String result=jedis.setex(key.getBytes(), timeout, bytes);
				return result;
			}finally{
				if (jedis!=null) {
					jedis.close();
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
