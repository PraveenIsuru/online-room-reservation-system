'use client';

import React from 'react';
import { useAuth } from '../context/AuthContext';
import { User } from 'lucide-react';

const Header = () => {
  const { user } = useAuth();

  return (
    <header className="h-14 md:h-16 bg-white border-b border-gray-200 sticky top-0 z-40 px-4 md:px-8 flex items-center justify-between">
      <div className="flex items-center space-x-2 min-w-0">
        <span className="text-gray-500 font-medium text-sm md:text-base truncate">Ocean View Resort</span>
        <span className="text-gray-300 hidden sm:inline">/</span>
        <span className="text-gray-900 font-semibold uppercase tracking-wider text-xs md:text-sm hidden sm:inline truncate">Hotel Management Portal</span>
      </div>

      <div className="flex items-center space-x-2 md:space-x-4 shrink-0">
        <div className="text-right hidden sm:block">
          <p className="text-xs md:text-sm font-semibold text-gray-900">{user?.username}</p>
          <p className="text-xs font-medium text-gray-500">{user?.role}</p>
        </div>
        <div className="h-8 w-8 md:h-10 md:w-10 rounded-full bg-blue-100 flex items-center justify-center text-[#0B3D6E]">
          <User size={16} className="md:w-5 md:h-5" />
        </div>
      </div>
    </header>
  );
};

export default Header;
