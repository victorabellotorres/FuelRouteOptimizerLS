package domain;

import IA.Gasolina.*;

/**
 * Clase que representa un estado del problema de distribución de gasolina.
 * Cada camión tiene hasta MAX_VIAJES, y cada viaje puede atender hasta 2 peticiones.
 * También se mantiene un vector con los días pendientes de cada petición.
 */
public class State {

    private Camion[] camiones;
    private int[] diasPeticiones;

    private static Gasolineras gasolineras;
    private static CentrosDistribucion centrosDistribucion;

    // ==============================
    // 🔹 Constructores
    // ==============================
    public State(int numCamiones, int numPeticiones) {
        camiones = new Camion[numCamiones];
        for (int i = 0; i < numCamiones; ++i)
            camiones[i] = new Camion();

        diasPeticiones = new int[numPeticiones];
        for (int i = 0; i < numPeticiones; ++i)
            diasPeticiones[i] = -1; // -1 = no atendida aún
    }

    public State(State other) {
        camiones = new Camion[other.camiones.length];
        for (int i = 0; i < camiones.length; i++)
            camiones[i] = new Camion(other.camiones[i]); // copia profunda

        diasPeticiones = new int[other.diasPeticiones.length];
        System.arraycopy(other.diasPeticiones, 0, diasPeticiones, 0, diasPeticiones.length);
    }

    // ==============================
    // 🔹 Configuración del entorno
    // ==============================
    public static void setEntorno(Gasolineras g, CentrosDistribucion c) {
        gasolineras = g;
        centrosDistribucion = c;
    }

    // ==============================
    // 🔹 Getters
    // ==============================
    public Camion[] getCamiones() { return camiones; }
    public int[] getDiasPeticiones() { return diasPeticiones; }
    public int getNumPeticiones() { return diasPeticiones.length; }
    public int getNumCamiones() { return camiones.length; }

    // ==============================
    // 🔹 Validación global
    // ==============================
    public boolean esValido() {
        for (Camion c : camiones) {
            if (c.getKmUsados() > Camion.MAX_KM) return false;
            if (c.getViajesRestantes() < 0) return false;
        }
        return true;
    }

    // ==============================
    // 🔹 Métodos auxiliares
    // ==============================

    /**
     * Busca una petición dentro de todos los camiones y devuelve:
     * [camionIndex, viajeIndex, posicionEnPair (1=first, 2=second)].
     * Si no se encuentra, devuelve [-1, -1, -1].
     */
    private int[] buscarPeticion(int idPeticion) {
        for (int c = 0; c < camiones.length; ++c) {
            Pair[] viajes = camiones[c].getViajes();
            for (int v = 0; v < Camion.MAX_VIAJES; ++v) {
                if (viajes[v].first == idPeticion)
                    return new int[]{c, v, 1};
                if (viajes[v].second == idPeticion)
                    return new int[]{c, v, 2};
            }
        }
        return new int[]{-1, -1, -1};
    }

    /**
     * Calcula la distancia total (ida y vuelta) para un camión.
     * Usa reflexión para acceder a coordenadas de los centros, ya que
     * la clase CentroDistribucion no es pública en Gasolina.jar.
     */
    private int calcularKmCamion(int idCamion) {
        Camion c = camiones[idCamion];
        int totalKm = 0;

        int camionesPorCentro = main.Constants.CAMIONES_POR_CENTRO;

        Object centroObj = centrosDistribucion.get(idCamion / camionesPorCentro);
        double cx = 0, cy = 0;
        try {
            Object xObj = centroObj.getClass().getMethod("getCoordX").invoke(centroObj);
            Object yObj = centroObj.getClass().getMethod("getCoordY").invoke(centroObj);
            cx = ((Number) xObj).doubleValue();
            cy = ((Number) yObj).doubleValue();
        } catch (Exception e) {
            throw new RuntimeException("Error accediendo a coordenadas del centro", e);
        }

        for (Pair v : c.getViajes()) {
            if (v.first != -1) {
                Gasolinera g1 = (Gasolinera) gasolineras.get(v.first);
                totalKm += 2 * Math.sqrt(Math.pow(cx - g1.getCoordX(), 2) + Math.pow(cy - g1.getCoordY(), 2));
            }
            if (v.second != -1) {
                Gasolinera g2 = (Gasolinera) gasolineras.get(v.second);
                totalKm += 2 * Math.sqrt(Math.pow(cx - g2.getCoordX(), 2) + Math.pow(cy - g2.getCoordY(), 2));
            }
        }
        return totalKm;
    }

    // ==============================
    // 🔹 Operador local seguro: swapPeticiones
    // ==============================
    public boolean swapPeticiones(int p1, int p2) {
        if (p1 == p2) return false;

        int[] loc1 = buscarPeticion(p1);
        int[] loc2 = buscarPeticion(p2);

        if (loc1[0] == -1 || loc2[0] == -1) return false; // alguna no encontrada

        int c1 = loc1[0], v1 = loc1[1];
        int c2 = loc2[0], v2 = loc2[1];

        // intercambiar los IDs en los pares correctos
        Pair[] viajes1 = camiones[c1].getViajes();
        Pair[] viajes2 = camiones[c2].getViajes();

        if (loc1[2] == 1 && loc2[2] == 1) {
            int tmp = viajes1[v1].first;
            viajes1[v1].first = viajes2[v2].first;
            viajes2[v2].first = tmp;
        } else if (loc1[2] == 2 && loc2[2] == 2) {
            int tmp = viajes1[v1].second;
            viajes1[v1].second = viajes2[v2].second;
            viajes2[v2].second = tmp;
        } else if (loc1[2] == 1 && loc2[2] == 2) {
            int tmp = viajes1[v1].first;
            viajes1[v1].first = viajes2[v2].second;
            viajes2[v2].second = tmp;
        } else if (loc1[2] == 2 && loc2[2] == 1) {
            int tmp = viajes1[v1].second;
            viajes1[v1].second = viajes2[v2].first;
            viajes2[v2].first = tmp;
        }

        // recalcular km de los camiones afectados
        int km1 = calcularKmCamion(c1);
        int km2 = calcularKmCamion(c2);

        // si alguno supera el límite, revertir
        if (km1 > Camion.MAX_KM || km2 > Camion.MAX_KM) {
            // revertimos los valores al estado original
            if (loc1[2] == 1 && loc2[2] == 1) {
                int tmp = viajes1[v1].first;
                viajes1[v1].first = viajes2[v2].first;
                viajes2[v2].first = tmp;
            } else if (loc1[2] == 2 && loc2[2] == 2) {
                int tmp = viajes1[v1].second;
                viajes1[v1].second = viajes2[v2].second;
                viajes2[v2].second = tmp;
            } else if (loc1[2] == 1 && loc2[2] == 2) {
                int tmp = viajes1[v1].first;
                viajes1[v1].first = viajes2[v2].second;
                viajes2[v2].second = tmp;
            } else if (loc1[2] == 2 && loc2[2] == 1) {
                int tmp = viajes1[v1].second;
                viajes1[v1].second = viajes2[v2].first;
                viajes2[v2].first = tmp;
            }
            return false;
        }

        camiones[c1].setKmRestantes(Camion.MAX_KM - km1);
        camiones[c2].setKmRestantes(Camion.MAX_KM - km2);
        return true;
    }

    // ==============================
    // 🔹 Representación
    // ==============================
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ESTADO ===\n");
        for (int i = 0; i < camiones.length; i++) {
            sb.append("Camión ").append(i).append(" -> ").append(camiones[i].toString()).append("\n");
        }
        sb.append("Días peticiones: ");
        for (int d : diasPeticiones) sb.append(d).append(" ");
        sb.append("\n================\n");
        return sb.toString();
    }
}
