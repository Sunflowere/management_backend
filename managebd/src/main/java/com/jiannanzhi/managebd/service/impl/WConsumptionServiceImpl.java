package com.jiannanzhi.managebd.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiannanzhi.managebd.Entity.Wconsumption;
import com.jiannanzhi.managebd.Entity.Wconsumption;
import com.jiannanzhi.managebd.Entity.dto.PieData;
import com.jiannanzhi.managebd.common.Result;
import com.jiannanzhi.managebd.service.WconsumptionService;
import com.jiannanzhi.managebd.mapper.WconsumptionMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
* @author 18447
* @description 针对表【com_w_consumption(用水数据报表)】的数据库操作Service实现
* @createDate 2024-03-23 10:40:55
*/
@Service
public class WConsumptionServiceImpl extends ServiceImpl<WconsumptionMapper, Wconsumption>
    implements WconsumptionService {

    @Override
    public Result getWSystemPie(Integer department_id, String start_date, String end_date) {
        Date start = DateUtil.parse(start_date);
        Date end = DateUtil.parse(end_date);
        System.out.println(department_id);
        System.out.println(start);
        System.out.println(end);
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("department_id", department_id);
        queryWrapper.ge("date_time", start);
        queryWrapper.lt("date_time", end);
        List<Wconsumption> list_wconsumption = list(queryWrapper);
        List<PieData> list_data = new ArrayList<>();
        HashMap<String, Double> system_map = new HashMap<>();
        for (Wconsumption wconsumption : list_wconsumption) {
            if (!system_map.containsKey(wconsumption.getSystem_W())) {
                system_map.put(wconsumption.getSystem_W(), wconsumption.getCalculation());
            } else {
                BigDecimal bigDecimal = new BigDecimal(wconsumption.getCalculation());
                BigDecimal bigDecimal_old = new BigDecimal(system_map.get(wconsumption.getSystem_W()));
                double value = bigDecimal_old.add(bigDecimal).doubleValue();
                system_map.put(wconsumption.getSystem_W(), value);
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
}




