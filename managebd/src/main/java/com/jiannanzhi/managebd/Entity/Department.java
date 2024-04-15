package com.jiannanzhi.managebd.Entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 部门信息表
 * @TableName com_department
 */
@TableName(value ="com_department")
@Data
public class Department implements Serializable {
    /**
     * 部门ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 部门名称
     */
    private String name;

    /**
     * 部门所属上级部门
     */
    private Integer pid;

    /**
     * 单位地址
     */
    private String address;

    /**
     * 累计用电
     */
    private Double sumE;

    /**
     * 累计用水
     */
    private Double sumW;

    /**
     * 累计用气
     */
    private Double sumG;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}