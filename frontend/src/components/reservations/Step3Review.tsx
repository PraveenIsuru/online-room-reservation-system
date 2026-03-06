'use client';

import React, { useState } from 'react';
import { api } from '@/lib/api';
import { RoomType } from '@/types';
import { Loader2, Calendar, User, Home, MessageSquare, CheckCircle2 } from 'lucide-react';

interface Step3Props {
  data: {
    checkInDate: string;
    checkOutDate: string;
    roomId: number | null;
    roomType: RoomType | '';
    guestId: number | null;
    guestName: string;
    contactNumber: string;
    email: string;
    address: string;
    specialRequests: string;
  };
  onUpdate: (data: Partial<Step3Props['data']>) => void;
  onConfirm: () => void;
  onBack: () => void;
  loading: boolean;
}

export default function Step3Review({ data, onUpdate, onConfirm, onBack, loading }: Step3Props) {
  const diffDays = (date1: string, date2: string) => {
    const d1 = new Date(date1);
    const d2 = new Date(date2);
    const diffTime = Math.abs(d2.getTime() - d1.getTime());
    return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
  };

  const nights = data.checkInDate && data.checkOutDate ? diffDays(data.checkInDate, data.checkOutDate) : 0;

  return (
    <div className="space-y-6">
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {/* Stay Details */}
        <div className="border rounded-lg p-4 space-y-4">
          <div className="flex items-center gap-2 font-bold text-gray-900 border-b pb-2">
            <Calendar className="w-5 h-5 text-blue-600" />
            Stay Details
          </div>
          <div className="grid grid-cols-2 gap-4 text-sm">
            <div>
              <span className="block text-gray-500">Check-in</span>
              <span className="font-medium">{data.checkInDate}</span>
            </div>
            <div>
              <span className="block text-gray-500">Check-out</span>
              <span className="font-medium">{data.checkOutDate}</span>
            </div>
            <div>
              <span className="block text-gray-500">Duration</span>
              <span className="font-medium">{nights} Night(s)</span>
            </div>
          </div>
        </div>

        {/* Room Details */}
        <div className="border rounded-lg p-4 space-y-4">
          <div className="flex items-center gap-2 font-bold text-gray-900 border-b pb-2">
            <Home className="w-5 h-5 text-blue-600" />
            Room Details
          </div>
          <div className="grid grid-cols-2 gap-4 text-sm">
            <div>
              <span className="block text-gray-500">Room ID</span>
              <span className="font-medium">{data.roomId}</span>
            </div>
            <div>
              <span className="block text-gray-500">Type</span>
              <span className="font-medium">{data.roomType || 'N/A'}</span>
            </div>
          </div>
        </div>

        {/* Guest Details */}
        <div className="border rounded-lg p-4 space-y-4 md:col-span-2">
          <div className="flex items-center gap-2 font-bold text-gray-900 border-b pb-2">
            <User className="w-5 h-5 text-blue-600" />
            Guest Details
          </div>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4 text-sm">
            <div>
              <span className="block text-gray-500">Name</span>
              <span className="font-medium">{data.guestName}</span>
            </div>
            <div>
              <span className="block text-gray-500">Contact</span>
              <span className="font-medium">{data.contactNumber}</span>
            </div>
            <div>
              <span className="block text-gray-500">Email</span>
              <span className="font-medium">{data.email || 'N/A'}</span>
            </div>
            <div className="md:col-span-3">
              <span className="block text-gray-500">Address</span>
              <span className="font-medium">{data.address || 'N/A'}</span>
            </div>
          </div>
        </div>

        {/* Special Requests */}
        <div className="border rounded-lg p-4 space-y-4 md:col-span-2">
          <div className="flex items-center gap-2 font-bold text-gray-900 border-b pb-2">
            <MessageSquare className="w-5 h-5 text-blue-600" />
            Special Requests
          </div>
          <textarea
            value={data.specialRequests}
            onChange={(e) => onUpdate({ specialRequests: e.target.value })}
            className="w-full p-2 border rounded-md focus:ring-blue-500 focus:border-blue-500 text-sm"
            rows={3}
            placeholder="Any special requirements or notes..."
          />
        </div>
      </div>

      <div className="flex justify-between pt-4 border-t">
        <button
          onClick={onBack}
          disabled={loading}
          className="px-6 py-2 border rounded-md hover:bg-gray-50 disabled:opacity-50"
        >
          Back: Guest Details
        </button>
        <button
          onClick={onConfirm}
          disabled={loading}
          className="bg-green-600 text-white px-8 py-2 rounded-md hover:bg-green-700 disabled:opacity-50 flex items-center gap-2"
        >
          {loading ? (
            <Loader2 className="w-5 h-5 animate-spin" />
          ) : (
            <>
              <CheckCircle2 className="w-5 h-5" />
              Confirm Reservation
            </>
          )}
        </button>
      </div>
    </div>
  );
}
