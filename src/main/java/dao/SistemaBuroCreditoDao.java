package dao;

import java.math.BigDecimal;

import modelo.Cliente;

public class SistemaBuroCreditoDao {

    public String puntaje(Cliente cliente) {
        if (cliente == null || cliente.getEdad() == null || cliente.getIngresosDeclarados() == null) {
            return "0";
        }

        if (cliente.getEdad() < 18) {
            return "0";
        }

        int puntaje = 0;

        if (cliente.getEdad() <= 25) {
            puntaje += 20;
        } else if (cliente.getEdad() <= 35) {
            puntaje += 30;
        } else if (cliente.getEdad() <= 50) {
            puntaje += 25;
        } else {
            puntaje += 15;
        }

        BigDecimal ingresos = cliente.getIngresosDeclarados();
        if (ingresos.compareTo(new BigDecimal("8000")) >= 0) {
            puntaje += 70;
        } else if (ingresos.compareTo(new BigDecimal("5000")) >= 0) {
            puntaje += 55;
        } else if (ingresos.compareTo(new BigDecimal("3000")) >= 0) {
            puntaje += 40;
        } else if (ingresos.compareTo(new BigDecimal("1500")) >= 0) {
            puntaje += 25;
        } else {
            puntaje += 10;
        }

        return String.valueOf(Math.min(puntaje, 100));
    }
}
