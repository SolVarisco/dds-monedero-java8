package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    cuenta.poner(1500);
    assertEquals(cuenta.getSaldo(), 1500);
  }

  @Test
  void PonerMontoNegativoTiraUnaExcepxion() {
    assertThrows(MontoNegativoException.class, () -> cuenta.poner(-1500));
  }

  @Test
  void SePuedenHacerTresDepositosPorDiaYElSaldoSeCalculaBien() {
    cuenta.poner(1500);
    cuenta.poner(456);
    cuenta.poner(1900);
    assertEquals(cuenta.getSaldo(), 3856);
  }

  @Test
  void LosMovimientosDeLaCuentaSeCreanBien() {
    cuenta.poner(1500);
    cuenta.poner(456);
    cuenta.poner(1900);
    cuenta.sacar(500);
    assertEquals(cuenta.getMovimientos().size(), 4);
  }

  @Test
  void NoSePuedenHacerMasDeTresDepositosPorDia() {
    assertThrows(MaximaCantidadDepositosException.class, () -> {
      cuenta.poner(1500);
      cuenta.poner(456);
      cuenta.poner(1900);
      cuenta.poner(245);
    });
  }

  @Test
  void NoSePuedeEstraerMasDineroQueElPresenteEnLaCuenta() {
    assertThrows(SaldoMenorException.class, () -> {
      cuenta.setSaldo(90);
      cuenta.sacar(1001);
    });
  }

  @Test
  public void NoSePuedeExtraerMasDelMaxiomoDiario() {
    assertThrows(MaximoExtraccionDiarioException.class, () -> {
      cuenta.setSaldo(5000);
      cuenta.sacar(1001);
    });
  }

  @Test
  public void NoSePuedeExtraerSaldoNegativo() {
    assertThrows(MontoNegativoException.class, () -> cuenta.sacar(-500));
  }

  @Test
  public void SePuedeExtraerSaldoPositivoMenorAlMaximoYMenorAlPresenteEnLaCuenta() {
    cuenta.setSaldo(5000);
    cuenta.sacar(900);
    assertEquals(cuenta.getSaldo(), 4100);
  }

  @Test
  public void SePuedeExtraer1000SiHayMasDe1000ENLaCuenta() {
    cuenta.setSaldo(5000);
    cuenta.sacar(1000);
    assertEquals(cuenta.getSaldo(), 4000);
  }

  @Test
  public void LosMontosExtraidosSeSUmanEnCasoDeHaberseRealizadoMasDeUnaExtraccionPorDia(){
    cuenta.setSaldo(5000);
    cuenta.sacar(300);
    cuenta.sacar(200);
    assertEquals(cuenta.getMontoExtraidoA(LocalDate.now()), 500);
  }

  @Test
  public void LosMontosExtraidosNoSeSUmanEnCasoDeHaberseRealizadoEnDistintosDias(){
    cuenta.setSaldo(5000);
    cuenta.sacar(300);
    cuenta.agregarMovimiento(LocalDate.now().minusDays(20), 400, false);
    assertEquals(cuenta.getMontoExtraidoA(LocalDate.now().minusDays(20)), 400);
  }

  @Test
  public void UnMovimientoEsDeLaFechaEnQueSeCreo(){
    Movimiento movimiento = new Movimiento(LocalDate.now().minusDays(20), 700, true);
    Assertions.assertTrue(movimiento.esDeLaFecha(LocalDate.now().minusDays(20)));
  }


}