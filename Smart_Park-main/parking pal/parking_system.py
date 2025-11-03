from dataclasses import dataclass
from datetime import datetime
from enum import Enum
from typing import Dict, List, Optional
import uuid

class VehicleType(Enum):
    MOTORCYCLE = 1
    CAR = 2
    SUV = 3
    TRUCK = 4

@dataclass
class Vehicle:
    license_plate: str
    vehicle_type: VehicleType
    owner_id: str
    entry_time: datetime

class SmartParkingSystem:
    def __init__(self):
        self.spots: Dict[int, bool] = {}  # spot_id -> occupied
        self.vehicle_map: Dict[str, int] = {}  # license -> spot_id
        self.waiting_list: List[Vehicle] = []
        self.spot_sensors: Dict[int, bool] = {}  # spot_id -> sensor_status
        self.revenue: float = 0.0

    def initialize_spots(self, num_spots: int):
        """Initialize parking spots with IoT sensors"""
        for i in range(num_spots):
            self.spots[i] = False
            self.spot_sensors[i] = True  # Sensor active

    def find_nearest_spot(self, vehicle_type: VehicleType) -> Optional[int]:
        """Find the nearest available spot using sensor data"""
        for spot_id, occupied in self.spots.items():
            if not occupied and self.spot_sensors[spot_id]:
                if self._is_spot_suitable(spot_id, vehicle_type):
                    return spot_id
        return None

    def park_vehicle(self, vehicle: Vehicle) -> dict:
        """Park a vehicle with smart allocation"""
        spot_id = self.find_nearest_spot(vehicle.vehicle_type)
        
        if spot_id is None:
            self.waiting_list.append(vehicle)
            return {
                "status": "waiting",
                "position": len(self.waiting_list),
                "estimated_wait": self._estimate_wait_time()
            }

        self.spots[spot_id] = True
        self.vehicle_map[vehicle.license_plate] = spot_id

        return {
            "status": "parked",
            "spot_id": spot_id,
            "entry_time": datetime.now(),
            "ticket_id": str(uuid.uuid4())
        }

    def exit_vehicle(self, license_plate: str) -> dict:
        """Process vehicle exit with automatic payment"""
        if license_plate not in self.vehicle_map:
            raise ValueError("Vehicle not found")

        spot_id = self.vehicle_map[license_plate]
        self.spots[spot_id] = False
        del self.vehicle_map[license_plate]

        # Process waiting list
        self._process_waiting_list()

        return {
            "status": "success",
            "exit_time": datetime.now(),
            "fee": self._calculate_fee(license_plate)
        }

    def _process_waiting_list(self):
        """Process vehicles in waiting list"""
        if not self.waiting_list:
            return

        vehicle = self.waiting_list[0]
        spot_id = self.find_nearest_spot(vehicle.vehicle_type)
        
        if spot_id is not None:
            self.waiting_list.pop(0)
            self.park_vehicle(vehicle) 