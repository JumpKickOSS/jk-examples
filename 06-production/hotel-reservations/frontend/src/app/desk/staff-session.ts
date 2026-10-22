import { Injectable, inject, signal } from '@angular/core';
import { Observable, catchError, of, switchMap, tap } from 'rxjs';
import { HotelApi } from '../api/hotel-api';
import { StaffUser } from '../api/models';

/** Who is signed in at the front desk, asked of the server once and remembered for the session. */
@Injectable({ providedIn: 'root' })
export class StaffSession {
  private readonly api = inject(HotelApi);
  readonly user = signal<StaffUser | null>(null);

  /** The signed-in user, or null when the session cookie is missing or expired. */
  current(): Observable<StaffUser | null> {
    if (this.user()) return of(this.user());
    return this.api.me().pipe(
      tap((u) => this.user.set(u)),
      catchError(() => of(null)),
    );
  }

  /** Signs in, then loads the user; errors (a 401 for wrong credentials, a 429) pass through. */
  signIn(username: string, password: string): Observable<StaffUser> {
    this.user.set(null);
    return this.api.login(username, password).pipe(
      switchMap(() => this.api.me()),
      tap((u) => this.user.set(u)),
    );
  }

  signOut(): Observable<void> {
    return this.api.logout().pipe(tap(() => this.user.set(null)));
  }
}
