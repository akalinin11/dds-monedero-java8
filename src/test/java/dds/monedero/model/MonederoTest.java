package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class MonederoTest {
  private Cuenta cuenta;

  @BeforeEach
  void init() {
    cuenta = new Cuenta();
  }

  @Test
  void UnaCuentaSabeDecirLaCantidadDeDepositosDeUnaFecha(){
    cuenta.poner(8);
    cuenta.sacar(4);
    cuenta.poner(23);
    cuenta.sacar(21);
    cuenta.poner(52);
    assertEquals(cuenta.cantidadDepositosDeUnaFecha(LocalDate.now()), 3);
  }

  @Test
  void UnaCuentaSabeDecirLaCantidadDeExtraccionesDeUnaFecha(){
    cuenta.poner(100);
    cuenta.sacar(23);
    cuenta.sacar(58);
    cuenta.poner(1000);
    assertEquals(cuenta.extraccionesDeUnaFecha(LocalDate.now()).count(), 2);
  }

  @Test
  void UnaCuentaEsCapazDeCargarUnMovimiento(){
    cuenta.concretarMovimiento(100, true);
    assertEquals(cuenta.ultimoMovimiento().getMonto(), 100);
    assertTrue(cuenta.ultimoMovimiento().isDeposito());
    assertEquals(cuenta.ultimoMovimiento().getFecha(), LocalDate.now());
  }

  @Test
  void Poner() {
    cuenta.poner(1500);
    assertEquals(cuenta.getSaldo(), 1500);
  }

  @Test
  void PonerMontoNegativo() {
    assertThrows(MontoNegativoException.class, () -> cuenta.poner(-1500));
  }

  @Test
  void TresDepositos() {
    cuenta.poner(1500);
    cuenta.poner(456);
    cuenta.poner(1900);
    assertEquals(cuenta.getSaldo(), 3856);
  }

  @Test
  void MasDeTresDepositos() {
    assertThrows(MaximaCantidadDepositosException.class, () -> {
          cuenta.poner(1500);
          cuenta.poner(456);
          cuenta.poner(1900);
          cuenta.poner(245);
    });
  }

  @Test
  void ExtraerMasQueElSaldo() {
    assertThrows(SaldoMenorException.class, () -> {
          cuenta.setSaldo(90);
          cuenta.sacar(1001);
    });
  }

  @Test
  public void ExtraerMasDe1000() {
    assertThrows(MaximoExtraccionDiarioException.class, () -> {
      cuenta.setSaldo(5000);
      cuenta.sacar(1001);
    });
  }

  @Test
  public void ExtraerMontoNegativo() {
    assertThrows(MontoNegativoException.class, () -> cuenta.sacar(-500));
  }

}