package com.jiannanzhi.managebd.Entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 用水数据报表
 * @TableName com_w_consumption
 */
@TableName(value ="com_w_consumption")
@Data
public class Wconsumption implements Serializable {
    /**
     * 数据ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用水部门
     */
    private Integer department_id;

    /**
     * 用水系统
     */
    private String system_W;

    /**
     * 计量
     */
    private Double calculation;

    /**
     * 统计日期
     */
    @TableField(value = "date_time")
    @JsonFormat(shape=JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Timestamp date_time;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}