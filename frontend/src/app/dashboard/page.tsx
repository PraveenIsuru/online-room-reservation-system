'use client';

import React, { useEffect, useState } from 'react';
import { api } from '../lib/api';
import { DashboardStats } from '../types';
import { 
  Users, 
  DoorOpen, 
  LogOut, 
  TrendingUp, 
  Calendar, 
  Loader2,
  AlertCircle,
  CheckCircle
} from 'lucide-react';
import { useSearchParams } from 'next/navigation';

export default function DashboardPage() {
  const [stats, setStats] = useState<DashboardStats | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const searchParams = useSearchParams();
  const successMsg = searchParams.get('success');

  useEffect(() => {
    async function fetchStats() {
      try {
        const response = await api.reports.dashboard();
        setStats(response.data);
      } catch (err: any) {
        console.error('Failed to fetch dashboard stats:', err);
        setError('Failed to load dashboard statistics.');
      } finally {
        setLoading(false);
      }
    }
    fetchStats();
  }, []);

  if (loading) {
    return (
      <div className="flex flex-col items-center justify-center min-h-[60vh]">
        <Loader2 className="h-10 w-10 animate-spin text-[#0B3D6E]" />
        <p className="mt-4 text-gray-600 font-medium">Loading statistics...</p>
      </div>
    );
  }

  if (error || !stats) {
    return (
      <div className="bg-red-50 border border-red-200 rounded-xl p-8 flex flex-col items-center text-center">
        <AlertCircle className="h-12 w-12 text-red-500 mb-4" />
        <h3 className="text-lg font-bold text-red-800">Error Loading Data</h3>
        <p className="text-red-600">{error || 'Something went wrong while fetching data.'}</p>
        <button 
          onClick={() => window.location.reload()}
          className="mt-6 px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors"
        >
          Try Again
        </button>
      </div>
    );
  }

  const kpis = [
    {
      title: "Today's Check-ins",
      value: stats.todayCheckIns,
      icon: DoorOpen,
      color: "bg-blue-500",
      textColor: "text-blue-600",
      bgColor: "bg-blue-50"
    },
    {
      title: "Today's Check-outs",
      value: stats.todayCheckOuts,
      icon: LogOut,
      color: "bg-orange-500",
      textColor: "text-orange-600",
      bgColor: "bg-orange-50"
    },
    {
      title: "Current Occupancy",
      value: `${stats.currentOccupancy}/${stats.totalRooms}`,
      icon: Users,
      color: "bg-green-500",
      textColor: "text-green-600",
      bgColor: "bg-green-50"
    },
    {
      title: "Monthly Revenue",
      value: `LKR ${stats.monthlyRevenue.toLocaleString()}`,
      icon: TrendingUp,
      color: "bg-purple-500",
      textColor: "text-purple-600",
      bgColor: "bg-purple-50"
    }
  ];

  return (
    <div className="space-y-8">
      <div>
        <h1 className="text-2xl font-bold text-gray-800">Dashboard Overview</h1>
        <p className="text-gray-500">Welcome to Ocean View Resort management portal.</p>
      </div>

      {successMsg === 'reservation_created' && (
        <div className="bg-green-50 border-l-4 border-green-500 p-4 rounded shadow-sm flex items-center gap-3">
          <CheckCircle className="text-green-600 h-6 w-6" />
          <span className="text-green-800 font-medium">Reservation has been successfully created!</span>
        </div>
      )}

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {kpis.map((kpi, index) => (
          <div key={index} className="bg-white rounded-xl shadow-sm border border-gray-100 p-6 flex items-center space-x-4">
            <div className={`p-3 rounded-lg ${kpi.bgColor}`}>
              <kpi.icon className={`h-6 w-6 ${kpi.textColor}`} />
            </div>
            <div>
              <p className="text-sm font-medium text-gray-500">{kpi.title}</p>
              <p className="text-2xl font-bold text-gray-900">{kpi.value}</p>
            </div>
          </div>
        ))}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <div className="lg:col-span-2 bg-white rounded-xl shadow-sm border border-gray-100 p-6">
          <div className="flex items-center justify-between mb-6">
            <h2 className="text-lg font-bold text-gray-800">Current Occupancy</h2>
            <span className="text-sm text-gray-500 flex items-center">
              <Calendar className="h-4 w-4 mr-1" />
              {new Date().toLocaleDateString('en-US', { month: 'long', day: 'numeric', year: 'numeric' })}
            </span>
          </div>
          
          <div className="relative pt-1">
            <div className="flex mb-2 items-center justify-between">
              <div>
                <span className="text-xs font-semibold inline-block py-1 px-2 uppercase rounded-full text-green-600 bg-green-200">
                  Occupancy Rate
                </span>
              </div>
              <div className="text-right">
                <span className="text-xs font-semibold inline-block text-green-600">
                  {Math.round((stats.currentOccupancy / stats.totalRooms) * 100)}%
                </span>
              </div>
            </div>
            <div className="overflow-hidden h-2 mb-4 text-xs flex rounded bg-green-100">
              <div 
                style={{ width: `${(stats.currentOccupancy / stats.totalRooms) * 100}%` }}
                className="shadow-none flex flex-col text-center whitespace-nowrap text-white justify-center bg-green-500 transition-all duration-500"
              ></div>
            </div>
          </div>

          <div className="mt-8 grid grid-cols-2 gap-4">
            <div className="p-4 rounded-lg bg-gray-50 border border-gray-100">
              <p className="text-sm text-gray-500 mb-1">Total Guests This Month</p>
              <p className="text-xl font-bold text-gray-800">{stats.totalGuestsThisMonth}</p>
            </div>
            <div className="p-4 rounded-lg bg-gray-50 border border-gray-100">
              <p className="text-sm text-gray-500 mb-1">Available Rooms</p>
              <p className="text-xl font-bold text-gray-800">{stats.totalRooms - stats.currentOccupancy}</p>
            </div>
          </div>
        </div>

        <div className="bg-[#0B3D6E] rounded-xl shadow-lg p-6 text-white flex flex-col justify-between">
          <div>
            <h2 className="text-lg font-bold mb-4">Quick Actions</h2>
            <div className="space-y-3">
              <a href="/dashboard/reservations/new" className="block w-full py-3 px-4 bg-white/10 hover:bg-white/20 rounded-lg text-center font-medium transition-colors border border-white/10">
                New Reservation
              </a>
              <a href="/dashboard/guests" className="block w-full py-3 px-4 bg-white/10 hover:bg-white/20 rounded-lg text-center font-medium transition-colors border border-white/10">
                Register New Guest
              </a>
              <a href="/dashboard/rooms" className="block w-full py-3 px-4 bg-white/10 hover:bg-white/20 rounded-lg text-center font-medium transition-colors border border-white/10">
                Check Room Status
              </a>
            </div>
          </div>
          <div className="mt-8 pt-6 border-t border-white/10">
            <p className="text-xs text-white/60 italic">
              "Providing excellence in hospitality since 2024."
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}
