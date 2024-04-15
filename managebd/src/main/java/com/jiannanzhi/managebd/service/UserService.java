package com.jiannanzhi.managebd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiannanzhi.managebd.Entity.User;
import com.jiannanzhi.managebd.Entity.dto.UserDTO;

public interface UserService extends IService<User> {
    UserDTO login(UserDTO userDTO);

    User register(UserDTO userDTO);
}
