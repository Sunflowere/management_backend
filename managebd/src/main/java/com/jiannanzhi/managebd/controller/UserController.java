package com.jiannanzhi.managebd.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiannanzhi.managebd.Entity.User;
import com.jiannanzhi.managebd.Entity.dto.UserDTO;
import com.jiannanzhi.managebd.common.Constants;
import com.jiannanzhi.managebd.common.Result;
import com.jiannanzhi.managebd.mapper.UserMapper;


import com.jiannanzhi.managebd.service.UserService;
import com.jiannanzhi.managebd.utils.TokenUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;

@RestController
@RequestMapping("user")
public class UserController {

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserService userService;

    @GetMapping("/")
    public String index() {
        return "ok";
    }

    @GetMapping("/search")
    public List<User> searchAll() {
        return userMapper.selectList(null);
    }

    @PostMapping("/addUser")
    public Result addUser(@RequestBody User user){
        Integer tmp_id = user.getId();
        String tmpUsername = user.getUsername();
        if (tmpUsername == null) {
            return Result.error(Constants.CODE_400, "用戶名為空");
        }
        if (tmp_id == null || tmp_id <= 0) {
            return Result.success(userMapper.insert(user));
        } else {
            return Result.success(userMapper.updateById(user));
        }
    }

    @PostMapping("/update")
    public int updateUser(@RequestBody User user) {
        return userMapper.updateById(user);
    }

    @PostMapping("/delete")
    public int deleteUser(@RequestBody User user) {
        return userMapper.deleteById(user);
    }

    @PostMapping("/delete/batch")
    public int deleteUserBatch(@RequestBody List<Integer> ids) {
        return userMapper.deleteBatchIds(ids);
    }

    @DeleteMapping("/{id}")
    public int deleteUserById(@PathVariable Integer id) {
        return userMapper.deleteById(id);
    }

    @GetMapping("/username/{username}")
    public Result getUserInfo(@PathVariable String username) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("username", username);
        return Result.success(userService.getOne(queryWrapper));
    }
    @GetMapping("/page")
    public IPage<User> searchUserByPage(@RequestParam Integer pageNum, @RequestParam Integer pageSize, @RequestParam(defaultValue = "") String username, @RequestParam(defaultValue = "") String email ,@RequestParam(defaultValue = "") String address) {
        IPage<User> page = new Page<>(pageNum, pageSize);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if(!"".equals(username)) {
            queryWrapper.like("username", username);
        }
        if(!"".equals(email)) {
            queryWrapper.like("email", email);
        }if(!"".equals(address)) {
            queryWrapper.like("address", address);
        }
        queryWrapper.orderByDesc("id");
        User currentUser = TokenUtils.getCurrentUser();
        System.out.println("当前用户===============" + currentUser.getNickname());
        return userMapper.selectPage(page, queryWrapper);
    }

    /**
     * 导出接口
     */
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception {
        // 从数据库查询出所有的数据
        List<User> list = userService.list();
        // 通过工具类创建writer 写出到磁盘路径
//        ExcelWriter writer = ExcelUtil.getWriter(filesUploadPath + "/用户信息.xlsx");
        // 在内存操作，写出到浏览器
        ExcelWriter writer = ExcelUtil.getWriter(true);
        //自定义标题别名
        writer.addHeaderAlias("username", "用户名");
        writer.addHeaderAlias("password", "密码");
        writer.addHeaderAlias("nickname", "昵称");
        writer.addHeaderAlias("email", "邮箱");
        writer.addHeaderAlias("phone", "电话");
        writer.addHeaderAlias("address", "地址");
        writer.addHeaderAlias("createTime", "创建时间");
        writer.addHeaderAlias("avatarUrl", "头像");
        writer.addHeaderAlias("role", "角色");
        // 一次性写出list内的对象到excel，使用默认样式，强制输出标题
        writer.write(list, true);

        // 设置浏览器响应的格式
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("用户信息", "UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

        ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true);
        out.close();
        writer.close();

    }

    /**
     * excel 导入
     * @param file
     * @throws Exception
     */
    @PostMapping("/import")
    public Boolean imp(MultipartFile file) throws Exception {
        InputStream inputStream = file.getInputStream();
        ExcelReader reader = ExcelUtil.getReader(inputStream);
        // 方式1：(推荐) 通过 javabean的方式读取Excel内的对象，但是要求表头必须是英文，跟javabean的属性要对应起来
//        List<User> list = reader.readAll(User.class);

        // 方式2：忽略表头的中文，直接读取表的内容
        List<List<Object>> list = reader.read(1);
        List<User> users = CollUtil.newArrayList();
        for (List<Object> row : list) {
            User user = new User();
            user.setUsername(row.get(1).toString());
            user.setPassword(row.get(2).toString());
            user.setNickname(row.get(3).toString());
            user.setEmail(row.get(4).toString());
            user.setPhone(row.get(5).toString());
            user.setAddress(row.get(6).toString());
            user.setAvatarUrl(row.get(8).toString());
            user.setRole(row.get(9).toString());
            users.add(user);
        }

        userService.saveBatch(users);
        return true;
    }

    @PostMapping("login")
    public Result login(@RequestBody UserDTO userDTO) {
        String username = userDTO.getUsername();
        String password = userDTO.getPassword();
        if (StrUtil.isBlank(username) || StrUtil.isBlank(password)) {
            return Result.error(Constants.CODE_400, "参数错误");
        }
        return Result.success(userService.login(userDTO));
    }

    @PostMapping("register")
    public Result register(@RequestBody UserDTO userDTO) {
        String username = userDTO.getUsername();
        String password = userDTO.getPassword();
        if (StrUtil.isBlank(username) || StrUtil.isBlank(password)) {
            return Result.error(Constants.CODE_400, "参数错误");
        }
        return Result.success(userService.register(userDTO));
    }

}
