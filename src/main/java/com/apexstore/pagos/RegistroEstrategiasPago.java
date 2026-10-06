package com.apexstore.pagos;

import ApexStore.EstrategiaPagoPrx;

import java.util.HashMap;
import java.util.Map;

public class RegistroEstrategiasPago {

    private final Map<String, EstrategiaPagoPrx> estrategias =
            new HashMap<>();

    public void registrar(
            String metodo,
            EstrategiaPagoPrx estrategia) {

        estrategias.put(
                metodo.toLowerCase(),
                estrategia);
    }

    public EstrategiaPagoPrx obtener(String metodo) {

        return estrategias.get(
                metodo.toLowerCase());
    }
}