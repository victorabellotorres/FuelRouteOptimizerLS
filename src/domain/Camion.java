package domain;

import main.*;

public class Camion {

    // Límites para todos los camiones
    public static int MAX_VIAJES = Constants.MAX_VIAJES;
    public static int MAX_KM = Constants.MAX_KM;

    // Atributos
    private int kmRestantes;
    private int viajesRestantes;
    private Pair[] viajes;

    // Constructora por defecto
    public Camion() {
        viajes = new Pair[MAX_VIAJES];
        for (int i = 0; i < MAX_VIAJES; ++i)
            viajes[i] = new Pair(-1, -1);
        kmRestantes = MAX_KM;
        viajesRestantes = MAX_VIAJES;
    }

    // Constructora por copia (profunda)
    public Camion(Camion camion) {
        this.kmRestantes = camion.kmRestantes;
        this.viajesRestantes = camion.viajesRestantes;
        this.viajes = new Pair[MAX_VIAJES];
        for (int i = 0; i < MAX_VIAJES; ++i) {
            Pair p = camion.getViajes()[i];
            this.viajes[i] = new Pair(p.first, p.second);
        }
    }

    // Clon rápido
    public Camion clone() {
        return new Camion(this);
    }

    // Getters y Setters
    public int getKmRestantes() { return kmRestantes; }
    public void setKmRestantes(int kmRestantes) { this.kmRestantes = kmRestantes; }

    public int getViajesRestantes() { return viajesRestantes; }
    public void setViajesRestantes(int viajesRestantes) { this.viajesRestantes = viajesRestantes; }

    public Pair[] getViajes() { return viajes; }
    public void setViajes(Pair[] viajes) { this.viajes = viajes; }

    public int getKmUsados() { return MAX_KM - kmRestantes; }

    // Añadir viaje si hay capacidad
    public void addViaje(int peticion, int dia, int km) {
        if (viajesRestantes > 0 && kmRestantes >= km) {
            viajes[MAX_VIAJES - viajesRestantes] = new Pair(peticion, dia);
            viajesRestantes--;
            kmRestantes -= km;
        }
    }

    public void recalcularViajesRestantes() {
        int usados = 0;
        for (Pair v : viajes) {
            if (v.first != -1 || v.second != -1)
                usados++;
        }
        this.viajesRestantes = MAX_VIAJES - usados;
        if (this.viajesRestantes < 0) this.viajesRestantes = 0;
    }


    // Representación legible
    @Override
  
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int kmUsados = MAX_KM - kmRestantes;
        sb.append("[KmUsados=").append(kmUsados)
        .append(", KmRestantes=").append(kmRestantes)
        .append(", ViajesRestantes=").append(viajesRestantes)
        .append(", Viajes={");
        for (int i = 0; i < MAX_VIAJES; ++i) {
            sb.append(viajes[i].toString());
            if (i < MAX_VIAJES - 1) sb.append(", ");
        }
        sb.append("}]");
        return sb.toString();
    }

}
 