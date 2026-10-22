import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { HotelApi, problemMessage } from '../api/hotel-api';
import { Lookup, Reservation } from '../api/models';
import { ReservationCard } from '../shared/reservation-card';

/** A guest finds a reservation with its code and their last name, and may cancel it. */
@Component({
  selector: 'kv-find-reservation',
  imports: [ReactiveFormsModule, MatFormFieldModule, MatInputModule, MatButtonModule, MatIconModule, ReservationCard],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="page narrow">
      <span class="eyebrow">Manage your stay</span>
      <h1>Find your reservation</h1>
      <form [formGroup]="form" (ngSubmit)="find()" class="card lookup">
        <mat-form-field appearance="outline"><mat-label>Confirmation code</mat-label><input matInput formControlName="confirmationCode" placeholder="KV-7H3QXM" data-testid="lookup-code" /></mat-form-field>
        <mat-form-field appearance="outline"><mat-label>Last name</mat-label><input matInput formControlName="lastName" autocomplete="family-name" data-testid="lookup-last-name" /></mat-form-field>
        <button mat-flat-button type="submit" [disabled]="form.invalid" data-testid="lookup">Find</button>
      </form>
      @if (error()) { <p class="error">{{ error() }}</p> }
      @if (reservation(); as r) {
        <kv-reservation-card [reservation]="r" />
        @if (r.cancellable) {
          <div class="cancel">
            @if (confirming()) {
              <span>Cancel this stay? This can't be undone.</span>
              <button mat-button type="button" (click)="confirming.set(false)">Keep it</button>
              <button mat-flat-button type="button" class="danger" (click)="cancel()" data-testid="confirm-cancel">Cancel reservation</button>
            } @else {
              <button mat-stroked-button type="button" (click)="confirming.set(true)" data-testid="cancel">Cancel reservation</button>
            }
          </div>
        }
      }
    </div>
  `,
  styles: `
    .narrow { max-width: 760px; }
    .lookup { padding: 1.25rem 1.25rem 0.25rem; display: grid; grid-template-columns: 1fr 1fr auto; gap: 0.75rem; margin: 1rem 0 1.5rem; align-items: start; }
    .lookup button { height: 56px; }
    .cancel { display: flex; gap: 0.75rem; align-items: center; justify-content: flex-end; margin-top: 1rem; flex-wrap: wrap; }
    .danger { --mat-button-filled-container-color: var(--mat-sys-error); --mat-button-filled-label-text-color: var(--mat-sys-on-error); }
    @media (max-width: 640px) { .lookup { grid-template-columns: 1fr; } }
  `,
})
export class FindReservation {
  private readonly api = inject(HotelApi);

  protected readonly form = new FormGroup({
    confirmationCode: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    lastName: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
  });
  protected readonly reservation = signal<Reservation | null>(null);
  protected readonly error = signal<string | null>(null);
  protected readonly confirming = signal(false);

  protected find(): void {
    this.call(this.api.find(this.lookup()));
  }

  protected cancel(): void {
    this.confirming.set(false);
    this.call(this.api.cancel(this.lookup()));
  }

  private lookup(): Lookup {
    const v = this.form.getRawValue();
    return { confirmationCode: v.confirmationCode.trim().toUpperCase(), lastName: v.lastName.trim() };
  }

  private call(request: ReturnType<HotelApi['find']>): void {
    this.error.set(null);
    request.subscribe({
      next: (r) => this.reservation.set(r),
      error: (e) => {
        this.reservation.set(null);
        this.error.set(problemMessage(e));
      },
    });
  }
}
