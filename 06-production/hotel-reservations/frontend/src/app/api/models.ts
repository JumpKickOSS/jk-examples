/** The API's JSON shapes; they mirror backend/contract one for one. Amounts are minor units. */

export interface Money {
  minor: number;
  currency: string;
}

export interface HotelSummary {
  slug: string;
  name: string;
  city: string;
  country: string;
  stars: number;
  tagline: string;
  heroImage: string;
  fromPrice: Money;
  amenities: string[];
}

export interface RoomType {
  code: string;
  name: string;
  description: string;
  maxGuests: number;
  beds: string;
  sizeSqm: number;
  image: string;
  baseRate: Money;
  features: string[];
}

export interface HotelDetail extends Omit<HotelSummary, 'fromPrice'> {
  description: string;
  gallery: string[];
  checkInTime: string;
  checkOutTime: string;
  roomTypes: RoomType[];
}

export interface NightlyPrice {
  night: string;
  price: Money;
  note: string;
}

export interface Offer {
  roomType: RoomType;
  roomsLeft: number;
  nights: NightlyPrice[];
  subtotal: Money;
  discount: Money;
  taxes: Money;
  total: Money;
}

export interface Availability {
  hotelSlug: string;
  checkIn: string;
  checkOut: string;
  guests: number;
  offers: Offer[];
}

export interface SearchResult {
  hotel: HotelSummary;
  lowestTotal: Money;
  nights: number;
}

export interface GuestDetails {
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
}

export interface BookingRequest {
  hotelSlug: string;
  roomTypeCode: string;
  checkIn: string;
  checkOut: string;
  adults: number;
  children: number;
  guest: GuestDetails;
  specialRequests: string;
}

export interface Reservation {
  confirmationCode: string;
  status: 'BOOKED' | 'CHECKED_IN' | 'CHECKED_OUT' | 'CANCELLED';
  hotelSlug: string;
  hotelName: string;
  roomTypeName: string;
  roomNumber: string;
  checkIn: string;
  checkOut: string;
  nights: number;
  adults: number;
  children: number;
  guestName: string;
  guestEmail: string;
  total: Money;
  specialRequests: string;
  cancellable: boolean;
  bookedAt: string;
}

export interface Lookup {
  confirmationCode: string;
  lastName: string;
}

export interface DeskReservation {
  confirmationCode: string;
  status: Reservation['status'];
  guestName: string;
  roomNumber: string;
  roomTypeName: string;
  checkIn: string;
  checkOut: string;
  guests: number;
}

export interface DeskDay {
  hotelSlug: string;
  hotelName: string;
  date: string;
  rooms: number;
  occupancyPercent: number;
  arrivals: DeskReservation[];
  departures: DeskReservation[];
  inHouse: DeskReservation[];
}

export interface OccupancyPoint {
  night: string;
  occupied: number;
  rooms: number;
}

export interface StaffUser {
  username: string;
  displayName: string;
  hotels: string[];
}

/** RFC 9457 problem details, as every API error is returned. */
export interface Problem {
  type?: string;
  title?: string;
  status?: number;
  detail?: string;
}
