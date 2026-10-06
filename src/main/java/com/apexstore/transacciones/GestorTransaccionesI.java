package com.apexstore.transacciones;

import ApexStore.AcuseCobro;
import ApexStore.GestorTransacciones;
import ApexStore.PersistenciaTransaccionesPrx;
import ApexStore.ProcesadorPagosContextoPrx;
import ApexStore.ResultadoPago;
import ApexStore.SolicitudCobro;
import ApexStore.Transaccion;
import com.zeroc.Ice.Current;

public class GestorTransaccionesI implements GestorTransacciones {

    private final PersistenciaTransaccionesPrx persistencia;
    private final ProcesadorPagosContextoPrx procesadorPagos;

    public GestorTransaccionesI(
            PersistenciaTransaccionesPrx persistencia,
            ProcesadorPagosContextoPrx procesadorPagos) {

        this.persistencia = persistencia;
        this.procesadorPagos = procesadorPagos;
    }

    @Override
    public AcuseCobro registrarYProcesar(
            SolicitudCobro solicitud,
            Current current) {

        boolean existe =
                persistencia.existeTransaccion(
                        solicitud.idTransaccion);

        if (existe) {
            System.out.println(
                    "[GestorTransacciones] Solicitud duplicada: "
                            + solicitud.idTransaccion);

            return new AcuseCobro(
                    solicitud.idTransaccion,
                    false,
                    "Transacción ya registrada");
        }

        System.out.println(
                "[GestorTransacciones] Registrando: "
                        + solicitud.idTransaccion);

        Transaccion transaccion =
                new Transaccion(
                        solicitud.idTransaccion,
                        solicitud.monto,
                        solicitud.moneda,
                        solicitud.metodoPago,
                        "PENDIENTE");

        persistencia.guardarTransaccion(transaccion);

        System.out.println(
                "[GestorTransacciones] Estado: PENDIENTE");

        return procesadorPagos.procesar(solicitud);
    }

    @Override
    public void recibirResultado(
            ResultadoPago resultado,
            Current current) {

        System.out.println(
                "[GestorTransacciones] Resultado recibido: "
                        + resultado.idTransaccion);

        String nuevoEstado =
                resultado.exitoso
                        ? "APROBADA"
                        : "RECHAZADA";

        persistencia.actualizarEstado(
                resultado.idTransaccion,
                nuevoEstado);

        System.out.println(
                "[GestorTransacciones] Estado actualizado: "
                        + nuevoEstado);
    }
}