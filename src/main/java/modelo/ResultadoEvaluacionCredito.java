package modelo;

public class ResultadoEvaluacionCredito {

    private final EstadoCredito estado;
    private final String mensaje;
    private final SolicitudCredito solicitudCredito;

    public ResultadoEvaluacionCredito(EstadoCredito estado, String mensaje, SolicitudCredito solicitudCredito) {
        this.estado = estado;
        this.mensaje = mensaje;
        this.solicitudCredito = solicitudCredito;
    }

    public EstadoCredito getEstado() {
        return estado;
    }

    public String getMensaje() {
        return mensaje;
    }

    public SolicitudCredito getSolicitudCredito() {
        return solicitudCredito;
    }
}
