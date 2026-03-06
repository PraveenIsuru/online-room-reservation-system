'use client';

import React, { useState } from 'react';
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
  LogOut,
  Menu,
  X
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { cn } from '../lib/utils';

const Sidebar = () => {
  const pathname = usePathname();
  const { isAdmin, logout } = useAuth();
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  const navItems = [
    { label: 'Dashboard', icon: Home, href: '/dashboard' },
    { label: 'Reservations', icon: Calendar, href: '/reservations' },
    { label: 'Guests', icon: Users, href: '/guests' },
    { label: 'Rooms', icon: Bed, href: '/rooms' },
    { label: 'Billing', icon: CreditCard, href: '/billing' },
    { label: 'Reports', icon: BarChart, href: '/reports/revenue', adminOnly: true },
    { label: 'Help', icon: HelpCircle, href: '/help' },
  ];

  const filteredNavItems = navItems.filter(item => !item.adminOnly || isAdmin);

  return (
    <>
      {/* Mobile Menu Button */}
      <button
        onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
        className="fixed top-4 left-4 z-50 lg:hidden p-2 bg-[#0B3D6E] text-white rounded-lg shadow-lg"
      >
        {mobileMenuOpen ? <X size={20} /> : <Menu size={20} />}
      </button>

      {/* Mobile Overlay */}
      {mobileMenuOpen && (
        <div
          className="fixed inset-0 bg-black/50 z-40 lg:hidden"
          onClick={() => setMobileMenuOpen(false)}
        />
      )}

      {/* Sidebar */}
      <aside className={cn(
        "fixed left-0 top-0 h-screen w-64 bg-[#0B3D6E] text-white flex flex-col z-50 transition-transform duration-300",
        mobileMenuOpen ? "translate-x-0" : "-translate-x-full lg:translate-x-0"
      )}>
        <div className="p-4 md:p-6">
          <h1 className="text-lg md:text-xl font-bold">Ocean View Resort</h1>
          <p className="text-xs text-blue-300">Management System</p>
        </div>

        <nav className="flex-1 px-3 md:px-4 py-4 space-y-1 overflow-y-auto">
          {filteredNavItems.map((item) => {
            const isActive = pathname.startsWith(item.href);
            return (
              <Link
                key={item.href}
                href={item.href}
                onClick={() => setMobileMenuOpen(false)}
                className={cn(
                  "flex items-center space-x-3 px-3 py-2.5 md:py-3 rounded-lg transition-colors",
                  isActive
                    ? "bg-white/10 text-white"
                    : "text-blue-100 hover:bg-white/5 hover:text-white"
                )}
              >
                <item.icon size={18} className="md:w-5 md:h-5 shrink-0" />
                <span className="font-medium text-sm md:text-base">{item.label}</span>
              </Link>
            );
          })}
        </nav>

        <div className="p-3 md:p-4 border-t border-white/10">
          <button
            onClick={() => {
              logout();
              setMobileMenuOpen(false);
            }}
            className="flex items-center space-x-3 w-full px-3 py-2.5 md:py-3 text-blue-100 hover:bg-white/5 hover:text-white rounded-lg transition-colors"
          >
            <LogOut size={18} className="md:w-5 md:h-5 shrink-0" />
            <span className="font-medium text-sm md:text-base">Logout</span>
          </button>
        </div>
      </aside>
    </>
  );
};

export default Sidebar;
