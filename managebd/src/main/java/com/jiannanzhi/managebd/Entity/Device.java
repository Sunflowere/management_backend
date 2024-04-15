package com.jiannanzhi.managebd.Entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import lombok.Data;

/**
 * 设备信息表
 * @TableName com_device
 */
@TableName(value ="com_device")
@Data
public class Device implements Serializable {
    /**
     * 设备ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 设备名称
     */
    private String name;

    /**
     * 设备型号
     */
    private String model;

    /**
     * 所属部门id
     */
    private Integer did;

    /**
     * 设备转态
     */
    private String status;

    /**
     * 设备异常次数
     */
    @TableField(value = "errorCount")
    private Integer errorCount;

    /**
     * 设备是否删除
     */
    @TableLogic(value="0", delval= "1")
    private Boolean deletion;

    @TableField(value = "isEdevice")
    private Boolean isEdevice;

    @TableField(value = "isWdevice")
    private Boolean isWdevice;

    @TableField(value = "isGdevice")
    private Boolean isGdevice;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}