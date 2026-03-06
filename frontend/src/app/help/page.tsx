'use client';

import React from 'react';
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
    <div className="max-w-4xl mx-auto space-y-8">
      <div>
        <h1 className="text-2xl font-black text-gray-800 flex items-center gap-2">
          <HelpCircle className="text-[#0B3D6E]" /> Help & User Guide
        </h1>
        <p className="text-gray-500">Everything you need to know about using the management system.</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {sections.map((section, idx) => (
          <div key={idx} className="bg-white p-6 rounded-2xl shadow-sm border border-gray-100 hover:shadow-md transition-shadow">
            <div className="flex items-center gap-3 mb-4">
              <div className="p-2 bg-blue-50 rounded-lg text-[#0B3D6E]">
                <section.icon size={20} />
              </div>
              <h3 className="font-bold text-gray-800">{section.title}</h3>
            </div>
            
            {section.content ? (
              <p className="text-sm text-gray-600 leading-relaxed">{section.content}</p>
            ) : (
              <ul className="space-y-2">
                {section.items?.map((item, i) => (
                  <li key={i} className="flex items-start gap-2 text-sm text-gray-600">
                    <ChevronRight size={14} className="mt-1 text-blue-400 flex-shrink-0" />
                    <span>{item}</span>
                  </li>
                ))}
              </ul>
            )}
          </div>
        ))}
      </div>

      <div className="bg-[#0B3D6E] text-white p-8 rounded-2xl shadow-lg flex flex-col md:flex-row items-center gap-6">
        <div className="p-4 bg-white/10 rounded-full">
          <Info size={32} />
        </div>
        <div>
          <h3 className="text-xl font-bold mb-1">Need Technical Support?</h3>
          <p className="text-blue-100 opacity-80 text-sm">If you encounter any issues or have suggestions for improvement, please contact the IT department or your system administrator.</p>
        </div>
        <button className="md:ml-auto px-6 py-3 bg-white text-[#0B3D6E] font-bold rounded-xl hover:bg-blue-50 transition-colors">
          Contact IT
        </button>
      </div>
    </div>
  );
}
