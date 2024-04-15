package com.jiannanzhi.managebd.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiannanzhi.managebd.Entity.Department;
import com.jiannanzhi.managebd.common.Constants;
import com.jiannanzhi.managebd.common.Result;

import com.jiannanzhi.managebd.mapper.DepartmentMapper;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/department")
public class DepartmentController {
    @Resource
    private DepartmentMapper departmentMapper;

    @GetMapping("/")
    public String index() {
        return "ok";
    }

    @GetMapping("departmentIfo/{id}")
    public Result getDepartment(@PathVariable Integer id) {
        return Result.success(departmentMapper.selectById(id));
    }

    @GetMapping("/search")
    public List<Department> searchAll() {
        return departmentMapper.selectList(null);
    }

    @PostMapping("/update")
    public int updateDepartment(@RequestBody Department department) {
        return departmentMapper.updateById(department);
    }

    @PostMapping("/delete")
    public int deleteDepartment(@RequestBody Department department) {
        return departmentMapper.deleteById(department);
    }

    @PostMapping("/delete/batch")
    public int deleteDepartmentBatch(@RequestBody List<Integer> ids) {
        return departmentMapper.deleteBatchIds(ids);
    }

    @DeleteMapping("/{id}")
    public int deleteDepartmentById(@PathVariable Integer id) {
        return departmentMapper.deleteById(id);
    }


    @GetMapping("/page")
    public IPage<Department> searchDepartmentByPage(@RequestParam Integer pageNum, @RequestParam Integer pageSize, @RequestParam(defaultValue = "") String name , @RequestParam(defaultValue = "") String model, @RequestParam(defaultValue = "") String address) {
        IPage<Department> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Department> queryWrapper = new QueryWrapper<>();
        if(!"".equals(name)) {
            queryWrapper.like("name", name);
        }
        if(!"".equals(model)) {
            queryWrapper.like("model", model);
        }
        if(!"".equals(address)) {
            queryWrapper.like("address", address);
        }
        queryWrapper.orderByDesc("id");
        return departmentMapper.selectPage(page, queryWrapper);
    }
}
