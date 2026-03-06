'use client';

import React from 'react';
import { useAuth } from '../context/AuthContext';
import { User } from 'lucide-react';

const Header = () => {
  const { user } = useAuth();

  return (
    <header className="h-16 bg-white border-b border-gray-200 sticky top-0 z-40 px-8 flex items-center justify-between">
      <div className="flex items-center space-x-2">
        <span className="text-gray-500 font-medium">Ocean View Resort</span>
        <span className="text-gray-300">/</span>
        <span className="text-gray-900 font-semibold uppercase tracking-wider text-sm">Hotel Management Portal</span>
      </div>

      <div className="flex items-center space-x-4">
        <div className="text-right">
          <p className="text-sm font-semibold text-gray-900">{user?.username}</p>
          <p className="text-xs font-medium text-gray-500">{user?.role}</p>
        </div>
        <div className="h-10 w-10 rounded-full bg-blue-100 flex items-center justify-center text-[#0B3D6E]">
          <User size={20} />
        </div>
      </div>
    </header>
  );
};

export default Header;
