'use client';

import React, { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useAuth } from '../../context/AuthContext';
import { api } from '../../lib/api';
import { loginSchema } from '../../lib/validators';
import { Loader2, Lock, User } from 'lucide-react';

type LoginFormValues = z.infer<typeof loginSchema>;

export default function LoginPage() {
  const [error, setError] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const { login, user } = useAuth();
  const router = useRouter();

  useEffect(() => {
    if (user) {
      router.push('/dashboard');
    }
  }, [user, router]);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginFormValues>({
    resolver: zodResolver(loginSchema),
  });

  const onSubmit = async (data: LoginFormValues) => {
    setIsSubmitting(true);
    setError(null);
    try {
      const response = await api.auth.login(data);
      if (response.data) {
        login(response.data);
        router.push('/dashboard');
      }
    } catch (err: any) {
      console.error('Login error:', err);
      setError(err.message || 'Invalid username or password');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-[#F4F7F6] px-4">
      <div className="max-w-md w-full space-y-8 bg-white p-10 rounded-xl shadow-lg border border-gray-100">
        <div className="text-center">
          <div className="mx-auto h-16 w-16 bg-[#0B3D6E] rounded-full flex items-center justify-center mb-4">
            <span className="text-white text-2xl font-bold italic">OV</span>
          </div>
          <h2 className="text-3xl font-extrabold text-gray-900">
            Ocean View Resort
          </h2>
          <p className="mt-2 text-sm text-gray-600 font-medium">
            Management System Login
          </p>
        </div>

        <form className="mt-8 space-y-6" onSubmit={handleSubmit(onSubmit)}>
          {error && (
            <div className="bg-red-50 border-l-4 border-red-500 p-4 mb-4">
              <p className="text-sm text-red-700 font-medium">{error}</p>
            </div>
          )}

          <div className="rounded-md shadow-sm space-y-4">
            <div>
              <label htmlFor="username" className="block text-sm font-semibold text-gray-700 mb-1">
                Username
              </label>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <User className="h-5 w-5 text-gray-400" />
                </div>
                <input
                  {...register('username')}
                  type="text"
                  className={`appearance-none block w-full pl-10 pr-3 py-3 border ${
                    errors.username ? 'border-red-500' : 'border-gray-300'
                  } rounded-lg placeholder-gray-400 text-gray-900 focus:outline-none focus:ring-2 focus:ring-[#0B3D6E] focus:border-transparent transition-all sm:text-sm`}
                  placeholder="Enter your username"
                />
              </div>
              {errors.username && (
                <p className="mt-1 text-xs text-red-600 font-medium">{errors.username.message}</p>
              )}
            </div>

            <div>
              <label htmlFor="password" className="block text-sm font-semibold text-gray-700 mb-1">
                Password
              </label>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <Lock className="h-5 w-5 text-gray-400" />
                </div>
                <input
                  {...register('password')}
                  type="password"
                  className={`appearance-none block w-full pl-10 pr-3 py-3 border ${
                    errors.password ? 'border-red-500' : 'border-gray-300'
                  } rounded-lg placeholder-gray-400 text-gray-900 focus:outline-none focus:ring-2 focus:ring-[#0B3D6E] focus:border-transparent transition-all sm:text-sm`}
                  placeholder="Enter your password"
                />
              </div>
              {errors.password && (
                <p className="mt-1 text-xs text-red-600 font-medium">{errors.password.message}</p>
              )}
            </div>
          </div>

          <div>
            <button
              type="submit"
              disabled={isSubmitting}
              className="group relative w-full flex justify-center py-3 px-4 border border-transparent text-sm font-bold rounded-lg text-white bg-[#0B3D6E] hover:bg-[#082d52] focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#0B3D6E] transition-all disabled:opacity-70 disabled:cursor-not-allowed"
            >
              {isSubmitting ? (
                <Loader2 className="animate-spin h-5 w-5" />
              ) : (
                'Sign In'
              )}
            </button>
          </div>
        </form>

        <div className="text-center">
          <p className="text-xs text-gray-500 italic">
            &copy; {new Date().getFullYear()} Ocean View Resort Management.
          </p>
        </div>
      </div>
    </div>
  );
}
