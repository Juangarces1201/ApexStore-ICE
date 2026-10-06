package com.apexstore.checkout;

import ApexStore.AcuseCobro;
import ApexStore.GestorTransaccionesPrx;
import ApexStore.ServicioCheckout;
import ApexStore.SolicitudCobro;
import com.zeroc.Ice.Current;

public class ServicioCheckoutI implements ServicioCheckout {

    private final GestorTransaccionesPrx gestorTransacciones;

    public ServicioCheckoutI(
            GestorTransaccionesPrx gestorTransacciones) {
        this.gestorTransacciones = gestorTransacciones;
    }

    @Override
    public AcuseCobro gestionarComprasHttp(
            SolicitudCobro solicitud,
            Current current) {

        System.out.println(
                "[Checkout] Recibida solicitud: "
                        + solicitud.idTransaccion);

        return gestorTransacciones.registrarYProcesar(solicitud);
    }
}