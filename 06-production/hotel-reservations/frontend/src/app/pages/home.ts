import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatIconModule } from '@angular/material/icon';
import { Router, RouterLink } from '@angular/router';
import { HotelApi } from '../api/hotel-api';
import { MoneyPipe } from '../shared/money.pipe';
import { SearchForm, StayQuery } from '../shared/search-form';

/** The landing page: where do you want to stay, and the five houses. */
@Component({
  selector: 'kv-home',
  imports: [RouterLink, MatIconModule, MoneyPipe, SearchForm],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <section class="hero">
      <img src="/images/hotels/harborview/hero.webp" alt="" class="hero-img" fetchpriority="high" />
      <div class="hero-shade"></div>
      <div class="hero-copy page">
        <span class="eyebrow">Kestrel &amp; Vine Hotels</span>
        <h1>Five houses,<br />each with a view worth waking for.</h1>
        <p class="lede">On the harbour, in the old town, among the vines, at the summit and in the city.</p>
        <div class="search card">
          <kv-search-form label="Find a room" (searched)="search($event)" />
        </div>
      </div>
    </section>

    <section class="page">
      <span class="eyebrow">Our hotels</span>
      <h2>Where would you like to wake up?</h2>
      <div class="grid">
        @for (hotel of hotels(); track hotel.slug) {
          <a class="hotel card" [routerLink]="['/hotels', hotel.slug]" [attr.data-testid]="'hotel-' + hotel.slug">
            <img [src]="hotel.heroImage" [alt]="hotel.name" loading="lazy" />
            <div class="body">
              <div class="where muted"><mat-icon svgIcon="location_on" />{{ hotel.city }}, {{ hotel.country }}</div>
              <h3>{{ hotel.name }}</h3>
              <p class="muted">{{ hotel.tagline }}</p>
              <div class="foot">
                <span class="stars" [attr.aria-label]="hotel.stars + ' stars'">{{ '★'.repeat(hotel.stars) }}</span>
                <span>from <strong>{{ hotel.fromPrice | money }}</strong> / night</span>
              </div>
            </div>
          </a>
        } @empty {
          @for (n of [1, 2, 3]; track n) { <div class="hotel card skeleton"></div> }
        }
      </div>
    </section>
  `,
  styles: `
    .hero { position: relative; min-height: min(78vh, 720px); display: flex; align-items: flex-end; color: #fff; overflow: hidden; }
    .hero-img { position: absolute; inset: 0; width: 100%; height: 100%; object-fit: cover; }
    .hero-shade { position: absolute; inset: 0; background: linear-gradient(180deg, rgba(10,30,32,0.15) 0%, rgba(10,30,32,0.75) 100%); }
    .hero-copy { position: relative; width: 100%; padding-bottom: 3rem; }
    .hero h1 { color: #fff; max-width: 16ch; }
    .lede { font-size: 1.15rem; max-width: 46ch; opacity: 0.92; }
    .search { margin-top: 1.5rem; padding: 1.25rem 1.25rem 0.25rem; color: var(--mat-sys-on-surface); box-shadow: 0 20px 50px rgba(0,0,0,0.25); }
    .grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(320px, 1fr)); gap: 1.5rem; margin-top: 1.5rem; }
    .hotel { text-decoration: none; color: inherit; transition: transform 160ms ease, box-shadow 160ms ease; }
    .hotel:hover { transform: translateY(-3px); box-shadow: 0 18px 40px rgba(15,82,87,0.16); }
    .hotel img { aspect-ratio: 16 / 10; width: 100%; object-fit: cover; }
    .body { padding: 1.1rem 1.25rem 1.25rem; }
    .where { display: flex; align-items: center; gap: 0.25rem; font-size: 0.9rem; }
    .where mat-icon { font-size: 18px; width: 18px; height: 18px; }
    .foot { display: flex; justify-content: space-between; align-items: center; margin-top: 0.75rem; }
    .skeleton { min-height: 340px; background: var(--mat-sys-surface-container); }
  `,
})
export class Home {
  private readonly api = inject(HotelApi);
  private readonly router = inject(Router);
  protected readonly hotels = toSignal(this.api.hotels(), { initialValue: [] });

  protected search(q: StayQuery): void {
    this.router.navigate(['/search'], { queryParams: q });
  }
}
