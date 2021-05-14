package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MonederoTest {
  private Cuenta cuenta;

  @BeforeEach
  void init() {
    cuenta = new Cuenta();
  }

  @Test
  void SePuedeHacerMenosDeTresDepositosPorDiaYElSaldoSeCalculaBien() {
    cuenta.poner(new BigDecimal(1500));
    assertEquals(cuenta.getSaldo(), new BigDecimal(1500));
  }

  @Test
  void PonerMontoNegativoTiraUnaExcepxion() {
    assertThrows(MontoNegativoException.class, () -> cuenta.poner(new BigDecimal(-1500)));
  }

  @Test
  void SePuedenHacerTresDepositosPorDiaYElSaldoSeCalculaBien() {
    cuenta.poner(new BigDecimal(1500));
    cuenta.poner(new BigDecimal(456));
    cuenta.poner(new BigDecimal(1900));
    assertEquals(cuenta.getSaldo(), new BigDecimal(3856));
  }

  @Test
  void LosMovimientosDeLaCuentaSeCreanBien() {
    cuenta.poner(new BigDecimal(1500));
    cuenta.poner(new BigDecimal(456));
    cuenta.poner(new BigDecimal(1900));
    cuenta.sacar(new BigDecimal(500));
    assertEquals(cuenta.getMovimientos().size(), 4);
  }

  @Test
  void NoSePuedenHacerMasDeTresDepositosPorDia() {
    assertThrows(MaximaCantidadDepositosException.class, () -> {
      cuenta.poner(new BigDecimal(1500));
      cuenta.poner(new BigDecimal(456));
      cuenta.poner(new BigDecimal(1900));
      cuenta.poner(new BigDecimal(245));
    });
  }

  @Test
  void NoSePuedeEstraerMasDineroQueElPresenteEnLaCuenta() {
    assertThrows(SaldoMenorException.class, () -> {
      cuenta.setSaldo(new BigDecimal(90));
      cuenta.sacar(new BigDecimal(700));
    });
  }

  @Test
  public void NoSePuedeExtraerMasDelMaxiomoDiario() {
    assertThrows(MaximoExtraccionDiarioException.class, () -> {
      cuenta.setSaldo(new BigDecimal(5000));
      cuenta.sacar(new BigDecimal(1001));
    });
  }

  @Test
  public void NoSePuedeExtraerSaldoNegativo() {
    assertThrows(MontoNegativoException.class, () -> cuenta.sacar(new BigDecimal(-500)));
  }

  @Test
  public void SePuedeExtraerSaldoPositivoMenorAlMaximoYMenorAlPresenteEnLaCuenta() {
    cuenta.setSaldo(new BigDecimal(5000));
    cuenta.sacar(new BigDecimal(900));
    assertEquals(cuenta.getSaldo(), new BigDecimal(4100));
  }

  @Test
  public void SePuedeExtraer1000SiHayMasDe1000ENLaCuenta() {
    cuenta.setSaldo(new BigDecimal(5000));
    cuenta.sacar(new BigDecimal(1000));
    assertEquals(cuenta.getSaldo(), new BigDecimal(4000));
  }

  @Test
  public void LosMontosExtraidosSeSUmanEnCasoDeHaberseRealizadoMasDeUnaExtraccionPorDia(){
    cuenta.setSaldo(new BigDecimal(5000));
    cuenta.sacar(new BigDecimal(300));
    cuenta.sacar(new BigDecimal(200));
    assertEquals(cuenta.getMontoExtraidoA(LocalDate.now()), new BigDecimal(500));
  }

  @Test
  public void LosMontosExtraidosNoSeSUmanEnCasoDeHaberseRealizadoEnDistintosDias(){
    cuenta.setSaldo(new BigDecimal(5000));
    cuenta.sacar(new BigDecimal(300));
    cuenta.agregarMovimiento(new Movimiento(LocalDate.now().minusDays(20), new BigDecimal(400), false));
    assertEquals(cuenta.getMontoExtraidoA(LocalDate.now().minusDays(20)), new BigDecimal(400));
  }

  @Test
  public void UnMovimientoEsDeLaFechaEnQueSeCreo(){
    Movimiento movimiento = new Movimiento(LocalDate.now().minusDays(20), new BigDecimal(700), true);
    Assertions.assertTrue(movimiento.esDeLaFecha(LocalDate.now().minusDays(20)));
  }

}