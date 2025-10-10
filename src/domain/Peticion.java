package domain;

public class Peticion {
    private Pair gasolinera;
    private int dias; // dias que lleva la peticion esperando

    private int idCamion; // Si tiene un camion asignado > 0  y < numCamiones, si no -1
    private int idViaje;  // Si tiene un viaje asignado > 0 y < maxViajes, si no -1

    public Peticion() {
        this.gasolinera = new Pair(-1, -1);
        this.dias = -1;
        idCamion = -1;
        idViaje = -1;
    }

    public Peticion(Pair g, int d) {
        this.gasolinera = g;
        this.dias = d;
        idCamion = -1;
        idViaje = -1;
    }

    public Peticion(Peticion p) {
        this.gasolinera = new Pair(p.gasolinera.first, p.gasolinera.second);
        this.dias = p.dias;
        this.idCamion = p.idCamion;
        this.idViaje = p.idViaje;
    }
    
    // getters i setters
    public Pair getGasolinera() {
        return gasolinera;
    }
    public void setGasolinera(Pair gasolinera) {
        this.gasolinera = gasolinera;
    }
    public int getDias() {
        return dias;
    }
    public int getIdCamion() {
        return idCamion;
    }
    public void setIdCamion(int idCamion) {
        this.idCamion = idCamion;
    }
    public int getIdViaje() {
        return idViaje;
    }
    public void setIdViaje(int idViaje) {
        this.idViaje = idViaje;
    }
}
