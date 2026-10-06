package com.apexstore;

import ApexStore.EstrategiaPagoPrx;
import com.apexstore.pagos.RegistroEstrategiasPago;
import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.Util;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class RegistroEstrategiasPagoTest {

    @Test
    void debeRegistrarYObtenerLasEstrategias() {

        try (Communicator communicator = Util.initialize()) {

            EstrategiaPagoPrx stripe =
                    EstrategiaPagoPrx.uncheckedCast(
                            communicator.stringToProxy(
                                    "Stripe:default -h localhost -p 10001"));

            EstrategiaPagoPrx pse =
                    EstrategiaPagoPrx.uncheckedCast(
                            communicator.stringToProxy(
                                    "PSE:default -h localhost -p 10002"));

            EstrategiaPagoPrx cripto =
                    EstrategiaPagoPrx.uncheckedCast(
                            communicator.stringToProxy(
                                    "Cripto:default -h localhost -p 10003"));

            RegistroEstrategiasPago registro =
                    new RegistroEstrategiasPago();

            registro.registrar("stripe", stripe);
            registro.registrar("pse", pse);
            registro.registrar("cripto", cripto);

            assertNotNull(registro.obtener("stripe"));
            assertNotNull(registro.obtener("pse"));
            assertNotNull(registro.obtener("cripto"));

            assertSame(
                    stripe,
                    registro.obtener("stripe"));

            assertSame(
                    pse,
                    registro.obtener("pse"));

            assertSame(
                    cripto,
                    registro.obtener("cripto"));
        }
    }
}