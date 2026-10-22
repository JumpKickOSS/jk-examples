import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { map } from 'rxjs';
import { StaffSession } from './staff-session';

/** The front desk is for signed-in staff; everyone else goes to the sign-in page. */
export const deskGuard: CanActivateFn = () => {
  const router = inject(Router);
  return inject(StaffSession).current().pipe(map((user) => (user ? true : router.parseUrl('/desk/login'))));
};
