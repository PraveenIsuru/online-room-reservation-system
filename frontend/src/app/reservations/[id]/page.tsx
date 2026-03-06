'use client';

import React, { useEffect, useState } from 'react';
import { useParams, useRouter } from 'next/navigation';
import { api } from '../../../lib/api';
import { Reservation, ReservationStatus, Bill } from '../../../types';
import { 
  ArrowLeft, 
  Calendar, 
  User, 
  DoorOpen, 
  Clock, 
  CreditCard, 
  FileText,
  Loader2,
  AlertCircle,
  CheckCircle2,
  XCircle,
  ChevronRight,
  LogOut
} from 'lucide-react';
import Link from 'next/link';

export default function ReservationDetailPage() {
  const { id } = useParams();
  const router = useRouter();
  const [reservation, setReservation] = useState<Reservation | null>(null);
  const [bill, setBill] = useState<Bill | null>(null);
  const [loading, setLoading] = useState(true);
  const [updating, setUpdating] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (id) {
      fetchData();
    }
  }, [id]);

  const fetchData = async () => {
    setLoading(true);
    try {
      const resResponse = await api.reservations.get(Number(id));
      setReservation(resResponse.data);
      
      try {
        const billResponse = await api.bills.getByReservation(Number(id));
        setBill(billResponse.data);
      } catch (e) {
        // Bill might not exist yet if not checked in/out depending on backend logic
      }
    } catch (err: any) {
      setError(err.message || 'Failed to load reservation details');
    } finally {
      setLoading(false);
    }
  };

  const handleStatusUpdate = async (newStatus: ReservationStatus) => {
    if (!reservation) return;
    setUpdating(true);
    try {
      await api.reservations.updateStatus(reservation.reservationId, newStatus);
      await fetchData(); // Refresh data
    } catch (err: any) {
      alert(err.message || 'Failed to update status');
    } finally {
      setUpdating(false);
    }
  };

  if (loading) {
    return (
      <div className="flex flex-col items-center justify-center min-h-[60vh]">
        <Loader2 className="h-10 w-10 animate-spin text-[#0B3D6E]" />
        <p className="mt-4 text-gray-600 font-medium">Loading details...</p>
      </div>
    );
  }

  if (error || !reservation) {
    return (
      <div className="bg-red-50 border border-red-200 rounded-xl p-8 flex flex-col items-center text-center">
        <AlertCircle className="h-12 w-12 text-red-500 mb-4" />
        <h3 className="text-lg font-bold text-red-800">Error</h3>
        <p className="text-red-600">{error || 'Reservation not found.'}</p>
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
      <div className="flex items-center justify-between pt-8">
        <button 
          onClick={() => router.back()}
          className="flex items-center gap-2 text-gray-600 hover:text-gray-900 transition-colors"
        >
          <ArrowLeft size={20} />
          <span className="font-medium">Back to List</span>
        </button>
        <div className="flex gap-3">
          {reservation.status === 'CONFIRMED' && (
            <button
              onClick={() => handleStatusUpdate('CHECKED_IN')}
              disabled={updating}
              className="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 disabled:opacity-50 font-medium text-sm flex items-center gap-2"
            >
              <CheckCircle2 size={18} /> Check In
            </button>
          )}
          {reservation.status === 'CHECKED_IN' && (
            <button
              onClick={() => handleStatusUpdate('CHECKED_OUT')}
              disabled={updating}
              className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50 font-medium text-sm flex items-center gap-2"
            >
              <LogOut size={18} /> Check Out
            </button>
          )}
          {['CONFIRMED'].includes(reservation.status) && (
            <button
              onClick={() => handleStatusUpdate('CANCELLED')}
              disabled={updating}
              className="px-4 py-2 border border-red-200 text-red-600 rounded-lg hover:bg-red-50 disabled:opacity-50 font-medium text-sm flex items-center gap-2"
            >
              <XCircle size={18} /> Cancel
            </button>
          )}
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2 space-y-6">
          <div className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
            <div className="p-6 border-b border-gray-100 flex justify-between items-center">
              <div>
                <h2 className="text-xl font-bold text-gray-800">Reservation #{reservation.reservationNumber}</h2>
                <p className="text-gray-500 text-sm">Created on {new Date(reservation.createdAt).toLocaleDateString()}</p>
              </div>
              <div className={`px-3 py-1 rounded-full text-xs font-bold uppercase ${
                reservation.status === 'CONFIRMED' ? 'bg-blue-100 text-blue-700' :
                reservation.status === 'CHECKED_IN' ? 'bg-green-100 text-green-700' :
                reservation.status === 'CHECKED_OUT' ? 'bg-gray-100 text-gray-700' :
                'bg-red-100 text-red-700'
              }`}>
                {reservation.status.replace('_', ' ')}
              </div>
            </div>
            
            <div className="p-6 grid grid-cols-1 md:grid-cols-2 gap-8">
              <div className="space-y-4">
                <div className="flex items-start gap-3">
                  <div className="p-2 bg-blue-50 rounded-lg text-blue-600">
                    <User size={20} />
                  </div>
                  <div>
                    <p className="text-xs text-gray-500 uppercase font-bold tracking-wider">Guest Information</p>
                    <p className="font-bold text-gray-900 text-lg">{reservation.guestName}</p>
                    <p className="text-gray-600">{reservation.contactNumber}</p>
                    <Link href={`/guests/${reservation.guestId}`} className="text-blue-600 text-sm hover:underline mt-1 inline-block">
                      View Guest Profile
                    </Link>
                  </div>
                </div>

                <div className="flex items-start gap-3">
                  <div className="p-2 bg-green-50 rounded-lg text-green-600">
                    <DoorOpen size={20} />
                  </div>
                  <div>
                    <p className="text-xs text-gray-500 uppercase font-bold tracking-wider">Room Details</p>
                    <p className="font-bold text-gray-900">Room {reservation.roomNumber}</p>
                    <p className="text-gray-600">{reservation.roomType} Room</p>
                    <p className="text-gray-600">LKR {reservation.pricePerNight.toLocaleString()} / night</p>
                  </div>
                </div>
              </div>

              <div className="space-y-4">
                <div className="flex items-start gap-3">
                  <div className="p-2 bg-orange-50 rounded-lg text-orange-600">
                    <Calendar size={20} />
                  </div>
                  <div>
                    <p className="text-xs text-gray-500 uppercase font-bold tracking-wider">Stay Schedule</p>
                    <div className="flex items-center gap-3 mt-1">
                      <div>
                        <p className="text-xs font-medium text-gray-400">CHECK-IN</p>
                        <p className="font-bold text-gray-900">{reservation.checkInDate}</p>
                      </div>
                      <ChevronRight className="text-gray-300" />
                      <div>
                        <p className="text-xs font-medium text-gray-400">CHECK-OUT</p>
                        <p className="font-bold text-gray-900">{reservation.checkOutDate}</p>
                      </div>
                    </div>
                    <p className="mt-2 text-sm bg-orange-100 text-orange-700 px-2 py-0.5 rounded-full w-fit">
                      {reservation.nights} Nights Stay
                    </p>
                  </div>
                </div>

                <div className="flex items-start gap-3">
                  <div className="p-2 bg-purple-50 rounded-lg text-purple-600">
                    <FileText size={20} />
                  </div>
                  <div>
                    <p className="text-xs text-gray-500 uppercase font-bold tracking-wider">Special Requests</p>
                    <p className="text-gray-700 mt-1 italic">
                      {reservation.specialRequests || 'No special requests provided.'}
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </div>

          {bill && (
            <div className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
              <div className="p-6 border-b border-gray-100 flex justify-between items-center">
                <h3 className="text-lg font-bold text-gray-800 flex items-center gap-2">
                  <CreditCard size={20} className="text-[#0B3D6E]" /> Billing Summary
                </h3>
                <div className={`px-2 py-1 rounded text-xs font-bold ${
                  bill.status === 'PAID' ? 'bg-green-100 text-green-700' : 'bg-orange-100 text-orange-700'
                }`}>
                  {bill.status}
                </div>
              </div>
              <div className="p-6">
                <div className="space-y-3">
                  <div className="flex justify-between text-gray-600">
                    <span>Room Charges ({bill.nights} nights x {bill.pricePerNight.toLocaleString()})</span>
                    <span>LKR {bill.roomCharges.toLocaleString()}</span>
                  </div>
                  <div className="flex justify-between text-gray-600">
                    <span>Service Charges & Tax</span>
                    <span>LKR {(bill.totalAmount - bill.roomCharges).toLocaleString()}</span>
                  </div>
                  <div className="pt-3 border-t border-gray-100 flex justify-between font-bold text-xl text-gray-900">
                    <span>Total Amount</span>
                    <span>LKR {bill.totalAmount.toLocaleString()}</span>
                  </div>
                </div>
                {bill.status === 'PENDING' && reservation.status === 'CHECKED_OUT' && (
                  <Link 
                    href={`/billing`}
                    className="mt-6 block w-full text-center py-3 bg-[#0B3D6E] text-white rounded-lg font-bold hover:bg-[#0B3D6E]/90 transition-colors"
                  >
                    Proceed to Payment
                  </Link>
                )}
              </div>
            </div>
          )}
        </div>

        <div className="space-y-6">
          <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
            <h3 className="text-sm font-bold text-gray-800 uppercase tracking-wider mb-4">Activity Log</h3>
            <div className="space-y-4">
              <div className="flex gap-3">
                <div className="relative">
                  <div className="w-2 h-2 bg-blue-500 rounded-full mt-1.5"></div>
                  <div className="absolute top-4 left-0.5 w-0.5 h-full bg-gray-100"></div>
                </div>
                <div>
                  <p className="text-sm font-bold text-gray-800">Reservation Created</p>
                  <p className="text-xs text-gray-500">by {reservation.createdByUsername}</p>
                  <p className="text-xs text-gray-400">{new Date(reservation.createdAt).toLocaleString()}</p>
                </div>
              </div>
              {/* This is a mockup of activity, in a real app we'd fetch this from audit logs */}
              {reservation.status !== 'CONFIRMED' && (
                <div className="flex gap-3">
                  <div className="w-2 h-2 bg-green-500 rounded-full mt-1.5"></div>
                  <div>
                    <p className="text-sm font-bold text-gray-800">Status Updated to {reservation.status}</p>
                    <p className="text-xs text-gray-400">Recently</p>
                  </div>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
