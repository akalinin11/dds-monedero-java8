package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Cuenta {

  private double saldo = 0;
  private List<Movimiento> movimientos = new ArrayList<>();

  // Esto reemplaza a los constructores de "cuenta" y al setMovimientos
  public Cuenta(Double saldo, List<Movimiento> movimientos) {
    this.saldo = saldo;
    this.movimientos = movimientos;
  }
  public Cuenta2(Double saldo, List<Movimiento> movimientos) {
    this.saldo = saldo;
    this.movimientos = movimientos;
  }

  public Cuenta() {
    saldo = 0;
  }
  /*
  public Cuenta(double montoInicial) {
    saldo = montoInicial;
  }

  public void setMovimientos(List<Movimiento> movimientos) {
    this.movimientos = movimientos;
  }
  */

  public void poner(double cuanto) {
    if (cuanto <= 0) {
      throw new MontoNegativoException(cuanto + ": el monto a ingresar debe ser un valor positivo");
    }
    //Code Smell corregido: Message Chains
    if (this.cantidadDepositosDeUnaFecha(LocalDate.now()) >= 3) {
      throw new MaximaCantidadDepositosException("Ya excedio los " + 3 + " depositos diarios");
    }
    this.concretarMovimiento(cuanto, true);

  }


  public Long cantidadDepositosDeUnaFecha(LocalDate fecha) {
    return movimientos.stream().filter(movimiento -> movimiento.fueDepositado(fecha)).count();
  } //No falta usar la funcion del getter de movimientos cuando estas dentro de la clase


  public void sacar(double cuanto) {
    if (cuanto <= 0) {
      throw new MontoNegativoException(cuanto + ": el monto a ingresar debe ser un valor positivo");
    }
    if (saldo < cuanto) { //NO usar getter cuando estas dentro de la clase
      throw new SaldoMenorException("No puede sacar mas de " + saldo + " $");
    }
    //Code Smell: temporary field
    /*
    double montoExtraidoHoy = getMontoExtraidoA(LocalDate.now());
    double limite = 1000 - montoExtraidoHoy;
    */
    if (cuanto > limiteRestante()) {
      throw new MaximoExtraccionDiarioException("No puede extraer mas de $ " + limiteDiario
          + " diarios, límite: " + limiteRestante());
    }
    this.concretarMovimiento(cuanto, false);
  }

  double limiteDiario = 1000;

  public double limiteRestante() {
    return limiteDiario - getMontoExtraidoA(LocalDate.now());
  }

  public void agregarMovimiento(LocalDate fecha, double cuanto, boolean esDeposito) {
    Movimiento movimiento = new Movimiento(fecha, cuanto, esDeposito);
    movimientos.add(movimiento);
  }

  public double getMontoExtraidoA(LocalDate fecha) {
    return extraccionesDeUnaFecha(fecha).mapToDouble(Movimiento::getMonto).sum();
  } // Code Smell: Long method

  public Stream<Movimiento> extraccionesDeUnaFecha(LocalDate fecha) {
    return movimientos.stream().filter(movimiento -> movimiento.fueExtraido(fecha));
  } // Code Smell: Duplicated code

  public List<Movimiento> getMovimientos() {
    return movimientos;
  }

  public double getSaldo() {
    return saldo;
  }

  public void setSaldo(double saldo) {
    this.saldo = saldo;
  }

  public void concretarMovimiento(double cuanto, boolean esDeposito) {
    this.agregarMovimiento(LocalDate.now(), cuanto, esDeposito);
    if(esDeposito){
      saldo+= cuanto;
    }else{
      saldo-=cuanto;
    }
  }

  public Movimiento ultimoMovimiento(){
    return movimientos.get(movimientos.size() - 1);
  }
}



