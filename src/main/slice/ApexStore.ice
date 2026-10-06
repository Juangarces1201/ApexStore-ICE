module ApexStore
{
    struct SolicitudCobro
    {
        string idTransaccion;
        double monto;
        string moneda;
        string metodoPago;
    };

    struct AcuseCobro
    {
        string idTransaccion;
        bool aceptado;
        string mensaje;
    };

    struct ResultadoPago
    {
        string idTransaccion;
        bool exitoso;
        string mensaje;
    };

    struct Transaccion
    {
        string idTransaccion;
        double monto;
        string moneda;
        string metodoPago;
        string estado;
    };

    interface PersistenciaTransacciones
    {
        void guardarTransaccion(Transaccion transaccion);
        void actualizarEstado(string idTransaccion, string estado);
        bool existeTransaccion(string idTransaccion);
        Transaccion obtenerTransaccion(string idTransaccion);
    };

    interface ReceptorResultadoPago
    {
        void recibirResultado(ResultadoPago resultado);
    };

    interface EstrategiaPago
    {
        AcuseCobro procesarCobro(SolicitudCobro solicitud);
    };

    interface GestorTransacciones extends ReceptorResultadoPago
    {
        AcuseCobro registrarYProcesar(SolicitudCobro solicitud);
    };

    interface ProcesadorPagosContexto
    {
        AcuseCobro procesar(SolicitudCobro solicitud);
    };

    interface ServicioCheckout
    {
        AcuseCobro gestionarComprasHttp(SolicitudCobro solicitud);
    };
};