package com.robotsafebox.service.demo1;

import com.robotsafebox.dao.cache.demo1.RedisDao;
import com.robotsafebox.dao.demo1.SeckillDao;
import com.robotsafebox.dao.demo1.SuccessKilledDao;
import com.robotsafebox.dto.demo1.Exposer;
import com.robotsafebox.dto.demo1.SeckillExecution;
import com.robotsafebox.entity.demo1.Seckill;
import com.robotsafebox.entity.demo1.SuccessKilled;
import com.robotsafebox.enums.demo1.SeckillStateEnums;
import com.robotsafebox.exception.demo1.RepeatKillException;
import com.robotsafebox.exception.demo1.SeckillCloseException;
import com.robotsafebox.exception.demo1.SeckillException;
import com.robotsafebox.framework.model.Pager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Service
public class SeckillServiceImpl implements SeckillService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    //注入Service依赖,自动装配,不需要手动新建相应实例
    @Autowired
    private SeckillDao seckillDao;

    @Autowired
    private SuccessKilledDao successKilledDao;

//    @Autowired
//    private RedisDao redisDao;

    //md5盐值字符串，用于混淆MD5
    private final String slat = "asd$%^$156120#BbK0-%^%*&!&*fef~{}@##VJ*{))&@@@@#";

    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0, 10);
    }

    public List<Seckill> getSeckillListByPager(Pager pager) {
        return seckillDao.queryAllByPager(pager);
    }

    public Seckill getById(int seckillId) {
        return seckillDao.queryById(seckillId);
    }

    public Exposer exportSeckillUrl(int seckillId) {

        Seckill seckill = seckillDao.queryById(seckillId);
        if (seckill == null) {
            return new Exposer(false, seckillId);
        }

//        //优化缓存
//        //1.访问redis,降低数据库访问压力
//        Seckill seckill = redisDao.getSeckill(seckillId);
//
//        if (seckill == null) {
//            //2.访问数据库
//            System.out.println("assess database");
//            seckill = seckillDao.queryById(seckillId);
//            if (seckill == null) {
//                return new Exposer(false, seckillId);
//            } else {
//                //3.放入redis
//                System.out.println("put into redis");
//                redisDao.putSeckill(seckill);
//            }
//        }


        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        //系统当前时间
        Date nowTime = new Date();
        if (nowTime.getTime() < startTime.getTime()
                || nowTime.getTime() > endTime.getTime()) {
            return new Exposer(startTime.getTime(), endTime.getTime(), nowTime.getTime(), false, seckillId);
        }
        //转化特定字符串的过程，不可逆
        String md5 = getMD5(seckillId);//
        return new Exposer(md5, true, seckillId);

    }

    //生成并返回一个md5
    private String getMD5(int seckillId) {

        String base = seckillId + "/" + slat;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }


    @Transactional
    /**
     * 使用注解控制事务
     */
    public SeckillExecution executeSeckill(int seckillId, String userPhone, String md5)
            throws SeckillException, RepeatKillException, SeckillCloseException {

        if (md5 == null || !md5.equals(getMD5(seckillId))) {
            throw new SeckillException("seckill data rewrite");
        }
        //执行秒杀逻辑; 减库存+记录购买明细
        Timestamp nowTime = new Timestamp(System.currentTimeMillis());
        try {
            int updateCount = seckillDao.reduceNumber(seckillId, nowTime);
            if (updateCount <= 0) {
                //并发量太高，有可能在等行级锁的时候库存没有了,并且秒杀时间问题在前面已经验证。
                throw new SeckillCloseException("seckill is closed");
            } else {

                int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone, nowTime);
                //唯一：seckillId,userPhone（联合主键）
                if (insertCount <= 0) {
                    //重复秒杀
                    throw new RepeatKillException("seckill repeated");
                } else {
                    //秒杀成功
                    SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillStateEnums.SUCCESS, successKilled);  //枚举
                }
            }
        } catch (SeckillCloseException e1) {
            throw e1;  //抛出异常
        } catch (RepeatKillException e2) {
            throw e2;   //抛出异常
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            //所有编译期异常转化为运行期异常    以便rollback回滚
            throw new SeckillException("seckill inner error:" + e.getMessage());    //抛出异常
        }
    }
}
