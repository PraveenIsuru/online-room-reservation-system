'use client';

import React, { useState } from 'react';
import { useRouter } from 'next/navigation';
import { api } from '@/lib/api';
import { RoomType } from '@/types';
import { ChevronRight, Home, Calendar, User, CheckCircle2 } from 'lucide-react';
import { cn } from '@/lib/utils';
import Step1DateRoom from '@/components/reservations/Step1DateRoom';
import Step2GuestDetails from '@/components/reservations/Step2GuestDetails';
import Step3Review from '@/components/reservations/Step3Review';

const STEPS = [
  { id: 1, title: 'Dates & Room', icon: Calendar },
  { id: 2, title: 'Guest Details', icon: User },
  { id: 3, title: 'Review & Confirm', icon: CheckCircle2 },
];

export default function NewReservationPage() {
  const router = useRouter();
  const [currentStep, setCurrentStep] = useState(1);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [formData, setFormData] = useState({
    // Step 1
    checkInDate: '',
    checkOutDate: '',
    roomId: null as number | null,
    roomType: '' as RoomType | '',
    // Step 2
    guestId: null as number | null,
    guestName: '',
    contactNumber: '',
    email: '',
    address: '',
    // Step 3
    specialRequests: '',
  });

  const updateFormData = (newData: Partial<typeof formData>) => {
    setFormData((prev) => ({ ...prev, ...newData }));
  };

  const handleNext = () => setCurrentStep((prev) => Math.min(prev + 1, 3));
  const handleBack = () => setCurrentStep((prev) => Math.max(prev - 1, 1));

  const handleConfirm = async () => {
    setLoading(true);
    setError(null);
    try {
      let guestId = formData.guestId;

      // 1. Create guest if it's a new one
      if (!guestId) {
        const guestRes = await api.guests.create({
          guestName: formData.guestName,
          contactNumber: formData.contactNumber,
          email: formData.email || null,
          address: formData.address || null,
        });
        guestId = guestRes.data.guestId;
      }

      // 2. Create reservation
      if (!formData.roomId) throw new Error('Room not selected');
      
      await api.reservations.create({
        guestId: guestId,
        roomId: formData.roomId,
        checkInDate: formData.checkInDate,
        checkOutDate: formData.checkOutDate,
        specialRequests: formData.specialRequests || undefined,
      });

      // 3. Success - redirect to dashboard (or reservations list if it existed)
      router.push('/dashboard?success=reservation_created');
    } catch (err: any) {
      setError(err.message || 'An error occurred while creating the reservation.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-5xl mx-auto py-6 px-4">
      <div className="mb-8">
        <h1 className="text-2xl font-bold text-gray-900">New Reservation Wizard</h1>
        <p className="text-gray-600">Complete the 3-step process to book a room.</p>
      </div>

      {/* Stepper */}
      <div className="flex items-center justify-between mb-8 bg-white p-4 rounded-lg shadow-sm border">
        {STEPS.map((step, idx) => {
          const Icon = step.icon;
          const isActive = currentStep === step.id;
          const isCompleted = currentStep > step.id;

          return (
            <React.Fragment key={step.id}>
              <div className="flex flex-col items-center flex-1">
                <div
                  className={cn(
                    'w-10 h-10 rounded-full flex items-center justify-center mb-2 transition-colors',
                    isActive ? 'bg-blue-600 text-white shadow-md' : 
                    isCompleted ? 'bg-green-100 text-green-600' : 'bg-gray-100 text-gray-400'
                  )}
                >
                  {isCompleted ? <CheckCircle2 className="w-6 h-6" /> : <Icon className="w-5 h-5" />}
                </div>
                <span className={cn(
                  'text-xs font-medium uppercase tracking-wider',
                  isActive ? 'text-blue-600' : isCompleted ? 'text-green-600' : 'text-gray-400'
                )}>
                  {step.title}
                </span>
              </div>
              {idx < STEPS.length - 1 && (
                <div className="w-12 h-px bg-gray-200 mt-[-20px]" />
              )}
            </React.Fragment>
          );
        })}
      </div>

      {error && (
        <div className="mb-6 p-4 bg-red-50 border-l-4 border-red-500 text-red-700">
          <p className="font-bold">Error</p>
          <p>{error}</p>
        </div>
      )}

      {/* Step Content */}
      <div className="bg-white rounded-xl shadow-sm border p-6 min-h-[400px]">
        {currentStep === 1 && (
          <Step1DateRoom 
            data={formData} 
            onUpdate={updateFormData} 
            onNext={handleNext} 
          />
        )}
        {currentStep === 2 && (
          <Step2GuestDetails 
            data={formData} 
            onUpdate={updateFormData} 
            onNext={handleNext} 
            onBack={handleBack} 
          />
        )}
        {currentStep === 3 && (
          <Step3Review 
            data={formData} 
            onUpdate={updateFormData} 
            onConfirm={handleConfirm} 
            onBack={handleBack}
            loading={loading}
          />
        )}
      </div>
    </div>
  );
}
