'use client';

import React, { useState, useEffect } from 'react';
import { api } from '@/lib/api';
import { Room, RoomType } from '@/types';
import { Loader2, Search } from 'lucide-react';
import { cn } from '@/lib/utils';

interface Step1Props {
  data: {
    checkInDate: string;
    checkOutDate: string;
    roomId: number | null;
    roomType: RoomType | '';
  };
  onUpdate: (data: Partial<Step1Props['data']>) => void;
  onNext: () => void;
}

const ROOM_TYPES: { value: RoomType; label: string }[] = [
  { value: 'STANDARD', label: 'Standard' },
  { value: 'DELUXE', label: 'Deluxe' },
  { value: 'SUITE', label: 'Suite' },
  { value: 'OCEAN_VIEW', label: 'Ocean View' },
  { value: 'PENTHOUSE', label: 'Penthouse' },
];

export default function Step1DateRoom({ data, onUpdate, onNext }: Step1Props) {
  const [rooms, setRooms] = useState<Room[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [searched, setSearched] = useState(false);

  const handleSearch = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!data.checkInDate || !data.checkOutDate) {
      setError('Please select both check-in and check-out dates.');
      return;
    }

    if (new Date(data.checkInDate) >= new Date(data.checkOutDate)) {
      setError('Check-out date must be after check-in date.');
      return;
    }

    setLoading(true);
    setError(null);
    try {
      const res = await api.rooms.available({
        checkIn: data.checkInDate,
        checkOut: data.checkOutDate,
        type: data.roomType || undefined,
      });
      setRooms(res.data);
      setSearched(true);
    } catch (err: any) {
      setError(err.message || 'Failed to fetch available rooms.');
    } finally {
      setLoading(false);
    }
  };

  const selectedRoom = rooms.find((r) => r.roomId === data.roomId);

  return (
    <div className="space-y-6">
      <form onSubmit={handleSearch} className="grid grid-cols-1 md:grid-cols-4 gap-4 items-end bg-gray-50 p-4 rounded-lg">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Check-in Date</label>
          <input
            type="date"
            required
            min={new Date().toISOString().split('T')[0]}
            value={data.checkInDate}
            onChange={(e) => onUpdate({ checkInDate: e.target.value, roomId: null })}
            className="w-full p-2 border rounded-md focus:ring-blue-500 focus:border-blue-500"
          />
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Check-out Date</label>
          <input
            type="date"
            required
            min={data.checkInDate || new Date().toISOString().split('T')[0]}
            value={data.checkOutDate}
            onChange={(e) => onUpdate({ checkOutDate: e.target.value, roomId: null })}
            className="w-full p-2 border rounded-md focus:ring-blue-500 focus:border-blue-500"
          />
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Room Type (Optional)</label>
          <select
            value={data.roomType}
            onChange={(e) => onUpdate({ roomType: e.target.value as RoomType | '', roomId: null })}
            className="w-full p-2 border rounded-md focus:ring-blue-500 focus:border-blue-500"
          >
            <option value="">All Types</option>
            {ROOM_TYPES.map((t) => (
              <option key={t.value} value={t.value}>
                {t.label}
              </option>
            ))}
          </select>
        </div>
        <button
          type="submit"
          disabled={loading}
          className="bg-blue-600 text-white p-2 rounded-md hover:bg-blue-700 disabled:opacity-50 flex items-center justify-center gap-2"
        >
          {loading ? <Loader2 className="w-5 h-5 animate-spin" /> : <Search className="w-5 h-5" />}
          Search Rooms
        </button>
      </form>

      {error && <div className="p-3 bg-red-100 text-red-700 rounded-md text-sm">{error}</div>}

      {searched && (
        <div className="space-y-4">
          <h3 className="text-lg font-semibold">Available Rooms</h3>
          {rooms.length === 0 ? (
            <p className="text-gray-500 italic text-center py-8">No rooms available for the selected criteria.</p>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {rooms.map((room) => (
                <div
                  key={room.roomId}
                  onClick={() => onUpdate({ roomId: room.roomId })}
                  className={cn(
                    'border rounded-lg p-4 cursor-pointer transition-all hover:border-blue-500',
                    data.roomId === room.roomId ? 'ring-2 ring-blue-500 border-blue-500 bg-blue-50' : 'bg-white'
                  )}
                >
                  <div className="flex justify-between items-start mb-2">
                    <span className="font-bold text-lg">Room {room.roomNumber}</span>
                    <span className="bg-blue-100 text-blue-800 text-xs px-2 py-1 rounded">
                      {room.roomTypeDisplay}
                    </span>
                  </div>
                  <p className="text-sm text-gray-600 mb-4 line-clamp-2">{room.description || 'No description available.'}</p>
                  <div className="flex justify-between items-center">
                    <span className="text-blue-600 font-semibold">${room.pricePerNight} / night</span>
                    {data.roomId === room.roomId && (
                      <span className="text-blue-600 text-sm font-medium">Selected</span>
                    )}
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      <div className="flex justify-end pt-4 border-t">
        <button
          onClick={onNext}
          disabled={!data.roomId}
          className="bg-blue-600 text-white px-6 py-2 rounded-md hover:bg-blue-700 disabled:opacity-50"
        >
          Next: Guest Details
        </button>
      </div>
    </div>
  );
}
