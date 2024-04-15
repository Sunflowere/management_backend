package com.jiannanzhi.managebd.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiannanzhi.managebd.Entity.Role;
import com.jiannanzhi.managebd.Entity.RoleMenu;
import com.jiannanzhi.managebd.common.Constants;
import com.jiannanzhi.managebd.common.Result;
import com.jiannanzhi.managebd.mapper.RoleMapper;
import com.jiannanzhi.managebd.mapper.RoleMenuMapper;
import com.jiannanzhi.managebd.service.RoleService;
import jakarta.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/role")
public class RoleController {

    @Resource
    private RoleMenuMapper roleMenuMapper;
    @Resource
    private RoleMapper roleMapper;

    @PostMapping("/addRole")
    public Result addRole(@RequestBody Role role){
        Integer tmp_id = role.getId();
        String tmpRolename = role.getName();
        if (tmpRolename == null || tmpRolename == "") {
            return Result.error(Constants.CODE_400, "角色為空");
        }
        if (tmp_id == null || tmp_id <= 0) {
            return Result.success(roleMapper.insert(role));
        } else {
            return Result.success(roleMapper.updateById(role));
        }
    }

    @PostMapping("/delete")
    public int deleteRole(@RequestBody Role role) {
        return roleMapper.deleteById(role);
    }

    @PostMapping("/delete/batch")
    public int deleteRoleBatch(@RequestBody List<Integer> ids) {
        return roleMapper.deleteBatchIds(ids);
    }

    @DeleteMapping("/{id}")
    public int deleteRoleById(@PathVariable Integer id) {
        return roleMapper.deleteById(id);
    }

    @GetMapping("/search")
    public List<Role> searchAll() {
        return roleMapper.selectList(null);
    }

    @GetMapping("/page")
    public IPage<Role> searchRoleByPage(@RequestParam Integer pageNum, @RequestParam Integer pageSize, @RequestParam(defaultValue = "") String name) {
        IPage<Role> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        if(!"".equals(name)) {
            queryWrapper.like("name", name);
        }

        queryWrapper.orderByDesc("id");

        return roleMapper.selectPage(page, queryWrapper);
    }
    @Transactional
    @PostMapping("/roleMenu/{roleId}")
    public Result setRoleMenu(@PathVariable Integer roleId, @RequestBody List<Integer> menuIds) {

        QueryWrapper<RoleMenu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_id", roleId);
        roleMenuMapper.delete(queryWrapper);

        for (Integer menuId : menuIds) {
            RoleMenu roleMenu = new RoleMenu();
            roleMenu.setRoleId(roleId);
            roleMenu.setMenuId(menuId);
            roleMenuMapper.insert(roleMenu);
        }
        return Result.success();

    }

    @GetMapping("/roleMenuIfo/{roleId}")
    public Result getRoleMenuIfo(@PathVariable Integer roleId) {
        return Result.success(roleMenuMapper.getRoleMenuIfo(roleId));

    }
}
