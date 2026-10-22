import { ChangeDetectionStrategy, Component, computed, inject, input, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSelectModule } from '@angular/material/select';
import { MatStepperModule } from '@angular/material/stepper';
import { RouterLink } from '@angular/router';
import { HotelApi, problemMessage } from '../api/hotel-api';
import { Reservation } from '../api/models';
import { shortDate } from '../shared/dates';
import { MoneyPipe } from '../shared/money.pipe';
import { ReservationCard } from '../shared/reservation-card';

/** Guest details, a review of the price, and the booking — three steps, one request. */
@Component({
  selector: 'kv-book',
  imports: [
    RouterLink, ReactiveFormsModule, MatStepperModule, MatFormFieldModule, MatInputModule, MatSelectModule,
    MatButtonModule, MatIconModule, MatProgressBarModule, MoneyPipe, ReservationCard,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="page narrow">
      @if (booked(); as r) {
        <div class="done">
          <mat-icon class="big" svgIcon="task_alt" />
          <h1>You're booked.</h1>
          <p class="muted">We've sent the details to {{ r.guestEmail }}. Keep your confirmation code to find or change your stay.</p>
          <kv-reservation-card [reservation]="r" />
          <p><a mat-button routerLink="/">Back to our hotels</a></p>
        </div>
      } @else if (offer(); as o) {
        <span class="eyebrow">{{ availability.value()?.hotelSlug }}</span>
        <h1>{{ o.roomType.name }}</h1>
        <p class="muted">{{ day(checkIn()) }} – {{ day(checkOut()) }} · {{ o.nights.length }} nights · {{ o.total | money }} total</p>

        <mat-stepper linear #stepper class="card stepper">
          <ng-template matStepperIcon="edit"><mat-icon svgIcon="edit" /></ng-template>
          <ng-template matStepperIcon="done"><mat-icon svgIcon="check" /></ng-template>
          <mat-step [stepControl]="guestForm" label="Who's staying">
            <form [formGroup]="guestForm" class="grid">
              <mat-form-field appearance="outline"><mat-label>First name</mat-label><input matInput formControlName="firstName" autocomplete="given-name" data-testid="first-name" /></mat-form-field>
              <mat-form-field appearance="outline"><mat-label>Last name</mat-label><input matInput formControlName="lastName" autocomplete="family-name" data-testid="last-name" /></mat-form-field>
              <mat-form-field appearance="outline" class="wide"><mat-label>Email</mat-label><input matInput type="email" formControlName="email" autocomplete="email" data-testid="email" />
                @if (guestForm.controls.email.hasError('email')) { <mat-error>That doesn't look like an email address</mat-error> }
              </mat-form-field>
              <mat-form-field appearance="outline"><mat-label>Phone (optional)</mat-label><input matInput formControlName="phone" autocomplete="tel" /></mat-form-field>
              <mat-form-field appearance="outline"><mat-label>Adults</mat-label>
                <mat-select formControlName="adults">@for (n of range(o.roomType.maxGuests); track n) { <mat-option [value]="n">{{ n }}</mat-option> }</mat-select>
              </mat-form-field>
              <mat-form-field appearance="outline"><mat-label>Children</mat-label>
                <mat-select formControlName="children">@for (n of [0, 1, 2, 3]; track n) { <mat-option [value]="n">{{ n }}</mat-option> }</mat-select>
              </mat-form-field>
              <mat-form-field appearance="outline" class="wide"><mat-label>Anything we should know?</mat-label><textarea matInput formControlName="specialRequests" rows="2" maxlength="500"></textarea></mat-form-field>
              @if (tooMany()) { <p class="error wide">This room sleeps at most {{ o.roomType.maxGuests }}.</p> }
              <div class="wide actions"><button mat-flat-button matStepperNext type="button" [disabled]="guestForm.invalid || tooMany()" data-testid="to-review">Review</button></div>
            </form>
          </mat-step>
          <mat-step label="Review and book">
            <table class="sum">
              @for (n of o.nights; track n.night) { <tr><td>{{ day(n.night) }} <span class="note">{{ n.note }}</span></td><td class="num">{{ n.price | money }}</td></tr> }
              @if (o.discount.minor > 0) { <tr><td>Week-long stay discount</td><td class="num">−{{ o.discount | money }}</td></tr> }
              <tr><td>Taxes</td><td class="num">{{ o.taxes | money }}</td></tr>
              <tr class="total"><td>Total</td><td class="num">{{ o.total | money }}</td></tr>
            </table>
            <p class="muted small">Free cancellation online until the day before check-in. Payment is taken at the hotel.</p>
            @if (error()) { <p class="error">{{ error() }}</p> }
            @if (submitting()) { <mat-progress-bar mode="indeterminate" /> }
            <div class="actions">
              <button mat-button matStepperPrevious type="button">Back</button>
              <button mat-flat-button type="button" (click)="book()" [disabled]="submitting()" data-testid="confirm-booking">Confirm booking</button>
            </div>
          </mat-step>
        </mat-stepper>
      } @else if (availability.isLoading()) {
        <mat-progress-bar mode="indeterminate" />
      } @else {
        <h1>That room is no longer available</h1>
        <p><a [routerLink]="['/hotels', slug()]" [queryParams]="{ checkIn: checkIn(), checkOut: checkOut(), guests: adults() }">See what else is free</a></p>
      }
    </div>
  `,
  styles: `
    .narrow { max-width: 820px; }
    .stepper { margin-top: 1.5rem; padding: 0.5rem; }
    .grid { display: grid; grid-template-columns: 1fr 1fr; gap: 0 1rem; padding-top: 0.5rem; }
    .wide { grid-column: 1 / -1; }
    .actions { display: flex; justify-content: flex-end; gap: 0.5rem; margin-top: 0.5rem; }
    .sum { width: 100%; border-collapse: collapse; margin: 0.5rem 0 1rem; }
    .sum td { padding: 0.35rem 0; border-bottom: 1px solid var(--mat-sys-outline-variant); }
    .total td { font-weight: 700; font-size: 1.15rem; border-bottom: 0; }
    .num { text-align: right; font-variant-numeric: tabular-nums; }
    .note { color: var(--kv-brass); font-size: 0.8rem; margin-left: 0.5rem; }
    .small { font-size: 0.85rem; }
    .done { text-align: center; display: grid; gap: 0.75rem; justify-items: center; }
    .big { font-size: 64px; width: 64px; height: 64px; color: var(--mat-sys-primary); }
    @media (max-width: 640px) { .grid { grid-template-columns: 1fr; } }
  `,
})
export class Book {
  private readonly api = inject(HotelApi);

  readonly slug = input.required<string>();
  readonly roomType = input.required<string>();
  readonly checkIn = input.required<string>();
  readonly checkOut = input.required<string>();
  readonly guests = input('2');
  protected readonly adults = computed(() => Math.max(1, Number(this.guests())));

  protected readonly availability = rxResource({
    params: () => ({ slug: this.slug(), checkIn: this.checkIn(), checkOut: this.checkOut() }),
    stream: ({ params }) => this.api.availability(params.slug, params.checkIn, params.checkOut, 1),
  });
  protected readonly offer = computed(() => this.availability.value()?.offers.find((o) => o.roomType.code === this.roomType()));

  protected readonly guestForm = new FormGroup({
    firstName: new FormControl('', { nonNullable: true, validators: [Validators.required, Validators.maxLength(80)] }),
    lastName: new FormControl('', { nonNullable: true, validators: [Validators.required, Validators.maxLength(80)] }),
    email: new FormControl('', { nonNullable: true, validators: [Validators.required, Validators.email] }),
    phone: new FormControl('', { nonNullable: true, validators: [Validators.pattern(/^[+0-9 ()-]{0,32}$/)] }),
    adults: new FormControl(2, { nonNullable: true }),
    children: new FormControl(0, { nonNullable: true }),
    specialRequests: new FormControl('', { nonNullable: true }),
  });

  protected readonly submitting = signal(false);
  protected readonly error = signal<string | null>(null);
  protected readonly booked = signal<Reservation | null>(null);

  ngOnInit(): void {
    this.guestForm.controls.adults.setValue(this.adults());
  }

  protected tooMany(): boolean {
    const o = this.offer();
    const v = this.guestForm.getRawValue();
    return !!o && v.adults + v.children > o.roomType.maxGuests;
  }

  protected book(): void {
    const v = this.guestForm.getRawValue();
    this.submitting.set(true);
    this.error.set(null);
    this.api
      .book({
        hotelSlug: this.slug(),
        roomTypeCode: this.roomType(),
        checkIn: this.checkIn(),
        checkOut: this.checkOut(),
        adults: v.adults,
        children: v.children,
        guest: { firstName: v.firstName, lastName: v.lastName, email: v.email, phone: v.phone },
        specialRequests: v.specialRequests,
      })
      .subscribe({
        next: (r) => {
          this.booked.set(r);
          this.submitting.set(false);
        },
        error: (e) => {
          this.error.set(problemMessage(e));
          this.submitting.set(false);
        },
      });
  }

  protected range(max: number): number[] {
    return Array.from({ length: max }, (_, i) => i + 1);
  }

  protected day(iso: string): string {
    return shortDate(iso);
  }
}
