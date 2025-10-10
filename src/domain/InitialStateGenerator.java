package domain;

import IA.Gasolina.*;
import java.util.*;

public class InitialStateGenerator {

    // ==============================
    // 🔹 Solución ALEATORIA (segura)
    // ==============================
    public static State randomSolution(Gasolineras gasolineras, CentrosDistribucion centros) {
        int numCamiones = centros.size();
        int numPeticiones = gasolineras.size();
        State estado = new State(numCamiones, numPeticiones);

        Random rand = new Random();

        for (int p = 0; p < numPeticiones; ++p) {
            Gasolinera g = (Gasolinera) gasolineras.get(p);
            double gx = g.getCoordX(), gy = g.getCoordY();

            boolean asignada = false;

            // Intentar asignar esta petición a un camión válido
            for (int intentos = 0; intentos < numCamiones * 2 && !asignada; ++intentos) {
                int idCamion = rand.nextInt(numCamiones);
                Camion camion = estado.getCamiones()[idCamion];

                // Calcular km extra que supondría este viaje
                int kmExtra = calcularDistanciaCamionGasolinera(centros, idCamion, g);

                // Solo asignamos si hay espacio y no se supera MAX_KM
                if (camion.getViajesRestantes() > 0 && camion.getKmRestantes() >= kmExtra) {
                    boolean nuevoViaje = false;

                    for (int v = 0; v < Camion.MAX_VIAJES; ++v) {
                        Pair pair = camion.getViajes()[v];
                        if (pair.first == -1) {
                            pair.first = p;
                            nuevoViaje = true;
                            break;
                        } else if (pair.second == -1) {
                            pair.second = p;
                            break;
                        }
                    }

                    camion.setKmRestantes(camion.getKmRestantes() - kmExtra);
                    if (nuevoViaje) camion.setViajesRestantes(camion.getViajesRestantes() - 1);
                    asignada = true;
                }
            }

            // Si no se asigna, la petición queda pendiente (dias=-1)
        }

        // Todas las atendidas hoy
        int[] dias = estado.getDiasPeticiones();
        for (int i = 0; i < dias.length; ++i)
            dias[i] = 0;

        // ✅ Recalcular viajesRestantes según los pares ocupados
        for (Camion c : estado.getCamiones()) {
            c.recalcularViajesRestantes();
        }

        return estado;
    }

    // ==============================
    // 🔹 Solución GREEDY (segura)
    // ==============================
    public static State greedySolution(Gasolineras gasolineras, CentrosDistribucion centros) {
        int numCamiones = centros.size();
        int numPeticiones = gasolineras.size();
        State estado = new State(numCamiones, numPeticiones);

        for (int p = 0; p < numPeticiones; ++p) {
            Gasolinera g = (Gasolinera) gasolineras.get(p);
            double gx = g.getCoordX(), gy = g.getCoordY();

            // Buscar el camión más cercano que pueda atender la petición
            int bestCamion = -1;
            double bestDist = Double.MAX_VALUE;
            int kmExtra = 0;

            for (int c = 0; c < numCamiones; ++c) {
                Camion camion = estado.getCamiones()[c];
                if (camion.getViajesRestantes() <= 0) continue;

                int dist = calcularDistanciaCamionGasolinera(centros, c, g);
                if (dist < bestDist && camion.getKmRestantes() >= dist) {
                    bestCamion = c;
                    bestDist = dist;
                    kmExtra = dist;
                }
            }

            // Si se encontró un camión válido, asignamos
            if (bestCamion != -1) {
                Camion camion = estado.getCamiones()[bestCamion];
                boolean nuevoViaje = false;

                for (int v = 0; v < Camion.MAX_VIAJES; ++v) {
                    Pair pair = camion.getViajes()[v];
                    if (pair.first == -1) {
                        pair.first = p;
                        nuevoViaje = true;
                        break;
                    } else if (pair.second == -1) {
                        pair.second = p;
                        break;
                    }
                }

                camion.setKmRestantes(camion.getKmRestantes() - kmExtra);
                if (nuevoViaje) camion.setViajesRestantes(camion.getViajesRestantes() - 1);
            }
        }

        // Todas las atendidas hoy
        int[] dias = estado.getDiasPeticiones();
        for (int i = 0; i < dias.length; ++i)
            dias[i] = 0;

        // ✅ Recalcular viajesRestantes según los pares ocupados
        for (Camion c : estado.getCamiones()) {
            c.recalcularViajesRestantes();
        }

        return estado;
    }

    // ==============================
    // 🔹 Cálculo de distancia entre un centro y una gasolinera
    // ==============================
    private static int calcularDistanciaCamionGasolinera(CentrosDistribucion centros, int idCamion, Gasolinera g) {
        int camionesPorCentro = main.Constants.CAMIONES_POR_CENTRO;
        Object centroObj = centros.get(idCamion / camionesPorCentro);
        double cx = 0, cy = 0;
        try {
            Object xObj = centroObj.getClass().getMethod("getCoordX").invoke(centroObj);
            Object yObj = centroObj.getClass().getMethod("getCoordY").invoke(centroObj);
            cx = ((Number) xObj).doubleValue();
            cy = ((Number) yObj).doubleValue();
        } catch (Exception e) {
            throw new RuntimeException("Error accediendo a coordenadas del centro", e);
        }

        return (int) (2 * Math.sqrt(Math.pow(cx - g.getCoordX(), 2) + Math.pow(cy - g.getCoordY(), 2)));
    }
}
