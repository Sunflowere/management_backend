package com.jiannanzhi.managebd.Entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@TableName("user")
@ToString()
public class User {

    @TableId(type = IdType.AUTO)
    private Integer id;
    @TableField("username")
    private String username;
    @JsonIgnore
    @TableField("password")
    private String password;
    @TableField("nickname")
    private String nickname;

    @TableField("avatarUrl")
    private String avatarUrl;
    @TableField("email")
    private String email;
    @TableField("phone")
    private String phone;
    @TableField("address")
    private String address;
    @TableField("createTime")
    private Date createTime;
    @TableField("role")
    private String role;


}
