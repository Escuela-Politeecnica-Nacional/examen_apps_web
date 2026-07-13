package dao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import modelo.Cliente;
import modelo.EstadoCredito;
import modelo.ResultadoEvaluacionCredito;
import modelo.SolicitudCredito;

public class SistemaDao {

    private final SistemaBuroCreditoDao sistemaBuroCreditoDao = new SistemaBuroCreditoDao();

    public int obtenerPuntaje(Cliente cliente) {
        return Integer.parseInt(sistemaBuroCreditoDao.puntaje(cliente));
    }

    public ResultadoEvaluacionCredito evaluarCredito(Cliente cliente, BigDecimal montoSolicitado, Integer plazoMeses) {
        BigDecimal monto = montoSolicitado == null ? BigDecimal.ZERO : montoSolicitado;
        Integer plazo = plazoMeses == null || plazoMeses <= 0 ? 1 : plazoMeses;
        BigDecimal cuotaMensual = monto.divide(BigDecimal.valueOf(plazo), 2, RoundingMode.HALF_UP);
        int puntaje = obtenerPuntaje(cliente);

        EstadoCredito estado;
        String mensaje;

        if (cliente == null || cliente.getEdad() == null || cliente.getEdad() < 18 || cliente.getIngresosDeclarados() == null || cuotaMensual.compareTo(cliente.getIngresosDeclarados().multiply(new BigDecimal("0.40"))) > 0) {
            estado = EstadoCredito.RECHAZADO;
            mensaje = "No es posible otorgar el credito en este momento. Puedes continuar con tarjeta o transferencia.";
        } else if (puntaje >= 75) {
            estado = EstadoCredito.APROBADO;
            mensaje = "Credito aprobado. La solicitud fue registrada como APROBADA.";
        } else if (puntaje >= 50) {
            estado = EstadoCredito.EN_REVISION;
            mensaje = "Tu solicitud sera evaluada por un asesor en un plazo maximo de 24 horas.";
        } else {
            estado = EstadoCredito.RECHAZADO;
            mensaje = "No es posible otorgar el credito en este momento. Puedes continuar con tarjeta o transferencia.";
        }

        SolicitudCredito solicitudCredito = new SolicitudCredito();
        solicitudCredito.setCliente(cliente);
        solicitudCredito.setMontoSolicitado(monto);
        solicitudCredito.setPlazoMeses(plazo);
        solicitudCredito.setCuotaMensual(cuotaMensual);
        solicitudCredito.setPuntaje(puntaje);
        solicitudCredito.setEstado(estado);
        solicitudCredito.setMensaje(mensaje);
        solicitudCredito.setFechaSolicitud(LocalDateTime.now());

        guardarSolicitud(solicitudCredito);

        return new ResultadoEvaluacionCredito(estado, mensaje, solicitudCredito);
    }

    private void guardarSolicitud(SolicitudCredito solicitudCredito) {
        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();
            if (solicitudCredito.getCliente() != null && solicitudCredito.getCliente().getId() == null) {
                entityManager.persist(solicitudCredito.getCliente());
            } else if (solicitudCredito.getCliente() != null) {
                solicitudCredito.setCliente(entityManager.merge(solicitudCredito.getCliente()));
            }

            entityManager.persist(solicitudCredito);
            transaction.commit();
        } catch (RuntimeException ex) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw ex;
        } finally {
            entityManager.close();
        }
    }
}
