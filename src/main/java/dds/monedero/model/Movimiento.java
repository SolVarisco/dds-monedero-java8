package dds.monedero.model;

import java.math.BigDecimal;
import java.time.LocalDate;

abstract class Movimiento {
  private LocalDate fecha;
  private BigDecimal monto;
  private boolean esDeposito;

  public Movimiento(LocalDate fecha, BigDecimal monto, boolean esDeposito) {
    this.fecha = fecha;
    this.monto = monto;
    this.esDeposito = esDeposito;
  }

  public BigDecimal getMonto() {
    return monto;
  }

  public LocalDate getFecha() {
    return fecha;
  }

  public boolean esDeLaFecha(LocalDate fecha) {
    return this.fecha.equals(fecha);
  }

  public boolean isDeposito() {
    return esDeposito;
  }

  public boolean isExtraccion() {
    return !esDeposito;
  }

  abstract void operacionValida(BigDecimal monto, Cuenta cuenta);

  abstract BigDecimal montoModificador();
}


