import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ApprovalService, Approval } from '../../servicios/agency.service';
@Component({
  selector: 'app-admin-arco',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './admin-arco.component.html',
  styleUrl: '../agency-crud/agency-crud.component.css',
})
export class AdminArcoComponent implements OnInit {
  private api = inject(ApprovalService);
  rows = signal<Approval[]>([]);
  busy = signal(false);
  error = signal('');
  message = signal('');
  selected = signal<Approval | null>(null);
  decision: 'APROBAR' | 'RECHAZAR' = 'APROBAR';
  reason = '';
  filter = 'PENDIENTE';
  ngOnInit() {
    this.load();
  }
  load() {
    this.api
      .list(true)
      .subscribe({
        next: (r) => this.rows.set(r),
        error: () => this.error.set('No fue posible cargar las solicitudes.'),
      });
  }
  open(r: Approval) {
    this.selected.set(r);
    this.decision = r.tablaAfectada ? 'APROBAR' : 'RECHAZAR';
    this.reason = '';
    this.error.set('');
  }
  resolve() {
    const r = this.selected();
    if (!r || this.busy() || this.reason.trim().length < 5) return;
    this.busy.set(true);
    this.error.set('');
    this.api.resolve(r.id, this.decision, this.reason).subscribe({
      next: () => {
        this.busy.set(false);
        this.selected.set(null);
        this.message.set('Solicitud resuelta.');
        this.load();
      },
      error: (e) => {
        this.busy.set(false);
        this.error.set(
          e.status === 409
            ? 'Conflicto: el registro cambió, tiene relaciones que impiden eliminarlo o la solicitud ya fue resuelta. Actualiza y revisa antes de continuar.'
            : 'No fue posible resolver. Revisa permisos y datos.',
        );
      },
    });
  }
}
