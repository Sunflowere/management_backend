package com.jiannanzhi.managebd.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiannanzhi.managebd.Entity.Department;
import com.jiannanzhi.managebd.Entity.Wconsumption;
import com.jiannanzhi.managebd.Entity.dto.Data;
import com.jiannanzhi.managebd.common.Result;
import com.jiannanzhi.managebd.mapper.DepartmentMapper;
import com.jiannanzhi.managebd.mapper.WconsumptionMapper;
import com.jiannanzhi.managebd.service.WconsumptionService;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("wconsumption")
public class WconsumptionController {

    @Resource
    private WconsumptionMapper wconsumptionMapper;

    @Resource
    private WconsumptionService wconsumptionService;

    @Resource
    private DepartmentMapper departmentMapper;

    @GetMapping("/systemPie")
    public Result getWSystemPie(@RequestParam Integer department_id, @RequestParam String start_date, @RequestParam String end_date) {

        return wconsumptionService.getWSystemPie(department_id, start_date, end_date);
    }

    @GetMapping("/Data/{id}")
    public Result getData(@PathVariable Integer id) {
        //起止时间
        LocalDateTime localDateTime_day = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime localDateTime_month = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime localDateTime_year = LocalDateTime.now().withMonth(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("department_id", id);
        List<Wconsumption> list = wconsumptionMapper.selectList(queryWrapper);
        BigDecimal bigDecimal_day = new BigDecimal(0.0);
        BigDecimal bigDecimal_month = new BigDecimal(0.0);
        BigDecimal bigDecimal_year = new BigDecimal(0.0);
        for (Wconsumption wconsumption : list) {
//            double calculation = wconsumption.getCalculation();
            BigDecimal bigDecimal = new BigDecimal(wconsumption.getCalculation().toString());
            LocalDateTime localDateTime = wconsumption.getDate_time().toLocalDateTime();
            if (localDateTime.isAfter(localDateTime_day)) {
                bigDecimal_day = bigDecimal_day.add(bigDecimal);
            }
            if (localDateTime.isAfter(localDateTime_month)) {
                bigDecimal_month = bigDecimal_month.add(bigDecimal);
            }
            if (localDateTime.isAfter(localDateTime_year)) {
                bigDecimal_year = bigDecimal_year.add(bigDecimal);
            }
        }
        Data data = new Data();
        data.setData_day(bigDecimal_day.doubleValue());
        data.setData_month(bigDecimal_month.doubleValue());
        data.setData_year(bigDecimal_year.doubleValue());

        return Result.success(data);
    }

    @GetMapping("/search")
    public List<Wconsumption> searchAll() {
        return wconsumptionMapper.selectList(null);
    }

    @PostMapping("/addWconsumption")
    public Result addWconsumption(@RequestBody Wconsumption wconsumption){
        Long tmp_id = wconsumption.getId();
        //获取本次用水数据
        Double wconsump = wconsumption.getCalculation();
        //创建BigDecimal对象
        BigDecimal bigDecimal_e = new BigDecimal(wconsump.toString());
        //获取用水部门id
        Integer department_id = wconsumption.getDepartment_id();
        Department department = departmentMapper.selectById(department_id);
        Double tmp_sumW = department.getSumW() == null ? 0 : department.getSumW();
        BigDecimal tmp_bigDecimal = new BigDecimal(tmp_sumW.toString());
        if (tmp_id == null || tmp_id <= 0) {

            Double result_add = bigDecimal_e.add(tmp_bigDecimal).doubleValue();
            department.setSumW(result_add);
            departmentMapper.updateById(department);
            return Result.success(wconsumptionMapper.insert(wconsumption));
        } else {
            //获取到当前数据库中的对应的用水数据信息
            QueryWrapper oldQueryWrapper = new QueryWrapper();
            oldQueryWrapper.eq("id", tmp_id);
            Wconsumption oldWconsumption = wconsumptionMapper.selectOne(oldQueryWrapper);
            BigDecimal bigDecimal_old = new BigDecimal(oldWconsumption.getCalculation().toString());
            Double result_update = tmp_bigDecimal.subtract(bigDecimal_old).add(bigDecimal_e).doubleValue();
            department.setSumW(result_update);
            departmentMapper.updateById(department);
            return Result.success(wconsumptionMapper.updateById(wconsumption));
        }
    }

    @PostMapping("/update")
    public int updateWconsumption(@RequestBody Wconsumption wconsumption) {
        return wconsumptionMapper.updateById(wconsumption);
    }

    @PostMapping("/delete")
    public int deleteWconsumption(@RequestBody Wconsumption wconsumption) {
        return wconsumptionMapper.deleteById(wconsumption);
    }

    @PostMapping("/delete/batch")
    public int deleteWconsumptionBatch(@RequestBody List<Integer> ids) {
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", ids);
        List<Wconsumption> wconsumption_list = wconsumptionMapper.selectList(queryWrapper);
        for (Wconsumption wconsumption : wconsumption_list) {
            Integer tmp_department_id = wconsumption.getDepartment_id();
            Department tmp_department = departmentMapper.selectById(tmp_department_id);
            BigDecimal bigDecimal_old = new BigDecimal(tmp_department.getSumW().toString());
            BigDecimal tmp_bigDecimal = new BigDecimal(wconsumption.getCalculation().toString());
            tmp_department.setSumW(bigDecimal_old.subtract(tmp_bigDecimal).doubleValue());
            departmentMapper.updateById(tmp_department);
        }
        return wconsumptionMapper.deleteBatchIds(ids);
    }

    @DeleteMapping("/{id}")
    public int deleteWconsumptionById(@PathVariable Integer id) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("id", id);
        Wconsumption get_wconsumption = wconsumptionMapper.selectOne(queryWrapper);
        Integer department_id = get_wconsumption.getDepartment_id();
        Department department = departmentMapper.selectById(department_id);
        BigDecimal bigDecimal_old = new BigDecimal(department.getSumW().toString());
        BigDecimal bigDecimal_e = new BigDecimal(get_wconsumption.getCalculation().toString());
        Double result_delete = bigDecimal_old.subtract(bigDecimal_e).doubleValue();
        department.setSumW(result_delete);
        departmentMapper.updateById(department);
        return wconsumptionMapper.deleteById(id);
    }

    @GetMapping("/wconsumptionname/{wconsumptionname}")
    public Result getWconsumptionInfo(@PathVariable String wconsumptionname) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("wconsumptionname", wconsumptionname);
        return Result.success(wconsumptionMapper.selectOne(queryWrapper));
    }
    @GetMapping("/page")
    public IPage<Wconsumption> searchWconsumptionByPage(@RequestParam Integer pageNum, @RequestParam Integer pageSize, @RequestParam(defaultValue = "") Integer department_id , @RequestParam(defaultValue = "") String system_W) {
        IPage<Wconsumption> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Wconsumption> queryWrapper = new QueryWrapper<>();
        if(!("".equals(department_id) || department_id == null)) {
            queryWrapper.eq("department_id", department_id);
        }
        if(!"".equals(system_W)) {
            queryWrapper.like("system_W", system_W);
        }
        queryWrapper.orderByDesc("id");
        return wconsumptionMapper.selectPage(page, queryWrapper);
    }

    /**
     * 导出接口
     */
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception {
        // 从数据库查询出所有的数据
        List<Wconsumption> list = wconsumptionService.list();
        // 通过工具类创建writer 写出到磁盘路径
//        ExcelWriter writer = ExcelUtil.getWriter(filesUploadPath + "/用水数据报表.xlsx");
        // 在内存操作，写出到浏览器
        ExcelWriter writer = ExcelUtil.getWriter(true);
        //自定义标题别名
        writer.addHeaderAlias("department_id", "用水部门ID");
        writer.addHeaderAlias("system_W", "用水系统");
        writer.addHeaderAlias("calculation", "计量（单位m³）");
        writer.addHeaderAlias("date_time", "统计日期");

        // 一次性写出list内的对象到excel，使用默认样式，强制输出标题
        writer.write(list, true);

        // 设置浏览器响应的格式
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("用水数据", "UTF-8");
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
//        List<Wconsumption> list = reader.readAll(Wconsumption.class);

        // 方式2：忽略表头的中文，直接读取表的内容
        List<List<Object>> list = reader.read(1);
        List<Wconsumption> wconsumptions = CollUtil.newArrayList();
        BigDecimal bigDecimal_old;
        BigDecimal bigDecimal_e;
        for (List<Object> row : list) {
            Wconsumption wconsumption = new Wconsumption();
            System.out.println(row.get(1));
            if (!StringUtils.isAnyBlank(row.get(1).toString())) {
                wconsumption.setDepartment_id((Integer.parseInt(row.get(1).toString())));
            }
            wconsumption.setSystem_W(row.get(2).toString());
            if (!StringUtils.isAnyBlank(row.get(3).toString())){
                wconsumption.setCalculation(Double.parseDouble(row.get(3).toString()));
            }
            // 获取每次用水的部门，累加这个部门的用水数据
            Department department = departmentMapper.selectById(wconsumption.getDepartment_id());
            bigDecimal_old = new BigDecimal(department.getSumW().toString());
            bigDecimal_e = new BigDecimal(wconsumption.getCalculation().toString());
            Double result_import = bigDecimal_old.add(bigDecimal_e).doubleValue();
            department.setSumW(result_import);
            departmentMapper.updateById(department);
            wconsumptions.add(wconsumption);

        }

        wconsumptionService.saveBatch(wconsumptions);
        return true;
    }
}
