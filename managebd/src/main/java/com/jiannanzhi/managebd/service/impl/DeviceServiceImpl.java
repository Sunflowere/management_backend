package com.jiannanzhi.managebd.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiannanzhi.managebd.Entity.Device;
import com.jiannanzhi.managebd.Entity.dto.DevicePieData;
import com.jiannanzhi.managebd.common.Result;
import com.jiannanzhi.managebd.service.DeviceService;
import com.jiannanzhi.managebd.mapper.DeviceMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* @author 18447
* @description 针对表【com_device(设备信息表)】的数据库操作Service实现
* @createDate 2024-03-03 14:16:50
*/
@Service
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, Device>
    implements DeviceService{

    @Override
    public Result getDevicePie(Integer department_id, String cost_type) {
        String column_device_type = "";
        switch (cost_type) {
            case "econsumption" :
                column_device_type = "isEdevice";
                break;
            case "wconsumption" :
                column_device_type = "isWdevice";
                break;
            case "gconsumption" :
                column_device_type = "isGdevice";
                break;
        }
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("did", department_id);
        queryWrapper.eq("status", "正常");
        queryWrapper.eq(column_device_type, true);
        Long num_normal = count(queryWrapper);
        QueryWrapper queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("did", department_id);
        queryWrapper1.eq("status", "异常");
        queryWrapper1.eq(column_device_type, true);
        Long num_error = count(queryWrapper1);
        QueryWrapper queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.eq("did", department_id);
        queryWrapper2.eq("status", "断开连接");
        queryWrapper2.eq(column_device_type, true);
        Long num_disconnected = count(queryWrapper2);

        List<DevicePieData> list_device = new ArrayList<>();
        DevicePieData devicePieData_normal = new DevicePieData();
        devicePieData_normal.setName("正常");
        devicePieData_normal.setValue(num_normal);

        DevicePieData devicePieData_error = new DevicePieData();
        devicePieData_error.setName("异常");
        devicePieData_error.setValue(num_error);

        DevicePieData devicePieData_disconnected = new DevicePieData();
        devicePieData_disconnected.setName("断开连接");
        devicePieData_disconnected.setValue(num_disconnected);

        list_device.add(devicePieData_normal);
        list_device.add(devicePieData_error);
        list_device.add(devicePieData_disconnected);

        return Result.success(list_device);
    }
}




