package com.apexstore.pagos;

import ApexStore.AcuseCobro;
import ApexStore.EstrategiaPagoPrx;
import ApexStore.ProcesadorPagosContexto;
import ApexStore.SolicitudCobro;
import com.zeroc.Ice.Current;

public class ProcesadorPagosContextoI implements ProcesadorPagosContexto {

    private final RegistroEstrategiasPago registro;

    public ProcesadorPagosContextoI(
            RegistroEstrategiasPago registro) {

        this.registro = registro;
    }

    @Override
    public AcuseCobro procesar(
            SolicitudCobro solicitud,
            Current current) {

        System.out.println(
                "[ProcesadorPagosContexto] Método de pago: "
                        + solicitud.metodoPago);

        EstrategiaPagoPrx estrategia =
                registro.obtener(solicitud.metodoPago);

        if (estrategia == null) {
            return new AcuseCobro(
                    solicitud.idTransaccion,
                    false,
                    "Método de pago no soportado");
        }

        System.out.println(
                "[ProcesadorPagosContexto] Estrategia encontrada: "
                        + estrategia);

        return estrategia.procesarCobro(solicitud);
    }
}