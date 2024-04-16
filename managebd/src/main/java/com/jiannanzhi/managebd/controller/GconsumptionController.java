package com.jiannanzhi.managebd.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiannanzhi.managebd.Entity.Department;
import com.jiannanzhi.managebd.Entity.Gconsumption;
import com.jiannanzhi.managebd.Entity.dto.Data;
import com.jiannanzhi.managebd.common.Result;
import com.jiannanzhi.managebd.mapper.DepartmentMapper;
import com.jiannanzhi.managebd.mapper.GconsumptionMapper;
import com.jiannanzhi.managebd.service.GconsumptionService;
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
@RequestMapping("gconsumption")
public class GconsumptionController {

    @Resource
    private GconsumptionMapper gconsumptionMapper;

    @Resource
    private GconsumptionService gconsumptionService;

    @Resource
    private DepartmentMapper departmentMapper;


    @GetMapping("/trend")
    public Result getTrend(@RequestParam Integer department_id, @RequestParam String start_date, @RequestParam String end_date) {
        return gconsumptionService.getTrend(department_id, start_date, end_date);
    }

    @GetMapping("/systemPie")
    public Result getGSystemPie(@RequestParam Integer department_id, @RequestParam String start_date, @RequestParam String end_date) {

        return gconsumptionService.getGSystemPie(department_id, start_date, end_date);
    }

    @GetMapping("/Data/{id}")
    public Result getData(@PathVariable Integer id) {
        //起止时间
        LocalDateTime localDateTime_day = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime localDateTime_month = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime localDateTime_year = LocalDateTime.now().withMonth(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("department_id", id);
        List<Gconsumption> list = gconsumptionMapper.selectList(queryWrapper);
        BigDecimal bigDecimal_day = new BigDecimal(0.0);
        BigDecimal bigDecimal_month = new BigDecimal(0.0);
        BigDecimal bigDecimal_year = new BigDecimal(0.0);
        for (Gconsumption gconsumption : list) {
//            double calculation = gconsumption.getCalculation();
            BigDecimal bigDecimal = new BigDecimal(gconsumption.getCalculation().toString());
            LocalDateTime localDateTime = gconsumption.getDate_time().toLocalDateTime();
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
    public List<Gconsumption> searchAll() {
        return gconsumptionMapper.selectList(null);
    }

    @PostMapping("/addGconsumption")
    public Result addGconsumption(@RequestBody Gconsumption gconsumption){
        Long tmp_id = gconsumption.getId();
        //获取本次用水数据
        Double gconsump = gconsumption.getCalculation();
        //创建BigDecimal对象
        BigDecimal bigDecimal_e = new BigDecimal(gconsump.toString());
        //获取用水部门id
        Integer department_id = gconsumption.getDepartment_id();
        Department department = departmentMapper.selectById(department_id);
        Double tmp_sumG = department.getSumG() == null ? 0 : department.getSumG();
        BigDecimal tmp_bigDecimal = new BigDecimal(tmp_sumG.toString());
        if (tmp_id == null || tmp_id <= 0) {

            Double result_add = bigDecimal_e.add(tmp_bigDecimal).doubleValue();
            department.setSumG(result_add);
            departmentMapper.updateById(department);
            return Result.success(gconsumptionMapper.insert(gconsumption));
        } else {
            //获取到当前数据库中的对应的用水数据信息
            QueryWrapper oldQueryWrapper = new QueryWrapper();
            oldQueryWrapper.eq("id", tmp_id);
            Gconsumption oldGconsumption = gconsumptionMapper.selectOne(oldQueryWrapper);
            BigDecimal bigDecimal_old = new BigDecimal(oldGconsumption.getCalculation().toString());
            Double result_update = tmp_bigDecimal.subtract(bigDecimal_old).add(bigDecimal_e).doubleValue();
            department.setSumG(result_update);
            departmentMapper.updateById(department);
            return Result.success(gconsumptionMapper.updateById(gconsumption));
        }
    }

    @PostMapping("/update")
    public int updateGconsumption(@RequestBody Gconsumption gconsumption) {
        return gconsumptionMapper.updateById(gconsumption);
    }

    @PostMapping("/delete")
    public int deleteGconsumption(@RequestBody Gconsumption gconsumption) {
        return gconsumptionMapper.deleteById(gconsumption);
    }

    @PostMapping("/delete/batch")
    public int deleteGconsumptionBatch(@RequestBody List<Integer> ids) {
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", ids);
        List<Gconsumption> gconsumption_list = gconsumptionMapper.selectList(queryWrapper);
        for (Gconsumption gconsumption : gconsumption_list) {
            Integer tmp_department_id = gconsumption.getDepartment_id();
            Department tmp_department = departmentMapper.selectById(tmp_department_id);
            BigDecimal bigDecimal_old = new BigDecimal(tmp_department.getSumG().toString());
            BigDecimal tmp_bigDecimal = new BigDecimal(gconsumption.getCalculation().toString());
            tmp_department.setSumG(bigDecimal_old.subtract(tmp_bigDecimal).doubleValue());
            departmentMapper.updateById(tmp_department);
        }
        return gconsumptionMapper.deleteBatchIds(ids);
    }

    @DeleteMapping("/{id}")
    public int deleteGconsumptionById(@PathVariable Integer id) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("id", id);
        Gconsumption get_gconsumption = gconsumptionMapper.selectOne(queryWrapper);
        Integer department_id = get_gconsumption.getDepartment_id();
        Department department = departmentMapper.selectById(department_id);
        BigDecimal bigDecimal_old = new BigDecimal(department.getSumG().toString());
        BigDecimal bigDecimal_e = new BigDecimal(get_gconsumption.getCalculation().toString());
        Double result_delete = bigDecimal_old.subtract(bigDecimal_e).doubleValue();
        department.setSumG(result_delete);
        departmentMapper.updateById(department);
        return gconsumptionMapper.deleteById(id);
    }

    @GetMapping("/gconsumptionname/{gconsumptionname}")
    public Result getGconsumptionInfo(@PathVariable String gconsumptionname) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("gconsumptionname", gconsumptionname);
        return Result.success(gconsumptionMapper.selectOne(queryWrapper));
    }
    @GetMapping("/page")
    public IPage<Gconsumption> searchGconsumptionByPage(@RequestParam Integer pageNum, @RequestParam Integer pageSize, @RequestParam(defaultValue = "") Integer department_id , @RequestParam(defaultValue = "") String system_G) {
        IPage<Gconsumption> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Gconsumption> queryWrapper = new QueryWrapper<>();
        if(!("".equals(department_id) || department_id == null)) {
            queryWrapper.eq("department_id", department_id);
        }
        if(!"".equals(system_G)) {
            queryWrapper.like("system_G", system_G);
        }
        queryWrapper.orderByDesc("id");
        return gconsumptionMapper.selectPage(page, queryWrapper);
    }

    /**
     * 导出接口
     */
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception {
        // 从数据库查询出所有的数据
        List<Gconsumption> list = gconsumptionService.list();
        // 通过工具类创建writer 写出到磁盘路径
//        ExcelWriter writer = ExcelUtil.getWriter(filesUploadPath + "/用水数据报表.xlsx");
        // 在内存操作，写出到浏览器
        ExcelWriter writer = ExcelUtil.getWriter(true);
        //自定义标题别名
        writer.addHeaderAlias("department_id", "用水部门ID");
        writer.addHeaderAlias("system_G", "用水系统");
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
//        List<Gconsumption> list = reader.readAll(Gconsumption.class);

        // 方式2：忽略表头的中文，直接读取表的内容
        List<List<Object>> list = reader.read(1);
        List<Gconsumption> gconsumptions = CollUtil.newArrayList();
        BigDecimal bigDecimal_old;
        BigDecimal bigDecimal_e;
        for (List<Object> row : list) {
            Gconsumption gconsumption = new Gconsumption();
            System.out.println(row.get(1));
            if (!StringUtils.isAnyBlank(row.get(1).toString())) {
                gconsumption.setDepartment_id((Integer.parseInt(row.get(1).toString())));
            }
            gconsumption.setSystem_G(row.get(2).toString());
            if (!StringUtils.isAnyBlank(row.get(3).toString())){
                gconsumption.setCalculation(Double.parseDouble(row.get(3).toString()));
            }
            // 获取每次用水的部门，累加这个部门的用水数据
            Department department = departmentMapper.selectById(gconsumption.getDepartment_id());
            bigDecimal_old = new BigDecimal(department.getSumG().toString());
            bigDecimal_e = new BigDecimal(gconsumption.getCalculation().toString());
            Double result_import = bigDecimal_old.add(bigDecimal_e).doubleValue();
            department.setSumG(result_import);
            departmentMapper.updateById(department);
            gconsumptions.add(gconsumption);

        }

        gconsumptionService.saveBatch(gconsumptions);
        return true;
    }
}
