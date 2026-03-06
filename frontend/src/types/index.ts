export type UserRole = 'ADMIN' | 'STAFF';

export type RoomType = 'STANDARD' | 'DELUXE' | 'SUITE' | 'OCEAN_VIEW' | 'PENTHOUSE';
export type RoomStatus = 'AVAILABLE' | 'OCCUPIED' | 'MAINTENANCE' | 'RESERVED';
export type ReservationStatus = 'CONFIRMED' | 'CHECKED_IN' | 'CHECKED_OUT' | 'CANCELLED';
export type BillStatus = 'PENDING' | 'PAID' | 'PARTIALLY_PAID' | 'CANCELLED';
export type PaymentMethod = 'CASH' | 'CARD' | 'BANK_TRANSFER' | 'ONLINE';

export interface AuthUser {
  userId: number;
  username: string;
  role: UserRole;
  token: string;
  expiresAt: string;
}

export interface Guest {
  guestId: number;
  guestName: string;
  address: string | null;
  contactNumber: string;
  email: string | null;
  createdAt: string;
  reservationCount?: number;
}

export interface Room {
  roomId: number;
  roomNumber: string;
  roomType: RoomType;
  roomTypeDisplay: string;
  pricePerNight: number;
  status: RoomStatus;
  floorNumber: number;
  description: string | null;
  estimatedTotal?: number; // only in availability response
}

export interface Bill {
  billId: number;
  reservationId: number;
  reservationNumber: string;
  guestName: string;
  contactNumber: string;
  roomNumber: string;
  roomType: RoomType;
  checkInDate: string;
  checkOutDate: string;
  nights: number;
  pricePerNight: number;
  roomCharges: number;
  totalAmount: number;
  status: BillStatus;
  paymentMethod: PaymentMethod | null;
  issuedDate: string;
  paidDate: string | null;
}

export interface Reservation {
  reservationId: number;
  reservationNumber: string;
  guestId: number;
  guestName: string;
  contactNumber: string;
  roomId: number;
  roomNumber: string;
  roomType: RoomType;
  pricePerNight: number;
  checkInDate: string;
  checkOutDate: string;
  nights: number;
  status: ReservationStatus;
  specialRequests: string | null;
  billId: number | null;
  totalAmount: number | null;
  billStatus: BillStatus | null;
  createdAt: string;
  createdByUsername: string;
}

export interface DashboardStats {
  todayCheckIns: number;
  todayCheckOuts: number;
  currentOccupancy: number;
  totalRooms: number;
  monthlyRevenue: number;
  totalGuestsThisMonth: number;
}

export interface ApiError {
  success: false;
  error: string;
  message: string;
  errors?: { field: string; message: string }[];
}
