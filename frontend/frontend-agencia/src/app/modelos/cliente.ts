export interface Cliente {
    id?: number;
    nombre: string;
    apellido: string;
    correo: string;
    telefono: string;
    direccion?: string; 
    recibirPublicidad?: boolean;
}