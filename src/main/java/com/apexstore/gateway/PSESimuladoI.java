package com.apexstore.gateway;

import ApexStore.AcuseCobro;
import ApexStore.EstrategiaPago;
import ApexStore.ReceptorResultadoPagoPrx;
import ApexStore.ResultadoPago;
import ApexStore.SolicitudCobro;
import com.zeroc.Ice.Current;

public class PSESimuladoI implements EstrategiaPago {

    private final ReceptorResultadoPagoPrx receptor;

    public PSESimuladoI(
            ReceptorResultadoPagoPrx receptor) {

        this.receptor = receptor;
    }

    @Override
    public AcuseCobro procesarCobro(
            SolicitudCobro solicitud,
            Current current) {

        System.out.println(
                "[PSE Simulado] Procesando transacción: "
                        + solicitud.idTransaccion);

        System.out.println(
                "[PSE Simulado] Monto: "
                        + solicitud.monto
                        + " "
                        + solicitud.moneda);

        ResultadoPago resultado =
                new ResultadoPago(
                        solicitud.idTransaccion,
                        true,
                        "Pago aprobado por PSE Simulado");

        receptor.recibirResultadoAsync(resultado);

        return new AcuseCobro(
                solicitud.idTransaccion,
                true,
                "Solicitud enviada a PSE Simulado");
    }
}