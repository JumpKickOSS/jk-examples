import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { Reservation } from '../api/models';
import { shortDate } from './dates';
import { MoneyPipe } from './money.pipe';

/** A reservation at a glance: code, hotel, dates, room, guests, total and status. */
@Component({
  selector: 'kv-reservation-card',
  imports: [MatIconModule, MoneyPipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    @let r = reservation();
    <div class="card res" [class.cancelled]="r.status === 'CANCELLED'">
      <div class="code"><span class="muted">Confirmation</span><strong data-testid="confirmation-code">{{ r.confirmationCode }}</strong></div>
      <div class="status" [attr.data-status]="r.status" data-testid="status">{{ label[r.status] }}</div>
      <h3>{{ r.hotelName }}</h3>
      <dl>
        <div><dt>Dates</dt><dd>{{ day(r.checkIn) }} – {{ day(r.checkOut) }} ({{ r.nights }} nights)</dd></div>
        <div><dt>Room</dt><dd>{{ r.roomTypeName }} · room {{ r.roomNumber }}</dd></div>
        <div><dt>Guests</dt><dd>{{ r.guestName }} · {{ r.adults }} adult{{ r.adults === 1 ? '' : 's' }}{{ r.children ? ', ' + r.children + ' children' : '' }}</dd></div>
        <div><dt>Total</dt><dd>{{ r.total | money }}</dd></div>
        @if (r.specialRequests) { <div><dt>Requests</dt><dd>{{ r.specialRequests }}</dd></div> }
      </dl>
    </div>
  `,
  styles: `
    .res { padding: 1.5rem; text-align: left; width: 100%; box-sizing: border-box; display: grid; gap: 0.25rem; position: relative; }
    .code { display: flex; flex-direction: column; }
    .code strong { font-family: 'Fraunces Variable', serif; font-size: 1.8rem; letter-spacing: 0.06em; color: var(--mat-sys-primary); }
    .status { position: absolute; top: 1.25rem; right: 1.25rem; padding: 0.25rem 0.75rem; border-radius: 999px; font: var(--mat-sys-label-large); background: var(--mat-sys-primary-container); color: var(--mat-sys-on-primary-container); }
    .status[data-status='CANCELLED'] { background: var(--mat-sys-error-container); color: var(--mat-sys-on-error-container); }
    dl { display: grid; gap: 0.4rem; margin: 0.5rem 0 0; }
    dl div { display: grid; grid-template-columns: 7rem 1fr; }
    dt { color: var(--mat-sys-on-surface-variant); }
    dd { margin: 0; }
    .cancelled h3, .cancelled dl { opacity: 0.6; }
  `,
})
export class ReservationCard {
  readonly reservation = input.required<Reservation>();
  protected readonly label: Record<Reservation['status'], string> = {
    BOOKED: 'Booked',
    CHECKED_IN: 'In house',
    CHECKED_OUT: 'Checked out',
    CANCELLED: 'Cancelled',
  };

  protected day(iso: string): string {
    return shortDate(iso);
  }
}
