import { ChangeDetectionStrategy, Component, input } from '@angular/core';

/** The Kestrel & Vine mark: a kestrel over a vine leaf, then the wordmark. */
@Component({
  selector: 'kv-logo',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <svg class="mark" viewBox="0 0 64 64" aria-hidden="true">
      <circle cx="32" cy="32" r="31" fill="var(--kv-teal)" />
      <path
        d="M9 27c9-8 17-7 23 2 6-9 14-10 23-2-8-1-14 2-18 8l-5 9-5-9c-4-6-10-9-18-8z"
        fill="var(--kv-paper)" />
      <circle cx="32" cy="28" r="1.8" fill="var(--kv-brass)" />
      <path d="M32 41c-6 4-7 10 0 16 7-6 6-12 0-16z" fill="var(--kv-brass)" />
      <path d="M32 44v11" stroke="var(--kv-teal)" stroke-width="1.2" stroke-linecap="round" />
    </svg>
    @if (wordmark()) {
      <span class="word"><span>Kestrel</span><span class="amp">&amp;</span><span>Vine</span></span>
    }
  `,
  styles: `
    :host { display: inline-flex; align-items: center; gap: 0.6rem; color: inherit; }
    .mark { width: 2.25rem; height: 2.25rem; flex: none; }
    .word { font-family: 'Fraunces Variable', serif; font-size: 1.35rem; font-weight: 600; letter-spacing: 0.01em; white-space: nowrap; }
    .amp { color: var(--kv-brass); font-style: italic; margin: 0 0.18em; }
  `,
})
export class Logo {
  readonly wordmark = input(true);
}
