package com.jiannanzhi.managebd.Entity.dto;

import lombok.Data;

@Data
public class DeviceData {
    private long normalEDevice;
    private long errorEDevice;
    private long disconnectedE;
    private long normalWDevice;
    private long errorWDevice;
    private long disconnectedW;
    private long normalGDevice;
    private long errorGDevice;
    private long disconnectedG;

}
