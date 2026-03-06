'use client';

import React, { createContext, useContext, useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { AuthUser } from '../types';
import { api } from '../lib/api';

interface AuthContextType {
  user: AuthUser | null;
  loading: boolean;
  login: (userData: AuthUser) => void;
  logout: () => void;
  isAuthenticated: boolean;
  isAdmin: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<AuthUser | null>(null);
  const [loading, setLoading] = useState(true);
  const router = useRouter();

  useEffect(() => {
    const storedUser = localStorage.getItem('ovr_user');
    const token = localStorage.getItem('ovr_token');

    if (storedUser && token) {
      try {
        const parsedUser = JSON.parse(storedUser);
        // Basic check for expiration if stored in token
        setUser(parsedUser);
      } catch (e) {
        localStorage.removeItem('ovr_user');
        localStorage.removeItem('ovr_token');
      }
    }
    setLoading(false);
  }, []);

  const login = (userData: AuthUser) => {
    localStorage.setItem('ovr_token', userData.token);
    localStorage.setItem('ovr_user', JSON.stringify(userData));
    setUser(userData);
  };

  const logout = async () => {
    try {
      await api.auth.logout();
    } catch (e) {
      console.error('Logout error:', e);
    } finally {
      localStorage.removeItem('ovr_token');
      localStorage.removeItem('ovr_user');
      setUser(null);
      router.push('/login');
    }
  };

  const value = {
    user,
    loading,
    login,
    logout,
    isAuthenticated: !!user,
    isAdmin: user?.role === 'ADMIN',
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
