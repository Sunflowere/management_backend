package com.jiannanzhi.managebd.Entity.dto;

import lombok.Data;

import java.util.List;

@Data
public class SourceDTO {
    private String[] dataYear;
    private List<double[]> list;

}
