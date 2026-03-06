'use client';

import React, { useEffect, useState } from 'react';
import { api } from '../../lib/api';
import { Reservation } from '../../types';
import {
  Calendar,
  Search,
  Filter,
  Loader2,
  ChevronLeft,
  ChevronRight,
  Eye,
  CheckCircle2,
  Clock,
  LogOut,
  XCircle
} from 'lucide-react';
import Link from 'next/link';

export default function ReservationsPage() {
  useEffect(() => {
    document.title = 'Reservations - Ocean View Resort';
  }, []);
  const [reservations, setReservations] = useState<Reservation[]>([]);
  const [loading, setLoading] = useState(true);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [status, setStatus] = useState<string>('');
  const [search, setSearch] = useState('');
  const pageSize = 10;

  useEffect(() => {
    fetchReservations();
  }, [page, status]);

  const fetchReservations = async () => {
    setLoading(true);
    try {
      const params: any = {
        offset: ((page - 1) * pageSize).toString(),
        limit: pageSize.toString()
      };
      if (status) params.status = status;
      if (search) params.search = search;

      const response = await api.reservations.list(params);
      setReservations(response.data);
      setTotal(response.total);
    } catch (err) {
      console.error('Failed to fetch reservations', err);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    setPage(1);
    fetchReservations();
  };

  const getStatusBadge = (status: string) => {
    switch (status) {
      case 'CONFIRMED':
        return <span className="px-2 py-1 bg-blue-100 text-blue-700 rounded-full text-xs font-medium flex items-center gap-1 w-fit"><CheckCircle2 size={12} /> Confirmed</span>;
      case 'CHECKED_IN':
        return <span className="px-2 py-1 bg-green-100 text-green-700 rounded-full text-xs font-medium flex items-center gap-1 w-fit"><Clock size={12} /> Checked In</span>;
      case 'CHECKED_OUT':
        return <span className="px-2 py-1 bg-gray-100 text-gray-700 rounded-full text-xs font-medium flex items-center gap-1 w-fit"><LogOut size={12} /> Checked Out</span>;
      case 'CANCELLED':
        return <span className="px-2 py-1 bg-red-100 text-red-700 rounded-full text-xs font-medium flex items-center gap-1 w-fit"><XCircle size={12} /> Cancelled</span>;
      default:
        return <span className="px-2 py-1 bg-gray-100 text-gray-700 rounded-full text-xs font-medium w-fit">{status}</span>;
    }
  };

  const totalPages = Math.ceil(total / pageSize);

  return (
    <div className="space-y-4 md:space-y-6 p-4 md:p-6">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3">
        <div>
          <h1 className="text-xl md:text-2xl lg:text-3xl font-bold text-gray-800">Reservations</h1>
          <p className="text-xs md:text-sm text-gray-500 mt-1">Manage resort bookings and guest stays.</p>
        </div>
        <Link
          href="/dashboard"
          className="bg-[#0B3D6E] text-white px-4 py-2 rounded-lg hover:bg-[#0B3D6E]/90 transition-colors text-sm font-medium text-center whitespace-nowrap"
        >
          New Reservation
        </Link>
      </div>

      <div className="bg-white p-4 rounded-xl shadow-sm border border-gray-100 flex flex-col md:flex-row gap-4 items-center justify-between">
        <form onSubmit={handleSearch} className="relative w-full md:w-96">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" size={18} />
          <input
            type="text"
            placeholder="Search by name, number or contact..."
            className="w-full pl-10 pr-4 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </form>

        <div className="flex items-center gap-2 w-full md:w-auto">
          <Filter size={18} className="text-gray-400" />
          <select 
            className="border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white"
            value={status}
            onChange={(e) => { setStatus(e.target.value); setPage(1); }}
          >
            <option value="">All Statuses</option>
            <option value="CONFIRMED">Confirmed</option>
            <option value="CHECKED_IN">Checked In</option>
            <option value="CHECKED_OUT">Checked Out</option>
            <option value="CANCELLED">Cancelled</option>
          </select>
        </div>
      </div>

      <div className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left min-w-[640px]">
            <thead className="bg-gray-50 border-b border-gray-100">
              <tr>
                <th className="px-3 md:px-6 py-3 md:py-4 text-xs font-semibold text-gray-500 uppercase tracking-wider">Res. Number</th>
                <th className="px-3 md:px-6 py-3 md:py-4 text-xs font-semibold text-gray-500 uppercase tracking-wider">Guest</th>
                <th className="px-3 md:px-6 py-3 md:py-4 text-xs font-semibold text-gray-500 uppercase tracking-wider">Room</th>
                <th className="px-3 md:px-6 py-3 md:py-4 text-xs font-semibold text-gray-500 uppercase tracking-wider hidden sm:table-cell">Stay Dates</th>
                <th className="px-3 md:px-6 py-3 md:py-4 text-xs font-semibold text-gray-500 uppercase tracking-wider">Status</th>
                <th className="px-3 md:px-6 py-3 md:py-4 text-xs font-semibold text-gray-500 uppercase tracking-wider">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {loading ? (
                <tr>
                  <td colSpan={6} className="px-6 py-12 text-center">
                    <Loader2 className="h-8 w-8 animate-spin text-[#0B3D6E] mx-auto" />
                    <p className="mt-2 text-gray-500">Loading reservations...</p>
                  </td>
                </tr>
              ) : reservations.length === 0 ? (
                <tr>
                  <td colSpan={6} className="px-6 py-12 text-center text-gray-500">
                    No reservations found.
                  </td>
                </tr>
              ) : (
                reservations.map((res) => (
                  <tr key={res.reservationId} className="hover:bg-gray-50 transition-colors">
                    <td className="px-3 md:px-6 py-3 md:py-4 whitespace-nowrap text-xs md:text-sm font-medium text-gray-900">{res.reservationNumber}</td>
                    <td className="px-3 md:px-6 py-3 md:py-4 whitespace-nowrap">
                      <div className="text-xs md:text-sm font-medium text-gray-900">{res.guestName}</div>
                      <div className="text-xs text-gray-500">{res.contactNumber}</div>
                    </td>
                    <td className="px-3 md:px-6 py-3 md:py-4 whitespace-nowrap">
                      <div className="text-xs md:text-sm text-gray-900">Room {res.roomNumber}</div>
                      <div className="text-xs text-gray-500">{res.roomType}</div>
                    </td>
                    <td className="px-3 md:px-6 py-3 md:py-4 whitespace-nowrap hidden sm:table-cell">
                      <div className="text-xs md:text-sm text-gray-900">{res.checkInDate} to {res.checkOutDate}</div>
                      <div className="text-xs text-gray-500">{res.nights} nights</div>
                    </td>
                    <td className="px-3 md:px-6 py-3 md:py-4 whitespace-nowrap">
                      {getStatusBadge(res.status)}
                    </td>
                    <td className="px-3 md:px-6 py-3 md:py-4 whitespace-nowrap">
                      <Link
                        href={`/reservations/${res.reservationId}`}
                        className="text-blue-600 hover:text-blue-800 font-medium text-xs md:text-sm flex items-center gap-1"
                      >
                        <Eye size={14} className="md:w-4 md:h-4" /> <span className="hidden sm:inline">View Details</span><span className="sm:hidden">View</span>
                      </Link>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        {totalPages > 1 && (
          <div className="px-3 md:px-6 py-3 md:py-4 border-t border-gray-100 flex flex-col sm:flex-row items-center justify-between gap-3">
            <span className="text-xs md:text-sm text-gray-500">
              Showing {(page - 1) * pageSize + 1} to {Math.min(page * pageSize, total)} of {total}
            </span>
            <div className="flex gap-2">
              <button
                onClick={() => setPage(p => Math.max(1, p - 1))}
                disabled={page === 1}
                className="p-1.5 md:p-2 border border-gray-200 rounded-lg disabled:opacity-50 hover:bg-gray-50 transition-colors"
              >
                <ChevronLeft size={18} className="md:w-5 md:h-5" />
              </button>
              <button
                onClick={() => setPage(p => Math.min(totalPages, p + 1))}
                disabled={page === totalPages}
                className="p-1.5 md:p-2 border border-gray-200 rounded-lg disabled:opacity-50 hover:bg-gray-50 transition-colors"
              >
                <ChevronRight size={18} className="md:w-5 md:h-5" />
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
