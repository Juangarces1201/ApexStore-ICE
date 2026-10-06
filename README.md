# ApexStore - Java + ICE

Implementación en Java del rediseño arquitectónico de ApexStore utilizando el middleware ICE (Internet Communications Engine).

## Descripción

Este proyecto implementa el diseño realizado para ApexStore, manteniendo la distribución del sistema en cuatro nodos y separando las responsabilidades relacionadas con el checkout, el procesamiento de pagos, la gestión de transacciones y la persistencia.

La comunicación entre los componentes distribuidos se realiza utilizando ICE.

Las pasarelas de pago son completamente simuladas. El proyecto no realiza conexiones con Stripe, PSE, bancos, redes blockchain ni ningún servicio financiero real.

## Arquitectura

La implementación está distribuida de la siguiente manera:

### Nodo 1 - Clientes

Representa los clientes que realizan las compras:

- WebApp
- MobileApp

En esta implementación el flujo se prueba mediante `ApexStoreTestClient`.

### Nodo 2 - E-Commerce Backend

Contiene los componentes principales del backend:

- `ServicioCheckout`
- `GestorTransacciones`
- `ProcesadorPagosContexto`
- `RegistroEstrategiasPago`

`ServicioCheckout` recibe las solicitudes de compra y las entrega al `GestorTransacciones`.

`GestorTransacciones` registra las transacciones, controla solicitudes duplicadas y actualiza el estado de las transacciones.

`ProcesadorPagosContexto` selecciona la estrategia de pago correspondiente utilizando `RegistroEstrategiasPago`.

### Nodo 3 - Pasarelas de pago

Contiene las estrategias simuladas:

- `StripeSimuladoI`
- `PSESimuladoI`
- `CriptoSimuladoI`

Las tres implementan la interfaz `EstrategiaPago`.

Las estrategias generan resultados simulados y los envían de manera asíncrona al `GestorTransacciones`.

### Nodo 4 - Persistencia

Contiene:

- `PersistenciaTransacciones`
- `PersistenciaTransaccionesI`

La implementación actual utiliza un `ConcurrentHashMap` como almacenamiento en memoria para simular la persistencia de las transacciones.

No se realiza una conexión real con PostgreSQL en esta versión.

## Tecnologías utilizadas

- Java 17
- ICE 3.7.11
- Gradle
- JUnit 5
- IntelliJ IDEA
- Git y GitHub

## Estructura del proyecto

```text
ApexStore-ICE/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/apexstore/
│   │   ├── resources/
│   │   │   ├── node2.cfg
│   │   │   ├── node3.cfg
│   │   │   └── persistence.cfg
│   │   └── slice/
│   │       └── ApexStore.ice
│   └── test/
│       └── java/
├── build.gradle
├── settings.gradle
├── gradlew
├── gradlew.bat
└── README.md
