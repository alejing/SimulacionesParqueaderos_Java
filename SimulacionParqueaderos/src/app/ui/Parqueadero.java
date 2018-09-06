
package app.ui;

/**
 *
 * @author alejandrofranco
 */
public class Parqueadero {
    
    private int id;
    private double horaInicioJornadaParqueadero;
    private double horaFinJornadaParqueadero;
    private double cuposTotales;
    private double porcentajeCuposDiscapacidad;
    private double miu;
    private double[] horasPico = new double[6];
    private int t;
    private int lambdaAnterior;
    private int cuposDinamicos;

    public Parqueadero(int id, double horaInicioJornadaParqueadero, 
                       double horaFinJornadaParqueadero, 
                       double cuposTotales, double porcentajeCuposDiscapacidad, 
                       double miu, int t, int lambdaAnterior, 
                       int cuposDinamicos) {
        this.id = id;
        this.horaInicioJornadaParqueadero = horaInicioJornadaParqueadero;
        this.horaFinJornadaParqueadero = horaFinJornadaParqueadero;
        this.cuposTotales = cuposTotales;
        this.porcentajeCuposDiscapacidad = porcentajeCuposDiscapacidad;
        this.miu = miu;
        this.t = t;
        this.lambdaAnterior = lambdaAnterior;
        this.cuposDinamicos = cuposDinamicos;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
   
    public double getHoraInicioJornadaParqueadero() {
        return horaInicioJornadaParqueadero;
    }

    public void setHoraInicioJornadaParqueadero(double horaInicioJornadaParqueadero) {
        this.horaInicioJornadaParqueadero = horaInicioJornadaParqueadero;
    }

    public double getHoraFinJornadaParqueadero() {
        return horaFinJornadaParqueadero;
    }

    public void setHoraFinJornadaParqueadero(double horaFinJornadaParqueadero) {
        this.horaFinJornadaParqueadero = horaFinJornadaParqueadero;
    }

    public double getCuposTotales() {
        return cuposTotales;
    }

    public void setCuposTotales(double cuposTotales) {
        this.cuposTotales = cuposTotales;
    }

    public double getPorcentajeCuposDiscapacidad() {
        return porcentajeCuposDiscapacidad;
    }

    public void setPorcentajeCuposDiscapacidad(double porcentajeCuposDiscapacidad) {
        this.porcentajeCuposDiscapacidad = porcentajeCuposDiscapacidad;
    }

    public double getMiu() {
        return miu;
    }

    public void setMiu(double miu) {
        this.miu = miu;
    }

    public double[] getHorasPico() {
        return horasPico;
    }

    public void setHorasPico(double[] horasPico) {
        this.horasPico = horasPico;
    }
    public int getT() {
        return t;
    }

    public void setT(int t) {
        this.t = t;
    }

    public int getLambdaAnterior() {
        return lambdaAnterior;
    }

    public void setLambdaAnterior(int lambdaAnterior) {
        this.lambdaAnterior = lambdaAnterior;
    }
    
     public int getCuposDinamicos() {
        return cuposDinamicos;
    }

    public void setCuposDinamicos(int cuposDinamicos) {
        this.cuposDinamicos = cuposDinamicos;
    }

}
