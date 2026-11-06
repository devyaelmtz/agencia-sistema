import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { forkJoin, map, switchMap, Observable } from 'rxjs';
import { AuthService } from '../../servicios/auth.service';
import {
  ClientService,
  UserService,
  SaleService,
  ApprovalService,
  Client,
  User,
  Sale,
  Notice,
  policyVersion,
} from '../../servicios/agency.service';
import { VehicleService, Vehicle } from '../../servicios/vehicle.service';
type Row = Client | User | Sale;
interface Field {
  key: string;
  label: string;
  type: string;
  required?: boolean;
  max?: number;
  pattern?: string;
  options?: string[];
}
@Component({
  selector: 'app-agency-crud',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './agency-crud.component.html',
  styleUrl: './agency-crud.component.css',
})
export class AgencyCrudComponent implements OnInit {
  auth = inject(AuthService);
  private route = inject(ActivatedRoute);
  private clientsApi = inject(ClientService);
  private usersApi = inject(UserService);
  private salesApi = inject(SaleService);
  private approvals = inject(ApprovalService);
  private vehiclesApi = inject(VehicleService);
  kind = this.route.snapshot.data['kind'] as 'clientes' | 'usuarios' | 'ventas' | 'perfil';
  title =
    this.kind === 'perfil'
      ? 'Mi perfil'
      : this.kind === 'usuarios'
        ? 'Usuarios'
        : this.kind === 'ventas'
          ? 'Ventas'
          : 'Clientes';
  rows = signal<Row[]>([]);
  clients = signal<Client[]>([]);
  vehicles = signal<Vehicle[]>([]);
  busy = signal(false);
  loading = signal(false);
  error = signal('');
  message = signal('');
  editing = signal(false);
  deleting = signal<Row | null>(null);
  selected?: Row;
  data: Record<string, string | number | boolean> = {};
  description = '';
  notice = false;
  action = 'ACCESO';
  requestRow = signal<Row | null>(null);
  linkRow = signal<Row | null>(null);
  users = signal<User[]>([]);
  ownerId = 0;
  fields: Field[] =
    this.kind === 'clientes'
      ? [
          { key: 'nombre', label: 'Nombre', type: 'text', required: true, max: 100 },
          { key: 'apellido', label: 'Apellido', type: 'text', required: true, max: 100 },
          {
            key: 'rfc',
            label: 'RFC',
            type: 'text',
            required: true,
            max: 13,
            pattern: '[A-ZÑ&]{3,4}[0-9]{6}[A-Z0-9]{3}',
          },
          { key: 'direccion', label: 'Dirección', type: 'text', required: true, max: 255 },
          {
            key: 'telefono',
            label: 'Teléfono (10 dígitos)',
            type: 'tel',
            required: true,
            pattern: '[0-9]{10}',
          },
          { key: 'correo', label: 'Correo', type: 'email', required: true, max: 100 },
          { key: 'recibirPublicidad', label: 'Recibir publicidad', type: 'checkbox' },
        ]
      : this.kind === 'ventas'
        ? [
            { key: 'clienteId', label: 'Cliente', type: 'client', required: true },
            { key: 'vehiculoId', label: 'Vehículo', type: 'vehicle', required: true },
            { key: 'montoTotal', label: 'Monto total', type: 'number', required: true },
            {
              key: 'metodoPago',
              label: 'Método de pago',
              type: 'select',
              required: true,
              options: ['EFECTIVO', 'TRANSFERENCIA', 'TARJETA', 'FINANCIAMIENTO'],
            },
          ]
        : [
            { key: 'nombreUsuario', label: 'Nombre', type: 'text', required: true, max: 50 },
            { key: 'correo', label: 'Correo', type: 'email', required: true, max: 100 },
            {
              key: 'telefono',
              label: 'Teléfono (10 dígitos)',
              type: 'tel',
              required: true,
              pattern: '[0-9]{10}',
            },
            ...(this.kind === 'usuarios'
              ? [
                  {
                    key: 'password',
                    label: 'Contraseña (dejar vacía al editar para conservarla)',
                    type: 'password',
                    max: 64,
                  },
                  {
                    key: 'rol',
                    label: 'Rol',
                    type: 'select',
                    required: true,
                    options: ['ROLE_USER', 'ROLE_ADMIN'],
                  },
                ]
              : []),
          ];
  get needsApproval() {
    return !this.auth.isAdmin && !!this.selected;
  }
  get noticeData(): Notice {
    return { descripcion: this.description, avisoLeido: this.notice, avisoVersion: policyVersion };
  }
  ngOnInit() {
    this.load();
  }
  load() {
    this.loading.set(true);
    this.error.set('');
    let request: Observable<Row[]>;
    if (this.kind === 'clientes') request = this.clientsApi.list();
    else if (this.kind === 'ventas') request = this.salesApi.list();
    else if (this.kind === 'perfil')
      request = this.auth.me().pipe(
        switchMap((p) => this.usersApi.get(p.id)),
        map((u) => [u]),
      );
    else request = this.usersApi.list();
    request.subscribe({
      next: (rows) => {
        this.rows.set(rows);
        this.loading.set(false);
      },
      error: (e) => {
        this.loading.set(false);
        this.fail(e);
      },
    });
    if (this.kind === 'clientes' && this.auth.isAdmin)
      this.usersApi
        .list()
        .subscribe({ next: (u) => this.users.set(u), error: (e) => this.fail(e) });
    if (this.kind === 'ventas')
      forkJoin({ clients: this.clientsApi.list(), vehicles: this.vehiclesApi.list() }).subscribe({
        next: (r) => {
          this.clients.set(r.clients);
          this.vehicles.set(r.vehicles);
        },
        error: (e) => this.fail(e),
      });
  }
  open(row?: Row) {
    this.selected = row;
    this.data = {};
    for (const f of this.fields) {
      this.data[f.key] = row
        ? ((row as unknown as Record<string, string | number | boolean>)[f.key] ?? '')
        : f.type === 'checkbox'
          ? false
          : '';
    }
    if (!row && this.kind === 'usuarios') this.data['rol'] = 'ROLE_USER';
    this.editing.set(true);
    this.deleting.set(null);
    this.requestRow.set(null);
    this.description = '';
    this.notice = false;
    this.error.set('');
  }
  private fail(e: { status?: number; error?: { message?: string; detail?: string } }) {
    this.busy.set(false);
    this.error.set(
      e.status === 409
        ? 'El registro cambió, está relacionado o el dato ya existe. Actualiza el listado.'
        : e.error?.message ||
            e.error?.detail ||
            'No se pudo completar la operación. Revisa los datos y permisos.',
    );
  }
  private perform(request: Observable<unknown>, pending: boolean) {
    this.busy.set(true);
    this.error.set('');
    this.message.set('');
    request.subscribe({
      next: () => {
        this.busy.set(false);
        this.editing.set(false);
        this.deleting.set(null);
        this.requestRow.set(null);
        this.message.set(
          pending
            ? 'Solicitud ARCO pendiente. El registro original no ha cambiado.'
            : 'Operación completada.',
        );
        this.load();
      },
      error: (e) => this.fail(e),
    });
  }
  save() {
    if (this.busy()) return;
    if (this.needsApproval && (!this.notice || this.description.trim().length < 10)) {
      this.error.set('Confirma el aviso y describe tu solicitud (mínimo 10 caracteres).');
      return;
    }
    const row = this.selected;
    let request: Observable<unknown>;
    if (this.kind === 'clientes')
      request = row
        ? this.clientsApi.update(row.id, row.version, this.data, this.noticeData)
        : this.clientsApi.create(this.data);
    else if (this.kind === 'ventas')
      request = row
        ? this.salesApi.update(row.id, row.version, this.data)
        : this.salesApi.create(this.data);
    else
      request = row
        ? this.usersApi.update(row.id, row.version, this.data, this.noticeData)
        : this.usersApi.create(this.data);
    this.perform(request, this.needsApproval);
  }
  askDelete(row: Row) {
    this.deleting.set(row);
    this.editing.set(false);
    this.requestRow.set(null);
    this.description = '';
    this.notice = false;
  }
  remove() {
    const row = this.deleting();
    if (!row || this.busy()) return;
    if (!this.auth.isAdmin && (!this.notice || this.description.trim().length < 10)) {
      this.error.set('Confirma el aviso y describe la solicitud.');
      return;
    }
    const request: Observable<unknown> =
      this.kind === 'clientes'
        ? this.clientsApi.delete(row.id, row.version, this.noticeData)
        : this.kind === 'ventas'
          ? this.salesApi.delete(row.id, row.version)
          : this.usersApi.delete(row.id, row.version, this.noticeData);
    this.perform(request, !this.auth.isAdmin);
  }
  askRequest(row: Row) {
    this.requestRow.set(row);
    this.editing.set(false);
    this.deleting.set(null);
    this.action = 'ACCESO';
    this.description = '';
    this.notice = false;
  }
  sendRequest() {
    const row = this.requestRow();
    if (!row || this.busy() || !this.notice || this.description.trim().length < 10) return;
    this.perform(
      this.approvals.request({
        tablaAfectada: this.kind === 'perfil' ? 'usuarios' : this.kind,
        registroId: row.id,
        registroVersion: row.version,
        tipo: this.action,
        ...this.noticeData,
      }),
      true,
    );
  }
  display(row: Row, key: string) {
    return (row as unknown as Record<string, unknown>)[key];
  }
  link() {
    const row = this.linkRow();
    if (!row || this.busy() || !this.ownerId) return;
    this.perform(this.clientsApi.link(row.id, row.version, Number(this.ownerId)), false);
    this.linkRow.set(null);
  }
}
