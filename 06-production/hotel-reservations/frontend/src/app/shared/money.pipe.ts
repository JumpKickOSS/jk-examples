import { Pipe, PipeTransform } from '@angular/core';
import { Money } from '../api/models';

const DIGITS: Record<string, number> = { JPY: 0, KRW: 0 };

/** `{ minor: 24000, currency: 'EUR' }` → `€240`, with cents only when there are any. */
@Pipe({ name: 'money' })
export class MoneyPipe implements PipeTransform {
  transform(money: Money | null | undefined, locale = 'en-GB'): string {
    if (!money) return '';
    const digits = DIGITS[money.currency] ?? 2;
    const amount = money.minor / 10 ** digits;
    const whole = Number.isInteger(amount);
    return new Intl.NumberFormat(locale, {
      style: 'currency',
      currency: money.currency,
      minimumFractionDigits: whole ? 0 : digits,
      maximumFractionDigits: digits,
    }).format(amount);
  }
}
