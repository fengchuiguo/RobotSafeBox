package com.robotsafebox.service.demo1;

import com.robotsafebox.dto.demo1.Exposer;
import com.robotsafebox.dto.demo1.SeckillExecution;
import com.robotsafebox.entity.demo1.Seckill;
import com.robotsafebox.exception.demo1.RepeatKillException;
import com.robotsafebox.exception.demo1.SeckillCloseException;
import com.robotsafebox.exception.demo1.SeckillException;
import com.robotsafebox.framework.model.Pager;

import java.util.List;

/**
 * 业务接口：站在“使用者”角度设计接口
 * 三个方面：方法定义粒度，参数，返回类型（return 类型/异常）
 * Created by Administrator on 2016/5/24.
 */
public interface SeckillService {

    /**
     * 查询所有秒杀记录
     *
     * @return
     */
    List<Seckill> getSeckillList();

    /**
     * 分页查询
     * @return
     */
    List<Seckill> getSeckillListByPager(Pager pager);

    /**
     * 查询单个秒杀记录
     *
     * @param seckillId
     * @return
     */
    Seckill getById(int seckillId);

    /**
     * 秒杀开启时输出秒杀接口地址
     * 否则输出系统时间和秒杀时间
     *
     * @param seckillId
     */
    Exposer exportSeckillUrl(int seckillId);

    /**
     * 执行秒杀操作
     *
     * @param seckillId
     * @param userPhone
     * @param md5
     */
    SeckillExecution executeSeckill(int seckillId, String userPhone, String md5)
            throws SeckillException, RepeatKillException, SeckillCloseException;
}
