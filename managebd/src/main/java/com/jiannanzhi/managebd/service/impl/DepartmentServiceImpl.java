package com.jiannanzhi.managebd.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiannanzhi.managebd.Entity.Department;
import com.jiannanzhi.managebd.service.DepartmentService;
import com.jiannanzhi.managebd.mapper.DepartmentMapper;
import org.springframework.stereotype.Service;

/**
* @author 18447
* @description 针对表【com_department(部门信息表)】的数据库操作Service实现
* @createDate 2024-03-03 12:15:17
*/
@Service
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department>
    implements DepartmentService{

}




