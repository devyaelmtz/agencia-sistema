export interface Vehiculo {
    id?: number; // Es opcional porque al crear (POST) aún no tiene ID
    vin: string;
    color: string;
    precio: number;
    estadoVehiculo: string;
}