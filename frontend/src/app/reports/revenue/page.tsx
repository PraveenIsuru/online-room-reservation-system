'use client';

import React, { useState, useEffect } from 'react';
import { api } from '../../../lib/api';
import { useAuth } from '../../../context/AuthContext';
import {
  BarChart3,
  Calendar,
  Download,
  TrendingUp,
  DollarSign,
  ArrowUpRight,
  ArrowDownRight,
  Loader2,
  AlertCircle,
  ChevronRight,
  Filter
} from 'lucide-react';

export default function RevenueReportPage() {
  const { isAdmin } = useAuth();
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<any>(null);
  const [error, setError] = useState<string | null>(null);

  // Date range defaults (current month)
  const today = new Date();
  const firstDay = new Date(today.getFullYear(), today.getMonth(), 1).toISOString().split('T')[0];
  const lastDay = today.toISOString().split('T')[0];

  const [startDate, setStartDate] = useState(firstDay);
  const [endDate, setEndDate] = useState(lastDay);

  useEffect(() => {
    document.title = 'Revenue Report - Ocean View Resort';
  }, []);

  const fetchReport = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await api.reports.revenue({ startDate, endDate });
      setData(response.data);
    } catch (err: any) {
      setError(err.message || 'Failed to load revenue report');
    } finally {
      setLoading(false);
    }
  };

  React.useEffect(() => {
    if (isAdmin) {
      fetchReport();
    }
  }, [isAdmin]);

  if (!isAdmin) {
    return (
      <div className="bg-orange-50 border border-orange-200 rounded-xl p-8 flex flex-col items-center text-center">
        <AlertCircle className="h-12 w-12 text-orange-500 mb-4" />
        <h3 className="text-lg font-bold text-orange-800">Access Denied</h3>
        <p className="text-orange-600">You do not have permission to view this report. Admin access only.</p>
      </div>
    );
  }

  return (
    <div className="space-y-4 md:space-y-6 p-4 md:p-6">
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
        <div>
          <h1 className="text-xl md:text-2xl lg:text-3xl font-bold text-gray-800">Revenue Report</h1>
          <p className="text-xs md:text-sm text-gray-500 mt-1">Analyze your resort's financial performance.</p>
        </div>


        <div className="flex flex-wrap items-center gap-3 bg-white p-4 rounded-xl border border-gray-100 shadow-sm">
          <Filter size={18} className="text-gray-400" />
          <input
            type="date"
            value={startDate}
            onChange={(e) => setStartDate(e.target.value)}
            className="border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
          <span className="text-gray-400">to</span>
          <input
            type="date"
            value={endDate}
            onChange={(e) => setEndDate(e.target.value)}
            className="border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
          <button
            onClick={fetchReport}
            disabled={loading}
            className="px-4 py-2 bg-[#0B3D6E] text-white text-sm font-medium rounded-lg hover:bg-[#0B3D6E]/90 transition-colors disabled:opacity-50"
          >
            Generate Report
          </button>
        </div>
      </div>

      {loading ? (
        <div className="flex flex-col items-center justify-center min-h-[40vh]">
          <Loader2 className="h-10 w-10 animate-spin text-[#0B3D6E]" />
          <p className="mt-4 text-gray-600 font-medium">Generating report...</p>
        </div>
      ) : error ? (
        <div className="bg-red-50 border border-red-200 rounded-xl p-8 flex flex-col items-center text-center">
          <AlertCircle className="h-12 w-12 text-red-500 mb-4" />
          <p className="text-red-600 font-medium">{error}</p>
        </div>
      ) : data ? (
        <>
          {/* Summary Cards */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4 md:gap-6">
            <div className="bg-white p-4 md:p-6 rounded-xl shadow-sm border border-gray-100">
              <div className="flex justify-between items-start mb-3 md:mb-4">
                <div className="p-2 md:p-3 bg-green-50 rounded-lg text-green-600">
                  <DollarSign size={20} className="md:w-6 md:h-6" />
                </div>
              </div>
              <p className="text-xs md:text-sm font-medium text-gray-500">Total Revenue</p>
              <h3 className="text-2xl md:text-3xl font-bold text-gray-900 mt-1">LKR {data.totalRevenue?.toLocaleString() || 0}</h3>
            </div>

            <div className="bg-white p-4 md:p-6 rounded-xl shadow-sm border border-gray-100">
              <div className="flex justify-between items-start mb-3 md:mb-4">
                <div className="p-2 md:p-3 bg-blue-50 rounded-lg text-blue-600">
                  <Calendar size={20} className="md:w-6 md:h-6" />
                </div>
              </div>
              <p className="text-xs md:text-sm font-medium text-gray-500">Paid Invoices</p>
              <h3 className="text-2xl md:text-3xl font-bold text-gray-900 mt-1">{data.paidBillsCount || 0}</h3>
              <p className="text-xs text-gray-500 mt-2">From {data.totalBillsCount || 0} total generated</p>
            </div>

            <div className="bg-white p-4 md:p-6 rounded-xl shadow-sm border border-gray-100">
              <div className="flex justify-between items-start mb-3 md:mb-4">
                <div className="p-2 md:p-3 bg-purple-50 rounded-lg text-purple-600">
                  <TrendingUp size={20} className="md:w-6 md:h-6" />
                </div>
              </div>
              <p className="text-xs md:text-sm font-medium text-gray-500">Average Booking Value</p>
              <h3 className="text-2xl md:text-3xl font-bold text-gray-900 mt-1">
                LKR {(data.paidBillsCount > 0 ? Math.round(data.totalRevenue / data.paidBillsCount) : 0).toLocaleString()}
              </h3>
            </div>
          </div>

          <div className="grid grid-cols-1 lg:grid-cols-2 gap-4 md:gap-6">
            {/* Revenue by Room Type */}
            <div className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
              <div className="p-4 md:p-6 border-b border-gray-100">
                <h3 className="text-base md:text-lg font-bold text-gray-800">Revenue by Room Type</h3>
              </div>
              <div className="p-4 md:p-6">
                <div className="space-y-4">
                  {Object.entries(data.revenueByRoomType || {}).map(([type, amount]: [any, any]) => (
                    <div key={type}>
                      <div className="flex justify-between items-center mb-2">
                        <span className="text-xs md:text-sm font-medium text-gray-700">{type.replace('_', ' ')}</span>
                        <span className="text-xs md:text-sm font-bold text-gray-900">LKR {amount.toLocaleString()}</span>
                      </div>
                      <div className="w-full bg-gray-100 rounded-full h-2">
                        <div
                          className="bg-[#0B3D6E] h-2 rounded-full"
                          style={{ width: `${(amount / data.totalRevenue) * 100}%` }}
                        ></div>
                      </div>
                    </div>
                  ))}
                  {(!data.revenueByRoomType || Object.keys(data.revenueByRoomType).length === 0) && (
                    <p className="text-center py-8 text-sm text-gray-500">No data available for this period</p>
                  )}
                </div>
              </div>
            </div>

            {/* Top Customers or other data if available */}
            <div className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
               <div className="p-4 md:p-6 border-b border-gray-100">
                <h3 className="text-base md:text-lg font-bold text-gray-800">Recent Transactions</h3>
              </div>
              <div className="overflow-x-auto">
                <table className="w-full text-left min-w-[400px]">
                  <thead className="bg-gray-50 border-b border-gray-100">
                    <tr>
                      <th className="px-3 md:px-6 py-3 md:py-4 text-xs font-semibold text-gray-500 uppercase tracking-wider">Guest</th>
                      <th className="px-3 md:px-6 py-3 md:py-4 text-xs font-semibold text-gray-500 uppercase tracking-wider">Date</th>
                      <th className="px-3 md:px-6 py-3 md:py-4 text-xs font-semibold text-gray-500 uppercase tracking-wider text-right">Amount</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-gray-100">
                    {data.recentPayments?.map((payment: any, idx: number) => (
                      <tr key={idx} className="hover:bg-gray-50 transition-colors">
                        <td className="px-3 md:px-6 py-3 md:py-4">
                          <p className="text-xs md:text-sm font-medium text-gray-900">{payment.guestName}</p>
                          <p className="text-xs text-gray-500">#{payment.reservationNumber}</p>
                        </td>
                        <td className="px-3 md:px-6 py-3 md:py-4 text-xs md:text-sm text-gray-600">
                          {new Date(payment.paidDate).toLocaleDateString()}
                        </td>
                        <td className="px-3 md:px-6 py-3 md:py-4 text-right">
                          <span className="text-xs md:text-sm font-medium text-green-600">LKR {payment.amount.toLocaleString()}</span>
                        </td>
                      </tr>
                    ))}
                    {(!data.recentPayments || data.recentPayments.length === 0) && (
                      <tr>
                        <td colSpan={3} className="px-6 py-12 text-center text-sm text-gray-500">No recent transactions</td>
                      </tr>
                    )}
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </>
      ) : (
        <div className="bg-white border-2 border-dashed border-gray-200 rounded-xl p-12 md:p-20 flex flex-col items-center text-center">
          <BarChart3 className="h-12 w-12 md:h-16 md:w-16 text-gray-300 mb-4 md:mb-6" />
          <h3 className="text-lg md:text-xl font-bold text-gray-500">No Data Available</h3>
          <p className="text-sm text-gray-500 max-w-xs mt-2">Choose a date range and click Generate Report to view revenue data.</p>
        </div>
      )}
    </div>
  );
}
