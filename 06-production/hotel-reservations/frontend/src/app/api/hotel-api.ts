import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import {
  Availability,
  BookingRequest,
  DeskDay,
  DeskReservation,
  HotelDetail,
  HotelSummary,
  Lookup,
  OccupancyPoint,
  Problem,
  Reservation,
  SearchResult,
  StaffUser,
} from './models';

/** Every call the app makes. Writes carry the XSRF-TOKEN cookie back as a header (HttpClient does it). */
@Injectable({ providedIn: 'root' })
export class HotelApi {
  private readonly http = inject(HttpClient);

  hotels(): Observable<HotelSummary[]> {
    return this.http.get<HotelSummary[]>('/api/hotels');
  }

  hotel(slug: string): Observable<HotelDetail> {
    return this.http.get<HotelDetail>(`/api/hotels/${encodeURIComponent(slug)}`);
  }

  search(checkIn: string, checkOut: string, guests: number): Observable<SearchResult[]> {
    return this.http.get<SearchResult[]>('/api/search', { params: { checkIn, checkOut, guests } });
  }

  availability(slug: string, checkIn: string, checkOut: string, guests: number): Observable<Availability> {
    return this.http.get<Availability>(`/api/hotels/${encodeURIComponent(slug)}/availability`, {
      params: { checkIn, checkOut, guests },
    });
  }

  book(request: BookingRequest): Observable<Reservation> {
    return this.http.post<Reservation>('/api/reservations', request);
  }

  find(lookup: Lookup): Observable<Reservation> {
    return this.http.post<Reservation>('/api/reservations/lookup', lookup);
  }

  cancel(lookup: Lookup): Observable<Reservation> {
    return this.http.post<Reservation>('/api/reservations/cancel', lookup);
  }

  login(username: string, password: string): Observable<void> {
    const body = new HttpParams().set('username', username).set('password', password);
    return this.http.post<void>('/api/auth/login', body);
  }

  logout(): Observable<void> {
    return this.http.post<void>('/api/auth/logout', null);
  }

  me(): Observable<StaffUser> {
    return this.http.get<StaffUser>('/api/desk/me');
  }

  deskDay(slug: string, date: string): Observable<DeskDay> {
    return this.http.get<DeskDay>(`/api/desk/${encodeURIComponent(slug)}/day`, { params: { date } });
  }

  occupancy(slug: string, from: string, to: string): Observable<OccupancyPoint[]> {
    return this.http.get<OccupancyPoint[]>(`/api/desk/${encodeURIComponent(slug)}/occupancy`, { params: { from, to } });
  }

  checkIn(code: string): Observable<DeskReservation> {
    return this.http.post<DeskReservation>(`/api/desk/reservations/${encodeURIComponent(code)}/check-in`, null);
  }

  checkOut(code: string): Observable<DeskReservation> {
    return this.http.post<DeskReservation>(`/api/desk/reservations/${encodeURIComponent(code)}/check-out`, null);
  }
}

/** The sentence to show for a failed call: the problem's detail, else a generic line. */
export function problemMessage(error: unknown): string {
  if (error instanceof HttpErrorResponse) {
    const problem = error.error as Problem | null;
    if (problem?.detail) return problem.detail;
    if (error.status === 0) return 'The server could not be reached. Check your connection and try again.';
  }
  return 'Something went wrong. Please try again.';
}
