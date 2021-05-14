package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Cuenta {

  private BigDecimal saldo;
  private List<Movimiento> movimientos = new ArrayList<>();

  public Cuenta() {
    saldo = new BigDecimal(0);
  }

  public Cuenta(BigDecimal montoInicial) {
    saldo = montoInicial;
  }

  public void setMovimientos(List<Movimiento> movimientos) {
    this.movimientos = movimientos;
  }

  public void poner(BigDecimal cuanto) {
    realizarOperacion(new Deposito(LocalDate.now(), cuanto));
  }

  public void sacar(BigDecimal cuanto) {
    realizarOperacion(new Extraccion(LocalDate.now(), cuanto));
  }

  private void realizarOperacion(Movimiento movimiento){
    if (movimiento.getMonto().compareTo(new BigDecimal(0)) != 1) {
      throw new MontoNegativoException(movimiento.getMonto() + ": el monto a ingresar debe ser un valor positivo");
    }
    movimiento.operacionValida(this);
    agregarMovimiento(movimiento);
  }

  public void agregarMovimiento(Movimiento movimiento) {
    actualizarSaldo(movimiento);
    movimientos.add(movimiento);
  }

  public BigDecimal getMontoExtraidoA(LocalDate fecha) {
    return getMovimientos().stream()
        .filter(movimiento -> movimiento.isExtraccion() && movimiento.getFecha().equals(fecha))
        .map(Movimiento::getMonto)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  public void depositoValido(){
    if (getMovimientos().stream().filter(movimiento -> movimiento.isDeposito()).count() >= 3) {
      throw new MaximaCantidadDepositosException("Ya excedio los " + 3 + " depositos diarios");
    }
  }

  public void extraccionValida(BigDecimal monto){
    if (getSaldo().subtract(monto).compareTo(new BigDecimal(0)) == -1) {
      throw new SaldoMenorException("No puede sacar mas de " + getSaldo() + " $");
    }
    BigDecimal montoExtraidoHoy = getMontoExtraidoA(LocalDate.now());
    BigDecimal limite = new BigDecimal(1000).subtract(montoExtraidoHoy);
    if (monto.compareTo(limite) == 1) {
      throw new MaximoExtraccionDiarioException("No puede extraer mas de $ " + 1000
          + " diarios, límite: " + limite);
    }
  }

  public List<Movimiento> getMovimientos() {
    return movimientos;
  }

  public BigDecimal getSaldo() {
    return saldo;
  }

  public void setSaldo(BigDecimal saldo) {
    this.saldo = saldo;
  }

  public void actualizarSaldo(Movimiento movimiento) {
    saldo = saldo.add(movimiento.montoModificador());
  }
}