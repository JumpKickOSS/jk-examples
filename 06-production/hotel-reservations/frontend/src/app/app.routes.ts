import { Routes } from '@angular/router';
import { deskGuard } from './desk/desk-guard';

export const routes: Routes = [
  { path: '', loadComponent: () => import('./pages/home').then((m) => m.Home), title: 'Kestrel & Vine Hotels' },
  { path: 'search', loadComponent: () => import('./pages/search').then((m) => m.Search), title: 'Available hotels' },
  { path: 'hotels/:slug', loadComponent: () => import('./pages/hotel').then((m) => m.HotelPage) },
  { path: 'book/:slug/:roomType', loadComponent: () => import('./pages/book').then((m) => m.Book), title: 'Book your stay' },
  { path: 'reservation', loadComponent: () => import('./pages/find-reservation').then((m) => m.FindReservation), title: 'Find your reservation' },
  { path: 'desk/login', loadComponent: () => import('./desk/desk-login').then((m) => m.DeskLogin), title: 'Front desk sign-in' },
  { path: 'desk', canActivate: [deskGuard], loadComponent: () => import('./desk/desk').then((m) => m.Desk), title: 'Front desk' },
  { path: '**', redirectTo: '' },
];
