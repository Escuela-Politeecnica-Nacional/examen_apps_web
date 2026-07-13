package modelo;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "clientes")
public class Cliente implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer edad;

    @Column(name = "ingresos_declarados", nullable = false, precision = 15, scale = 2)
    private BigDecimal ingresosDeclarados;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getEdad() {
        return edad;
    }

    public void setEdad(Integer edad) {
        this.edad = edad;
    }

    public BigDecimal getIngresosDeclarados() {
        return ingresosDeclarados;
    }

    public void setIngresosDeclarados(BigDecimal ingresosDeclarados) {
        this.ingresosDeclarados = ingresosDeclarados;
    }
}
