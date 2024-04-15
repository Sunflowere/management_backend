package com.jiannanzhi.managebd.Entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.sql.Timestamp;

@TableName(value = "com_e_consumption")
@Data
public class Econsumption {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(value = "department_id")
    private Integer departmentId;

    @TableField(value = "system_E")
    private String systemE;

    @TableField(value = "part_E")
    private String partE;

    @TableField(value = "calculation")
    private Double calculation;

    @TableField(value = "date_time")
    @JsonFormat(shape=JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Timestamp dateTime;
}
