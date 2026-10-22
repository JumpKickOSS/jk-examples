import { ChangeDetectionStrategy, Component, computed, effect, inject, input, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { MatButtonModule } from '@angular/material/button';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { Title } from '@angular/platform-browser';
import { Router, RouterLink } from '@angular/router';
import { HotelApi, problemMessage } from '../api/hotel-api';
import { amenityIcon } from '../shared/amenity-icon';
import { addDays, isoDate, parseIso, shortDate } from '../shared/dates';
import { MoneyPipe } from '../shared/money.pipe';
import { SearchForm, StayQuery } from '../shared/search-form';

/** One hotel: the place, then what it can offer for the chosen dates. */
@Component({
  selector: 'kv-hotel',
  imports: [RouterLink, MatButtonModule, MatIconModule, MatExpansionModule, MatProgressBarModule, MoneyPipe, SearchForm],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    @if (hotel.value(); as h) {
      <section class="gallery">
        <img class="main" [src]="selected() ?? h.heroImage" [alt]="h.name" />
        <div class="thumbs">
          @for (img of [h.heroImage].concat(h.gallery); track img) {
            <button type="button" (click)="selected.set(img)" [class.on]="(selected() ?? h.heroImage) === img" [attr.aria-label]="'Show photo ' + ($index + 1)">
              <img [src]="img" alt="" loading="lazy" />
            </button>
          }
        </div>
      </section>

      <div class="page layout">
        <article>
          <span class="eyebrow">{{ h.city }}, {{ h.country }}</span>
          <h1>{{ h.name }}</h1>
          <p class="stars" [attr.aria-label]="h.stars + ' stars'">{{ '★'.repeat(h.stars) }}</p>
          <p class="lede">{{ h.description }}</p>
          <div class="chips">
            @for (a of h.amenities; track a) { <span class="chip"><mat-icon [svgIcon]="icon(a)" />{{ a }}</span> }
          </div>
          <p class="muted times"><mat-icon svgIcon="schedule" />Check-in from {{ h.checkInTime.slice(0, 5) }} · check-out by {{ h.checkOutTime.slice(0, 5) }}</p>
        </article>

        <aside class="card dates">
          <h3>Your stay</h3>
          <kv-search-form [initial]="query()" label="Check availability" (searched)="choose($event)" />
        </aside>
      </div>

      <section class="page">
        <h2>Rooms for {{ stay() }}</h2>
        @if (availability.isLoading()) { <mat-progress-bar mode="indeterminate" /> }
        @if (availability.error()) { <p class="error">{{ message(availability.error()) }}</p> }
        <div class="offers">
          @for (o of availability.value()?.offers ?? []; track o.roomType.code) {
            <div class="offer card" [attr.data-testid]="'offer-' + o.roomType.code">
              <img [src]="o.roomType.image" [alt]="o.roomType.name" loading="lazy" />
              <div class="body">
                <h3>{{ o.roomType.name }}</h3>
                <p class="muted">{{ o.roomType.description }}</p>
                <div class="facts muted">
                  <span><mat-icon svgIcon="bed" />{{ o.roomType.beds }}</span>
                  <span><mat-icon svgIcon="group" />Up to {{ o.roomType.maxGuests }}</span>
                  <span><mat-icon svgIcon="square_foot" />{{ o.roomType.sizeSqm }} m²</span>
                </div>
                <div class="chips small">
                  @for (f of o.roomType.features; track f) { <span class="chip">{{ f }}</span> }
                </div>
                <mat-expansion-panel class="nights">
                  <mat-expansion-panel-header>Price per night</mat-expansion-panel-header>
                  <table>
                    @for (n of o.nights; track n.night) {
                      <tr><td>{{ day(n.night) }}</td><td class="note">{{ n.note }}</td><td class="num">{{ n.price | money }}</td></tr>
                    }
                    @if (o.discount.minor > 0) { <tr class="disc"><td>Week-long stay</td><td></td><td class="num">−{{ o.discount | money }}</td></tr> }
                    <tr><td>Taxes</td><td></td><td class="num">{{ o.taxes | money }}</td></tr>
                  </table>
                </mat-expansion-panel>
              </div>
              <div class="price">
                @if (o.roomsLeft <= 2) { <span class="scarce">Only {{ o.roomsLeft }} left</span> }
                <strong>{{ o.total | money }}</strong>
                <span class="muted small">total for {{ o.nights.length }} nights, taxes included</span>
                <a mat-flat-button [routerLink]="['/book', h.slug, o.roomType.code]" [queryParams]="query()" data-testid="choose-room">Choose</a>
              </div>
            </div>
          } @empty {
            @if (!availability.isLoading() && !availability.error()) {
              <p class="muted">Nothing is free for those dates and guests. Try other dates or another of our hotels.</p>
            }
          }
        </div>
      </section>
    } @else if (hotel.error()) {
      <div class="page"><h1>Hotel not found</h1><p><a routerLink="/">See all our hotels</a></p></div>
    } @else {
      <mat-progress-bar mode="indeterminate" />
    }
  `,
  styles: `
    .gallery { max-width: 1180px; margin: 1.5rem auto 0; padding: 0 1.5rem; display: grid; grid-template-columns: 1fr 160px; gap: 0.75rem; }
    .gallery .main { width: 100%; aspect-ratio: 16 / 8; object-fit: cover; border-radius: var(--kv-radius); }
    .thumbs { display: grid; gap: 0.75rem; align-content: start; }
    .thumbs button { padding: 0; border: 2px solid transparent; border-radius: 12px; overflow: hidden; cursor: pointer; background: none; }
    .thumbs button.on { border-color: var(--kv-brass); }
    .thumbs img { width: 100%; aspect-ratio: 4 / 3; object-fit: cover; }
    .layout { display: grid; grid-template-columns: 1fr 380px; gap: 2rem; align-items: start; }
    .lede { font-size: 1.1rem; line-height: 1.6; }
    .times { display: flex; gap: 0.4rem; align-items: center; margin-top: 1rem; }
    .dates { padding: 1.25rem 1.25rem 0.5rem; position: sticky; top: 88px; }
    .dates ::ng-deep .search { grid-template-columns: 1fr !important; }
    .offers { display: grid; gap: 1rem; margin-top: 1rem; }
    .offer { display: grid; grid-template-columns: 240px 1fr 220px; }
    .offer img { width: 100%; height: 100%; object-fit: cover; min-height: 200px; }
    .body { padding: 1.25rem; }
    .facts { display: flex; flex-wrap: wrap; gap: 1rem; margin: 0.5rem 0; }
    .facts span { display: inline-flex; gap: 0.3rem; align-items: center; }
    .facts mat-icon { font-size: 18px; width: 18px; height: 18px; }
    .chips.small .chip { font-size: 0.8rem; padding: 0.15rem 0.6rem; }
    .nights { margin-top: 0.75rem; box-shadow: none !important; background: var(--mat-sys-surface-container-low); }
    table { width: 100%; border-collapse: collapse; }
    td { padding: 0.2rem 0; }
    .num { text-align: right; font-variant-numeric: tabular-nums; }
    .note { color: var(--kv-brass); font-size: 0.85rem; }
    .disc { color: var(--mat-sys-primary); }
    .price { padding: 1.25rem; display: flex; flex-direction: column; align-items: flex-end; justify-content: center; gap: 0.35rem; border-left: 1px solid var(--mat-sys-outline-variant); text-align: right; }
    .price strong { font-family: 'Fraunces Variable', serif; font-size: 1.7rem; }
    .small { font-size: 0.8rem; }
    .scarce { color: var(--mat-sys-error); font-weight: 600; font-size: 0.85rem; }
    @media (max-width: 900px) {
      .layout, .offer, .gallery { grid-template-columns: 1fr; }
      .thumbs { grid-template-columns: repeat(3, 1fr); }
      .price { border-left: 0; align-items: flex-start; text-align: left; }
    }
  `,
})
export class HotelPage {
  private readonly api = inject(HotelApi);
  private readonly router = inject(Router);
  private readonly title = inject(Title);

  readonly slug = input.required<string>();
  readonly checkIn = input<string>();
  readonly checkOut = input<string>();
  readonly guests = input('2');

  protected readonly selected = signal<string | null>(null);
  protected readonly query = computed<StayQuery>(() => {
    const inDate = this.checkIn() ?? defaultIn();
    return { checkIn: inDate, checkOut: this.checkOut() ?? plusDays(inDate, 3), guests: Number(this.guests()) };
  });
  protected readonly stay = computed(() => `${shortDate(this.query().checkIn)} – ${shortDate(this.query().checkOut)}`);

  protected readonly hotel = rxResource({ params: () => this.slug(), stream: ({ params }) => this.api.hotel(params) });
  protected readonly availability = rxResource({
    params: () => ({ slug: this.slug(), ...this.query() }),
    stream: ({ params }) => this.api.availability(params.slug, params.checkIn, params.checkOut, params.guests),
  });

  constructor() {
    effect(() => {
      const h = this.hotel.value();
      if (h) this.title.setTitle(`${h.name} · Kestrel & Vine`);
    });
  }

  protected choose(q: StayQuery): void {
    this.router.navigate(['/hotels', this.slug()], { queryParams: q });
  }

  protected icon(name: string): string {
    return amenityIcon(name);
  }

  protected day(iso: string): string {
    return shortDate(iso);
  }

  protected message(error: unknown): string {
    return problemMessage(error);
  }
}

function defaultIn(): string {
  return isoDate(addDays(new Date(), 14));
}

function plusDays(iso: string, days: number): string {
  return isoDate(addDays(parseIso(iso), days));
}
