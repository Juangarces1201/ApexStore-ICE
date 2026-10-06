package com.apexstore;

import ApexStore.EstrategiaPagoPrx;
import ApexStore.GestorTransaccionesPrx;
import ApexStore.PersistenciaTransaccionesPrx;
import ApexStore.ProcesadorPagosContextoPrx;
import com.apexstore.checkout.ServicioCheckoutI;
import com.apexstore.pagos.ProcesadorPagosContextoI;
import com.apexstore.pagos.RegistroEstrategiasPago;
import com.apexstore.transacciones.GestorTransaccionesI;
import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.ObjectPrx;
import com.zeroc.Ice.Util;

public class ApexStoreNode2Server {

    public static void main(String[] args) {

        try (Communicator communicator =
                     Util.initialize(args, "node2.cfg")) {

            ObjectAdapter adapter =
                    communicator.createObjectAdapter(
                            "Node2Adapter");

            // Conexión con el Nodo 4.
            PersistenciaTransaccionesPrx persistencia =
                    PersistenciaTransaccionesPrx.checkedCast(
                            communicator.stringToProxy(
                                    "Persistence:default -h localhost -p 10004"));

            if (persistencia == null) {
                throw new RuntimeException(
                        "No se pudo conectar con Nodo 4.");
            }

            // Registro de las estrategias disponibles en el Nodo 3.
            RegistroEstrategiasPago registro =
                    new RegistroEstrategiasPago();

            registro.registrar(
                    "stripe",
                    EstrategiaPagoPrx.uncheckedCast(
                            communicator.stringToProxy(
                                    "Stripe:default -h localhost -p 10006")));

            registro.registrar(
                    "pse",
                    EstrategiaPagoPrx.uncheckedCast(
                            communicator.stringToProxy(
                                    "PSE:default -h localhost -p 10006")));

            registro.registrar(
                    "cripto",
                    EstrategiaPagoPrx.uncheckedCast(
                            communicator.stringToProxy(
                                    "Cripto:default -h localhost -p 10006")));

            System.out.println("[Nodo 2] Stripe registrado.");
            System.out.println("[Nodo 2] PSE registrado.");
            System.out.println("[Nodo 2] Cripto registrado.");

            // Publicar el ProcesadorPagosContexto.
            ProcesadorPagosContextoI procesadorServant =
                    new ProcesadorPagosContextoI(registro);

            ObjectPrx procesadorBase =
                    adapter.add(
                            procesadorServant,
                            Util.stringToIdentity(
                                    "ProcesadorPagos"));

            ProcesadorPagosContextoPrx procesador =
                    ProcesadorPagosContextoPrx.checkedCast(
                            procesadorBase);

            if (procesador == null) {
                throw new RuntimeException(
                        "No se pudo publicar ProcesadorPagosContexto.");
            }

            // Publicar el GestorTransacciones.
            GestorTransaccionesI gestorServant =
                    new GestorTransaccionesI(
                            persistencia,
                            procesador);

            ObjectPrx gestorBase =
                    adapter.add(
                            gestorServant,
                            Util.stringToIdentity(
                                    "GestorTransacciones"));

            GestorTransaccionesPrx gestor =
                    GestorTransaccionesPrx.checkedCast(
                            gestorBase);

            if (gestor == null) {
                throw new RuntimeException(
                        "No se pudo publicar GestorTransacciones.");
            }

            // Publicar el ServicioCheckout.
            ServicioCheckoutI checkoutServant =
                    new ServicioCheckoutI(gestor);

            ObjectPrx checkoutBase =
                    adapter.add(
                            checkoutServant,
                            Util.stringToIdentity(
                                    "Checkout"));

            var checkout =
                    ApexStore.ServicioCheckoutPrx.checkedCast(
                            checkoutBase);

            if (checkout == null) {
                throw new RuntimeException(
                        "No se pudo publicar ServicioCheckout.");
            }

            adapter.activate();

            System.out.println(
                    "[Nodo 2] Persistencia conectada.");

            System.out.println(
                    "[Nodo 2] ProcesadorPagos publicado.");

            System.out.println(
                    "[Nodo 2] GestorTransacciones publicado.");

            System.out.println(
                    "[Nodo 2] Checkout publicado.");

            System.out.println(
                    "[Nodo 2] iniciado correctamente.");

            communicator.waitForShutdown();
        }
    }
}