
package app.ui;


public class ParqueaderoSIM {
    
    private int idParqueadero;
    private double latitud, longitud;
    private int valor, ofertas, servicios, llaves, porcentaje;

    public ParqueaderoSIM(int idParqueadero, double latitud, double longitud, int valor, int ofertas, int servicios, int llaves) {
        this.idParqueadero = idParqueadero;
        this.latitud = latitud;
        this.longitud = longitud;
        this.valor = valor;
        this.ofertas = ofertas;
        this.servicios = servicios;
        this.llaves = llaves;
        this.porcentaje = 0;
    }

    public int getIdParqueadero() {
        return idParqueadero;
    }

    public void setIdParqueadero(int idParqueadero) {
        this.idParqueadero = idParqueadero;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public int getValor() {
        return valor;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }

    public int getOfertas() {
        return ofertas;
    }

    public void setOfertas(int ofertas) {
        this.ofertas = ofertas;
    }

    public int getServicios() {
        return servicios;
    }

    public void setServicios(int servicios) {
        this.servicios = servicios;
    }

    public int getLlaves() {
        return llaves;
    }

    public void setLlaves(int llaves) {
        this.llaves = llaves;
    }

    public int getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(int porcentaje) {
        this.porcentaje = porcentaje;
    }

}
