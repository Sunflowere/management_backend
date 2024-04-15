package com.jiannanzhi.managebd.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiannanzhi.managebd.Entity.Dict;
import com.jiannanzhi.managebd.Entity.Menu;
import com.jiannanzhi.managebd.common.Constants;
import com.jiannanzhi.managebd.common.Result;
import com.jiannanzhi.managebd.mapper.DictMapper;
import com.jiannanzhi.managebd.mapper.MenuMapper;
import com.jiannanzhi.managebd.service.MenuService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/menu")
public class MenuController {
    @Resource
    private MenuService menuService;

    @Resource
    private DictMapper dictMapper;
    @Resource
    private MenuMapper menuMapper;

    @PostMapping("/addMenu")
    public Result addMenu(@RequestBody Menu menu){
        Integer tmp_id = menu.getId();
        String tmpMenuname = menu.getName();
        if (tmpMenuname == null || tmpMenuname == "") {
            return Result.error(Constants.CODE_400, "菜单参数为空");
        }
        if (tmp_id == null || tmp_id <= 0) {
            return Result.success(menuMapper.insert(menu));
        } else {
            return Result.success(menuMapper.updateById(menu));
        }
    }

    @PostMapping("/delete")
    public int deleteMenu(@RequestBody Menu menu) {
        return menuMapper.deleteById(menu);
    }

    @PostMapping("/delete/batch")
    public int deleteMenuBatch(@RequestBody List<Integer> ids) {
        return menuMapper.deleteBatchIds(ids);
    }

    @DeleteMapping("/{id}")
    public int deleteMenuById(@PathVariable Integer id) {
        return menuMapper.deleteById(id);
    }

    @GetMapping("/search")
    public Result searchAll(@RequestParam(defaultValue = "") String name) {
        QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
        if(!"".equals(name)) {
            queryWrapper.like("name", name);
        }
        queryWrapper.orderByDesc("id");
        List<Menu> list = menuService.list(queryWrapper);
        List<Menu> parentNode = list.stream().filter(menu -> menu.getPid() == null).collect(Collectors.toList());
        for (Menu menu : parentNode) {
            menu.setChildren(list.stream().filter(menu1 -> menu1.getPid() == menu.getId()).collect(Collectors.toList()));
        }

        return Result.success(parentNode);
    }

    @GetMapping("/page")
    public IPage<Menu> searchMenuByPage(@RequestParam Integer pageNum, @RequestParam Integer pageSize, @RequestParam(defaultValue = "") String name) {
        IPage<Menu> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
        if(!"".equals(name)) {
            queryWrapper.like("name", name);
        }
        queryWrapper.orderByDesc("id");
        return menuMapper.selectPage(page, queryWrapper);
    }
    @GetMapping("/icons")
    public Result getIcons() {
        QueryWrapper<Dict> queryWrapper = new QueryWrapper();
        queryWrapper.eq("type", Constants.DICT_TYPE_ICON);
        return Result.success(dictMapper.selectList(queryWrapper));
    }
}
