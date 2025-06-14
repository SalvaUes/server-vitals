package com.svit.server_vitals.service;

import com.svit.server_vitals.dto.SystemResourceDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class SystemMonitorService {

    
    private static final Logger log = LoggerFactory.getLogger(SystemMonitorService.class);

    private final SystemInfo si = new SystemInfo();
    private final HardwareAbstractionLayer hal = si.getHardware();
    private final OperatingSystem os = si.getOperatingSystem();
    private final CentralProcessor processor = hal.getProcessor();
    private final GlobalMemory memory = hal.getMemory();
    private final Sensors sensors = hal.getSensors();

    
    private long[] prevCpuTicks;



    private final AtomicReference<SystemResourceDto> latestMetrics = new AtomicReference<>();

    
    
        
    @PostConstruct
    private void initialize() {
        log.info("Initializing SystemMonitorService...");
        


          
        this.prevCpuTicks = processor.getSystemCpuLoadTicks();

        
        try {
            Thread.sleep(500); 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Initialization sleep interrupted.");
        }
        updateMetrics(); 
        log.info("SystemMonitorService initialized.");
    }

  
  

    @Scheduled(fixedDelay = 1500)
    public void updateMetrics() {
        log.debug("Updating system metrics..."); 


        
        long[] currentCpuTicks = processor.getSystemCpuLoadTicks();
        double cpuUsage = 0.0; 
        if (this.prevCpuTicks != null) { 
             cpuUsage = processor.getSystemCpuLoadBetweenTicks(this.prevCpuTicks) * 100;
             
             
             if (Double.isNaN(cpuUsage)) {
                 log.warn("CPU usage calculation resulted in NaN. Falling back to default value 0.");
                 cpuUsage = 0;
             }
        } else {
             log.warn("prevCpuTicks is null, cannot calculate CPU load between ticks yet.");
             cpuUsage = 0;
        }
        this.prevCpuTicks = currentCpuTicks; 

        int cpuCores = processor.getLogicalProcessorCount();
        double cpuTemp = sensors.getCpuTemperature(); 



        
        long totalMemory = memory.getTotal();
        long availableMemory = memory.getAvailable();
        long usedMemory = totalMemory - availableMemory;
        int memoryUsage = (totalMemory > 0) ? (int) (((double) usedMemory / totalMemory) * 100) : 0; 




        
        FileSystem fileSystem = os.getFileSystem();
        List<OSFileStore> fsArray = fileSystem.getFileStores(true); 
        long totalDisk = 0;
        long usableDisk = 0; 
        for (OSFileStore fs : fsArray) {
            
                totalDisk += fs.getTotalSpace();
                usableDisk += fs.getUsableSpace();
             
        }
        long usedDisk = totalDisk - usableDisk;
        int diskUsage = (totalDisk > 0) ? (int) (((double) usedDisk / totalDisk) * 100) : 0; 




        
        SystemResourceDto dto = new SystemResourceDto();
        try {
            dto.setServerName(os.getNetworkParams().getHostName());
        } catch (Exception e) {
             log.warn("Could not retrieve hostname.", e);
             dto.setServerName("Unknown"); 
        }
        dto.setCpuUsage((int) Math.max(0, Math.min(100, Math.round(cpuUsage)))); 
        dto.setCpuCores(cpuCores);
        
        
        dto.setCpuTemperature(Double.isNaN(cpuTemp) ? "N/A" : String.format("%.1f°C", cpuTemp));
        dto.setMemoryUsage(memoryUsage);
        dto.setMemoryTotal(totalMemory);
        dto.setMemoryUsed(usedMemory);
        dto.setDiskUsage(diskUsage);
        dto.setDiskTotal(totalDisk);
        dto.setDiskFree(usableDisk); 
        

        
        
        this.latestMetrics.set(dto);

        log.debug("Metrics updated: CPU {}%", dto.getCpuUsage());
    }

    

    public SystemResourceDto getLatestMetrics() {
        
        SystemResourceDto metrics = this.latestMetrics.get();
        
        if (metrics == null) {
            log.warn("Latest metrics requested but not available yet. Returning default DTO.");
            return createDefaultDto(); 
        }
        return metrics;
    }

    

    private SystemResourceDto createDefaultDto() {

        SystemResourceDto defaultDto = new SystemResourceDto();
        defaultDto.setServerName("Initializing...");
        defaultDto.setCpuUsage(0);
        defaultDto.setCpuCores(0);
        defaultDto.setCpuTemperature("N/A");
        defaultDto.setMemoryUsage(0);
        
        return defaultDto;
    }

    
}