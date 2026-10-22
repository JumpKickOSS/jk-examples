import { ChangeDetectionStrategy, Component, computed, input } from '@angular/core';
import { OccupancyPoint } from '../api/models';
import { parseIso } from '../shared/dates';

/** Nightly occupancy as bars, today marked; plain SVG, no chart library. */
@Component({
  selector: 'kv-occupancy-chart',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <svg [attr.viewBox]="'0 0 ' + width + ' ' + height" role="img" [attr.aria-label]="summary()">
      @for (line of [25, 50, 75, 100]; track line) {
        <line [attr.x1]="0" [attr.x2]="width" [attr.y1]="y(line)" [attr.y2]="y(line)" class="grid" />
        <text [attr.x]="width - 2" [attr.y]="y(line) - 3" class="axis" text-anchor="end">{{ line }}%</text>
      }
      @for (b of bars(); track b.night) {
        <rect [attr.x]="b.x" [attr.y]="y(b.percent)" [attr.width]="b.w" [attr.height]="height - pad - y(b.percent)" rx="2"
              [class.today]="b.today" [class.weekend]="b.weekend"><title>{{ b.label }}: {{ b.percent }}%</title></rect>
      }
    </svg>
  `,
  styles: `
    svg { width: 100%; height: auto; display: block; }
    rect { fill: color-mix(in srgb, var(--kv-teal) 70%, transparent); }
    rect.weekend { fill: var(--kv-teal); }
    rect.today { fill: var(--kv-brass); }
    .grid { stroke: var(--mat-sys-outline-variant); stroke-dasharray: 2 4; }
    .axis { fill: var(--mat-sys-on-surface-variant); font-size: 10px; }
  `,
})
export class OccupancyChart {
  readonly points = input.required<OccupancyPoint[]>();
  readonly today = input.required<string>();
  protected readonly width = 720;
  protected readonly height = 180;
  protected readonly pad = 8;

  protected readonly bars = computed(() => {
    const pts = this.points();
    const w = this.width / Math.max(pts.length, 1);
    return pts.map((p, i) => {
      const d = parseIso(p.night);
      return {
        night: p.night,
        x: i * w + 1,
        w: Math.max(w - 2, 1),
        percent: p.rooms ? Math.round((p.occupied * 100) / p.rooms) : 0,
        today: p.night === this.today(),
        weekend: d.getDay() === 5 || d.getDay() === 6,
        label: d.toLocaleDateString('en-GB', { weekday: 'short', day: 'numeric', month: 'short' }),
      };
    });
  });

  protected readonly summary = computed(() => {
    const b = this.bars();
    const avg = b.length ? Math.round(b.reduce((s, x) => s + x.percent, 0) / b.length) : 0;
    return `Occupancy over ${b.length} nights, average ${avg}%`;
  });

  protected y(percent: number): number {
    return this.pad + (this.height - 2 * this.pad) * (1 - percent / 100);
  }
}
