package dds.monedero.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Extraccion extends Movimiento{

  public Extraccion(LocalDate fecha, BigDecimal monto) {
    super(fecha, monto, false);
  }

  @Override
  void operacionValida(BigDecimal monto, Cuenta cuenta) {
    cuenta.extraccionValida(monto);
  }

  @Override
  BigDecimal montoModificador() {
    return getMonto().negate();
  }
}

