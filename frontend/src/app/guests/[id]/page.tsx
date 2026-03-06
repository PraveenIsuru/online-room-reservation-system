'use client';

import React, { useEffect, useState } from 'react';
import { useParams, useRouter } from 'next/navigation';
import { api } from '../../lib/api';
import { Guest, Reservation } from '../../types';
import { 
  ArrowLeft, 
  User, 
  Mail, 
  Phone, 
  MapPin, 
  Calendar, 
  Loader2,
  AlertCircle,
  History,
  ExternalLink
} from 'lucide-react';
import Link from 'next/link';

export default function GuestDetailPage() {
  const { id } = useParams();
  const router = useRouter();
  const [guest, setGuest] = useState<Guest | null>(null);
  const [history, setHistory] = useState<Reservation[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (id) {
      fetchData();
    }
  }, [id]);

  const fetchData = async () => {
    setLoading(true);
    try {
      const guestRes = await api.guests.get(Number(id));
      setGuest(guestRes.data);
      
      const historyRes = await api.reservations.list({ guestId: id.toString() });
      setHistory(historyRes.data);
    } catch (err: any) {
      setError(err.message || 'Failed to load guest details');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="flex flex-col items-center justify-center min-h-[60vh]">
        <Loader2 className="h-10 w-10 animate-spin text-[#0B3D6E]" />
        <p className="mt-4 text-gray-600 font-medium">Loading profile...</p>
      </div>
    );
  }

  if (error || !guest) {
    return (
      <div className="bg-red-50 border border-red-200 rounded-xl p-8 flex flex-col items-center text-center">
        <AlertCircle className="h-12 w-12 text-red-500 mb-4" />
        <h3 className="text-lg font-bold text-red-800">Error</h3>
        <p className="text-red-600">{error || 'Guest not found.'}</p>
        <button 
          onClick={() => router.back()}
          className="mt-6 px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors"
        >
          Go Back
        </button>
      </div>
    );
  }

  return (
    <div className="max-w-5xl mx-auto space-y-6">
      <button 
        onClick={() => router.back()}
        className="flex items-center gap-2 text-gray-600 hover:text-gray-900 transition-colors"
      >
        <ArrowLeft size={20} />
        <span className="font-medium">Back to Guests</span>
      </button>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="space-y-6">
          <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6 text-center">
            <div className="w-24 h-24 bg-blue-100 text-[#0B3D6E] rounded-full flex items-center justify-center font-bold text-3xl mx-auto mb-4">
              {guest.guestName.charAt(0)}
            </div>
            <h2 className="text-2xl font-bold text-gray-900">{guest.guestName}</h2>
            <p className="text-gray-500 text-sm">Member since {new Date(guest.createdAt).toLocaleDateString()}</p>
            
            <div className="mt-8 pt-6 border-t border-gray-100 space-y-4 text-left">
              <div className="flex items-center gap-3 text-gray-700">
                <Mail size={18} className="text-gray-400" />
                <span className="text-sm">{guest.email || 'No email provided'}</span>
              </div>
              <div className="flex items-center gap-3 text-gray-700">
                <Phone size={18} className="text-gray-400" />
                <span className="text-sm">{guest.contactNumber}</span>
              </div>
              <div className="flex items-start gap-3 text-gray-700">
                <MapPin size={18} className="text-gray-400 shrink-0 mt-0.5" />
                <span className="text-sm">{guest.address || 'No address provided'}</span>
              </div>
            </div>
          </div>

          <div className="bg-[#0B3D6E] rounded-xl shadow-sm p-6 text-white">
            <h3 className="font-bold text-lg mb-4">Guest Statistics</h3>
            <div className="grid grid-cols-2 gap-4">
              <div className="bg-white/10 p-4 rounded-lg">
                <p className="text-xs text-blue-200 uppercase font-bold">Total Stays</p>
                <p className="text-2xl font-bold">{history.length}</p>
              </div>
              <div className="bg-white/10 p-4 rounded-lg">
                <p className="text-xs text-blue-200 uppercase font-bold">Status</p>
                <p className="text-lg font-bold">Regular</p>
              </div>
            </div>
          </div>
        </div>

        <div className="lg:col-span-2">
          <div className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
            <div className="p-6 border-b border-gray-100 flex items-center gap-2">
              <History size={20} className="text-[#0B3D6E]" />
              <h3 className="text-lg font-bold text-gray-800">Stay History</h3>
            </div>
            <div className="divide-y divide-gray-100">
              {history.length === 0 ? (
                <div className="p-12 text-center text-gray-500">
                  No stay history found for this guest.
                </div>
              ) : (
                history.map((res) => (
                  <div key={res.reservationId} className="p-6 hover:bg-gray-50 transition-colors flex items-center justify-between">
                    <div className="flex items-start gap-4">
                      <div className="p-2 bg-gray-100 rounded text-gray-500 mt-1">
                        <Calendar size={20} />
                      </div>
                      <div>
                        <p className="font-bold text-gray-900">Room {res.roomNumber} - {res.roomType}</p>
                        <p className="text-sm text-gray-500">{res.checkInDate} to {res.checkOutDate} ({res.nights} nights)</p>
                        <div className="flex items-center gap-2 mt-2">
                          <span className={`px-2 py-0.5 rounded text-[10px] font-bold uppercase ${
                            res.status === 'CONFIRMED' ? 'bg-blue-100 text-blue-700' :
                            res.status === 'CHECKED_IN' ? 'bg-green-100 text-green-700' :
                            res.status === 'CHECKED_OUT' ? 'bg-gray-100 text-gray-700' :
                            'bg-red-100 text-red-700'
                          }`}>
                            {res.status.replace('_', ' ')}
                          </span>
                          <span className="text-xs text-gray-400">#{res.reservationNumber}</span>
                        </div>
                      </div>
                    </div>
                    <Link 
                      href={`/reservations/${res.reservationId}`}
                      className="text-blue-600 hover:bg-blue-50 p-2 rounded-lg transition-colors"
                      title="View Details"
                    >
                      <ExternalLink size={20} />
                    </Link>
                  </div>
                ))
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
