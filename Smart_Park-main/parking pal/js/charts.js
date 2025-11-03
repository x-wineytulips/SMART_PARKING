class ParkingCharts {
    constructor() {
        this.occupancyChart = null;
        this.initializeCharts();
    }

    initializeCharts() {
        const ctx = document.getElementById('occupancyChart').getContext('2d');
        this.occupancyChart = new Chart(ctx, {
            type: 'line',
            data: {
                labels: [],
                datasets: [{
                    label: 'Occupancy Rate',
                    data: [],
                    borderColor: '#c4a47c',
                    tension: 0.4
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    title: {
                        display: true,
                        text: 'Parking Occupancy Over Time'
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        max: 100,
                        ticks: {
                            callback: value => `${value}%`
                        }
                    }
                }
            }
        });
    }

    updateChart(data) {
        const timestamp = new Date().toLocaleTimeString();
        this.occupancyChart.data.labels.push(timestamp);
        this.occupancyChart.data.datasets[0].data.push(data.occupancyRate);
        
        if (this.occupancyChart.data.labels.length > 20) {
            this.occupancyChart.data.labels.shift();
            this.occupancyChart.data.datasets[0].data.shift();
        }
        
        this.occupancyChart.update();
    }
} 