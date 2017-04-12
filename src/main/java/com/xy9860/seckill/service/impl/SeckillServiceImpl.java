package com.xy9860.seckill.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import com.xy9860.seckill.dao.SeckillDao;
import com.xy9860.seckill.dao.SuccessKilledDao;
import com.xy9860.seckill.dao.cache.RedisDao;
import com.xy9860.seckill.dto.Exposer;
import com.xy9860.seckill.dto.SeckillExecution;
import com.xy9860.seckill.entity.Seckill;
import com.xy9860.seckill.entity.SuccessKilled;
import com.xy9860.seckill.enums.SeckillStatEnum;
import com.xy9860.seckill.exception.RepeatKillException;
import com.xy9860.seckill.exception.SeckillCloseException;
import com.xy9860.seckill.exception.SeckillException;
import com.xy9860.seckill.service.SeckillService;

@Service("seckillService")
public class SeckillServiceImpl implements SeckillService {

	private Logger logger=LoggerFactory.getLogger(this.getClass());

	@Autowired
	private SeckillDao seckillDao;
	@Autowired
	private SuccessKilledDao successKilledDao;
	@Autowired
	private RedisDao rdisDao;
	//用于混淆方法
	private final String slat="asdasfJASLFKDAJ@#(*&%(@#%><?ˉ＆asdjalfg";

	public List<Seckill> getSeckillList() {
		// TODO Auto-generated method stub
		return seckillDao.queryAll(0, 100);
	}

	public Seckill getSeckillById(long seckillId) {
		// TODO Auto-generated method stub
		return seckillDao.queryByid(seckillId);
	}

	public Exposer exportSeckillUrl(long seckillId) {
		//优化点:缓存优化  建立在超时的一致性维护(修改比较少)
		//Seckill seckill=seckillDao.queryByid(seckillId);
		Seckill seckill=rdisDao.getSeckill(seckillId);
		
		if (seckill==null) {
			seckill=seckillDao.queryByid(seckillId);
			if (seckill==null) {
				return new Exposer(false,seckillId);
			}else{
				//放进redis 中
				rdisDao.putSeckill(seckill);
			}
		}
		Date startTime=seckill.getStrartTime();
		Date endTime=seckill.getEndTime();
		Date nowTime=new Date();
		if (nowTime.getTime()>endTime.getTime()||nowTime.getTime()<startTime.getTime()) {
			return new Exposer(false,seckillId,nowTime.getTime(),startTime.getTime(),endTime.getTime());
		}
		String md5=getMD5(seckillId);
		return new Exposer(true,md5,seckillId);
	}

	@Transactional
	public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
			throws SeckillException, SeckillCloseException, RepeatKillException {
		if (md5==null||!md5.equals(getMD5(seckillId))) {
			throw new SeckillException("seckill date rewrite");
		}
		//执行秒杀逻辑
		
		
		try {
			int insertCount =successKilledDao.insertSuccessKilled(seckillId, userPhone);
			if (insertCount<=0) {
				//重复秒杀
				throw new RepeatKillException("seckillId repeated");
			}else {
				int updateCount=seckillDao.reduceNumber(seckillId, new Date());
				if (updateCount<=0) {
					//没有更新操作
					throw new SeckillCloseException("seckill is closed");
				}else {
					SuccessKilled successKilled=successKilledDao.queryByid(seckillId, userPhone);
					return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS,successKilled);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new SeckillException("seckill iner error"+e.getMessage());
		}
	}

	private String getMD5(long seckillId){
		String base=seckillId+"/"+slat;
		String md5=DigestUtils.md5DigestAsHex(base.getBytes());
		return md5;
	}

	public SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5) {
		if (md5==null||!md5.equals(getMD5(seckillId))) {
			return new SeckillExecution(seckillId,SeckillStatEnum.DATE_REWRITE);
		}
		//执行秒杀逻辑
		Map<String, Object> paramMap=new HashMap<String, Object>();
		paramMap.put("seckillId", seckillId);
		paramMap.put("userPhone", userPhone);
		paramMap.put("killTime", new Date());
		paramMap.put("result", null);
		
		try {
			seckillDao.killByProcedure(paramMap);
			Integer result=MapUtils.getInteger(paramMap, "result", -2);
			if (result==1) {
				return new SeckillExecution(seckillId,SeckillStatEnum.SUCCESS,successKilledDao.queryByid(seckillId, userPhone));
			}else {
				return new SeckillExecution(seckillId,SeckillStatEnum.stateOf(result));
			}
		} catch (Exception e) {
			throw new SecurityException(e);
		}
		
	}
}
