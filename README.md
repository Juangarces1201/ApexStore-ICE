# ApexStore - Java + ICE

Implementación del rediseño arquitectónico de ApexStore usando Java y el middleware ICE (Internet Communications Engine).

## 1. Descripción

Este proyecto corresponde a la implementación en Java del rediseño realizado para ApexStore.

La implementación mantiene la distribución del sistema en cuatro nodos y separa las responsabilidades relacionadas con el checkout, el procesamiento de pagos, la gestión de transacciones y la persistencia.

La comunicación entre los componentes distribuidos se realiza utilizando ICE.

Las pasarelas de pago son completamente simuladas. El proyecto no realiza conexiones con Stripe, PSE, bancos, redes blockchain ni ningún servicio financiero real.

## 2. Arquitectura implementada

La distribución de los componentes es la siguiente:

### Nodo 1 - Clientes

Este nodo representa los clientes que realizan las compras:

- WebApp
- MobileApp

Para las pruebas de la implementación se utilizó `ApexStoreTestClient`, que permite enviar solicitudes de cobro y comprobar las respuestas del sistema.

### Nodo 2 - E-Commerce Backend

Este nodo contiene los componentes principales del backend:

- `ServicioCheckout`
- `GestorTransacciones`
- `ProcesadorPagosContexto`
- `RegistroEstrategiasPago`

`ServicioCheckout` recibe la solicitud de compra y la entrega al `GestorTransacciones`.

`GestorTransacciones` se encarga de registrar la transacción, verificar solicitudes duplicadas y actualizar el estado cuando recibe el resultado del pago.

`ProcesadorPagosContexto` selecciona la estrategia correspondiente al método de pago utilizando `RegistroEstrategiasPago`.

### Nodo 3 - Pasarelas de pago

Este nodo contiene las tres estrategias de pago simuladas:

- `StripeSimuladoI`
- `PSESimuladoI`
- `CriptoSimuladoI`

Las tres implementan la interfaz común:

```text
EstrategiaPago
```

Esto permite que el Contexto trabaje con una misma abstracción sin depender directamente de una implementación específica.

Las estrategias generan resultados simulados y los envían de manera asíncrona al `GestorTransacciones`.

### Nodo 4 - Persistencia

Este nodo contiene:

- `PersistenciaTransacciones`
- `PersistenciaTransaccionesI`

En la implementación actual se utiliza un `ConcurrentHashMap` como almacenamiento en memoria para simular la persistencia de las transacciones.

No se realiza una conexión real con PostgreSQL en esta versión.

## 3. Comunicación mediante ICE

Los contratos utilizados para la comunicación entre los componentes están definidos en:

```text
src/main/slice/ApexStore.ice
```

Las principales interfaces definidas son:

- `ServicioCheckout`
- `GestorTransacciones`
- `ProcesadorPagosContexto`
- `EstrategiaPago`
- `ReceptorResultadoPago`
- `PersistenciaTransacciones`

A partir del archivo `.ice`, ICE genera los proxies utilizados para realizar las invocaciones remotas entre los diferentes componentes.

## 4. Flujo principal

El flujo de una compra implementado es:

```text
Cliente
   |
   v
ServicioCheckout
   |
   v
GestorTransacciones
   |
   v
ProcesadorPagosContexto
   |
   v
EstrategiaPago
   |
   v
GestorTransacciones
   |
   v
PersistenciaTransacciones
```

El proceso comienza cuando `ServicioCheckout` recibe una solicitud.

El `GestorTransacciones` verifica primero si ya existe una transacción con el mismo identificador. Si no existe, la registra con estado `PENDIENTE`.

Después, `ProcesadorPagosContexto` obtiene la estrategia correspondiente al método de pago y envía la solicitud.

Cuando la estrategia termina el procesamiento simulado, genera un `ResultadoPago` y lo envía de manera asíncrona al `GestorTransacciones`.

Finalmente, el Gestor actualiza el estado de la transacción:

```text
PENDIENTE -> APROBADA
```

o:

```text
PENDIENTE -> RECHAZADA
```

## 5. Estrategias de pago

Las tres estrategias utilizan el mismo contrato:

```text
EstrategiaPago
```

La selección de la estrategia se realiza mediante `RegistroEstrategiasPago`.

Actualmente se encuentran registradas:

```text
stripe  -> StripeSimuladoI
pse     -> PSESimuladoI
cripto  -> CriptoSimuladoI
```

De esta forma, `ProcesadorPagosContexto` no necesita tener una condición diferente para cada implementación.

Las pasarelas no realizan ningún cobro real. Todo el comportamiento de Stripe, PSE y Cripto se encuentra simulado dentro de la aplicación.

## 6. Manejo de resultados

Cuando una estrategia termina el procesamiento, envía un objeto `ResultadoPago` al `GestorTransacciones` mediante la interfaz:

```text
ReceptorResultadoPago
```

El resultado contiene:

- Identificador de la transacción.
- Resultado del pago.
- Mensaje asociado.

El Gestor utiliza esta información para actualizar el estado de la transacción en la persistencia.

## 7. Manejo de transacciones duplicadas

Se implementó un control para evitar procesar dos veces una misma transacción.

Antes de registrar una nueva solicitud, `GestorTransacciones` consulta `PersistenciaTransacciones` utilizando el identificador de la transacción.

Si la transacción ya existe, el proceso se detiene y se devuelve el mensaje:

```text
Transacción ya registrada
```

De esta manera, una solicitud repetida no vuelve a enviarse a la estrategia de pago.

Este comportamiento permite probar la idea de idempotencia utilizada en el diseño arquitectónico.

## 8. Pasarelas simuladas

### Stripe

`StripeSimuladoI` recibe la solicitud, muestra la información de la transacción y genera un resultado simulado de aprobación.

### PSE

`PSESimuladoI` funciona de forma similar y genera un resultado simulado para una transferencia mediante PSE.

### Cripto

`CriptoSimuladoI` simula el procesamiento de un pago mediante criptomonedas.

Además, se implementó un caso de rechazo utilizando identificadores que comienzan con:

```text
RECHAZO-
```

Esto permite comprobar tanto el flujo de aprobación como el de rechazo.

## 9. Pruebas realizadas

Se realizaron pruebas con los diferentes métodos de pago y con algunos de los escenarios considerados en el rediseño.

| Prueba | Resultado |
|---|---|
| Pago con Stripe | Aprobado |
| Pago con PSE | Aprobado |
| Pago con Cripto | Aprobado |
| Pago rechazado con Cripto | Rechazado |
| Solicitud duplicada | Bloqueada correctamente |

También se verificó la comunicación entre los nodos, el envío de resultados mediante ICE y la actualización del estado de las transacciones.

## 10. Tecnologías utilizadas

- Java 17
- ICE 3.7.11
- Gradle
- JUnit 5
- IntelliJ IDEA
- Git
- GitHub

## 11. Estructura del proyecto

```text
ApexStore-ICE/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── apexstore/
│   │   │           ├── ApexStoreNode2Server.java
│   │   │           ├── ApexStoreNode3Server.java
│   │   │           ├── ApexStoreTestClient.java
│   │   │           ├── checkout/
│   │   │           ├── gateway/
│   │   │           ├── pagos/
│   │   │           ├── persistencia/
│   │   │           └── transacciones/
│   │   ├── resources/
│   │   │   ├── node2.cfg
│   │   │   ├── node3.cfg
│   │   │   └── persistence.cfg
│   │   └── slice/
│   │       └── ApexStore.ice
│   │
│   └── test/
│       └── java/
│           └── com/
│               └── apexstore/
│                   └── RegistroEstrategiasPagoTest.java
│
├── gradle/
│   └── wrapper/
│
├── build.gradle
├── settings.gradle
├── gradlew
├── gradlew.bat
├── .gitignore
└── README.md
```

## 12. Configuración de ICE

Los archivos de configuración de los nodos se encuentran en:

```text
src/main/resources/
```

Los endpoints utilizados son:

```text
Nodo 4 - Persistencia:
localhost:10004

Nodo 2 - Backend:
localhost:10005

Nodo 3 - Pasarelas:
localhost:10006
```

### persistence.cfg

```text
PersistenceAdapter.Endpoints = default -h localhost -p 10004
```

### node2.cfg

```text
Node2Adapter.Endpoints = default -h localhost -p 10005
```

### node3.cfg

```text
Node3Adapter.Endpoints = default -h localhost -p 10006
```

## 13. Compilación

Desde la carpeta principal del proyecto se puede compilar utilizando Gradle.

En Windows:

```bash
.\gradlew clean build
```

La compilación utilizada durante el desarrollo terminó correctamente con:

```text
BUILD SUCCESSFUL
```

## 14. Ejecución

Para ejecutar la implementación completa se deben iniciar los nodos en el siguiente orden.

### 1. Nodo 4 - Persistencia

Clase:

```text
com.apexstore.persistencia.PersistenciaServer
```

Puerto:

```text
10004
```

### 2. Nodo 2 - Backend

Clase:

```text
com.apexstore.ApexStoreNode2Server
```

Puerto:

```text
10005
```

### 3. Nodo 3 - Pasarelas

Clase:

```text
com.apexstore.ApexStoreNode3Server
```

Puerto:

```text
10006
```

### 4. Cliente de pruebas

Clase:

```text
com.apexstore.ApexStoreTestClient
```

El cliente permite realizar solicitudes de prueba utilizando los diferentes métodos de pago.

## 15. Pruebas automatizadas

El proyecto también contiene pruebas para verificar el registro de las estrategias de pago.

Para ejecutarlas:

```bash
.\gradlew test
```

## 16. Consideraciones

Las pasarelas de pago utilizadas en este proyecto son simulaciones realizadas únicamente para probar la arquitectura y el flujo de comunicación.

No se utilizan credenciales reales ni se realizan conexiones con servicios financieros externos.

La persistencia también se encuentra simulada mediante almacenamiento en memoria. Por esta razón, las transacciones almacenadas se pierden cuando se detiene el proceso del Nodo 4.


https://github.com/Juangarces1201/ApexStore-ICE
