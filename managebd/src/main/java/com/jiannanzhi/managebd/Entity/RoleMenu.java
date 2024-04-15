package com.jiannanzhi.managebd.Entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName(value = "sys_role_menu")
@Data
public class RoleMenu {

    private Integer roleId;

    private Integer menuId;
}
