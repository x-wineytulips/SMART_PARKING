import time
from datetime import datetime
import psycopg2
import json
import websockets

class ParkingMonitor:
    def __init__(self):
        self.db_connection = psycopg2.connect(
            dbname="parking_db",
            user="admin",
            password="password",
            host="localhost"
        )
        self.websocket = None

    async def monitor_occupancy(self):
        while True:
            occupancy_data = self.get_occupancy_data()
            await self.send_alerts(occupancy_data)
            await self.update_dashboard(occupancy_data)
            time.sleep(60)  # Check every minute

    def get_occupancy_data(self):
        cursor = self.db_connection.cursor()
        cursor.execute("""
            SELECT 
                location_id,
                COUNT(CASE WHEN is_occupied THEN 1 END) as occupied_spots,
                COUNT(*) as total_spots
            FROM parking_spots
            GROUP BY location_id
        """)
        return cursor.fetchall()

    async def send_alerts(self, occupancy_data):
        for location in occupancy_data:
            occupancy_rate = location[1] / location[2]
            if occupancy_rate > 0.9:  # 90% full
                await self.send_notification(
                    f"Warning: Location {location[0]} is {occupancy_rate*100}% full"
                )

    async def update_dashboard(self, data):
        if self.websocket:
            await self.websocket.send(json.dumps({
                'type': 'occupancy_update',
                'data': data,
                'timestamp': datetime.now().isoformat()
            })) 