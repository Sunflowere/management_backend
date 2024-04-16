package com.jiannanzhi.managebd.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiannanzhi.managebd.Entity.Econsumption;
import com.jiannanzhi.managebd.Entity.Gconsumption;
import com.jiannanzhi.managebd.Entity.dto.PieData;
import com.jiannanzhi.managebd.Entity.dto.TrendData;
import com.jiannanzhi.managebd.common.Result;
import com.jiannanzhi.managebd.service.GconsumptionService;
import com.jiannanzhi.managebd.mapper.GconsumptionMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
* @author 18447
* @description 针对表【com_g_consumption(用气数据报表)】的数据库操作Service实现
* @createDate 2024-03-24 15:16:45
*/
@Service
public class GConsumptionServiceImpl extends ServiceImpl<GconsumptionMapper, Gconsumption>
    implements GconsumptionService {

    @Override
    public Result getGSystemPie(Integer department_id, String start_date, String end_date) {
        Date start = DateUtil.parse(start_date);
        Date end = DateUtil.parse(end_date);
        System.out.println(department_id);
        System.out.println(start);
        System.out.println(end);
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("department_id", department_id);
        queryWrapper.ge("date_time", start);
        queryWrapper.lt("date_time", end);
        List<Gconsumption> list_gconsumption = list(queryWrapper);
        List<PieData> list_data = new ArrayList<>();
        HashMap<String, Double> system_map = new HashMap<>();
        for (Gconsumption gconsumption : list_gconsumption) {
            if (!system_map.containsKey(gconsumption.getSystem_G())) {
                system_map.put(gconsumption.getSystem_G(), gconsumption.getCalculation());
            } else {
                BigDecimal bigDecimal = new BigDecimal(gconsumption.getCalculation());
                BigDecimal bigDecimal_old = new BigDecimal(system_map.get(gconsumption.getSystem_G()));
                double value = bigDecimal_old.add(bigDecimal).doubleValue();
                system_map.put(gconsumption.getSystem_G(), value);
            }
        }
        for (Map.Entry<String, Double> entry : system_map.entrySet()) {
            PieData data = new PieData();
            data.setName(entry.getKey());
            data.setValue(entry.getValue());
            list_data.add(data);
        }
        return Result.success(list_data);
    }

    @Override
    public Result getTrend(Integer department_id, String start_date, String end_date) {
        Date start = DateUtil.parse(start_date);
        Date end = DateUtil.parse(end_date);
        System.out.println(department_id);
        System.out.println(start);
        System.out.println(end);
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("department_id", department_id);
        queryWrapper.ge("date_time", start);
        queryWrapper.lt("date_time", end);
        queryWrapper.orderByAsc("date_time");
        List<Gconsumption> list_gconsumption = list(queryWrapper);

        HashMap<String, Double> days_map = new HashMap<>();
        List<String> xAxisArray = new ArrayList<>();
        List<Double> valueArray = new ArrayList<>();
        for (Gconsumption gconsumption : list_gconsumption) {
            // TimeStamp->Date
            Date timestampToDate = new Date(gconsumption.getDate_time().getTime());
            // 获取日期字符串
            String day_str = DateUtil.format(timestampToDate, "yyyy/MM/dd");
            System.out.println(day_str);
            if (!days_map.containsKey(day_str)) {
                days_map.put(day_str, gconsumption.getCalculation());
            } else {
                BigDecimal bigDecimal = new BigDecimal(gconsumption.getCalculation());
                BigDecimal bigDecimal_old = new BigDecimal(days_map.get(day_str));
                double value = bigDecimal_old.add(bigDecimal).doubleValue();
                days_map.put(day_str, value);
            }
        }

        // 使用TreeMap对日期进行排序
        TreeMap<String, Double> sorted_days_map = new TreeMap<>(days_map);

        // 将排序后的键值对存储到ArrayList中
        List<Map.Entry<String, Double>> sorted_list = new ArrayList<>(sorted_days_map.entrySet());

        // 输出结果
        for (Map.Entry<String, Double> entry : sorted_list) {
            System.out.println("日期： " + entry.getKey() + ", 数值： " + entry.getValue());
            xAxisArray.add(entry.getKey());
            valueArray.add(entry.getValue());
        }
        TrendData trendData = new TrendData();
        trendData.setXAxisArray(xAxisArray);
        trendData.setValueArray(valueArray);
        return Result.success(trendData);
    }
}




