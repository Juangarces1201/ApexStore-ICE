package com.apexstore.persistencia;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Util;

public class PersistenciaServer {

    public static void main(String[] args) {

        try (Communicator communicator =
                     Util.initialize(args, "persistence.cfg")) {

            ObjectAdapter adapter =
                    communicator.createObjectAdapter(
                            "PersistenceAdapter");

            PersistenciaTransaccionesI persistencia =
                    new PersistenciaTransaccionesI();

            adapter.add(
                    persistencia,
                    Util.stringToIdentity("Persistence"));

            adapter.activate();

            System.out.println(
                    "[Nodo 4] PersistenciaTransacciones iniciado.");

            communicator.waitForShutdown();
        }
    }
}