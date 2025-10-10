package domain;

import java.util.ArrayList;

import main.*;

public class Camion {

    // Límites para todos los camiones
    public static int MAX_VIAJES = Constants.MAX_VIAJES;
    public static int MAX_KM = Constants.MAX_KM;

    // Atributos
    private int kmRestantes;
    private int viajesRestantes;
    private Pair[] viajes; // viaje = {idPeticion1, idPeticion2}

    // Constructores
    /**
     * Constructora por defecto.
     */
    public Camion() {
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
        this.kmRestantes = camion.kmRestantes;
        this.viajesRestantes = camion.viajesRestantes;
        this.viajes = camion.getViajes();
    }

    // Getters y Setters
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
    public void addViaje(int peticion, int dia, int km) {
        if (viajesRestantes > 0 && kmRestantes >= km) {
            viajes[MAX_VIAJES - viajesRestantes] = new Pair(peticion, dia);
            --viajesRestantes;
            kmRestantes -= km;
        }
    }

}