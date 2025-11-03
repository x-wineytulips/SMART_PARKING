class ParkingRealtime {
    constructor() {
        this.socket = new WebSocket('ws://localhost:8080/parking/ws');
        this.initializeWebSocket();
        this.setupEventListeners();
    }

    initializeWebSocket() {
        this.socket.onmessage = (event) => {
            const data = JSON.parse(event.data);
            switch(data.type) {
                case 'occupancy_update':
                    this.updateOccupancyDisplay(data.data);
                    break;
                case 'spot_status':
                    this.updateSpotStatus(data.spotId, data.status);
                    break;
                case 'alert':
                    this.showAlert(data.message);
                    break;
            }
        };
    }

    updateOccupancyDisplay(data) {
        const occupancyContainer = document.getElementById('occupancy-display');
        data.forEach(location => {
            const locationElement = document.querySelector(`[data-location-id="${location.id}"]`);
            if (locationElement) {
                const percentage = (location.occupied / location.total) * 100;
                locationElement.querySelector('.occupancy-bar').style.width = `${percentage}%`;
                locationElement.querySelector('.occupancy-text').textContent = 
                    `${location.occupied}/${location.total} spots occupied`;
            }
        });
    }

    showAlert(message) {
        const alert = document.createElement('div');
        alert.className = 'parking-alert';
        alert.textContent = message;
        document.body.appendChild(alert);
        setTimeout(() => alert.remove(), 5000);
    }
} 