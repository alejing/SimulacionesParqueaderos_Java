
package app.ui;

import java.math.BigInteger;

/**
 *
 * @author alejandrofranco
 */
public class mms {

    public mms() {
    }
    
    // ro: Intensidad de tráfico MMS
    public double intencidadDeTraficoMMS(double lambda, double miu, int servidores){
        return lambda / (servidores * miu);
    }
    // Po: Probabilidad de que es sistema este vacío MMS
    public double PoMMS(double lambda, double miu, int servidores, double intencidadDeTrafico){
        
        double a=0, b=0;
        // SUM n=0 hasta S-1 de: ((lambda/miu)^2)/n!
        for(int n = 0; n < servidores; n++){
            a += Math.pow((lambda/miu), n)/factorial(n).doubleValue();
        }
        // (lambda/miu)^servidores / (servidores ! * (1 - ro))
        b = Math.pow((lambda/miu), servidores) / (factorial(servidores).doubleValue() * (1 - intencidadDeTrafico));
        
        return 1/(a+b);
    }
    // Lq: Valor esperado de número de clientes en la cola MMS
    public double LqMMS(double lambda, double miu, int servidores, double Po, double intencidadDeTrafico){
        return (Math.pow((lambda/miu), servidores) * Po * intencidadDeTrafico)/(factorial(servidores).doubleValue() * Math.pow((1 - intencidadDeTrafico), 2));
    }
    // Wq: Tiempo medio de espera en la cola MMS
    public double WqMMS(double Lq, double lambda){
        return Lq/lambda;
    }
    // W: Tiempo medio de espera en el sistema MMS
    public double WMMS(double wq, double miu){
        return wq + (1/miu);
    }
    // L: Valor esperado de número de clientes en el sistema MMS
    public double LMMS(double lambda, double w){
        return lambda * w;
    }
    // Pw: Probabilidad de que un nuevo cliente tenga que esperar MMS
    public double PwMMS(double lambda, double miu, int servidores, double Po, double intencidadDeTrafico){
        return (Math.pow((lambda/miu), servidores))*((Po)/(factorial(servidores).doubleValue()*(1-intencidadDeTrafico)));
    }
    
     // Factorial de un número
    public static BigInteger factorial(int n) {
        BigInteger f = new BigInteger("1");
        for (int i = 1; i <= n; i++) {
            f = f.multiply(new BigInteger(i + ""));
        }
        return f;
    }
    
}
