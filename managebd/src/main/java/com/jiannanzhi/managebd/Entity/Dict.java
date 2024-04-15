package com.jiannanzhi.managebd.Entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName(value = "sys_dict")
@Data
public class Dict {
    private String name;
    private String value;
    private String type;
}
