import { describe, expect, it } from 'vitest';
import { addDays, isoDate, nightsBetween, parseIso } from './dates';

describe('dates', () => {
  it('round-trips ISO dates in local time', () => {
    expect(isoDate(parseIso('2026-11-02'))).toBe('2026-11-02');
  });

  it('counts nights across a month end', () => {
    expect(nightsBetween('2026-10-30', '2026-11-02')).toBe(3);
    expect(isoDate(addDays(parseIso('2026-12-31'), 1))).toBe('2027-01-01');
  });
});
