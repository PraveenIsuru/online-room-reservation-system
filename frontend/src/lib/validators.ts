import { z } from 'zod';

const sriLankaPhone = /^(07\d{8}|\+947\d{8})$/;

export const guestSchema = z.object({
  guestName: z
    .string()
    .min(2, 'Name must be at least 2 characters')
    .max(100, 'Name must be under 100 characters')
    .regex(/^[a-zA-Z\s.\-']+$/, 'Name contains invalid characters'),
  contactNumber: z
    .string()
    .regex(sriLankaPhone, 'Enter a valid Sri Lankan contact number (e.g. 0771234567)'),
  email: z
    .string()
    .email('Enter a valid email address')
    .optional()
    .or(z.literal('')),
  address: z.string().optional(),
});

export const reservationStep1Schema = z.object({
  checkInDate: z
    .string()
    .min(1, 'Check-in date is required')
    .refine((d) => new Date(d) >= new Date(new Date().setHours(0,0,0,0)), {
      message: 'Check-in date cannot be in the past',
    }),
  checkOutDate: z.string().min(1, 'Check-out date is required'),
  roomId: z.number({ required_error: 'Please select a room' }).positive(),
}).refine(
  (data) => new Date(data.checkOutDate) > new Date(data.checkInDate),
  { message: 'Check-out must be after check-in', path: ['checkOutDate'] }
);

export const loginSchema = z.object({
  username: z.string().min(1, 'Username is required'),
  password: z.string().min(1, 'Password is required'),
});

export const paymentSchema = z.object({
  paymentMethod: z.enum(['CASH', 'CARD', 'BANK_TRANSFER', 'ONLINE'], {
    required_error: 'Please select a payment method',
  }),
});
