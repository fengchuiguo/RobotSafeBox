package com.robotsafebox.dao.demo1.seckillTest;

import com.robotsafebox.entity.demo1.seckillTest.seckillTest;

public interface seckillTestMapper {
    int deleteByPrimaryKey(Long seckillId);

    int insert(seckillTest record);

    int insertSelective(seckillTest record);

    seckillTest selectByPrimaryKey(Long seckillId);

    int updateByPrimaryKeySelective(seckillTest record);

    int updateByPrimaryKey(seckillTest record);
}