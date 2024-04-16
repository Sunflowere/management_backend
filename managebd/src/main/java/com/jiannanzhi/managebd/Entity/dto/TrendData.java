package com.jiannanzhi.managebd.Entity.dto;

import lombok.Data;

import java.util.List;

@Data
public class TrendData {
    private List<String> xAxisArray;
    private List<Double> valueArray;
}
