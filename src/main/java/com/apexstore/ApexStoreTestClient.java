package com.apexstore;

import ApexStore.AcuseCobro;
import ApexStore.ServicioCheckoutPrx;
import ApexStore.SolicitudCobro;
import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.Util;

public class ApexStoreTestClient {

    public static void main(String[] args) {

        try (Communicator communicator = Util.initialize(args)) {

            ServicioCheckoutPrx checkout =
                    ServicioCheckoutPrx.checkedCast(
                            communicator.stringToProxy(
                                    "Checkout:default -h localhost -p 10005"));

            if (checkout == null) {
                throw new RuntimeException(
                        "No se pudo conectar con el Checkout del Nodo 2.");
            }

            SolicitudCobro solicitud =
                    new SolicitudCobro(
                            "RECHAZO-FINAL-001",
                            150000.0,
                            "COP",
                            "cripto");

            System.out.println(
                    "[Cliente] Enviando solicitud: "
                            + solicitud.idTransaccion);

            AcuseCobro respuesta =
                    checkout.gestionarComprasHttp(solicitud);

            System.out.println(
                    "[Cliente] Respuesta recibida:");
            System.out.println(
                    "  Transacción: "
                            + respuesta.idTransaccion);
            System.out.println(
                    "  Aceptado: "
                            + respuesta.aceptado);
            System.out.println(
                    "  Mensaje: "
                            + respuesta.mensaje);
        }
    }
}