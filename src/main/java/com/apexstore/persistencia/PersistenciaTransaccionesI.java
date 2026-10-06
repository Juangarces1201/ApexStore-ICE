package com.apexstore.persistencia;

import ApexStore.PersistenciaTransacciones;
import ApexStore.Transaccion;
import com.zeroc.Ice.Current;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PersistenciaTransaccionesI
        implements PersistenciaTransacciones {

    private final Map<String, Transaccion> transacciones =
            new ConcurrentHashMap<>();

    @Override
    public void guardarTransaccion(
            Transaccion transaccion,
            Current current) {

        transacciones.putIfAbsent(
                transaccion.idTransaccion,
                transaccion);

        System.out.println(
                "[Persistencia] Transacción guardada: "
                        + transaccion.idTransaccion);
    }

    @Override
    public void actualizarEstado(
            String idTransaccion,
            String estado,
            Current current) {

        Transaccion transaccion =
                transacciones.get(idTransaccion);

        if (transaccion != null) {
            transaccion.estado = estado;

            System.out.println(
                    "[Persistencia] "
                            + idTransaccion
                            + " -> "
                            + estado);
        }
    }

    @Override
    public boolean existeTransaccion(
            String idTransaccion,
            Current current) {

        return transacciones.containsKey(idTransaccion);
    }

    @Override
    public Transaccion obtenerTransaccion(
            String idTransaccion,
            Current current) {

        return transacciones.get(idTransaccion);
    }
}