package com.apexstore.gateway;

import ApexStore.AcuseCobro;
import ApexStore.EstrategiaPago;
import ApexStore.ReceptorResultadoPagoPrx;
import ApexStore.ResultadoPago;
import ApexStore.SolicitudCobro;
import com.zeroc.Ice.Current;

public class CriptoSimuladoI implements EstrategiaPago {

    private final ReceptorResultadoPagoPrx receptor;

    public CriptoSimuladoI(
            ReceptorResultadoPagoPrx receptor) {

        this.receptor = receptor;
    }

    @Override
    public AcuseCobro procesarCobro(
            SolicitudCobro solicitud,
            Current current) {

        System.out.println(
                "[Cripto Simulado] Procesando transacción: "
                        + solicitud.idTransaccion);

        System.out.println(
                "[Cripto Simulado] Monto: "
                        + solicitud.monto
                        + " "
                        + solicitud.moneda);

        boolean exitoso =
                !solicitud.idTransaccion.startsWith("RECHAZO-");

        String mensaje =
                exitoso
                        ? "Pago aprobado por Cripto Simulado"
                        : "Pago rechazado por Cripto Simulado";

        ResultadoPago resultado =
                new ResultadoPago(
                        solicitud.idTransaccion,
                        exitoso,
                        mensaje);

        receptor.recibirResultadoAsync(resultado);

        return new AcuseCobro(
                solicitud.idTransaccion,
                true,
                "Solicitud enviada a Cripto Simulado");
    }
}