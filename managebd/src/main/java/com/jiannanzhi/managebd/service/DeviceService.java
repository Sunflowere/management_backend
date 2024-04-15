package com.jiannanzhi.managebd.service;

import com.jiannanzhi.managebd.Entity.Device;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jiannanzhi.managebd.common.Result;

/**
* @author 18447
* @description 针对表【com_device(设备信息表)】的数据库操作Service
* @createDate 2024-03-03 14:16:50
*/
public interface DeviceService extends IService<Device> {

    Result getDevicePie(Integer department_id, String cost_type);

}
