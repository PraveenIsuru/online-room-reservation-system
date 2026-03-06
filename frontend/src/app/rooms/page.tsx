'use client';

import React, { useEffect, useState } from 'react';
import { api } from '../lib/api';
import { Room, RoomStatus } from '../types';
import { 
  Bed, 
  Loader2, 
  AlertCircle,
  Settings,
  CheckCircle2,
  XCircle,
  Hammer,
  Search,
  Filter
} from 'lucide-react';

export default function RoomsPage() {
  const [rooms, setRooms] = useState<Room[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [updating, setUpdating] = useState<number | null>(null);
  const [filter, setFilter] = useState<string>('');
  const [search, setSearch] = useState('');

  useEffect(() => {
    fetchRooms();
  }, []);

  const fetchRooms = async () => {
    setLoading(true);
    try {
      const response = await api.rooms.list();
      setRooms(response.data);
    } catch (err: any) {
      setError(err.message || 'Failed to load rooms');
    } finally {
      setLoading(false);
    }
  };

  const handleStatusUpdate = async (roomId: number, newStatus: RoomStatus) => {
    setUpdating(roomId);
    try {
      await api.rooms.updateStatus(roomId, newStatus);
      setRooms(rooms.map(r => r.roomId === roomId ? { ...r, status: newStatus } : r));
    } catch (err: any) {
      alert(err.message || 'Failed to update status');
    } finally {
      setUpdating(null);
    }
  };

  const filteredRooms = rooms.filter(room => {
    const matchesFilter = filter ? room.status === filter : true;
    const matchesSearch = search ? 
      room.roomNumber.toLowerCase().includes(search.toLowerCase()) || 
      room.roomType.toLowerCase().includes(search.toLowerCase()) : true;
    return matchesFilter && matchesSearch;
  });

  if (loading) {
    return (
      <div className="flex flex-col items-center justify-center min-h-[60vh]">
        <Loader2 className="h-10 w-10 animate-spin text-[#0B3D6E]" />
        <p className="mt-4 text-gray-600 font-medium">Loading rooms...</p>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-800">Rooms Management</h1>
          <p className="text-gray-500 text-sm">Monitor and manage room statuses and availability.</p>
        </div>
      </div>

      <div className="bg-white p-4 rounded-xl shadow-sm border border-gray-100 flex flex-col md:flex-row gap-4 items-center justify-between">
        <div className="relative w-full md:w-96">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" size={18} />
          <input
            type="text"
            placeholder="Search by room number or type..."
            className="w-full pl-10 pr-4 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </div>

        <div className="flex items-center gap-2 w-full md:w-auto">
          <Filter size={18} className="text-gray-400" />
          <select 
            className="border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white"
            value={filter}
            onChange={(e) => setFilter(e.target.value)}
          >
            <option value="">All Statuses</option>
            <option value="AVAILABLE">Available</option>
            <option value="OCCUPIED">Occupied</option>
            <option value="RESERVED">Reserved</option>
            <option value="MAINTENANCE">Maintenance</option>
          </select>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
        {filteredRooms.length === 0 ? (
          <div className="col-span-full py-12 text-center text-gray-500 bg-white rounded-xl border border-dashed border-gray-300">
            No rooms matching your criteria.
          </div>
        ) : (
          filteredRooms.map((room) => (
            <div key={room.roomId} className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden flex flex-col">
              <div className={cn(
                "p-4 flex justify-between items-center",
                room.status === 'AVAILABLE' ? "bg-green-50" : 
                room.status === 'OCCUPIED' ? "bg-blue-50" : 
                room.status === 'MAINTENANCE' ? "bg-red-50" : "bg-orange-50"
              )}>
                <div className="flex items-center gap-2">
                  <div className={cn(
                    "p-2 rounded-lg",
                    room.status === 'AVAILABLE' ? "bg-green-100 text-green-700" : 
                    room.status === 'OCCUPIED' ? "bg-blue-100 text-blue-700" : 
                    room.status === 'MAINTENANCE' ? "bg-red-100 text-red-700" : "bg-orange-100 text-orange-700"
                  )}>
                    <Bed size={18} />
                  </div>
                  <div>
                    <p className="text-xs text-gray-500 font-bold uppercase tracking-wider leading-none">Floor {room.floorNumber}</p>
                    <p className="text-lg font-bold text-gray-800 leading-tight">Room {room.roomNumber}</p>
                  </div>
                </div>
                <div className={cn(
                  "px-2 py-0.5 rounded text-[10px] font-bold uppercase",
                  room.status === 'AVAILABLE' ? "bg-green-200 text-green-800" : 
                  room.status === 'OCCUPIED' ? "bg-blue-200 text-blue-800" : 
                  room.status === 'MAINTENANCE' ? "bg-red-200 text-red-800" : "bg-orange-200 text-orange-800"
                )}>
                  {room.status}
                </div>
              </div>

              <div className="p-4 flex-1 space-y-4">
                <div>
                  <p className="text-sm font-medium text-gray-900">{room.roomTypeDisplay}</p>
                  <p className="text-xs text-gray-500">{room.description || 'No description'}</p>
                </div>
                
                <div className="pt-4 border-t border-gray-50 flex items-center justify-between">
                  <span className="text-sm font-bold text-[#0B3D6E]">LKR {room.pricePerNight.toLocaleString()}</span>
                  <span className="text-xs text-gray-400">per night</span>
                </div>
              </div>

              <div className="p-4 bg-gray-50 border-t border-gray-100 flex gap-2">
                {room.status === 'AVAILABLE' && (
                  <button 
                    onClick={() => handleStatusUpdate(room.roomId, 'MAINTENANCE')}
                    disabled={updating === room.roomId}
                    className="flex-1 py-2 bg-red-600 text-white rounded-lg text-xs font-bold hover:bg-red-700 transition-colors flex items-center justify-center gap-1 disabled:opacity-50"
                  >
                    <Hammer size={14} /> Maintenance
                  </button>
                )}
                {room.status === 'MAINTENANCE' && (
                  <button 
                    onClick={() => handleStatusUpdate(room.roomId, 'AVAILABLE')}
                    disabled={updating === room.roomId}
                    className="flex-1 py-2 bg-green-600 text-white rounded-lg text-xs font-bold hover:bg-green-700 transition-colors flex items-center justify-center gap-1 disabled:opacity-50"
                  >
                    <CheckCircle2 size={14} /> Set Available
                  </button>
                )}
                {room.status === 'OCCUPIED' && (
                  <p className="text-center w-full text-xs text-blue-600 font-medium italic">Occupied by guest</p>
                )}
                {room.status === 'RESERVED' && (
                  <p className="text-center w-full text-xs text-orange-600 font-medium italic">Reserved for arrival</p>
                )}
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
}
