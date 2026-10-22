import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { MatButtonModule } from '@angular/material/button';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTableModule } from '@angular/material/table';
import { Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import { HotelApi, problemMessage } from '../api/hotel-api';
import { DeskReservation } from '../api/models';
import { addDays, isoDate, shortDate } from '../shared/dates';
import { OccupancyChart } from './occupancy-chart';
import { StaffSession } from './staff-session';

type Tab = 'arrivals' | 'inHouse' | 'departures';

/** The front desk's day for one hotel: tonight's numbers, who arrives and leaves, the next weeks. */
@Component({
  selector: 'kv-desk',
  imports: [
    MatButtonModule, MatButtonToggleModule, MatIconModule, MatProgressBarModule, MatSelectModule, MatSnackBarModule,
    MatTableModule, OccupancyChart,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="page">
      <header class="head">
        <div>
          <span class="eyebrow">Front desk · {{ session.user()?.displayName }}</span>
          <h1>{{ day.value()?.day?.hotelName ?? 'Today' }}</h1>
          <p class="muted">{{ dateLabel() }}</p>
        </div>
        <div class="controls">
          <mat-select [value]="hotel()" (valueChange)="hotel.set($event)" class="hotel-select" aria-label="Hotel" data-testid="desk-hotel">
            @for (slug of session.user()?.hotels ?? []; track slug) { <mat-option [value]="slug">{{ slug }}</mat-option> }
          </mat-select>
          <button mat-icon-button (click)="shift(-1)" aria-label="Previous day"><mat-icon svgIcon="chevron_left" /></button>
          <button mat-stroked-button (click)="date.set(todayIso)">Today</button>
          <button mat-icon-button (click)="shift(1)" aria-label="Next day"><mat-icon svgIcon="chevron_right" /></button>
          <button mat-button (click)="signOut()"><mat-icon svgIcon="logout" />Sign out</button>
        </div>
      </header>

      @if (day.isLoading()) { <mat-progress-bar mode="indeterminate" /> }
      @if (day.error()) { <p class="error">{{ message(day.error()) }}</p> }

      @if (day.value(); as d) {
        <section class="kpis">
          <div class="card kpi"><span class="muted">Occupancy tonight</span><strong data-testid="kpi-occupancy">{{ d.day.occupancyPercent }}%</strong><span class="muted">of {{ d.day.rooms }} rooms</span></div>
          <div class="card kpi"><span class="muted">Arrivals</span><strong>{{ d.day.arrivals.length }}</strong><span class="muted">{{ pending(d.day.arrivals) }} still to check in</span></div>
          <div class="card kpi"><span class="muted">In house</span><strong>{{ d.day.inHouse.length }}</strong><span class="muted">guests checked in</span></div>
          <div class="card kpi"><span class="muted">Departures</span><strong>{{ d.day.departures.length }}</strong><span class="muted">{{ pendingOut(d.day.departures) }} still to check out</span></div>
        </section>

        <section class="card chart">
          <h3>Next four weeks</h3>
          <kv-occupancy-chart [points]="d.occupancy" [today]="date()" />
        </section>

        <section class="card lists">
          <mat-button-toggle-group [value]="tab()" (change)="tab.set($event.value)" aria-label="List">
            <mat-button-toggle value="arrivals">Arrivals ({{ d.day.arrivals.length }})</mat-button-toggle>
            <mat-button-toggle value="inHouse">In house ({{ d.day.inHouse.length }})</mat-button-toggle>
            <mat-button-toggle value="departures">Departures ({{ d.day.departures.length }})</mat-button-toggle>
          </mat-button-toggle-group>
          <table mat-table [dataSource]="rows()" class="table">
            <ng-container matColumnDef="room"><th mat-header-cell *matHeaderCellDef>Room</th><td mat-cell *matCellDef="let r">{{ r.roomNumber }}</td></ng-container>
            <ng-container matColumnDef="guest"><th mat-header-cell *matHeaderCellDef>Guest</th><td mat-cell *matCellDef="let r">{{ r.guestName }}<div class="muted small">{{ r.confirmationCode }}</div></td></ng-container>
            <ng-container matColumnDef="type"><th mat-header-cell *matHeaderCellDef>Room type</th><td mat-cell *matCellDef="let r">{{ r.roomTypeName }}</td></ng-container>
            <ng-container matColumnDef="stay"><th mat-header-cell *matHeaderCellDef>Stay</th><td mat-cell *matCellDef="let r">{{ short(r.checkIn) }} – {{ short(r.checkOut) }}</td></ng-container>
            <ng-container matColumnDef="guests"><th mat-header-cell *matHeaderCellDef>Guests</th><td mat-cell *matCellDef="let r">{{ r.guests }}</td></ng-container>
            <ng-container matColumnDef="action">
              <th mat-header-cell *matHeaderCellDef></th>
              <td mat-cell *matCellDef="let r" class="act">
                @let row = asRow(r);
                @if (row.status === 'BOOKED' && tab() === 'arrivals') {
                  <button mat-flat-button (click)="checkIn(row)" [attr.data-testid]="'check-in-' + row.confirmationCode">Check in</button>
                } @else if (row.status === 'CHECKED_IN' && tab() !== 'arrivals') {
                  <button mat-stroked-button (click)="checkOut(row)">Check out</button>
                } @else {
                  <span class="pill" [attr.data-status]="row.status">{{ statusLabel[row.status] }}</span>
                }
              </td>
            </ng-container>
            <tr mat-header-row *matHeaderRowDef="columns"></tr>
            <tr mat-row *matRowDef="let row; columns: columns"></tr>
            <tr class="mat-row" *matNoDataRow><td class="mat-cell muted" [attr.colspan]="columns.length">Nobody on this list today.</td></tr>
          </table>
        </section>
      }
    </div>
  `,
  styles: `
    .head { display: flex; justify-content: space-between; align-items: flex-end; gap: 1rem; flex-wrap: wrap; }
    .controls { display: flex; align-items: center; gap: 0.25rem; flex-wrap: wrap; }
    .hotel-select { width: 12rem; margin-right: 0.5rem; padding: 0.5rem 0.75rem; border: 1px solid var(--mat-sys-outline-variant); border-radius: 999px; }
    .kpis { display: grid; grid-template-columns: repeat(4, 1fr); gap: 1rem; margin: 1.5rem 0; }
    .kpi { padding: 1.25rem; display: grid; gap: 0.15rem; }
    .kpi strong { font-family: 'Fraunces Variable', serif; font-size: 2.4rem; color: var(--mat-sys-primary); }
    .chart, .lists { padding: 1.25rem; margin-bottom: 1.5rem; }
    .table { width: 100%; margin-top: 1rem; background: transparent; }
    .small { font-size: 0.8rem; }
    .act { text-align: right; }
    .pill { padding: 0.2rem 0.65rem; border-radius: 999px; background: var(--mat-sys-surface-container-high); font-size: 0.85rem; }
    @media (max-width: 900px) { .kpis { grid-template-columns: repeat(2, 1fr); } }
  `,
})
export class Desk {
  private readonly api = inject(HotelApi);
  private readonly router = inject(Router);
  private readonly snack = inject(MatSnackBar);
  protected readonly session = inject(StaffSession);

  protected readonly todayIso = isoDate(new Date());
  protected readonly date = signal(this.todayIso);
  protected readonly hotel = signal(this.session.user()?.hotels[0] ?? 'harborview');
  protected readonly tab = signal<Tab>('arrivals');
  protected readonly columns = ['room', 'guest', 'type', 'stay', 'guests', 'action'];
  protected readonly statusLabel: Record<DeskReservation['status'], string> = {
    BOOKED: 'Booked', CHECKED_IN: 'In house', CHECKED_OUT: 'Checked out', CANCELLED: 'Cancelled',
  };

  protected readonly day = rxResource({
    params: () => ({ hotel: this.hotel(), date: this.date() }),
    stream: ({ params }) =>
      forkJoin({
        day: this.api.deskDay(params.hotel, params.date),
        occupancy: this.api.occupancy(params.hotel, params.date, isoDate(addDays(new Date(params.date + 'T00:00'), 28))),
      }),
  });
  protected readonly rows = computed(() => this.day.value()?.day[this.tab()] ?? []);
  protected readonly dateLabel = computed(() =>
    new Date(this.date() + 'T00:00').toLocaleDateString('en-GB', { weekday: 'long', day: 'numeric', month: 'long', year: 'numeric' }),
  );

  protected shift(days: number): void {
    this.date.set(isoDate(addDays(new Date(this.date() + 'T00:00'), days)));
  }

  protected checkIn(r: DeskReservation): void {
    this.api.checkIn(r.confirmationCode).subscribe({
      next: () => { this.snack.open(`${r.guestName} checked in to ${r.roomNumber}`, undefined, { duration: 3000 }); this.day.reload(); },
      error: (e) => this.snack.open(problemMessage(e), 'Dismiss'),
    });
  }

  protected checkOut(r: DeskReservation): void {
    this.api.checkOut(r.confirmationCode).subscribe({
      next: () => { this.snack.open(`${r.guestName} checked out of ${r.roomNumber}`, undefined, { duration: 3000 }); this.day.reload(); },
      error: (e) => this.snack.open(problemMessage(e), 'Dismiss'),
    });
  }

  protected signOut(): void {
    this.session.signOut().subscribe(() => this.router.navigateByUrl('/'));
  }

  protected pending(list: DeskReservation[]): number {
    return list.filter((r) => r.status === 'BOOKED').length;
  }

  protected pendingOut(list: DeskReservation[]): number {
    return list.filter((r) => r.status === 'CHECKED_IN').length;
  }

  /** mat-table hands rows to the template untyped; this restores the type. */
  protected asRow(row: unknown): DeskReservation {
    return row as DeskReservation;
  }

  protected short(iso: string): string {
    return shortDate(iso);
  }

  protected message(error: unknown): string {
    return problemMessage(error);
  }
}
