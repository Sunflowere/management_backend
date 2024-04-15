package com.jiannanzhi.managebd.controller;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.jiannanzhi.managebd.Entity.Gconsumption;
import com.jiannanzhi.managebd.Entity.dto.SourceDTO;
import com.jiannanzhi.managebd.common.Result;
import com.jiannanzhi.managebd.mapper.DepartmentMapper;
import com.jiannanzhi.managebd.mapper.GconsumptionMapper;
import com.jiannanzhi.managebd.service.UserService;
import com.jiannanzhi.managebd.service.GconsumptionService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/gEcharts")
public class GEchartsController {

    @Resource
    private DepartmentMapper departmentMapper;

    @Resource
    private GconsumptionService gconsumptionService;

    @Resource
    private GconsumptionMapper gconsumptionMapper;


    @GetMapping("/source/{id}")
    public Result getSource(@PathVariable Integer id) {
        //获取此部门的用气总数据集
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("department_id", id);
        List<Gconsumption> Glist_all = gconsumptionMapper.selectList(queryWrapper);

        //获取近三年年份
        String[] data_year = new String[3];
        LocalDate currentDate = LocalDate.now();
        for (int i = 1; i <= 3; i++) {
            LocalDate year = currentDate.minusYears(i);
            String yearStr = year.format(DateTimeFormatter.ofPattern("yyyy"));
            System.out.println(yearStr);
            data_year[i - 1] = yearStr;
        }

        //设置年份
        //2021年
        LocalDateTime time2021 = LocalDateTime.now().minusYears(3).withMonth(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        //2022年
        LocalDateTime time2022 = LocalDateTime.now().minusYears(2).withMonth(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        //2023年
        LocalDateTime time2023 = LocalDateTime.now().minusYears(1).withMonth(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        //2024年
        LocalDateTime time2024 = LocalDateTime.now().withMonth(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);

        //获取对应年份数据
        double[] data_01 = new double[12];
        double[] data_02 = new double[12];
        double[] data_03 = new double[12];
        for (Gconsumption g : Glist_all) {
            LocalDateTime g_localDateTime = g.getDate_time().toLocalDateTime();
            Date wDateTime = g.getDate_time();
            BigDecimal bigDecimal = new BigDecimal(g.getCalculation().toString());
            int month = DateUtil.month(wDateTime);
            if (g_localDateTime.isAfter(time2021) && g_localDateTime.isBefore(time2022)){
                BigDecimal bigDecimal1 = new BigDecimal(data_01[month]);
                bigDecimal1 = bigDecimal1.add(bigDecimal);
                data_01[month] = bigDecimal1.doubleValue();
            }
            if (g_localDateTime.isAfter(time2022) && g_localDateTime.isBefore(time2023)) {
                BigDecimal bigDecimal2 = new BigDecimal(data_02[month]);
                bigDecimal2 = bigDecimal2.add(bigDecimal);
                data_02[month] = bigDecimal2.doubleValue();
            }
            if (g_localDateTime.isAfter(time2023) && g_localDateTime.isBefore(time2024)) {
                BigDecimal bigDecimal3 = new BigDecimal(data_03[month]);
                bigDecimal3 = bigDecimal3.add(bigDecimal);
                data_03[month] += bigDecimal3.doubleValue();
            }
        }

        SourceDTO sourceDTO = new SourceDTO();
        sourceDTO.setDataYear(data_year);
        List<double[]> list = new ArrayList<>();
        list.add(data_01);
        list.add(data_02);
        list.add(data_03);
        sourceDTO.setList(list);
        System.out.println(time2021);
        return Result.success(sourceDTO);
    }

    @GetMapping("/yearDepartmentIfo/{id}")
    public Result getYearDepartmentIfo(@PathVariable Integer id) {
        //获取此部门的用气总数据集
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("department_id", id);
        List<Gconsumption> Glist_all = gconsumptionMapper.selectList(queryWrapper);
        //各时间段内的总用气数据总计
        double[] data_all = new double[12];
        //获取此部门各用气系统用气数据集
        double[] data_01 = new double[12];
        double[] data_02 = new double[12];
        double[] data_03 = new double[12];
        double[] data_04 = new double[12];

        int i = 0;
        for (Gconsumption gconsumption : Glist_all) {
            Date dateTime = gconsumption.getDate_time();
            String systemG = gconsumption.getSystem_G();
            BigDecimal bigDecimal = new BigDecimal(gconsumption.getCalculation());
            int month = DateUtil.month(dateTime);
            i = month;
            BigDecimal bigDecimal1 = new BigDecimal(data_all[month]);
            data_all[month] = bigDecimal1.add(bigDecimal).doubleValue();
            switch (systemG) {
                case "城市燃气" :
                    BigDecimal bigDecimal01 = new BigDecimal(data_01[i]);
                    data_01[i] = bigDecimal01.add(bigDecimal).doubleValue();
                    break;
                case "工业燃料" :
                    BigDecimal bigDecimal02 = new BigDecimal(data_02[i]);
                    data_02[i] = bigDecimal02.add(bigDecimal).doubleValue();
                    break;
                case "化工" :
                    BigDecimal bigDecimal03 = new BigDecimal(data_03[i]);
                    data_03[i] = bigDecimal03.add(bigDecimal).doubleValue();
                    break;
                case "发电" :
                    BigDecimal bigDecimal04 = new BigDecimal(data_04[i]);
                    data_04[i] = bigDecimal04.add(bigDecimal).doubleValue();
                    break;
                default : break;
            }
        }

        List<double[]> list = new ArrayList<>();
        list.add(data_all);
        list.add(data_01);
        list.add(data_02);
        list.add(data_03);
        list.add(data_04);
        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        System.out.println(list);


        return Result.success(list);
    }

    @GetMapping("/departmentIfo/{id}")
    public Result getDepartmentIfo(@PathVariable Integer id) throws JsonProcessingException {
        //获取此部门的用气总数据集
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("department_id", id);
        List<Gconsumption> Glist_all = gconsumptionMapper.selectList(queryWrapper);
        //各时间段内的总用气数据总计
        double[] data_all = new double[12];

        //获取此部门各用气系统用气数据集
        double[] data_01 = new double[12];
        double[] data_02 = new double[12];
        double[] data_03 = new double[12];
        double[] data_04 = new double[12];

        //再筛选从昨日13点到今天13点的所有数据，先创建这些时间对象
        LocalDateTime time13 = LocalDateTime.now().minusDays(1).withHour(13).withMinute(0).withSecond(0);
        LocalDateTime time15 = LocalDateTime.now().minusDays(1).withHour(15).withMinute(0).withSecond(0);
        LocalDateTime time17 = LocalDateTime.now().minusDays(1).withHour(17).withMinute(0).withSecond(0);
        LocalDateTime time19 = LocalDateTime.now().minusDays(1).withHour(19).withMinute(0).withSecond(0);
        LocalDateTime time21 = LocalDateTime.now().minusDays(1).withHour(21).withMinute(0).withSecond(0);
        LocalDateTime time23 = LocalDateTime.now().minusDays(1).withHour(23).withMinute(0).withSecond(0);
        LocalDateTime time01 = LocalDateTime.now().withHour(1).withMinute(0).withSecond(0);
        LocalDateTime time03 = LocalDateTime.now().withHour(3).withMinute(0).withSecond(0);
        LocalDateTime time05 = LocalDateTime.now().withHour(5).withMinute(0).withSecond(0);
        LocalDateTime time07 = LocalDateTime.now().withHour(7).withMinute(0).withSecond(0);
        LocalDateTime time09 = LocalDateTime.now().withHour(9).withMinute(0).withSecond(0);
        LocalDateTime time011 = LocalDateTime.now().withHour(11).withMinute(0).withSecond(0);
        LocalDateTime time013 = LocalDateTime.now().withHour(13).withMinute(0).withSecond(0);

        int i;
        for (Gconsumption g : Glist_all) {
            LocalDateTime g_localDateTime = g.getDate_time().toLocalDateTime();
            String systemG = g.getSystem_G();
            double calculation = g.getCalculation();
            if (g_localDateTime.isAfter(time13) && g_localDateTime.isBefore(time15)) {
                i = 0;
                data_all[i] += calculation;
                switch (systemG) {
                    case "城市燃气" :
                        data_01[i] += calculation;
                        break;
                    case "工业燃料" :
                        data_02[i] += calculation;
                        break;
                    case "化工" :
                        data_03[i] += calculation;
                        break;
                    case "发电" :
                        data_04[i] += calculation;
                        break;
                }
            } else if (g_localDateTime.isAfter(time15) && g_localDateTime.isBefore(time17)) {
                i = 1;
                data_all[i] += calculation;
                switch (systemG) {
                    case "城市燃气" :
                        data_01[i] += calculation;
                        break;
                    case "工业燃料" :
                        data_02[i] += calculation;
                        break;
                    case "化工" :
                        data_03[i] += calculation;
                        break;
                    case "发电" :
                        data_04[i] += calculation;
                        break;
                }
            } else if (g_localDateTime.isAfter(time17) && g_localDateTime.isBefore(time19)) {
                i = 2;
                data_all[i] += calculation;
                switch (systemG) {
                    case "城市燃气" :
                        data_01[i] += calculation;
                        break;
                    case "工业燃料" :
                        data_02[i] += calculation;
                        break;
                    case "化工" :
                        data_03[i] += calculation;
                        break;
                    case "发电" :
                        data_04[i] += calculation;
                        break;
                }
            } else if (g_localDateTime.isAfter(time19) && g_localDateTime.isBefore(time21)) {
                i = 3;
                data_all[i] += calculation;
                switch (systemG) {
                    case "城市燃气" :
                        data_01[i] += calculation;
                        break;
                    case "工业燃料" :
                        data_02[i] += calculation;
                        break;
                    case "化工" :
                        data_03[i] += calculation;
                        break;
                    case "发电" :
                        data_04[i] += calculation;
                        break;
                }
            } else if (g_localDateTime.isAfter(time21) && g_localDateTime.isBefore(time23)) {
                i = 4;
                data_all[i] += calculation;
                switch (systemG) {
                    case "城市燃气" :
                        data_01[i] += calculation;
                        break;
                    case "工业燃料" :
                        data_02[i] += calculation;
                        break;
                    case "化工" :
                        data_03[i] += calculation;
                        break;
                    case "发电" :
                        data_04[i] += calculation;
                        break;
                }
            } else if (g_localDateTime.isAfter(time23) && g_localDateTime.isBefore(time01)) {
                i = 5;
                data_all[i] += calculation;
                switch (systemG) {
                    case "城市燃气" :
                        data_01[i] += calculation;
                        break;
                    case "工业燃料" :
                        data_02[i] += calculation;
                        break;
                    case "化工" :
                        data_03[i] += calculation;
                        break;
                    case "发电" :
                        data_04[i] += calculation;
                        break;

                }
            } else if (g_localDateTime.isAfter(time01) && g_localDateTime.isBefore(time03)) {
                i = 6;
                data_all[i] += calculation;
                switch (systemG) {
                    case "城市燃气" :
                        data_01[i] += calculation;
                        break;
                    case "工业燃料" :
                        data_02[i] += calculation;
                        break;
                    case "化工" :
                        data_03[i] += calculation;
                        break;
                    case "发电" :
                        data_04[i] += calculation;
                        break;
                }
            } else if (g_localDateTime.isAfter(time03) && g_localDateTime.isBefore(time05)) {
                i = 7;
                data_all[i] += calculation;
                switch (systemG) {
                    case "城市燃气" :
                        data_01[i] += calculation;
                        break;
                    case "工业燃料" :
                        data_02[i] += calculation;
                        break;
                    case "化工" :
                        data_03[i] += calculation;
                        break;
                    case "发电" :
                        data_04[i] += calculation;
                        break;
                }
            } else if (g_localDateTime.isAfter(time05) && g_localDateTime.isBefore(time07)) {
                i = 8;
                data_all[i] += calculation;
                switch (systemG) {
                    case "城市燃气" :
                        data_01[i] += calculation;
                        break;
                    case "工业燃料" :
                        data_02[i] += calculation;
                        break;
                    case "化工" :
                        data_03[i] += calculation;
                        break;
                    case "发电" :
                        data_04[i] += calculation;
                        break;
                }
            } else if (g_localDateTime.isAfter(time07) && g_localDateTime.isBefore(time09)) {
                i = 9;
                data_all[i] += calculation;
                switch (systemG) {
                    case "城市燃气" :
                        data_01[i] += calculation;
                        break;
                    case "工业燃料" :
                        data_02[i] += calculation;
                        break;
                    case "化工" :
                        data_03[i] += calculation;
                        break;
                    case "发电" :
                        data_04[i] += calculation;
                        break;
                }
            } else if (g_localDateTime.isAfter(time09) && g_localDateTime.isBefore(time011)) {
                i = 10;
                data_all[i] += calculation;
                switch (systemG) {
                    case "城市燃气" :
                        data_01[i] += calculation;
                        break;
                    case "工业燃料" :
                        data_02[i] += calculation;
                        break;
                    case "化工" :
                        data_03[i] += calculation;
                        break;
                    case "发电" :
                        data_04[i] += calculation;
                        break;
                }
            } else if (g_localDateTime.isAfter(time011) && g_localDateTime.isBefore(time013)) {
                i = 11;
                data_all[i] += calculation;
                switch (systemG) {
                    case "城市燃气" :
                        data_01[i] += calculation;
                        break;
                    case "工业燃料" :
                        data_02[i] += calculation;
                        break;
                    case "化工" :
                        data_03[i] += calculation;
                        break;
                    case "发电" :
                        data_04[i] += calculation;
                        break;
                }
            }
        }
        List<double[]> list = new ArrayList<>();
        list.add(data_all);
        list.add(data_01);
        list.add(data_02);
        list.add(data_03);
        list.add(data_04);
        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        System.out.println(list);
        return Result.success(list);
    }
}
