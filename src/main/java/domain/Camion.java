package domain;

import java.util.ArrayList;

import main.*;

public class Camion {

    // Límites para todos los camiones
    public static int MAX_VIAJES = Constants.MAX_VIAJES;
    public static int MAX_KM = Constants.MAX_KM;

    // Atributos
    private Pair posicion; // (x,y) del camión
    private int kmRestantes;
    private int viajesRestantes;
    private Pair[] viajes; // viaje = {idPeticion1, idPeticion2}

    // Constructores
    /**
     * Constructora por defecto.
     */
    public Camion(Pair posicion) {
        this.posicion = posicion;
        viajes = new Pair[MAX_VIAJES];
        for (int i = 0; i < MAX_VIAJES; ++i) {
            viajes[i] = new Pair(-1, -1);
        }
        kmRestantes = MAX_KM;
        viajesRestantes = MAX_VIAJES;
    }

    /**
     * Constructora por copia.
     */
    public Camion(Camion camion) {
        this.posicion = new Pair(camion.posicion.first, camion.posicion.second);
        this.kmRestantes = camion.kmRestantes;
        this.viajesRestantes = camion.viajesRestantes;

        this.viajes = new Pair[MAX_VIAJES];
        for (int i = 0; i < MAX_VIAJES; ++i) {
            this.viajes[i] = new Pair(camion.viajes[i].first, camion.viajes[i].second);
        }
    }

    // Getters y Setters
    public Pair getPosicion() {
        return posicion;
    }
    public void setPosicion(Pair posicion) {
        this.posicion = posicion;
    }
    public int getKmRestantes() {
        return kmRestantes;
    }
    public void setKmRestantes(int kmRestantes) {
        this.kmRestantes = kmRestantes;
    }
    public int getViajesRestantes() {
        return viajesRestantes;
    }
    public void setViajesRestantes(int viajesRestantes) {
        this.viajesRestantes = viajesRestantes;
    }
    public Pair[] getViajes() {
        return viajes;
    }
    public void setViajes(Pair[] viajes) {
        this.viajes = viajes;
    }
    public void setViaje(int id, Pair viaje) {
        this.viajes[id] = viaje;
    }
    public void addViaje(int peticion, int dia, int km) {
        if (viajesRestantes > 0 && kmRestantes >= km) {
            viajes[MAX_VIAJES - viajesRestantes] = new Pair(peticion, dia);
            --viajesRestantes;
            kmRestantes -= km;
        }
    }

    public int getKmUsados() {
        return MAX_KM - kmRestantes;
    }
    public int getViajesUsados() {
        return MAX_VIAJES - viajesRestantes;
    }

    // Otros métodos

    // ==============================
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("posicion=").append(posicion);
        sb.append(", kmRestantes=").append(kmRestantes);
        sb.append(", viajesRestantes=").append(viajesRestantes);
        sb.append(", viajes=[");
        for (int i = 0; i < MAX_VIAJES; i++) {
            sb.append(viajes[i]);
            if (i < MAX_VIAJES - 1) sb.append(", ");
        }
        sb.append("]}");
        return sb.toString();
    }


}