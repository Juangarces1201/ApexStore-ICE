package com.apexstore;

import ApexStore.GestorTransaccionesPrx;
import com.apexstore.gateway.CriptoSimuladoI;
import com.apexstore.gateway.PSESimuladoI;
import com.apexstore.gateway.StripeSimuladoI;
import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Util;

public class ApexStoreNode3Server {

    public static void main(String[] args) {

        try (Communicator communicator =
                     Util.initialize(args, "node3.cfg")) {

            ObjectAdapter adapter =
                    communicator.createObjectAdapter(
                            "Node3Adapter");

            // Conexión con el GestorTransacciones del Nodo 2.
            GestorTransaccionesPrx gestor =
                    GestorTransaccionesPrx.uncheckedCast(
                            communicator.stringToProxy(
                                    "GestorTransacciones:default -h localhost -p 10005"));

            if (gestor == null) {
                throw new RuntimeException(
                        "No se pudo conectar con GestorTransacciones.");
            }

            // Las tres pasarelas son completamente simuladas.
            StripeSimuladoI stripe =
                    new StripeSimuladoI(gestor);

            PSESimuladoI pse =
                    new PSESimuladoI(gestor);

            CriptoSimuladoI cripto =
                    new CriptoSimuladoI(gestor);

            // Publicar las estrategias de pago.
            adapter.add(
                    stripe,
                    Util.stringToIdentity("Stripe"));

            adapter.add(
                    pse,
                    Util.stringToIdentity("PSE"));

            adapter.add(
                    cripto,
                    Util.stringToIdentity("Cripto"));

            adapter.activate();

            System.out.println(
                    "[Nodo 3] Stripe publicado.");

            System.out.println(
                    "[Nodo 3] PSE publicado.");

            System.out.println(
                    "[Nodo 3] Cripto publicado.");

            System.out.println(
                    "[Nodo 3] iniciado correctamente.");

            communicator.waitForShutdown();
        }
    }
}