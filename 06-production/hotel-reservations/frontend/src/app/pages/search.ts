import { ChangeDetectionStrategy, Component, computed, inject, input } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { Router, RouterLink } from '@angular/router';
import { HotelApi, problemMessage } from '../api/hotel-api';
import { MoneyPipe } from '../shared/money.pipe';
import { SearchForm, StayQuery } from '../shared/search-form';
import { shortDate } from '../shared/dates';

/** Hotels with a room for the whole stay, cheapest first. */
@Component({
  selector: 'kv-search',
  imports: [RouterLink, MatIconModule, MatProgressBarModule, MoneyPipe, SearchForm],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="page">
      <span class="eyebrow">Available hotels</span>
      <h1>{{ summary() }}</h1>
      <div class="card refine"><kv-search-form [initial]="query()" label="Update" (searched)="search($event)" /></div>

      @if (results.isLoading()) { <mat-progress-bar mode="indeterminate" /> }
      @if (results.error()) { <p class="error">{{ message(results.error()) }}</p> }

      <div class="list">
        @for (r of results.value() ?? []; track r.hotel.slug) {
          <a class="result card" [routerLink]="['/hotels', r.hotel.slug]" [queryParams]="query()" data-testid="result">
            <img [src]="r.hotel.heroImage" [alt]="r.hotel.name" loading="lazy" />
            <div class="body">
              <div class="muted">{{ r.hotel.city }}, {{ r.hotel.country }} · <span class="stars">{{ '★'.repeat(r.hotel.stars) }}</span></div>
              <h2>{{ r.hotel.name }}</h2>
              <p class="muted">{{ r.hotel.tagline }}</p>
            </div>
            <div class="price">
              <span class="muted">{{ r.nights }} {{ r.nights === 1 ? 'night' : 'nights' }} from</span>
              <strong>{{ r.lowestTotal | money }}</strong>
              <span class="muted small">incl. taxes</span>
              <span class="cta">See rooms <mat-icon svgIcon="arrow_forward" /></span>
            </div>
          </a>
        } @empty {
          @if (!results.isLoading() && !results.error()) {
            <p class="muted">No hotel has a room for those dates and guests. Try other dates.</p>
          }
        }
      </div>
    </div>
  `,
  styles: `
    .refine { padding: 1rem 1rem 0; margin: 1rem 0 1.5rem; }
    .list { display: grid; gap: 1rem; margin-top: 1rem; }
    .result { display: grid; grid-template-columns: 260px 1fr auto; text-decoration: none; color: inherit; }
    .result img { width: 100%; height: 100%; object-fit: cover; min-height: 170px; }
    .body { padding: 1.25rem; }
    .price { padding: 1.25rem 1.5rem; display: flex; flex-direction: column; align-items: flex-end; justify-content: center; gap: 0.15rem; border-left: 1px solid var(--mat-sys-outline-variant); }
    .price strong { font: var(--mat-sys-headline-small); font-family: 'Fraunces Variable', serif; }
    .small { font-size: 0.8rem; }
    .cta { display: inline-flex; align-items: center; gap: 0.25rem; color: var(--mat-sys-primary); font-weight: 600; margin-top: 0.5rem; }
    @media (max-width: 760px) { .result { grid-template-columns: 1fr; } .price { border-left: 0; align-items: flex-start; } }
  `,
})
export class Search {
  private readonly api = inject(HotelApi);
  private readonly router = inject(Router);

  readonly checkIn = input.required<string>();
  readonly checkOut = input.required<string>();
  readonly guests = input('2');

  protected readonly query = computed<StayQuery>(() => ({ checkIn: this.checkIn(), checkOut: this.checkOut(), guests: Number(this.guests()) }));
  protected readonly summary = computed(() => `${shortDate(this.checkIn())} – ${shortDate(this.checkOut())} · ${this.guests()} guests`);
  protected readonly results = rxResource({
    params: () => this.query(),
    stream: ({ params }) => this.api.search(params.checkIn, params.checkOut, params.guests),
  });

  protected search(q: StayQuery): void {
    this.router.navigate(['/search'], { queryParams: q });
  }

  protected message(error: unknown): string {
    return problemMessage(error);
  }
}
