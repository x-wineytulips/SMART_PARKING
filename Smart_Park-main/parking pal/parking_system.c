#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <string.h>

#define MAX_SPOTS 1000
#define MAX_VEHICLES 2000
#define PLATE_LENGTH 10

typedef enum {
    MOTORCYCLE = 0,
    CAR,
    SUV,
    TRUCK
} VehicleType;

typedef struct {
    int id;
    VehicleType type;
    int floor;
    int is_occupied;
    time_t occupied_since;
} ParkingSpot;

typedef struct {
    char license_plate[PLATE_LENGTH];
    VehicleType type;
    int spot_id;
    time_t entry_time;
} Vehicle;

typedef struct {
    ParkingSpot* spots[MAX_SPOTS];
    Vehicle* vehicles[MAX_VEHICLES];
    int spot_count;
    int vehicle_count;
    double revenue;
} ParkingSystem;

ParkingSystem* initialize_parking_system(int num_spots) {
    ParkingSystem* system = (ParkingSystem*)malloc(sizeof(ParkingSystem));
    system->spot_count = num_spots;
    system->vehicle_count = 0;
    system->revenue = 0.0;

    for (int i = 0; i < num_spots; i++) {
        system->spots[i] = (ParkingSpot*)malloc(sizeof(ParkingSpot));
        system->spots[i]->id = i;
        system->spots[i]->is_occupied = 0;
        system->spots[i]->floor = i / 100; // 100 spots per floor
        system->spots[i]->type = i % 4; // Distribute spot types
    }

    return system;
}

int find_optimal_spot(ParkingSystem* system, VehicleType type) {
    // Use bit manipulation for faster processing
    int spot_mask = 1 << type;
    
    for (int i = 0; i < system->spot_count; i++) {
        if (!system->spots[i]->is_occupied && 
            (spot_mask & (1 << system->spots[i]->type))) {
            return i;
        }
    }
    return -1;
}

double calculate_fee(time_t entry_time, VehicleType type) {
    time_t exit_time = time(NULL);
    double duration = difftime(exit_time, entry_time) / 3600.0; // Hours
    double base_rate = 2.0;
    
    switch (type) {
        case MOTORCYCLE:
            return duration * base_rate;
        case CAR:
            return duration * base_rate * 1.5;
        case SUV:
            return duration * base_rate * 2.0;
        case TRUCK:
            return duration * base_rate * 3.0;
        default:
            return 0.0;
    }
}

int park_vehicle(ParkingSystem* system, const char* license_plate, VehicleType type) {
    if (system->vehicle_count >= MAX_VEHICLES) {
        return -1; // System full
    }

    int spot_id = find_optimal_spot(system, type);
    if (spot_id == -1) {
        return -2; // No suitable spot
    }

    Vehicle* vehicle = (Vehicle*)malloc(sizeof(Vehicle));
    strncpy(vehicle->license_plate, license_plate, PLATE_LENGTH - 1);
    vehicle->type = type;
    vehicle->spot_id = spot_id;
    vehicle->entry_time = time(NULL);

    system->vehicles[system->vehicle_count++] = vehicle;
    system->spots[spot_id]->is_occupied = 1;
    system->spots[spot_id]->occupied_since = vehicle->entry_time;

    return spot_id;
} 