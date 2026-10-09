import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ProgressService } from '../../core/services/progress.service';
import { apiErrorMessage } from '../../core/services/api-error';
import { ProgressLog } from '../../core/models/plan.model';
import { Shell } from '../layout/shell/shell';

@Component({
  selector: 'app-progress',
  imports: [ReactiveFormsModule, Shell],
  templateUrl: './progress.html',
  styleUrl: './progress.css',
})
export class Progress implements OnInit {
  private fb = inject(FormBuilder);
  private readonly progressService = inject(ProgressService);

  readonly logs = signal<ProgressLog[]>([]);
  readonly loading = signal(true);
  readonly submitting = signal(false);
  readonly errorMessage = signal('');
  readonly formError = signal('');

  readonly form = this.fb.nonNullable.group({
    logDate: [this.today(), Validators.required],
    // Ranges mirror dto/progress/ProgressLogRequest.java.
    weightKg: [null as number | null, [Validators.min(20), Validators.max(400)]],
    bodyFatPct: [null as number | null, [Validators.min(1), Validators.max(80)]],
    notes: ['', Validators.maxLength(500)],
  });

  /** Backend returns newest first; the range summary reads oldest to newest. */
  readonly chronological = computed(() => [...this.logs()].reverse());

  ngOnInit(): void {
    this.reload();
  }

  reload(): void {
    this.loading.set(true);
    this.progressService.getLogs().subscribe({
      next: (logs) => {
        this.logs.set(logs);
        this.loading.set(false);
      },
      error: (err) => {
        this.errorMessage.set(apiErrorMessage(err, 'Záznamy sa nepodarilo načítať.'));
        this.loading.set(false);
      },
    });
  }

  OnSubmit(): void {
    if (this.form.invalid || this.submitting()) {
      this.form.markAllAsTouched();
      return;
    }

    const { logDate, weightKg, bodyFatPct, notes } = this.form.getRawValue();
    if (weightKg === null && bodyFatPct === null) {
      this.formError.set('Vyplň aspoň váhu alebo percento tuku.');
      return;
    }

    this.submitting.set(true);
    this.errorMessage.set('');
    this.formError.set('');

    this.progressService
      .addLog({
        logDate,
        // The backend accepts nulls, but sending undefined keeps the payload clean.
        weightKg: weightKg ?? undefined,
        bodyFatPct: bodyFatPct ?? undefined,
        notes: notes.trim() === '' ? undefined : notes.trim(),
      })
      .subscribe({
        next: () => {
          this.submitting.set(false);
          this.form.patchValue({ weightKg: null, bodyFatPct: null, notes: '' });
          this.reload();
        },
        error: (err) => {
          this.submitting.set(false);
          this.errorMessage.set(apiErrorMessage(err, 'Záznam sa nepodarilo uložiť.'));
        },
      });
  }

  /** Weight change between the first and last chronological entry. */
  weightDelta(): number | null {
    const entries = this.chronological().filter((log) => log.weightKg !== null);
    if (entries.length < 2) return null;
    const first = entries[0].weightKg as number;
    const last = entries[entries.length - 1].weightKg as number;
    return Math.round((last - first) * 10) / 10;
  }

  /** Oldest to newest span for a metric, or an em dash when there is no data. */
  rangeLabel(key: 'weightKg' | 'bodyFatPct'): string {
    const values = this.chronological()
      .map((log) => log[key])
      .filter((v): v is number => v !== null);
    if (values.length < 2) return '—';

    return `${values[0]} → ${values[values.length - 1]}`;
  }

  has(key: 'weightKg' | 'bodyFatPct'): boolean {
    return this.chronological().some((log) => log[key] !== null);
  }

  private today(): string {
    return new Date().toISOString().slice(0, 10);
  }
}
