'use client';

import React, { useEffect, useState } from 'react';
import { api } from '../../lib/api';
import { Guest } from '../../types';
import {
  Users,
  Search,
  Loader2,
  ChevronLeft,
  ChevronRight,
  Eye,
  Mail,
  Phone,
  MapPin,
  Plus
} from 'lucide-react';
import Link from 'next/link';

export default function GuestsPage() {
  const [guests, setGuests] = useState<Guest[]>([]);
  const [loading, setLoading] = useState(true);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [search, setSearch] = useState('');
  const pageSize = 10;

  useEffect(() => {
    document.title = 'Guests - Ocean View Resort';
  }, []);

  useEffect(() => {
    fetchGuests();
  }, [page]);

  const fetchGuests = async () => {
    setLoading(true);
    try {
      const params: any = {
        offset: ((page - 1) * pageSize).toString(),
        limit: pageSize.toString()
      };
      if (search) params.search = search;

      const response = await api.guests.list(params);
      setGuests(response.data);
      setTotal(response.total);
    } catch (err) {
      console.error('Failed to fetch guests', err);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    setPage(1);
    fetchGuests();
  };

  const totalPages = Math.ceil(total / pageSize);

  return (
    <div className="space-y-4 md:space-y-6 p-4 md:p-6">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3">
        <div>
          <h1 className="text-xl md:text-2xl lg:text-3xl font-bold text-gray-800">Guest Management</h1>
          <p className="text-xs md:text-sm text-gray-500 mt-1">View and manage resort guest records.</p>
        </div>
        <Link
          href="/dashboard"
          className="bg-[#0B3D6E] text-white px-4 py-2 rounded-lg hover:bg-[#0B3D6E]/90 transition-colors text-sm font-medium flex items-center justify-center gap-2 whitespace-nowrap"
        >
          <Plus size={18} /> Register Guest
        </Link>
      </div>

      <div className="bg-white p-4 rounded-xl shadow-sm border border-gray-100">
        <form onSubmit={handleSearch} className="relative max-w-md">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" size={18} />
          <input
            type="text"
            placeholder="Search by name, email or contact..."
            className="w-full pl-10 pr-4 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </form>
      </div>

      <div className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left min-w-[640px]">
            <thead className="bg-gray-50 border-b border-gray-100">
              <tr>
                <th className="px-3 md:px-6 py-3 md:py-4 text-xs font-semibold text-gray-500 uppercase tracking-wider">Guest Name</th>
                <th className="px-3 md:px-6 py-3 md:py-4 text-xs font-semibold text-gray-500 uppercase tracking-wider">Contact Info</th>
                <th className="px-3 md:px-6 py-3 md:py-4 text-xs font-semibold text-gray-500 uppercase tracking-wider hidden lg:table-cell">Location</th>
                <th className="px-3 md:px-6 py-3 md:py-4 text-xs font-semibold text-gray-500 uppercase tracking-wider hidden sm:table-cell">Reservations</th>
                <th className="px-3 md:px-6 py-3 md:py-4 text-xs font-semibold text-gray-500 uppercase tracking-wider hidden md:table-cell">Member Since</th>
                <th className="px-3 md:px-6 py-3 md:py-4 text-xs font-semibold text-gray-500 uppercase tracking-wider">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {loading ? (
                <tr>
                  <td colSpan={6} className="px-6 py-12 text-center">
                    <Loader2 className="h-8 w-8 animate-spin text-[#0B3D6E] mx-auto" />
                    <p className="mt-2 text-gray-500">Loading guests...</p>
                  </td>
                </tr>
              ) : guests.length === 0 ? (
                <tr>
                  <td colSpan={6} className="px-6 py-12 text-center text-gray-500">
                    No guests found.
                  </td>
                </tr>
              ) : (
                guests.map((guest) => (
                  <tr key={guest.guestId} className="hover:bg-gray-50 transition-colors">
                    <td className="px-3 md:px-6 py-3 md:py-4 whitespace-nowrap">
                      <div className="flex items-center gap-2 md:gap-3">
                        <div className="w-8 h-8 md:w-10 md:h-10 bg-blue-100 text-blue-700 rounded-full flex items-center justify-center font-bold text-xs md:text-sm shrink-0">
                          {guest.guestName.charAt(0)}
                        </div>
                        <div className="text-xs md:text-sm font-medium text-gray-900 truncate">{guest.guestName}</div>
                      </div>
                    </td>
                    <td className="px-3 md:px-6 py-3 md:py-4 whitespace-nowrap">
                      <div className="flex flex-col gap-1">
                        <div className="text-xs text-gray-600 flex items-center gap-1">
                          <Phone size={12} /> {guest.contactNumber}
                        </div>
                        <div className="text-xs text-gray-600 flex items-center gap-1 truncate max-w-[150px]">
                          <Mail size={12} className="shrink-0" /> {guest.email || 'N/A'}
                        </div>
                      </div>
                    </td>
                    <td className="px-3 md:px-6 py-3 md:py-4 whitespace-nowrap hidden lg:table-cell">
                      <div className="text-xs text-gray-600 flex items-center gap-1 max-w-[200px] truncate">
                        <MapPin size={12} className="shrink-0" /> {guest.address || 'N/A'}
                      </div>
                    </td>
                    <td className="px-3 md:px-6 py-3 md:py-4 whitespace-nowrap hidden sm:table-cell">
                      <span className="px-2 py-1 bg-gray-100 text-gray-700 rounded text-xs font-medium">
                        {guest.reservationCount || 0} bookings
                      </span>
                    </td>
                    <td className="px-3 md:px-6 py-3 md:py-4 whitespace-nowrap text-xs md:text-sm text-gray-500 hidden md:table-cell">
                      {new Date(guest.createdAt).toLocaleDateString()}
                    </td>
                    <td className="px-3 md:px-6 py-3 md:py-4 whitespace-nowrap">
                      <Link
                        href={`/guests/${guest.guestId}`}
                        className="text-blue-600 hover:text-blue-800 font-medium text-xs md:text-sm flex items-center gap-1"
                      >
                        <Eye size={14} className="md:w-4 md:h-4" /> <span className="hidden sm:inline">Profile</span>
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
