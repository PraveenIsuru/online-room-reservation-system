'use client';

import React, { useEffect, useState } from 'react';
import { api } from '../lib/api';
import { Bill, Reservation } from '../types';
import { 
  CreditCard, 
  Search, 
  Loader2, 
  AlertCircle,
  FileText,
  ChevronRight,
  Filter,
  CheckCircle2,
  Clock
} from 'lucide-react';
import Link from 'next/link';

export default function BillingListPage() {
  const [reservations, setReservations] = useState<Reservation[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');

  useEffect(() => {
    fetchReservations();
  }, []);

  const fetchReservations = async () => {
    setLoading(true);
    try {
      // Fetch reservations and we'll show those that have bills or can have bills
      const response = await api.reservations.list({ limit: '100' });
      setReservations(response.data);
    } catch (err: any) {
      setError(err.message || 'Failed to load reservations');
    } finally {
      setLoading(false);
    }
  };

  const filteredReservations = reservations.filter(res => {
    const matchesSearch = 
      res.reservationNumber.toLowerCase().includes(searchTerm.toLowerCase()) ||
      res.guestName.toLowerCase().includes(searchTerm.toLowerCase());
    
    const matchesStatus = 
      statusFilter === 'ALL' || 
      (statusFilter === 'PAID' && res.billStatus === 'PAID') ||
      (statusFilter === 'PENDING' && res.billStatus === 'PENDING') ||
      (statusFilter === 'NONE' && !res.billStatus);
      
    return matchesSearch && matchesStatus;
  });

  return (
    <div className="space-y-6">
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
        <div>
          <h1 className="text-2xl font-black text-gray-800 flex items-center gap-2">
            <CreditCard className="text-[#0B3D6E]" /> Billing & Invoices
          </h1>
          <p className="text-gray-500">Manage guest bills and payments.</p>
        </div>
      </div>

      <div className="bg-white p-4 rounded-xl shadow-sm border border-gray-100 flex flex-col md:flex-row gap-4">
        <div className="relative flex-1">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" size={18} />
          <input
            type="text"
            placeholder="Search by reservation # or guest name..."
            className="w-full pl-10 pr-4 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#0B3D6E]/20 focus:border-[#0B3D6E] transition-all"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>
        <div className="flex items-center gap-2">
          <Filter size={18} className="text-gray-400" />
          <select 
            className="border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-[#0B3D6E]/20"
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
          >
            <option value="ALL">All Statuses</option>
            <option value="PENDING">Pending Payment</option>
            <option value="PAID">Paid</option>
            <option value="NONE">No Bill Yet</option>
          </select>
        </div>
      </div>

      {loading ? (
        <div className="flex flex-col items-center justify-center min-h-[40vh]">
          <Loader2 className="h-10 w-10 animate-spin text-[#0B3D6E]" />
          <p className="mt-4 text-gray-600 font-medium">Loading records...</p>
        </div>
      ) : error ? (
        <div className="bg-red-50 border border-red-200 rounded-xl p-8 flex flex-col items-center text-center">
          <AlertCircle className="h-12 w-12 text-red-500 mb-4" />
          <p className="text-red-600 font-medium">{error}</p>
          <button onClick={fetchReservations} className="mt-4 text-sm font-bold text-red-700 hover:underline">Try Again</button>
        </div>
      ) : (
        <div className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full text-left">
              <thead className="bg-gray-50 border-b border-gray-100">
                <tr>
                  <th className="px-6 py-4 text-xs font-black text-gray-400 uppercase tracking-widest">Reservation</th>
                  <th className="px-6 py-4 text-xs font-black text-gray-400 uppercase tracking-widest">Guest</th>
                  <th className="px-6 py-4 text-xs font-black text-gray-400 uppercase tracking-widest text-right">Amount</th>
                  <th className="px-6 py-4 text-xs font-black text-gray-400 uppercase tracking-widest text-center">Status</th>
                  <th className="px-6 py-4 text-xs font-black text-gray-400 uppercase tracking-widest text-right">Action</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-50">
                {filteredReservations.map((res) => (
                  <tr key={res.reservationId} className="hover:bg-gray-50 transition-colors">
                    <td className="px-6 py-4">
                      <p className="font-bold text-gray-800">#{res.reservationNumber}</p>
                      <p className="text-xs text-gray-500">{res.roomNumber} ({res.roomType})</p>
                    </td>
                    <td className="px-6 py-4">
                      <p className="font-bold text-gray-800">{res.guestName}</p>
                      <p className="text-xs text-gray-500">{res.checkInDate} - {res.checkOutDate}</p>
                    </td>
                    <td className="px-6 py-4 text-right">
                      <p className="font-black text-gray-800">
                        {res.totalAmount ? `LKR ${res.totalAmount.toLocaleString()}` : '-'}
                      </p>
                    </td>
                    <td className="px-6 py-4">
                      <div className="flex justify-center">
                        {res.billStatus === 'PAID' ? (
                          <span className="inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-xs font-black bg-green-100 text-green-700 uppercase">
                            <CheckCircle2 size={12} /> Paid
                          </span>
                        ) : res.billStatus === 'PENDING' ? (
                          <span className="inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-xs font-black bg-orange-100 text-orange-700 uppercase">
                            <Clock size={12} /> Pending
                          </span>
                        ) : (
                          <span className="inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-xs font-black bg-gray-100 text-gray-500 uppercase">
                            No Bill
                          </span>
                        )}
                      </div>
                    </td>
                    <td className="px-6 py-4 text-right">
                      {res.billStatus ? (
                        <Link 
                          href={`/billing/${res.reservationId}`}
                          className="inline-flex items-center gap-1.5 text-sm font-bold text-[#0B3D6E] hover:underline"
                        >
                          View Bill <ChevronRight size={16} />
                        </Link>
                      ) : (
                        <span className="text-xs text-gray-400 italic">Checkout to generate</span>
                      )}
                    </td>
                  </tr>
                ))}
                {filteredReservations.length === 0 && (
                  <tr>
                    <td colSpan={5} className="px-6 py-12 text-center text-gray-400 italic">
                      No matching records found.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  );
}
