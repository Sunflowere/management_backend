package com.jiannanzhi.managebd.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiannanzhi.managebd.Entity.Econsumption;
import com.jiannanzhi.managebd.Entity.dto.PieData;
import com.jiannanzhi.managebd.common.Result;
import com.jiannanzhi.managebd.mapper.EconsumptionMapper;
import com.jiannanzhi.managebd.service.EconsumptionService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class EconsumptionServiceImpl extends ServiceImpl<EconsumptionMapper, Econsumption> implements EconsumptionService {
    @Override
    public Result getESystemPie(Integer department_id, String start_date, String end_date) {
        Date start = DateUtil.parse(start_date);
        Date end = DateUtil.parse(end_date);
        System.out.println(department_id);
        System.out.println(start);
        System.out.println(end);
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("department_id", department_id);
        queryWrapper.ge("date_time", start);
        queryWrapper.lt("date_time", end);
        List<Econsumption> list_econsumption = list(queryWrapper);
        List<PieData> list_data = new ArrayList<>();
        HashMap<String, Double> system_map = new HashMap<>();
        for (Econsumption econsumption : list_econsumption) {
            if (!system_map.containsKey(econsumption.getSystemE())) {
                system_map.put(econsumption.getSystemE(), econsumption.getCalculation());
            } else {
                BigDecimal bigDecimal = new BigDecimal(econsumption.getCalculation());
                BigDecimal bigDecimal_old = new BigDecimal(system_map.get(econsumption.getSystemE()));
                double value = bigDecimal_old.add(bigDecimal).doubleValue();
                system_map.put(econsumption.getSystemE(), value);
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
        List<Econsumption> list_econsumption = list(queryWrapper);

        HashMap<String, Double> days_map = new HashMap<>();
        for (Econsumption econsumption : list_econsumption) {
            // TimeStamp->Date
            Date timestampToDate = new Date(econsumption.getDateTime().getTime());
            // 获取日期字符串
            String day_str = DateUtil.format(timestampToDate, "yyyy/MM/dd");
            if (!days_map.containsKey(day_str)) {
                days_map.put(day_str, econsumption.getCalculation());
            } else {
                BigDecimal bigDecimal = new BigDecimal(econsumption.getCalculation());
                BigDecimal bigDecimal_old = new BigDecimal(days_map.get(day_str));
                double value = bigDecimal_old.add(bigDecimal).doubleValue();
                days_map.put(day_str, value);
            }
        }
        for (Map.Entry<String, Double> entry : days_map.entrySet()) {
            PieData data = new PieData();
            data.setName(entry.getKey());
            data.setValue(entry.getValue());
            list_data.add(data);
        }
        return Result.success(list_data);
    }
}
