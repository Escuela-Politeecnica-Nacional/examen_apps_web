package dao;

import java.util.concurrent.ThreadLocalRandom;

public class SistemaBuroCreditoDao {

    public String puntaje() {
        int puntaje = ThreadLocalRandom.current().nextInt(0, 101);
        return String.valueOf(puntaje);
    }
}
