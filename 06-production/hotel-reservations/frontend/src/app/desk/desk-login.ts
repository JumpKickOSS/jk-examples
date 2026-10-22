import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { Router } from '@angular/router';
import { Logo } from '../shared/logo';
import { StaffSession } from './staff-session';

@Component({
  selector: 'kv-desk-login',
  imports: [ReactiveFormsModule, MatFormFieldModule, MatInputModule, MatButtonModule, MatIconModule, Logo],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="wrap">
      <form [formGroup]="form" (ngSubmit)="signIn()" class="card login">
        <kv-logo />
        <h1>Front desk</h1>
        <p class="muted">Sign in with your staff account.</p>
        <mat-form-field appearance="outline"><mat-label>Username</mat-label><input matInput formControlName="username" autocomplete="username" data-testid="desk-username" /></mat-form-field>
        <mat-form-field appearance="outline"><mat-label>Password</mat-label><input matInput type="password" formControlName="password" autocomplete="current-password" data-testid="desk-password" /></mat-form-field>
        @if (error()) { <p class="error">{{ error() }}</p> }
        <button mat-flat-button type="submit" [disabled]="form.invalid" data-testid="desk-sign-in">Sign in</button>
        <p class="hint muted">Demo accounts: <code>desk</code> (all hotels) and <code>harbor</code> (Harborview), password <code>kestrel-desk</code>.</p>
      </form>
    </div>
  `,
  styles: `
    .wrap { min-height: 70vh; display: grid; place-items: center; padding: 2rem 1rem; }
    .login { width: min(420px, 100%); padding: 2rem; display: grid; gap: 0.25rem; }
    h1 { margin-top: 1rem; }
    .hint { font-size: 0.85rem; margin-top: 1rem; }
  `,
})
export class DeskLogin {
  private readonly session = inject(StaffSession);
  private readonly router = inject(Router);

  protected readonly form = new FormGroup({
    username: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    password: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
  });
  protected readonly error = signal<string | null>(null);

  protected signIn(): void {
    const { username, password } = this.form.getRawValue();
    this.error.set(null);
    this.session.signIn(username, password).subscribe({
      next: () => this.router.navigateByUrl('/desk'),
      error: (e: HttpErrorResponse) =>
        this.error.set(e.status === 429 ? 'Too many attempts. Wait a minute and try again.' : 'That username and password do not match.'),
    });
  }
}
