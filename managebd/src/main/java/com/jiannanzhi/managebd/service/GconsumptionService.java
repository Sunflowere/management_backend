package com.jiannanzhi.managebd.service;

import com.jiannanzhi.managebd.Entity.Gconsumption;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jiannanzhi.managebd.common.Result;

/**
* @author 18447
* @description 针对表【com_g_consumption(用气数据报表)】的数据库操作Service
* @createDate 2024-03-24 15:16:45
*/
public interface GconsumptionService extends IService<Gconsumption> {

    Result getGSystemPie(Integer department_id, String start_date, String end_date);
}
