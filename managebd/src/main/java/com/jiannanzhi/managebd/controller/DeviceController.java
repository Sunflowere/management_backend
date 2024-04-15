package com.jiannanzhi.managebd.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiannanzhi.managebd.Entity.Department;
import com.jiannanzhi.managebd.Entity.Device;
import com.jiannanzhi.managebd.Entity.dto.DeviceData;
import com.jiannanzhi.managebd.Entity.dto.DevicePieData;
import com.jiannanzhi.managebd.common.Constants;
import com.jiannanzhi.managebd.common.Result;
import com.jiannanzhi.managebd.mapper.DepartmentMapper;
import com.jiannanzhi.managebd.mapper.DeviceMapper;
import com.jiannanzhi.managebd.service.DeviceService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/device")
public class DeviceController {
    @Resource
    private DeviceMapper deviceMapper;

    @Resource
    private DeviceService deviceService;

    @Resource
    private DepartmentMapper departmentMapper;

    @GetMapping("/")
    public String index() {
        return "ok";
    }

    @GetMapping("/devicePie")
    public Result getDevicePie(@RequestParam Integer department_id, @RequestParam String cost_type) {
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", department_id);
        if (!departmentMapper.exists(queryWrapper)) {
            return Result.error(Constants.CODE_400, "参数错误，请检查部门是否存在");
        }
        return deviceService.getDevicePie(department_id, cost_type);

    }

    @GetMapping("/deviceEData/{id}")
    public Result getDeviceEData(@PathVariable Integer id) {
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("did", id);
        queryWrapper.eq("status", "正常");
        queryWrapper.eq("isEdevice", true);
        Long num_normalE = deviceMapper.selectCount(queryWrapper);
        QueryWrapper queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("did", id);
        queryWrapper1.eq("status", "异常");
        queryWrapper1.eq("isEdevice", true);
        Long num_errorE = deviceMapper.selectCount(queryWrapper1);
        QueryWrapper queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.eq("did", id);
        queryWrapper2.eq("status", "断开连接");
        queryWrapper2.eq("isEdevice", true);
        Long num_disconnectedE = deviceMapper.selectCount(queryWrapper2);

        DeviceData deviceData = new DeviceData();
        deviceData.setNormalEDevice(num_normalE);
        deviceData.setErrorEDevice(num_errorE);
        deviceData.setDisconnectedE(num_disconnectedE);
        return Result.success(deviceData);
    }
    @GetMapping("/deviceWData/{id}")
    public Result getDeviceWData(@PathVariable Integer id) {
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("did", id);
        queryWrapper.eq("status", "正常");
        queryWrapper.eq("isWdevice", true);
        Long num_normalW = deviceMapper.selectCount(queryWrapper);
        QueryWrapper queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("did", id);
        queryWrapper1.eq("status", "异常");
        queryWrapper1.eq("isWdevice", true);
        Long num_errorW = deviceMapper.selectCount(queryWrapper1);
        QueryWrapper queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.eq("did", id);
        queryWrapper2.eq("status", "断开连接");
        queryWrapper2.eq("isWdevice", true);
        Long num_disconnectedW = deviceMapper.selectCount(queryWrapper2);

        DeviceData deviceData = new DeviceData();
        deviceData.setNormalWDevice(num_normalW);
        deviceData.setErrorWDevice(num_errorW);
        deviceData.setDisconnectedW(num_disconnectedW);
        return Result.success(deviceData);
    }
    @GetMapping("/deviceGData/{id}")
    public Result getDeviceGData(@PathVariable Integer id) {
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("did", id);
        queryWrapper.eq("status", "正常");
        queryWrapper.eq("isGdevice", true);
        Long num_normalG = deviceMapper.selectCount(queryWrapper);
        QueryWrapper queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("did", id);
        queryWrapper1.eq("status", "异常");
        queryWrapper1.eq("isGdevice", true);
        Long num_errorG = deviceMapper.selectCount(queryWrapper1);
        QueryWrapper queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.eq("did", id);
        queryWrapper2.eq("status", "断开连接");
        queryWrapper2.eq("isGdevice", true);
        Long num_disconnectedG = deviceMapper.selectCount(queryWrapper2);

        DeviceData deviceData = new DeviceData();
        deviceData.setNormalGDevice(num_normalG);
        deviceData.setErrorGDevice(num_errorG);
        deviceData.setDisconnectedG(num_disconnectedG);
        return Result.success(deviceData);
    }

    @GetMapping("/search")
    public List<Device> searchAll() {
        return deviceMapper.selectList(null);
    }

    @PostMapping("/addDevice")
    public Result addDevice(@RequestBody Device device){
        Long tmp_id = device.getId();
        String tmpDevicename = device.getName();
        if (tmpDevicename == null) {
            return Result.error(Constants.CODE_400, "用戶名為空");
        }
        if (tmp_id == null || tmp_id <= 0) {
            return Result.success(deviceMapper.insert(device));
        } else {
            return Result.success(deviceMapper.updateById(device));
        }
    }

    @PostMapping("/update")
    public int updateDevice(@RequestBody Device device) {
        return deviceMapper.updateById(device);
    }

    @PostMapping("/delete")
    public int deleteDevice(@RequestBody Device device) {
        return deviceMapper.deleteById(device);
    }

    @PostMapping("/delete/batch")
    public int deleteDeviceBatch(@RequestBody List<Integer> ids) {
        return deviceMapper.deleteBatchIds(ids);
    }

    @DeleteMapping("/{id}")
    public int deleteDeviceById(@PathVariable Integer id) {
        return deviceMapper.deleteById(id);
    }

    @GetMapping("/devicename/{devicename}")
    public Result getDeviceInfo(@PathVariable String devicename) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("devicename", devicename);
        return Result.success(deviceService.getOne(queryWrapper));
    }
    @GetMapping("/page")
    public IPage<Device> searchDeviceByPage(@RequestParam Integer pageNum, @RequestParam Integer pageSize, @RequestParam(defaultValue = "") String name, @RequestParam(defaultValue = "") String model , @RequestParam(defaultValue = "") Integer did, @RequestParam(defaultValue = "") String status) {
        IPage<Device> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Device> queryWrapper = new QueryWrapper<>();
        if(!"".equals(name)) {
            queryWrapper.like("name", name);
        }
        if(!"".equals(model)) {
            queryWrapper.like("model", model);
        }
        if(!("".equals(did) || did == null)) {
            queryWrapper.eq("did", did);
        }
        if(!"".equals(status)) {
            queryWrapper.like("status", status);
        }
        queryWrapper.orderByDesc("id");


        return deviceMapper.selectPage(page, queryWrapper);
    }
}
