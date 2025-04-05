document.addEventListener('DOMContentLoaded', function() {

    
    // Elementos del para el DOM
    const sidebar = document.getElementById('sidebar');
    const mainContent = document.getElementById('mainContent');
    const toggleBtn = document.getElementById('toggleSidebar');
    const btnHealth = document.getElementById('btnHealth');
    const healthSection = document.getElementById('healthChecksSection');
    const serverStatusElement = document.getElementById('serverStatus');

    const cpuCircle = document.getElementById('cpuCircle');
    const ramCircle = document.getElementById('ramCircle');
    const diskCircle = document.getElementById('diskCircle');

    const cpuValue = document.getElementById('cpuValue');
    const ramValue = document.getElementById('ramValue');
    const diskValue = document.getElementById('diskValue');

    const serverNameValue = document.getElementById('serverNameValue');
    const cpuCoresValue = document.getElementById('cpuCoresValue');
    const cpuTempValue = document.getElementById('cpuTempValue');
    const ramTotalValue = document.getElementById('ramTotalValue');
    const ramUsedValue = document.getElementById('ramUsedValue');
    const diskTotalValue = document.getElementById('diskTotalValue');
    const diskFreeValue = document.getElementById('diskFreeValue');

    // Config
    const CIRCLE_RADIUS = parseFloat(cpuCircle?.getAttribute('r') || 70); // Usa valor por defecto si falla
    const CIRCLE_CIRCUMFERENCE = 2 * Math.PI * CIRCLE_RADIUS;

    // funciones de utilidades

    function updateProgressRing(circle, value) {
        if (!circle) return; // Salir si el circulo no existe
        const percentage = Math.max(0, Math.min(100, value || 0));
        const offset = CIRCLE_CIRCUMFERENCE - (percentage / 100 * CIRCLE_CIRCUMFERENCE);
        circle.style.strokeDashoffset = offset;

        let baseColorVar = '--secondary-color'; // Default CPU
        if (circle.id === 'ramCircle') baseColorVar = '--primary-color';
        if (circle.id === 'diskCircle') baseColorVar = '--accent-color';

        let targetColorVar;
        if (percentage >= 90) {
            targetColorVar = '--danger-color';
        } else if (percentage >= 70) {
            targetColorVar = '--warning-color';
        } else {
            targetColorVar = baseColorVar;
        }
        circle.style.stroke = `var(${targetColorVar})`;
    }

    function formatBytes(bytes, decimals = 2) {
        if (typeof bytes !== 'number' || isNaN(bytes) || bytes <= 0) return '0 Bytes';
        const k = 1024;
        const dm = Math.max(0, decimals);
        const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];
        const i = Math.floor(Math.log(bytes) / Math.log(k));
        const index = Math.min(i, sizes.length - 1);
        return parseFloat((bytes / Math.pow(k, index)).toFixed(dm)) + ' ' + sizes[index];
    }

    function updateConnectionStatus(statusText, statusClass) {
        if (serverStatusElement) {
            serverStatusElement.textContent = statusText;
            serverStatusElement.className = 'server-status ' + statusClass; // Asignar clases
        }
    }

    // logica para el main

    function updateSystemInfo(data) {
        if (!data || typeof data !== 'object') {
            console.error('Invalid data:', data);
            return;
        }

        // CPU
        const cpuUsage = data.cpuUsage !== undefined ? data.cpuUsage : 0;
        if(cpuValue) cpuValue.textContent = cpuUsage + '%';
        updateProgressRing(cpuCircle, cpuUsage);
        if(cpuCoresValue) cpuCoresValue.textContent = data.cpuCores || '--';
        if(cpuTempValue) cpuTempValue.textContent = data.cpuTemperature || '--';

        // RAM
        const memoryUsage = data.memoryUsage !== undefined ? data.memoryUsage : 0;
        if(ramValue) ramValue.textContent = memoryUsage + '%';
        updateProgressRing(ramCircle, memoryUsage);
        if(ramTotalValue) ramTotalValue.textContent = formatBytes(data.memoryTotal);
        if(ramUsedValue) ramUsedValue.textContent = formatBytes(data.memoryUsed);

        // Disco
        const diskUsage = data.diskUsage !== undefined ? data.diskUsage : 0;
        if(diskValue) diskValue.textContent = diskUsage + '%';
        updateProgressRing(diskCircle, diskUsage);
        if(diskTotalValue) diskTotalValue.textContent = formatBytes(data.diskTotal);
        if(diskFreeValue) diskFreeValue.textContent = formatBytes(data.diskFree);

        // Server Info
        if(serverNameValue) serverNameValue.textContent = data.serverName || 'Unknown';
    }

    // para conectarse al servidor SSE

    function initializeSSE() {
        console.log('Init SSE');
        updateConnectionStatus('Connecting', 'status-connecting');

        const eventSource = new EventSource('/api/system/resources-stream');

        eventSource.onopen = function() {
            console.log('SSE open');
            updateConnectionStatus('Online', 'status-online');
        };

        eventSource.addEventListener('system-update', function(event) {
            try {
                const systemData = JSON.parse(event.data);
                updateSystemInfo(systemData);
            } catch (error) {
                console.error('SSE parse error:', error, event.data);
            }
        });

        eventSource.onerror = function(error) {
            console.error('SSE error:', error);
            updateConnectionStatus('Offline', 'status-offline');
        };
    }

    // interactua con el DOM

    if (toggleBtn && sidebar && mainContent) {
        toggleBtn.addEventListener('click', () => {
            const isCollapsed = sidebar.classList.toggle('collapsed');
            mainContent.classList.toggle('expanded', isCollapsed);
            document.body.classList.toggle('sidebar-collapsed', isCollapsed);
        });
    } else {
         console.warn("Sidebar elements not found");
    }


    if (btnHealth && healthSection) {
        btnHealth.addEventListener('click', () => {
            const isHidden = healthSection.style.display === 'none' || healthSection.style.display === '';
            healthSection.style.display = isHidden ? 'block' : 'none';
            btnHealth.innerHTML = `<i class="fas fa-database"></i> ${isHidden ? 'Hide' : 'Show'} Health Checks`; // English example
        });
    } else {
        console.warn("Health check elements not found");
    }


    // inicializa los csrculos
    [cpuCircle, ramCircle, diskCircle].forEach(circle => {
         if (circle) {
             circle.style.strokeDasharray = CIRCLE_CIRCUMFERENCE;
             circle.style.strokeDashoffset = CIRCLE_CIRCUMFERENCE;
             
             let baseColorVar = '--secondary-color';
             if (circle.id === 'ramCircle') baseColorVar = '--primary-color';
             if (circle.id === 'diskCircle') baseColorVar = '--accent-color';
             circle.style.stroke = `var(${baseColorVar})`;
         }
     });



    // para conectarse al servidor SSE
    initializeSSE();

}); 