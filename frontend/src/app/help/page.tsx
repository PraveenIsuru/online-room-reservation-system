'use client';

import React, { useEffect } from 'react';
import {
  HelpCircle,
  BookOpen,
  Calendar,
  Users,
  Bed,
  CreditCard,
  BarChart,
  ChevronRight,
  Info
} from 'lucide-react';

export default function HelpPage() {
  useEffect(() => {
    document.title = 'Help & User Guide - Ocean View Resort';
  }, []);
  const sections = [
    {
      title: "Getting Started",
      icon: BookOpen,
      content: "Welcome to the Ocean View Resort Management System. This platform allows you to manage reservations, guests, rooms, and billing efficiently. Use the sidebar to navigate between different modules."
    },
    {
      title: "Reservations",
      icon: Calendar,
      items: [
        "Create new reservations by clicking 'New Reservation' in the dashboard or sidebar.",
        "Check-in guests when they arrive to update room status to 'OCCUPIED'.",
        "Check-out guests to automatically generate their final bill."
      ]
    },
    {
      title: "Guest Management",
      icon: Users,
      items: [
        "Maintain a database of all guests and their stay history.",
        "Search for existing guests by contact number during the reservation process.",
        "Update guest profiles and contact information as needed."
      ]
    },
    {
      title: "Room Operations",
      icon: Bed,
      items: [
        "Monitor room availability in real-time.",
        "Update room status to 'MAINTENANCE' if a room is undergoing repairs.",
        "View room details including type, floor, and price per night."
      ]
    },
    {
      title: "Billing & Invoices",
      icon: CreditCard,
      items: [
        "Bills are generated automatically upon guest check-out.",
        "View detailed invoices with a breakdown of room charges.",
        "Mark bills as paid once payment is received via Cash, Card, or other methods.",
        "Print invoices for guests using the print layout."
      ]
    },
    {
      title: "Reports (Admin Only)",
      icon: BarChart,
      items: [
        "Access the Revenue Report to see financial performance over any period.",
        "Analyze revenue trends by room type.",
        "Monitor dashboard KPIs for daily operations overview."
      ]
    }
  ];

  return (
    <div className="space-y-4 md:space-y-6 p-4 md:p-6">
      <div>
        <h1 className="text-xl md:text-2xl lg:text-3xl font-bold text-gray-800">Help & User Guide</h1>
        <p className="text-xs md:text-sm text-gray-500 mt-1">Everything you need to know about using the management system.</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4 md:gap-6">
        {sections.map((section, idx) => (
          <div key={idx} className="bg-white p-4 md:p-6 rounded-xl shadow-sm border border-gray-100 hover:shadow-md transition-shadow">
            <div className="flex items-center gap-3 mb-3 md:mb-4">
              <div className="p-2 md:p-3 bg-blue-50 rounded-lg text-[#0B3D6E]">
                <section.icon size={18} className="md:w-5 md:h-5" />
              </div>
              <h3 className="text-base md:text-lg font-bold text-gray-800">{section.title}</h3>
            </div>

            {section.content ? (
              <p className="text-xs md:text-sm text-gray-600 leading-relaxed">{section.content}</p>
            ) : (
              <ul className="space-y-2">
                {section.items?.map((item, i) => (
                  <li key={i} className="flex items-start gap-2 text-xs md:text-sm text-gray-600">
                    <ChevronRight size={14} className="mt-0.5 md:mt-1 text-blue-500 flex-shrink-0" />
                    <span>{item}</span>
                  </li>
                ))}
              </ul>
            )}
          </div>
        ))}
      </div>

      <div className="bg-[#0B3D6E] text-white p-6 md:p-8 rounded-xl shadow-sm border border-gray-100 flex flex-col md:flex-row items-center gap-4 md:gap-6">
        <div className="p-3 md:p-4 bg-white/10 rounded-full">
          <Info size={24} className="md:w-8 md:h-8" />
        </div>
        <div className="flex-1 text-center md:text-left">
          <h3 className="text-lg md:text-xl font-bold mb-1">Need Technical Support?</h3>
          <p className="text-blue-100 text-xs md:text-sm">If you encounter any issues or have suggestions for improvement, please contact the IT department or your system administrator.</p>
        </div>
        <button className="px-4 md:px-6 py-2 md:py-3 bg-white text-[#0B3D6E] font-medium text-sm md:text-base rounded-lg hover:bg-blue-50 transition-colors whitespace-nowrap">
          Contact IT
        </button>
      </div>
    </div>
  );
}
