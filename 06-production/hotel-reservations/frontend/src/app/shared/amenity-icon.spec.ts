import { describe, expect, it } from 'vitest';
import { amenityIcon } from './amenity-icon';

describe('amenityIcon', () => {
  it('pictures an amenity by keyword', () => {
    expect(amenityIcon('Heated outdoor pool')).toBe('pool');
    expect(amenityIcon('Free Wi-Fi')).toBe('wifi');
  });

  it('falls back to a check mark', () => {
    expect(amenityIcon('Butler')).toBe('check_circle');
  });
});
