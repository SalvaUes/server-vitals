package com.svit.server_vitals.dto;

public class SystemResourceDto {
    private String serverName;
    private int cpuUsage;
    private int cpuCores;
    private String cpuTemperature;
    private int memoryUsage;
    private long memoryTotal;
    private long memoryUsed;
    private int diskUsage;
    private long diskTotal;
    private long diskFree;

    // Getters y Setters
    public String getServerName() {
        return serverName;
    }
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
    public int getCpuUsage() {
        return cpuUsage;
    }
    public void setCpuUsage(int cpuUsage) {
        this.cpuUsage = cpuUsage;
    }
    public int getCpuCores() {
        return cpuCores;
    }
    public void setCpuCores(int cpuCores) {
        this.cpuCores = cpuCores;
    }
    public String getCpuTemperature() {
        return cpuTemperature;
    }
    public void setCpuTemperature(String cpuTemperature) {
        this.cpuTemperature = cpuTemperature;
    }
    public int getMemoryUsage() {
        return memoryUsage;
    }
    public void setMemoryUsage(int memoryUsage) {
        this.memoryUsage = memoryUsage;
    }
    public long getMemoryTotal() {
        return memoryTotal;
    }
    public void setMemoryTotal(long memoryTotal) {
        this.memoryTotal = memoryTotal;
    }
    public long getMemoryUsed() {
        return memoryUsed;
    }
    public void setMemoryUsed(long memoryUsed) {
        this.memoryUsed = memoryUsed;
    }
    public int getDiskUsage() {
        return diskUsage;
    }
    public void setDiskUsage(int diskUsage) {
        this.diskUsage = diskUsage;
    }
    public long getDiskTotal() {
        return diskTotal;
    }
    public void setDiskTotal(long diskTotal) {
        this.diskTotal = diskTotal;
    }
    public long getDiskFree() {
        return diskFree;
    }
    public void setDiskFree(long diskFree) {
        this.diskFree = diskFree;
    }
}
