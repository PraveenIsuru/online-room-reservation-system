'use client';

import React, { useState } from 'react';
import { api } from '@/lib/api';
import { Guest } from '@/types';
import { Loader2, Search, Plus, UserCheck } from 'lucide-react';
import { cn } from '@/lib/utils';

interface Step2Props {
  data: {
    guestId: number | null;
    guestName: string;
    contactNumber: string;
    email: string;
    address: string;
  };
  onUpdate: (data: Partial<Step2Props['data']>) => void;
  onNext: () => void;
  onBack: () => void;
}

export default function Step2GuestDetails({ data, onUpdate, onNext, onBack }: Step2Props) {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [searchContact, setSearchContact] = useState('');
  const [foundGuest, setFoundGuest] = useState<Guest | null>(null);
  const [mode, setMode] = useState<'SEARCH' | 'NEW' | 'SELECTED'>(data.guestId ? 'SELECTED' : 'SEARCH');

  const handleSearch = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!searchContact) return;

    setLoading(true);
    setError(null);
    try {
      const res = await api.guests.search(searchContact);
      if (res.data) {
        setFoundGuest(res.data);
      } else {
        setError('No guest found with this contact number.');
        setFoundGuest(null);
      }
    } catch (err: any) {
      setError(err.message || 'Error searching for guest.');
      setFoundGuest(null);
    } finally {
      setLoading(false);
    }
  };

  const selectGuest = (guest: Guest) => {
    onUpdate({
      guestId: guest.guestId,
      guestName: guest.guestName,
      contactNumber: guest.contactNumber,
      email: guest.email || '',
      address: guest.address || '',
    });
    setMode('SELECTED');
  };

  const handleCreateNew = () => {
    onUpdate({
      guestId: null,
      guestName: '',
      contactNumber: '',
      email: '',
      address: '',
    });
    setMode('NEW');
    setError(null);
  };

  const canProceed = mode === 'SELECTED' || (mode === 'NEW' && data.guestName && data.contactNumber);

  return (
    <div className="space-y-6">
      <div className="flex gap-4 border-b pb-4">
        <button
          onClick={() => setMode('SEARCH')}
          className={cn(
            'px-4 py-2 rounded-md transition-colors',
            mode === 'SEARCH' ? 'bg-blue-600 text-white' : 'bg-gray-100 hover:bg-gray-200'
          )}
        >
          Search Existing Guest
        </button>
        <button
          onClick={handleCreateNew}
          className={cn(
            'px-4 py-2 rounded-md transition-colors',
            mode === 'NEW' ? 'bg-blue-600 text-white' : 'bg-gray-100 hover:bg-gray-200'
          )}
        >
          Register New Guest
        </button>
      </div>

      {mode === 'SEARCH' && (
        <div className="space-y-6">
          <form onSubmit={handleSearch} className="flex gap-2">
            <div className="relative flex-1">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 w-5 h-5" />
              <input
                type="text"
                placeholder="Enter contact number..."
                value={searchContact}
                onChange={(e) => setSearchContact(e.target.value)}
                className="w-full pl-10 pr-4 py-2 border rounded-md focus:ring-blue-500 focus:border-blue-500"
              />
            </div>
            <button
              type="submit"
              disabled={loading}
              className="bg-blue-600 text-white px-6 py-2 rounded-md hover:bg-blue-700 disabled:opacity-50"
            >
              {loading ? <Loader2 className="w-5 h-5 animate-spin" /> : 'Search'}
            </button>
          </form>

          {error && <p className="text-red-600 text-sm bg-red-50 p-3 rounded">{error}</p>}

          {foundGuest && (
            <div className="border rounded-lg p-4 bg-green-50 border-green-200 flex justify-between items-center">
              <div>
                <h4 className="font-bold text-gray-900">{foundGuest.guestName}</h4>
                <p className="text-sm text-gray-600">{foundGuest.contactNumber} | {foundGuest.email || 'No email'}</p>
                <p className="text-xs text-gray-500 mt-1">{foundGuest.address || 'No address'}</p>
              </div>
              <button
                onClick={() => selectGuest(foundGuest)}
                className="flex items-center gap-2 bg-green-600 text-white px-4 py-2 rounded-md hover:bg-green-700"
              >
                <UserCheck className="w-5 h-5" />
                Select Guest
              </button>
            </div>
          )}
        </div>
      )}

      {mode === 'NEW' && (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div className="md:col-span-2">
            <label className="block text-sm font-medium text-gray-700 mb-1">Full Name *</label>
            <input
              type="text"
              required
              value={data.guestName}
              onChange={(e) => onUpdate({ guestName: e.target.value })}
              className="w-full p-2 border rounded-md focus:ring-blue-500 focus:border-blue-500"
              placeholder="e.g. John Doe"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Contact Number *</label>
            <input
              type="text"
              required
              value={data.contactNumber}
              onChange={(e) => onUpdate({ contactNumber: e.target.value })}
              className="w-full p-2 border rounded-md focus:ring-blue-500 focus:border-blue-500"
              placeholder="e.g. +1 234 567 890"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Email Address</label>
            <input
              type="email"
              value={data.email}
              onChange={(e) => onUpdate({ email: e.target.value })}
              className="w-full p-2 border rounded-md focus:ring-blue-500 focus:border-blue-500"
              placeholder="e.g. john@example.com"
            />
          </div>
          <div className="md:col-span-2">
            <label className="block text-sm font-medium text-gray-700 mb-1">Residential Address</label>
            <textarea
              value={data.address}
              onChange={(e) => onUpdate({ address: e.target.value })}
              className="w-full p-2 border rounded-md focus:ring-blue-500 focus:border-blue-500"
              rows={2}
              placeholder="Street, City, Country"
            />
          </div>
        </div>
      )}

      {mode === 'SELECTED' && (
        <div className="bg-blue-50 border border-blue-200 rounded-lg p-6">
          <div className="flex justify-between items-start">
            <div className="flex items-center gap-3">
              <div className="bg-blue-600 text-white p-2 rounded-full">
                <UserCheck className="w-6 h-6" />
              </div>
              <div>
                <h4 className="text-xl font-bold text-gray-900">{data.guestName}</h4>
                <p className="text-gray-600">{data.contactNumber}</p>
              </div>
            </div>
            <button
              onClick={() => setMode('SEARCH')}
              className="text-blue-600 text-sm hover:underline font-medium"
            >
              Change Guest
            </button>
          </div>
          <div className="mt-4 grid grid-cols-2 gap-4 text-sm">
            <div>
              <span className="block text-gray-500">Email</span>
              <span className="font-medium">{data.email || 'N/A'}</span>
            </div>
            <div>
              <span className="block text-gray-500">Address</span>
              <span className="font-medium">{data.address || 'N/A'}</span>
            </div>
          </div>
        </div>
      )}

      <div className="flex justify-between pt-4 border-t">
        <button
          onClick={onBack}
          className="px-6 py-2 border rounded-md hover:bg-gray-50"
        >
          Back: Dates & Room
        </button>
        <button
          onClick={onNext}
          disabled={!canProceed}
          className="bg-blue-600 text-white px-6 py-2 rounded-md hover:bg-blue-700 disabled:opacity-50"
        >
          Next: Review
        </button>
      </div>
    </div>
  );
}
