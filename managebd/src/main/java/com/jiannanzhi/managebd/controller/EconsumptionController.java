package com.jiannanzhi.managebd.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiannanzhi.managebd.Entity.Department;
import com.jiannanzhi.managebd.Entity.Econsumption;
import com.jiannanzhi.managebd.Entity.dto.Data;
import com.jiannanzhi.managebd.common.Result;
import com.jiannanzhi.managebd.mapper.DepartmentMapper;
import com.jiannanzhi.managebd.mapper.DeviceMapper;
import com.jiannanzhi.managebd.mapper.EconsumptionMapper;
import com.jiannanzhi.managebd.service.EconsumptionService;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/econsumption")
public class EconsumptionController {

    @Resource
    private EconsumptionMapper econsumptionMapper;

    @Resource
    private EconsumptionService econsumptionService;

    @Resource
    private DepartmentMapper departmentMapper;

    @GetMapping("/trend")
    public Result getTrend(@RequestParam Integer department_id, @RequestParam String start_date, @RequestParam String end_date) {
        return econsumptionService.getTrend(department_id, start_date, end_date);
    }


    @GetMapping("/systemPie")
    public Result getESystemPie(@RequestParam Integer department_id, @RequestParam String start_date, @RequestParam String end_date) {

        return econsumptionService.getESystemPie(department_id, start_date, end_date);
    }

    @GetMapping("/Data/{id}")
    public Result getData(@PathVariable Integer id) {
        //起止时间
        LocalDateTime localDateTime_day = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime localDateTime_month = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime localDateTime_year = LocalDateTime.now().withMonth(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("department_id", id);
        List<Econsumption> list = econsumptionMapper.selectList(queryWrapper);
        BigDecimal bigDecimal_day = new BigDecimal(0.0);
        BigDecimal bigDecimal_month = new BigDecimal(0.0);
        BigDecimal bigDecimal_year = new BigDecimal(0.0);
        for (Econsumption econsumption : list) {
//            double calculation = econsumption.getCalculation();
            BigDecimal bigDecimal = new BigDecimal(econsumption.getCalculation().toString());
            LocalDateTime localDateTime = econsumption.getDateTime().toLocalDateTime();
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
    public List<Econsumption> searchAll() {
        return econsumptionMapper.selectList(null);
    }

    @PostMapping("/addEconsumption")
    public Result addEconsumption(@RequestBody Econsumption econsumption){
        Long tmp_id = econsumption.getId();
        //获取本次用电数据
        Double econsump = econsumption.getCalculation();
        //创建BigDecimal对象
        BigDecimal bigDecimal_e = new BigDecimal(econsump.toString());
        //获取用电部门id
        Integer departmentId = econsumption.getDepartmentId();
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", departmentId);
        Department department = departmentMapper.selectOne(queryWrapper);
        Double tmp_sumE = department.getSumE() == null ? 0 : department.getSumE();
        BigDecimal tmp_bigDecimal = new BigDecimal(tmp_sumE.toString());
        if (tmp_id == null || tmp_id <= 0) {

            Double result_add = bigDecimal_e.add(tmp_bigDecimal).doubleValue();
            department.setSumE(result_add);
            departmentMapper.updateById(department);
            return Result.success(econsumptionMapper.insert(econsumption));
        } else {
            //获取到当前数据库中的对应的用电数据信息
            QueryWrapper oldQueryWrapper = new QueryWrapper();
            oldQueryWrapper.eq("id", tmp_id);
            Econsumption oldEconsumption = econsumptionMapper.selectOne(oldQueryWrapper);
            BigDecimal bigDecimal_old = new BigDecimal(oldEconsumption.getCalculation().toString());
            Double result_update = tmp_bigDecimal.subtract(bigDecimal_old).add(bigDecimal_e).doubleValue();
            department.setSumE(result_update);
            departmentMapper.updateById(department);
            return Result.success(econsumptionMapper.updateById(econsumption));
        }
    }

    @PostMapping("/update")
    public int updateEconsumption(@RequestBody Econsumption econsumption) {
        return econsumptionMapper.updateById(econsumption);
    }

    @PostMapping("/delete")
    public int deleteEconsumption(@RequestBody Econsumption econsumption) {
        return econsumptionMapper.deleteById(econsumption);
    }

    @PostMapping("/delete/batch")
    public int deleteEconsumptionBatch(@RequestBody List<Integer> ids) {
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", ids);
        List<Econsumption> econsumption_list = econsumptionMapper.selectList(queryWrapper);
        for (Econsumption econsumption : econsumption_list) {
            Integer tmp_departmentId = econsumption.getDepartmentId();
            Department tmp_department = departmentMapper.selectById(tmp_departmentId);
            BigDecimal bigDecimal_old = new BigDecimal(tmp_department.getSumE().toString());
            BigDecimal tmp_bigDecimal = new BigDecimal(econsumption.getCalculation().toString());
            tmp_department.setSumE(bigDecimal_old.subtract(tmp_bigDecimal).doubleValue());
            departmentMapper.updateById(tmp_department);
        }
        return econsumptionMapper.deleteBatchIds(ids);
    }

    @DeleteMapping("/{id}")
    public int deleteEconsumptionById(@PathVariable Integer id) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("id", id);
        Econsumption get_econsumption = econsumptionMapper.selectOne(queryWrapper);
        Integer department_id = get_econsumption.getDepartmentId();
        Department department = departmentMapper.selectById(department_id);
        BigDecimal bigDecimal_old = new BigDecimal(department.getSumE().toString());
        BigDecimal bigDecimal_e = new BigDecimal(get_econsumption.getCalculation().toString());
        Double result_delete = bigDecimal_old.subtract(bigDecimal_e).doubleValue();
        department.setSumE(result_delete);
        departmentMapper.updateById(department);
        return econsumptionMapper.deleteById(id);
    }

    @GetMapping("/econsumptionname/{econsumptionname}")
    public Result getEconsumptionInfo(@PathVariable String econsumptionname) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("econsumptionname", econsumptionname);
        return Result.success(econsumptionMapper.selectOne(queryWrapper));
    }
    @GetMapping("/page")
    public IPage<Econsumption> searchEconsumptionByPage(@RequestParam Integer pageNum, @RequestParam Integer pageSize, @RequestParam(defaultValue = "") Integer departmentId , @RequestParam(defaultValue = "") String systemE) {
        IPage<Econsumption> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Econsumption> queryWrapper = new QueryWrapper<>();
        if(!("".equals(departmentId) || departmentId == null)) {
            queryWrapper.eq("department_id", departmentId);
        }
        if(!"".equals(systemE)) {
            queryWrapper.like("system_E", systemE);
        }
        queryWrapper.orderByDesc("id");
        return econsumptionMapper.selectPage(page, queryWrapper);
    }

    /**
     * 导出接口
     */
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception {
        // 从数据库查询出所有的数据
        List<Econsumption> list = econsumptionService.list();
        // 通过工具类创建writer 写出到磁盘路径
//        ExcelWriter writer = ExcelUtil.getWriter(filesUploadPath + "/用电数据报表.xlsx");
        // 在内存操作，写出到浏览器
        ExcelWriter writer = ExcelUtil.getWriter(true);
        //自定义标题别名
        writer.addHeaderAlias("departmentId", "用电部门ID");
        writer.addHeaderAlias("systemE", "用电系统");
        writer.addHeaderAlias("partE", "用电结构");
        writer.addHeaderAlias("calculation", "计量（单位kWh）");
        writer.addHeaderAlias("dateTime", "统计日期");

        // 一次性写出list内的对象到excel，使用默认样式，强制输出标题
        writer.write(list, true);

        // 设置浏览器响应的格式
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("用电数据", "UTF-8");
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
//        List<Econsumption> list = reader.readAll(Econsumption.class);

        // 方式2：忽略表头的中文，直接读取表的内容
        List<List<Object>> list = reader.read(1);
        List<Econsumption> econsumptions = CollUtil.newArrayList();
        BigDecimal bigDecimal_old;
        BigDecimal bigDecimal_e;
        for (List<Object> row : list) {
            Econsumption econsumption = new Econsumption();
            System.out.println(row.get(1));
            if (!StringUtils.isAnyBlank(row.get(1).toString())) {
                econsumption.setDepartmentId(Integer.parseInt(row.get(1).toString()));
            }
            econsumption.setSystemE(row.get(2).toString());
            econsumption.setPartE(row.get(3).toString());
            if (!StringUtils.isAnyBlank(row.get(4).toString())){
                econsumption.setCalculation(Double.parseDouble(row.get(4).toString()));
            }
            // 获取每次用电的部门，累加这个部门的用电数据
            Department department = departmentMapper.selectById(econsumption.getDepartmentId());
            bigDecimal_old = new BigDecimal(department.getSumE().toString());
            bigDecimal_e = new BigDecimal(econsumption.getCalculation().toString());
            Double result_import = bigDecimal_old.add(bigDecimal_e).doubleValue();
            department.setSumE(result_import);
            departmentMapper.updateById(department);
            econsumptions.add(econsumption);

        }

        econsumptionService.saveBatch(econsumptions);
        return true;
    }
}
