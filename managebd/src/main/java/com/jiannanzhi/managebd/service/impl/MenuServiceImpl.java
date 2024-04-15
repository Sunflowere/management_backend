package com.jiannanzhi.managebd.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiannanzhi.managebd.Entity.Menu;
import com.jiannanzhi.managebd.service.MenuService;
import com.jiannanzhi.managebd.mapper.MenuMapper;
import org.springframework.stereotype.Service;

/**
* @author 18447
* @description 针对表【sys_menu(菜单列表)】的数据库操作Service实现
* @createDate 2024-02-27 15:15:50
*/
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu>
    implements MenuService{

}




