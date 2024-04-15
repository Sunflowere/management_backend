package com.jiannanzhi.managebd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiannanzhi.managebd.Entity.Econsumption;
import com.jiannanzhi.managebd.common.Result;

import java.util.Date;

public interface EconsumptionService extends IService<Econsumption> {
    Result getESystemPie(Integer department_id, String start_date, String end_date);

    Result getTrend(Integer department_id, String start_date, String end_date);

}
