'use client';

import React, { useEffect, useState } from 'react';
import { useParams, useRouter } from 'next/navigation';
import { api } from '../../../lib/api';
import { Bill } from '../../../types';
import { 
  ArrowLeft, 
  Printer, 
  CreditCard, 
  Loader2, 
  AlertCircle,
  CheckCircle2,
  Calendar,
  User,
  MapPin,
  Phone,
  Mail,
  Building2
} from 'lucide-react';

export default function BillDetailPage() {
  const { id } = useParams();
  const router = useRouter();
  const [bill, setBill] = useState<Bill | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [paying, setPaying] = useState(false);

  useEffect(() => {
    if (id) {
      fetchBill();
    }
  }, [id]);

  const fetchBill = async () => {
    setLoading(true);
    try {
      // The API doesn't have a direct get bill by ID, but it has getByReservation.
      // However, usually we can get it via reservation detail or we might need a direct endpoint.
      // Let's check if the API can be extended or if there is another way.
      // Looking at api.ts, it has getByReservation(reservationId).
      // If 'id' is reservationId, we can use that.
      const response = await api.bills.getByReservation(Number(id));
      setBill(response.data);
    } catch (err: any) {
      setError(err.message || 'Failed to load bill details');
    } finally {
      setLoading(false);
    }
  };

  const handlePrint = () => {
    window.print();
  };

  const handlePay = async () => {
    if (!bill) return;
    setPaying(true);
    try {
      // Default to CASH for this implementation, but could be a selector
      await api.bills.pay(bill.billId, 'CASH');
      await fetchBill();
    } catch (err: any) {
      alert(err.message || 'Failed to process payment');
    } finally {
      setPaying(false);
    }
  };

  if (loading) {
    return (
      <div className="flex flex-col items-center justify-center min-h-[60vh]">
        <Loader2 className="h-10 w-10 animate-spin text-[#0B3D6E]" />
        <p className="mt-4 text-gray-600 font-medium">Loading bill...</p>
      </div>
    );
  }

  if (error || !bill) {
    return (
      <div className="bg-red-50 border border-red-200 rounded-xl p-8 flex flex-col items-center text-center">
        <AlertCircle className="h-12 w-12 text-red-500 mb-4" />
        <h3 className="text-lg font-bold text-red-800">Error</h3>
        <p className="text-red-600">{error || 'Bill not found.'}</p>
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
    <div className="max-w-4xl mx-auto space-y-6 pb-12 pt-8">
      <div className="flex items-center justify-between no-print">
        <button 
          onClick={() => router.back()}
          className="flex items-center gap-2 text-gray-600 hover:text-gray-900 transition-colors"
        >
          <ArrowLeft size={20} />
          <span className="font-medium">Back</span>
        </button>
        <div className="flex gap-3">
          <button
            onClick={handlePrint}
            className="px-4 py-2 bg-white border border-gray-200 text-gray-700 rounded-lg hover:bg-gray-50 font-medium text-sm flex items-center gap-2 shadow-sm"
          >
            <Printer size={18} /> Print Invoice
          </button>
          {bill.status === 'PENDING' && (
            <button
              onClick={handlePay}
              disabled={paying}
              className="px-4 py-2 bg-[#0B3D6E] text-white rounded-lg hover:bg-[#092d52] disabled:opacity-50 font-medium text-sm flex items-center gap-2 shadow-sm"
            >
              <CreditCard size={18} /> Mark as Paid
            </button>
          )}
        </div>
      </div>

      <div className="bg-white rounded-xl shadow-lg border border-gray-100 overflow-hidden print:shadow-none print:border-none">
        {/* Invoice Header */}
        <div className="p-8 border-b-2 border-gray-100 flex flex-col md:flex-row justify-between items-start md:items-center gap-6">
          <div className="flex items-center gap-4">
            <div className="p-3 bg-[#0B3D6E] rounded-xl text-white">
              <Building2 size={32} />
            </div>
            <div>
              <h1 className="text-2xl font-black text-[#0B3D6E] tracking-tight uppercase">Ocean View Resort</h1>
              <p className="text-gray-500 font-medium">Luxury Accommodation & Services</p>
            </div>
          </div>
          <div className="text-right">
            <h2 className="text-3xl font-bold text-gray-800 uppercase tracking-wider">Invoice</h2>
            <p className="text-gray-500 font-bold"># {bill.reservationNumber}</p>
            <div className={`mt-2 inline-block px-3 py-1 rounded-full text-xs font-black uppercase ${
              bill.status === 'PAID' ? 'bg-green-100 text-green-700' : 'bg-orange-100 text-orange-700'
            }`}>
              {bill.status}
            </div>
          </div>
        </div>

        <div className="p-8 grid grid-cols-1 md:grid-cols-2 gap-12">
          {/* Resort Info */}
          <div className="space-y-4">
            <h3 className="text-xs font-black text-gray-400 uppercase tracking-widest">From</h3>
            <div className="space-y-2">
              <p className="font-bold text-gray-800 text-lg">Ocean View Resort</p>
              <div className="flex items-center gap-2 text-gray-600">
                <MapPin size={14} className="text-gray-400" />
                <p className="text-sm">123 Galle Road, Hikkaduwa, Sri Lanka</p>
              </div>
              <div className="flex items-center gap-2 text-gray-600">
                <Phone size={14} className="text-gray-400" />
                <p className="text-sm">+94 91 234 5678</p>
              </div>
              <div className="flex items-center gap-2 text-gray-600">
                <Mail size={14} className="text-gray-400" />
                <p className="text-sm">billing@oceanviewresort.com</p>
              </div>
            </div>
          </div>

          {/* Guest Info */}
          <div className="space-y-4 md:text-right md:items-end flex flex-col">
            <h3 className="text-xs font-black text-gray-400 uppercase tracking-widest">Bill To</h3>
            <div className="space-y-2">
              <p className="font-bold text-gray-800 text-lg">{bill.guestName}</p>
              <div className="flex items-center gap-2 text-gray-600 md:justify-end">
                <Phone size={14} className="text-gray-400" />
                <p className="text-sm">{bill.contactNumber}</p>
              </div>
              <div className="flex items-center gap-2 text-gray-600 md:justify-end">
                <Calendar size={14} className="text-gray-400" />
                <p className="text-sm">Issued: {new Date(bill.issuedDate).toLocaleDateString()}</p>
              </div>
              {bill.paidDate && (
                <div className="flex items-center gap-2 text-green-600 md:justify-end font-medium">
                  <CheckCircle2 size={14} />
                  <p className="text-sm">Paid on: {new Date(bill.paidDate).toLocaleDateString()}</p>
                </div>
              )}
            </div>
          </div>
        </div>

        {/* Invoice Table */}
        <div className="px-8 pb-8">
          <div className="overflow-x-auto">
            <table className="w-full text-left">
              <thead>
                <tr className="border-b-2 border-gray-100">
                  <th className="py-4 font-black text-gray-400 uppercase text-xs tracking-widest">Description</th>
                  <th className="py-4 font-black text-gray-400 uppercase text-xs tracking-widest text-center">Stay</th>
                  <th className="py-4 font-black text-gray-400 uppercase text-xs tracking-widest text-right">Rate</th>
                  <th className="py-4 font-black text-gray-400 uppercase text-xs tracking-widest text-right">Amount</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-50">
                <tr>
                  <td className="py-6">
                    <p className="font-bold text-gray-800">Room {bill.roomNumber} - {bill.roomType}</p>
                    <p className="text-sm text-gray-500">{bill.checkInDate} to {bill.checkOutDate}</p>
                  </td>
                  <td className="py-6 text-center">
                    <span className="font-medium text-gray-700">{bill.nights} Nights</span>
                  </td>
                  <td className="py-6 text-right">
                    <span className="text-gray-700 font-medium">LKR {bill.pricePerNight.toLocaleString()}</span>
                  </td>
                  <td className="py-6 text-right">
                    <span className="text-gray-800 font-black">LKR {bill.roomCharges.toLocaleString()}</span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          <div className="mt-8 border-t-2 border-gray-100 pt-8 flex flex-col md:flex-row justify-between gap-8">
            <div className="max-w-xs">
              <p className="text-xs font-black text-gray-400 uppercase tracking-widest mb-2">Payment Info</p>
              <div className="bg-gray-50 rounded-lg p-4 space-y-1">
                <p className="text-sm text-gray-600"><span className="font-bold">Method:</span> {bill.paymentMethod || 'Not specified'}</p>
                <p className="text-sm text-gray-600"><span className="font-bold">Reservation ID:</span> {bill.reservationId}</p>
              </div>
              <p className="mt-4 text-xs text-gray-400 italic">Thank you for choosing Ocean View Resort. We hope you enjoyed your stay!</p>
            </div>
            
            <div className="space-y-3 min-w-[240px]">
              <div className="flex justify-between items-center text-gray-500">
                <span className="font-medium">Subtotal</span>
                <span>LKR {bill.roomCharges.toLocaleString()}</span>
              </div>
              <div className="flex justify-between items-center text-gray-500">
                <span className="font-medium">Service Charge (0%)</span>
                <span>LKR 0</span>
              </div>
              <div className="flex justify-between items-center text-gray-500">
                <span className="font-medium">Tax (0%)</span>
                <span>LKR 0</span>
              </div>
              <div className="flex justify-between items-center pt-3 border-t border-gray-100">
                <span className="text-lg font-black text-gray-800 uppercase">Total Amount</span>
                <span className="text-2xl font-black text-[#0B3D6E]">LKR {bill.totalAmount.toLocaleString()}</span>
              </div>
            </div>
          </div>
        </div>

        <div className="bg-gray-50 p-6 flex flex-col md:flex-row justify-between items-center gap-4 text-gray-400 print:hidden">
          <p className="text-xs font-medium">Invoice generated on {new Date().toLocaleString()}</p>
          <div className="flex gap-4">
            <p className="text-xs font-medium uppercase tracking-widest">Property of Ocean View Resort</p>
          </div>
        </div>
      </div>

      <style jsx global>{`
        @media print {
          .no-print {
            display: none !important;
          }
          body {
            background-color: white !important;
          }
          main {
            padding: 0 !important;
          }
          .fixed, aside {
            display: none !important;
          }
          .pl-64 {
            padding-left: 0 !important;
          }
        }
      `}</style>
    </div>
  );
}
