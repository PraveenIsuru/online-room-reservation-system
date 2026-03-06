import { AuthUser, Guest, Room, Reservation, Bill, DashboardStats, ReservationStatus, RoomStatus, PaymentMethod } from '../types';

const BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080/backend/api';

function getHeaders(): HeadersInit {
  if (typeof window === 'undefined') return { 'Content-Type': 'application/json' };
  const token = localStorage.getItem('ovr_token');
  return {
    'Content-Type': 'application/json',
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
  };
}

async function request<T>(
  path: string,
  options: RequestInit = {}
): Promise<T> {
  const res = await fetch(`${BASE_URL}${path}`, {
    ...options,
    headers: { ...getHeaders(), ...options.headers },
  });

  let json;
  try {
    json = await res.json();
  } catch (e) {
    json = { success: false, message: 'Invalid response from server' };
  }

  if (!res.ok) {
    throw json;
  }

  return json;
}

export const api = {
  auth: {
    login: (body: { username: string; password: string }) =>
      request<{ data: AuthUser }>('/auth/login', { method: 'POST', body: JSON.stringify(body) }),
    logout: () =>
      request('/auth/logout', { method: 'POST' }),
  },
  guests: {
    list: (params?: Record<string, string>) =>
      request<{ data: Guest[]; total: number }>(`/guests?${new URLSearchParams(params)}`),
    get: (id: number) =>
      request<{ data: Guest }>(`/guests/${id}`),
    search: (contact: string) =>
      request<{ data: Guest }>(`/guests/search?contact=${contact}`),
    create: (body: Partial<Guest>) =>
      request<{ data: Guest }>('/guests', { method: 'POST', body: JSON.stringify(body) }),
  },
  rooms: {
    available: (params: { checkIn: string; checkOut: string; type?: string }) => {
        const searchParams = new URLSearchParams();
        searchParams.append('checkIn', params.checkIn);
        searchParams.append('checkOut', params.checkOut);
        if (params.type) searchParams.append('type', params.type);
        return request<{ data: Room[]; nights: number }>(`/rooms/available?${searchParams.toString()}`);
    },
    list: () => request<{ data: Room[] }>('/rooms'),
    updateStatus: (roomId: number, status: RoomStatus) =>
      request(`/rooms/${roomId}/status`, { method: 'PATCH', body: JSON.stringify({ status }) }),
  },
  reservations: {
    list: (params?: Record<string, string>) =>
      request<{ data: Reservation[]; total: number }>(`/reservations?${new URLSearchParams(params)}`),
    get: (id: number) =>
      request<{ data: Reservation }>(`/reservations/${id}`),
    getByNumber: (number: string) =>
      request<{ data: Reservation }>(`/reservations/number/${number}`),
    create: (body: { guestId: number; roomId: number; checkInDate: string; checkOutDate: string; specialRequests?: string }) =>
      request<{ data: Reservation }>('/reservations', { method: 'POST', body: JSON.stringify(body) }),
    updateStatus: (id: number, status: ReservationStatus) =>
      request(`/reservations/${id}/status`, { method: 'PATCH', body: JSON.stringify({ status }) }),
  },
  bills: {
    getByReservation: (reservationId: number) =>
      request<{ data: Bill }>(`/bills/reservation/${reservationId}`),
    pay: (billId: number, paymentMethod: PaymentMethod) =>
      request<{ data: Bill }>(`/bills/${billId}/pay`, { method: 'PATCH', body: JSON.stringify({ paymentMethod }) }),
  },
  reports: {
    dashboard: () => request<{ data: DashboardStats }>('/reports/dashboard'),
    revenue: (params: { startDate: string; endDate: string }) =>
      request<{ data: any }>(`/reports/revenue?${new URLSearchParams(params)}`),
  },
};
