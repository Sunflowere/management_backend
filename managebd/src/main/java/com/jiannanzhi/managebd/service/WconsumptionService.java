package com.jiannanzhi.managebd.service;

import com.jiannanzhi.managebd.Entity.Wconsumption;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jiannanzhi.managebd.common.Result;

/**
* @author 18447
* @description 针对表【com_w_consumption(用水数据报表)】的数据库操作Service
* @createDate 2024-03-23 10:40:55
*/
public interface WconsumptionService extends IService<Wconsumption> {

    Result getWSystemPie(Integer department_id, String start_date, String end_date);

}
