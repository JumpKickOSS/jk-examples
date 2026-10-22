import { describe, expect, it } from 'vitest';
import { MoneyPipe } from './money.pipe';

describe('MoneyPipe', () => {
  const pipe = new MoneyPipe();

  it('drops cents from whole amounts', () => {
    expect(pipe.transform({ minor: 24000, currency: 'EUR' })).toBe('€240');
  });

  it('keeps cents when there are any', () => {
    expect(pipe.transform({ minor: 80640, currency: 'GBP' })).toBe('£806.40');
  });

  it('renders nothing for a missing amount', () => {
    expect(pipe.transform(null)).toBe('');
  });
});
