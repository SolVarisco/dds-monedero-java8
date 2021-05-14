package dds.monedero.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Deposito extends Movimiento{

  public Deposito(LocalDate fecha, BigDecimal monto) {
    super(fecha, monto, true);
  }

  @Override
  void operacionValida(Cuenta cuenta){
    cuenta.depositoValido();
  }

  @Override
  public BigDecimal montoModificador(){
    return getMonto();
  }
}
