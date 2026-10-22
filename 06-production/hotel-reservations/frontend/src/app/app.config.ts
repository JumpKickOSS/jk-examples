import { provideHttpClient, withFetch, withXsrfConfiguration } from '@angular/common/http';
import { ApplicationConfig, inject, provideAppInitializer, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideNativeDateAdapter } from '@angular/material/core';
import { MatIconRegistry } from '@angular/material/icon';
import { DomSanitizer } from '@angular/platform-browser';
import { provideRouter, withComponentInputBinding, withInMemoryScrolling } from '@angular/router';
import { routes } from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes, withComponentInputBinding(), withInMemoryScrolling({ scrollPositionRestoration: 'top' })),
    // Spring Security's csrf.spa() speaks Angular's defaults; spelled out so the pairing is visible.
    provideHttpClient(withFetch(), withXsrfConfiguration({ cookieName: 'XSRF-TOKEN', headerName: 'X-XSRF-TOKEN' })),
    provideNativeDateAdapter(),
    // Icons are Material Symbols SVGs, copied one by one into /icons by angular.json: the app ships
    // the few dozen it draws, not the 4 MB icon font.
    provideAppInitializer(() => {
      const sanitizer = inject(DomSanitizer);
      inject(MatIconRegistry).addSvgIconResolver((name) => sanitizer.bypassSecurityTrustResourceUrl(`/icons/${name}.svg`));
    }),
  ],
};
