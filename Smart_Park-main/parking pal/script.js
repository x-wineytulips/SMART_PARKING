class ParkingUI {
    constructor() {
        this.availableSpots = {};
        this.init();
        this.setupLocationSearch();
        this.initializeLocationFilters();
        this.setupServiceStatus();
    }

    init() {
        this.updateAvailableSpots();
        this.setupEventListeners();
    }

    updateAvailableSpots() {
        // Simulate getting data from backend
        fetch('/api/parking/available')
            .then(response => response.json())
            .then(data => {
                this.availableSpots = data;
                this.renderAvailableSpots();
            });
    }

    renderAvailableSpots() {
        const container = document.getElementById('available-spots');
        container.innerHTML = '';
        
        Object.entries(this.availableSpots).forEach(([type, count]) => {
            const div = document.createElement('div');
            div.className = 'spot-count';
            div.innerHTML = `${type}: ${count} spots available`;
            container.appendChild(div);
        });
    }

    setupEventListeners() {
        document.getElementById('parkingForm').addEventListener('submit', (e) => {
            e.preventDefault();
            this.handleParking();
        });

        document.getElementById('checkoutForm').addEventListener('submit', (e) => {
            e.preventDefault();
            this.handleCheckout();
        });
    }

    handleParking() {
        const licensePlate = document.getElementById('licensePlate').value;
        const vehicleType = document.getElementById('vehicleType').value;

        // Send parking request to backend
        fetch('/api/parking/park', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ licensePlate, vehicleType })
        })
        .then(response => response.json())
        .then(data => {
            alert(`Vehicle parked successfully! Ticket ID: ${data.ticketId}`);
            this.updateAvailableSpots();
        })
        .catch(error => {
            alert('Error parking vehicle: ' + error.message);
        });
    }

    handleCheckout() {
        const ticketId = document.getElementById('ticketId').value;

        fetch('/api/parking/checkout', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ ticketId })
        })
        .then(response => response.json())
        .then(data => {
            alert(`Parking fee: $${data.fee}`);
            this.updateAvailableSpots();
        })
        .catch(error => {
            alert('Error checking out: ' + error.message);
        });
    }

    setupLocationSearch() {
        const searchInput = document.getElementById('locationSearch');
        if (searchInput) {
            searchInput.addEventListener('input', (e) => {
                const searchTerm = e.target.value.toLowerCase();
                const locationItems = document.querySelectorAll('.location-item');
                
                locationItems.forEach(item => {
                    const locationName = item.querySelector('h4').textContent.toLowerCase();
                    const locationAddress = item.querySelector('p').textContent.toLowerCase();
                    
                    if (locationName.includes(searchTerm) || locationAddress.includes(searchTerm)) {
                        item.style.display = 'block';
                    } else {
                        item.style.display = 'none';
                    }
                });
            });
        }
    }

    initializeLocationFilters() {
        const categories = document.querySelectorAll('.category-title');
        categories.forEach(category => {
            category.addEventListener('click', () => {
                const locations = category.nextElementSibling;
                locations.style.display = 
                    locations.style.display === 'none' ? 'block' : 'none';
            });
        });
    }

    setupServiceStatus() {
        const status = document.createElement('div');
        status.className = 'service-status';
        status.innerHTML = '24/7 Service Active';
        document.body.appendChild(status);
    }

    updateSpotCounts() {
        // Simulate real-time updates
        setInterval(() => {
            document.querySelectorAll('.spot-details').forEach(detail => {
                const spots = detail.querySelector('span:first-child');
                const available = Math.floor(Math.random() * 100);
                spots.innerHTML = `<i class="fas fa-car"></i> ${available} spots available`;
            });
        }, 30000); // Update every 30 seconds
    }
}

class MobileMenu {
    constructor() {
        this.nav = document.querySelector('.nav-links');
        this.burger = document.createElement('div');
        this.burger.className = 'burger';
        this.burger.innerHTML = `
            <div class="line"></div>
            <div class="line"></div>
            <div class="line"></div>
        `;
        this.init();
    }

    init() {
        document.querySelector('.nav-content').appendChild(this.burger);
        this.burger.addEventListener('click', () => {
            this.nav.classList.toggle('nav-active');
            this.burger.classList.toggle('toggle');
        });
    }
}

// Initialize mobile menu
document.addEventListener('DOMContentLoaded', () => {
    new MobileMenu();
    new ParkingUI();
}); 