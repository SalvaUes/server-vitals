document.addEventListener('DOMContentLoaded', function () {
    const sidebar = document.getElementById('sidebar');
    const mainContent = document.getElementById('mainContent');
    const toggleBtn = document.getElementById('toggleSidebar');
     const btnHealth = document.querySelector('#btnHealth');
const healthSection = document.querySelector('#healthSection');
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

    const CIRCLE_RADIUS = parseFloat(cpuCircle?.getAttribute('r') || 70);
    const CIRCLE_CIRCUMFERENCE = 2 * Math.PI * CIRCLE_RADIUS;

    function updateProgressRing(circle, value) {
        if (!circle) return;
        const percentage = Math.max(0, Math.min(100, value || 0));
        const offset = CIRCLE_CIRCUMFERENCE - (percentage / 100 * CIRCLE_CIRCUMFERENCE);
        circle.style.strokeDashoffset = offset;

        let baseColorVar = '--secondary-color';
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
            serverStatusElement.className = 'server-status ' + statusClass;
        }
    }

    function updateSystemInfo(data) {
        if (!data || typeof data !== 'object') {
            console.error('Invalid data:', data);
            return;
        }

        const cpuUsage = data.cpuUsage !== undefined ? data.cpuUsage : 0;
        if (cpuValue) cpuValue.textContent = cpuUsage + '%';
        updateProgressRing(cpuCircle, cpuUsage);
        if (cpuCoresValue) cpuCoresValue.textContent = data.cpuCores || '--';
        if (cpuTempValue) cpuTempValue.textContent = data.cpuTemperature || '--';

        const memoryUsage = data.memoryUsage !== undefined ? data.memoryUsage : 0;
        if (ramValue) ramValue.textContent = memoryUsage + '%';
        updateProgressRing(ramCircle, memoryUsage);
        if (ramTotalValue) ramTotalValue.textContent = formatBytes(data.memoryTotal);
        if (ramUsedValue) ramUsedValue.textContent = formatBytes(data.memoryUsed);

        const diskUsage = data.diskUsage !== undefined ? data.diskUsage : 0;
        if (diskValue) diskValue.textContent = diskUsage + '%';
        updateProgressRing(diskCircle, diskUsage);
        if (diskTotalValue) diskTotalValue.textContent = formatBytes(data.diskTotal);
        if (diskFreeValue) diskFreeValue.textContent = formatBytes(data.diskFree);

        if (serverNameValue) serverNameValue.textContent = data.serverName || 'Unknown';
    }

    function initializeSSE() {
        console.log('Init SSE');
        updateConnectionStatus('Connecting', 'status-connecting');

        const eventSource = new EventSource('/api/system/resources-stream');

        eventSource.onopen = function () {
            console.log('SSE open');
            updateConnectionStatus('Online', 'status-online');
        };

        eventSource.addEventListener('system-update', function (event) {
            try {
                const systemData = JSON.parse(event.data);
                updateSystemInfo(systemData);
            } catch (error) {
                console.error('SSE parse error:', error, event.data);
            }
        });

        eventSource.onerror = function (error) {
            console.error('SSE error:', error);
            updateConnectionStatus('Offline', 'status-offline');
        };
    }

    if (toggleBtn && sidebar && mainContent) {
        toggleBtn.addEventListener('click', () => {
            const isCollapsed = sidebar.classList.toggle('collapsed');
            mainContent.classList.toggle('expanded', isCollapsed);
            document.body.classList.toggle('sidebar-collapsed', isCollapsed);
        });
    } else {
        console.warn("Sidebar elements not found");
    }

     // 1) Función que pide el estado y actualiza las tarjetas
  function updateHealth() {
    console.log('🔄 updateHealth() llamado a las', new Date().toLocaleTimeString());

    // Tomamos tiempo de inicio para medir HTTP
    const startFetch = performance.now();
    // Le añadimos un param _=timestamp para bustear cache
    const url = '/api/databases/status?_=' + Date.now();

    fetch(url, { cache: 'no-store' })
      .then(res => {
        if (!res.ok) {
          throw new Error(`HTTP status ${res.status}`);
        }
        return res.json();
      })
      .then(json => {
        const httpMs = (performance.now() - startFetch).toFixed(2);
        console.log('  → datos recibidos:', json, `HTTP ${httpMs} ms`);

        document.querySelectorAll('.db-card').forEach(card => {
          const name  = card.querySelector('h4').innerText.trim();
          const badge = card.querySelector('.status-badge');
          const info  = json[name];

          if (info) {
            // Tiempo BD en segundos
            const bdSecs = (info.responseTimeMs / 1000).toFixed(2) + ' s';
            // Escribimos dos spans (evitamos usar <div> dentro de <span>)
                    badge.innerHTML = `
                <span class="line1">
                ${info.active ? 'Operativo' : 'No disponible'}
                </span>
                <span class="line2">
                BD: ${bdSecs}
                </span>
                <span class="line3">
                HTTP: ${httpMs} ms
                </span>
            `;

            badge.classList.remove('status-healthy','status-warning','status-critical');
            if (info.active) {
              badge.classList.add(
                info.responseTimeMs > 1000 ? 'status-warning' : 'status-healthy'
              );
            } else {
              badge.classList.add('status-critical');
            }
          } else {
            badge.textContent = 'Desconocido';
            badge.className = 'status-badge status-critical';
          }
        });
      })
      .catch(err => {
        console.error('❌ Error en fetch de estados:', err);
        // Si falla, marcamos todos en crítico
        document.querySelectorAll('.db-card .status-badge')
          .forEach(b => {
            b.textContent = 'No disponible';
            b.className = 'status-badge status-critical';
          });
      });
  }

  // 2) Arranca YA y luego cada 30 s
  updateHealth();
  setInterval(updateHealth, 15000);


  // 3) Click del botón solo alterna visibilidad
  if (btnHealth && healthSection) {
    btnHealth.addEventListener('click', () => {
      const wasHidden = healthSection.style.display === 'none';
      healthSection.style.display = wasHidden ? 'block' : 'none';
      btnHealth.innerHTML = `
        <i class="fas fa-database"></i>
        ${wasHidden ? 'Ocultar' : 'Mostrar'} Health Checks
      `;
    });
  } else {
    console.warn('Health check elements not found');
  }
  

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

    initializeSSE();
});
