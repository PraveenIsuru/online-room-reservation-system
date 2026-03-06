'use client';

import React from 'react';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { 
  Home, 
  Calendar, 
  Users, 
  Bed, 
  CreditCard, 
  BarChart, 
  HelpCircle,
  LogOut
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { cn } from '../lib/utils';

const Sidebar = () => {
  const pathname = usePathname();
  const { isAdmin, logout } = useAuth();

  const navItems = [
    { label: 'Dashboard', icon: Home, href: '/dashboard' },
    { label: 'Reservations', icon: Calendar, href: '/reservations' },
    { label: 'Guests', icon: Users, href: '/guests' },
    { label: 'Rooms', icon: Bed, href: '/rooms' },
    { label: 'Billing', icon: CreditCard, href: '/billing' },
    { label: 'Reports', icon: BarChart, href: '/reports', adminOnly: true },
    { label: 'Help', icon: HelpCircle, href: '/help' },
  ];

  const filteredNavItems = navItems.filter(item => !item.adminOnly || isAdmin);

  return (
    <aside className="fixed left-0 top-0 h-screen w-64 bg-[#0B3D6E] text-white flex flex-col z-50">
      <div className="p-6">
        <h1 className="text-xl font-bold">Ocean View Resort</h1>
        <p className="text-xs text-blue-300">Management System</p>
      </div>

      <nav className="flex-1 px-4 py-4 space-y-1">
        {filteredNavItems.map((item) => {
          const isActive = pathname.startsWith(item.href);
          return (
            <Link
              key={item.href}
              href={item.href}
              className={cn(
                "flex items-center space-x-3 px-3 py-3 rounded-lg transition-colors",
                isActive 
                  ? "bg-white/10 text-white" 
                  : "text-blue-100 hover:bg-white/5 hover:text-white"
              )}
            >
              <item.icon size={20} />
              <span className="font-medium">{item.label}</span>
            </Link>
          );
        })}
      </nav>

      <div className="p-4 border-t border-white/10">
        <button
          onClick={logout}
          className="flex items-center space-x-3 w-full px-3 py-3 text-blue-100 hover:bg-white/5 hover:text-white rounded-lg transition-colors"
        >
          <LogOut size={20} />
          <span className="font-medium">Logout</span>
        </button>
      </div>
    </aside>
  );
};

export default Sidebar;
