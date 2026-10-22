import { ChangeDetectionStrategy, Component, OnInit, input, output } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { addDays, isoDate, parseIso } from './dates';

/** What a search asks for. */
export interface StayQuery {
  checkIn: string;
  checkOut: string;
  guests: number;
}

/** Dates and guests, the same form on the home page, the results and a hotel's page. */
@Component({
  selector: 'kv-search-form',
  imports: [ReactiveFormsModule, MatFormFieldModule, MatInputModule, MatDatepickerModule, MatSelectModule, MatButtonModule, MatIconModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <form [formGroup]="form" (ngSubmit)="submit()" class="search" [class.compact]="compact()">
      <mat-form-field appearance="outline" class="dates">
        <mat-label>Check-in — check-out</mat-label>
        <mat-date-range-input [rangePicker]="picker" [min]="today">
          <input matStartDate formControlName="checkIn" placeholder="Check-in" data-testid="check-in" />
          <input matEndDate formControlName="checkOut" placeholder="Check-out" data-testid="check-out" />
        </mat-date-range-input>
        <mat-datepicker-toggle matIconSuffix [for]="picker" />
        <mat-date-range-picker #picker />
      </mat-form-field>
      <mat-form-field appearance="outline" class="guests">
        <mat-label>Guests</mat-label>
        <mat-select formControlName="guests" data-testid="guests">
          @for (n of guestOptions; track n) {
            <mat-option [value]="n">{{ n }} {{ n === 1 ? 'guest' : 'guests' }}</mat-option>
          }
        </mat-select>
      </mat-form-field>
      <button mat-flat-button type="submit" class="go" [disabled]="form.invalid" data-testid="search">
        <mat-icon svgIcon="search" /> {{ label() }}
      </button>
    </form>
  `,
  styles: `
    .search { display: grid; grid-template-columns: minmax(16rem, 2fr) minmax(8rem, 1fr) auto; gap: 0.75rem; align-items: start; }
    .go { height: 56px; padding: 0 1.75rem; }
    mat-form-field { width: 100%; }
    @media (max-width: 720px) { .search { grid-template-columns: 1fr; } .go { width: 100%; } }
  `,
})
export class SearchForm implements OnInit {
  readonly initial = input<StayQuery | null>(null);
  readonly compact = input(false);
  readonly label = input('Search');
  readonly searched = output<StayQuery>();

  protected readonly today = new Date();
  protected readonly guestOptions = [1, 2, 3, 4, 5, 6];

  protected readonly form = new FormGroup({
    checkIn: new FormControl<Date | null>(addDays(new Date(), 14), Validators.required),
    checkOut: new FormControl<Date | null>(addDays(new Date(), 17), Validators.required),
    guests: new FormControl(2, { nonNullable: true }),
  });

  ngOnInit(): void {
    const q = this.initial();
    if (q) this.form.setValue({ checkIn: parseIso(q.checkIn), checkOut: parseIso(q.checkOut), guests: q.guests });
  }

  protected submit(): void {
    const { checkIn, checkOut, guests } = this.form.getRawValue();
    if (!checkIn || !checkOut) return;
    this.searched.emit({ checkIn: isoDate(checkIn), checkOut: isoDate(checkOut), guests });
  }
}
