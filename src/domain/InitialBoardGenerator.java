package domain;

import IA.Gasolina.*;
import java.util.*;


public class InitialBoardGenerator {

    // Importante, las distancias estaran precalculadas

    public static Peticion[] crearPeticiones(Gasolineras gasolineras) {
        // Contamos el numero de peticiones
        int numPeticiones = 0;
        for (Gasolinera gasolinera : gasolineras) {
            numPeticiones += gasolinera.getPeticiones().size();
        }

        //Recorremos las gasolineras y guardamos las peticioens en un vector de peticiones
        Peticion[] peticiones = new Peticion[numPeticiones];
        int count = 0;
        for (int i = 0; i < gasolineras.size(); i++) {
            Gasolinera gasolinera = gasolineras.get(i);
            for (int j = 0; j < gasolinera.getPeticiones().size(); j++) {
                peticiones[count] = new Peticion(i, gasolinera.getPeticiones().get(j));
                ++count;
            }
        }

        return peticiones;
    }

    public static Camion[] crearCamiones(CentrosDistribucion centrosDistribucion) {
        // Creamos los camiones
        Camion[] camiones = new Camion[centrosDistribucion.size()];
        for (int i = 0; i < centrosDistribucion.size(); i++) {
            Pair posicion = new Pair(centrosDistribucion.get(i).getCoordX(), centrosDistribucion.get(i).getCoordY());
            camiones[i] = new Camion(posicion);
        }

        return camiones;
    }

    public static Pair[] crearGasolineras(Gasolineras gasolineras) {
        // Creamos el vector de gasolineras
        Pair[] gasolinerasPos = new Pair[gasolineras.size()];
        for (int i = 0; i < gasolineras.size(); i++) {
            gasolinerasPos[i] = new Pair(gasolineras.get(i).getCoordX(), gasolineras.get(i).getCoordY());
        }

        return gasolinerasPos;
    }

    public static int[][] precalcularDistanciasGasolineras(Pair[] gasolineras) {
        int[][] distancias = new int[gasolineras.length][gasolineras.length];

        for (int i = 0; i < gasolineras.length; i++) {
            // no hace falta calcularlas todas, ya que la matriz es simétrica
            for (int j = i + 1; j < gasolineras.length; j++) {
                // se calcula con la distancia manhattan
                distancias[i][j] = distancias [j][i] = Math.abs(gasolineras[i].first - gasolineras[j].first) + Math.abs(gasolineras[i].second - gasolineras[j].second);
            }
        }

        return distancias;
    }

    public static int[][] precalcularDistanciasCamionesGasolineras(Camion[] camiones, Pair[] gasolineras) {
        int[][] distancias = new int[camiones.length][gasolineras.length];

        for (int i = 0; i < camiones.length; i++) {
            for (int j = 0; j < gasolineras.length; j++) {
                // se calcula con la distancia manhattan
                distancias[i][j] = Math.abs(camiones[i].getPosicion().first - gasolineras[j].first) + Math.abs(camiones[i].getPosicion().second - gasolineras[j].second);
            }
        }

        return distancias;
    }

    public static P1Board SolucionSinAsignaciones(Gasolineras gasolineras, CentrosDistribucion centrosDistribucion) {
        Peticion[] peticiones = crearPeticiones(gasolineras);
        Camion[] camiones = crearCamiones(centrosDistribucion);
        Pair[] gasolinerasPos = crearGasolineras(gasolineras);
        int[][] distanciasGasolineras = precalcularDistanciasGasolineras(gasolinerasPos);
        int[][] distanciasCamionesGasolineras = precalcularDistanciasCamionesGasolineras(camiones, gasolinerasPos);

        return new P1Board(camiones, gasolinerasPos, peticiones, distanciasGasolineras, distanciasCamionesGasolineras);
    }

    public static P1Board SolucionAsignacionOrdenada(Gasolineras gasolineras, CentrosDistribucion centrosDistribucion) {

        // Creamos los vectores necesarios
        Peticion[] peticiones = crearPeticiones(gasolineras);
        Camion[] camiones = crearCamiones(centrosDistribucion);
        Pair[] gasolinerasPos = crearGasolineras(gasolineras);
        int[][] distanciasGasolineras = precalcularDistanciasGasolineras(gasolinerasPos);
        int[][] distanciasCamionesGasolineras = precalcularDistanciasCamionesGasolineras(camiones, gasolinerasPos);

        int p = 0;
        while (p < peticiones.length) {
            for (int i = 0; i < camiones.length; i++) {
                Camion camion = camiones[i];
                boolean assignada = false;
                if (camion.getKmRestantes() > 0 && camion.getViajesRestantes() > 0) {
                    for (int j = 0; j < camion.getViajes().length; j++) {
                        // Calcular la distancia del viaje
                        Pair v = camion.getViajes()[j];

                        // Como estamos añadiendo en orden, si la primera posición esta vacía, la segunda también lo estará, y si la segunda esta vacía, la primera no lo estará
                        if (v.first == -1) {
                            int distancia = distanciasCamionesGasolineras[i][peticiones[p].getGasolinera()] * 2; // ida y vuelta
                            if (distancia > camion.getKmRestantes()) {
                                continue; // alomejor puede atender la petición en otro viaje si la otra gasolinera del viaje esta cerca
                            }
                            // Asignar la peticion al viaje
                            camion.setKmRestantes(camion.getKmRestantes() - distancia);
                            camion.setViajesRestantes(camion.getViajesRestantes() - 1);
                            v.first = p;

                            // Assignar el camion y el viaje a la peticion
                            peticiones[p].setIdCamion(i);
                            peticiones[p].setIdViaje(j);

                            assignada = true;
                            break;
                        } else if (v.second == -1) {
                            int gasFirstP = peticiones[v.first].getGasolinera();
                            int distancia =  distanciasCamionesGasolineras[i][gasFirstP] + distanciasCamionesGasolineras[i][peticiones[p].getGasolinera()] + distanciasGasolineras[gasFirstP][peticiones[p].getGasolinera()]; // ida a la primera, de la primera a la segunda y vuelta al centro
                            if (distancia > (camion.getKmRestantes() + (distanciasCamionesGasolineras[i][gasFirstP])*2)) {
                                continue; // alomejor puede atender la petición en otro viaje si la otra gasolinera del viaje esta cerca
                            }
                            // Asignar la peticion al viaje
                            camion.setKmRestantes(camion.getKmRestantes() - distancia + (distanciasCamionesGasolineras[i][gasFirstP])*2);
                            v.second = p;

                            peticiones[p].setIdCamion(i);
                            peticiones[p].setIdViaje(j);

                            assignada = true;
                            break;
                        }
                    }
                }
                if (assignada) break;
            }
            ++p;
        }

        return new P1Board(camiones, gasolinerasPos, peticiones, distanciasGasolineras, distanciasCamionesGasolineras);

    }

    // Helper: assign petitions in the provided order using the same logic as ordenada
    private static P1Board assignPeticionesInOrder(Peticion[] peticiones, Camion[] camiones, Pair[] gasolinerasPos, int[][] distanciasGasolineras, int[][] distanciasCamionesGasolineras, int[] order) {
        int pIndex = 0;
        while (pIndex < order.length) {
            int petIdx = order[pIndex];
            // defensive check: ensure petition index is valid
            if (petIdx < 0 || petIdx >= peticiones.length) {
                System.err.println("[WARN] Ignoring invalid petition index in order: " + petIdx + " (peticiones.length=" + peticiones.length + ")");
                ++pIndex;
                continue;
            }
            for (int i = 0; i < camiones.length; i++) {
                Camion camion = camiones[i];
                boolean assignada = false;
                if (camion.getKmRestantes() > 0 && camion.getViajesRestantes() > 0) {
                    for (int j = 0; j < camion.getViajes().length; j++) {
                        Pair v = camion.getViajes()[j];

                        if (v.first == -1) {
                            int gasId = peticiones[petIdx].getGasolinera();
                            // pre-access gasolineraId = " + gasId + " (debug removed)
                            int distancia = distanciasCamionesGasolineras[i][gasId] * 2; // ida y vuelta
                            if (distancia > camion.getKmRestantes()) {
                                continue;
                            }
                            camion.setKmRestantes(camion.getKmRestantes() - distancia);
                            camion.setViajesRestantes(camion.getViajesRestantes() - 1);
                            v.first = petIdx;

                            peticiones[petIdx].setIdCamion(i);
                            peticiones[petIdx].setIdViaje(j);

                            assignada = true;
                            break;
                        } else if (v.second == -1) {
                            int gasId2 = peticiones[petIdx].getGasolinera();
                            int gasFirst = peticiones[v.first].getGasolinera();
                            // pre-access gasolineraId2 = " + gasId2 + ", gasFirst = " + gasFirst + " (debug removed)
                            int distancia = distanciasCamionesGasolineras[i][gasFirst] + distanciasCamionesGasolineras[i][gasId2] + distanciasGasolineras[gasFirst][gasId2];
                            if (distancia > (camion.getKmRestantes() + (distanciasCamionesGasolineras[i][gasFirst]) * 2)) {
                                continue;
                            }
                            camion.setKmRestantes(camion.getKmRestantes() - distancia + (distanciasCamionesGasolineras[i][gasFirst]) * 2);
                            v.second = petIdx;

                            peticiones[petIdx].setIdCamion(i);
                            peticiones[petIdx].setIdViaje(j);

                            assignada = true;
                            break;
                        }
                    }
                }
                if (assignada) break;
            }
            ++pIndex;
        }

        return new P1Board(camiones, gasolinerasPos, peticiones, distanciasGasolineras, distanciasCamionesGasolineras);
    }

    // Random initial solution: shuffle petition order and assign greedily in that order
    public static P1Board SolucionAsignacionAleatoria(Gasolineras gasolineras, CentrosDistribucion centrosDistribucion) {
        Peticion[] peticiones = crearPeticiones(gasolineras);
        Camion[] camiones = crearCamiones(centrosDistribucion);
        Pair[] gasolinerasPos = crearGasolineras(gasolineras);
        int[][] distanciasGasolineras = precalcularDistanciasGasolineras(gasolinerasPos);
        int[][] distanciasCamionesGasolineras = precalcularDistanciasCamionesGasolineras(camiones, gasolinerasPos);

        int n = peticiones.length;
        int[] order = new int[n];
        for (int i = 0; i < n; i++) order[i] = i;
        Random rnd = new Random();
        for (int i = n - 1; i > 0; i--) {
            int j = rnd.nextInt(i + 1);
            int tmp = order[i]; order[i] = order[j]; order[j] = tmp;
        }

        return assignPeticionesInOrder(peticiones, camiones, gasolinerasPos, distanciasGasolineras, distanciasCamionesGasolineras, order);
    }

    // Greedy by axes (eixos): sort petitions by X coordinate (then Y) and assign in that order
    public static P1Board SolucionAsignacionGreedyEjes(Gasolineras gasolineras, CentrosDistribucion centrosDistribucion) {
        Peticion[] peticiones = crearPeticiones(gasolineras);
        Camion[] camiones = crearCamiones(centrosDistribucion);
        Pair[] gasolinerasPos = crearGasolineras(gasolineras);
        int[][] distanciasGasolineras = precalcularDistanciasGasolineras(gasolinerasPos);
        int[][] distanciasCamionesGasolineras = precalcularDistanciasCamionesGasolineras(camiones, gasolinerasPos);

        Integer[] idx = new Integer[peticiones.length];
        for (int i = 0; i < idx.length; i++) idx[i] = i;
    // Pre-sort sample (removed verbose runtime logging)
        Arrays.sort(idx, (a, b) -> {
            int xa = gasolinerasPos[peticiones[a].getGasolinera()].first;
            int ya = gasolinerasPos[peticiones[a].getGasolinera()].second;
            int xb = gasolinerasPos[peticiones[b].getGasolinera()].first;
            int yb = gasolinerasPos[peticiones[b].getGasolinera()].second;
            if (xa != xb) return Integer.compare(xa, xb);
            return Integer.compare(ya, yb);
        });

    // Post-sort sample (removed verbose runtime logging)

        int[] order = new int[peticiones.length];
        for (int i = 0; i < order.length; i++) order[i] = idx[i];

        // debug: check order bounds
        int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
        for (int v : order) {
            if (v < min) min = v;
            if (v > max) max = v;
        }
    // Order bounds computed: min=" + min + ", max=" + max + " (no verbose output)

        return assignPeticionesInOrder(peticiones, camiones, gasolinerasPos, distanciasGasolineras, distanciasCamionesGasolineras, order);
    }

    // Greedy by distance: sort petitions by their minimum distance to any distribution center (camion start)
    public static P1Board SolucionAsignacionGreedyDistancia(Gasolineras gasolineras, CentrosDistribucion centrosDistribucion) {
        Peticion[] peticiones = crearPeticiones(gasolineras);
        Camion[] camiones = crearCamiones(centrosDistribucion);
        Pair[] gasolinerasPos = crearGasolineras(gasolineras);
        int[][] distanciasGasolineras = precalcularDistanciasGasolineras(gasolinerasPos);
        int[][] distanciasCamionesGasolineras = precalcularDistanciasCamionesGasolineras(camiones, gasolinerasPos);

        Integer[] idx = new Integer[peticiones.length];
        for (int i = 0; i < idx.length; i++) idx[i] = i;
        Arrays.sort(idx, (a, b) -> {
            int mina = Integer.MAX_VALUE;
            int minb = Integer.MAX_VALUE;
            for (int i = 0; i < distanciasCamionesGasolineras.length; i++) {
                mina = Math.min(mina, distanciasCamionesGasolineras[i][peticiones[a].getGasolinera()]);
                minb = Math.min(minb, distanciasCamionesGasolineras[i][peticiones[b].getGasolinera()]);
            }
            return Integer.compare(mina, minb);
        });

        int[] order = new int[peticiones.length];
        for (int i = 0; i < order.length; i++) order[i] = idx[i];

        return assignPeticionesInOrder(peticiones, camiones, gasolinerasPos, distanciasGasolineras, distanciasCamionesGasolineras, order);
    }

    // Greedy by quadrants: split petitions into 4 quadrants around center and assign quadrant by quadrant
    public static P1Board SolucionAsignacionGreedyQuadrants(Gasolineras gasolineras, CentrosDistribucion centrosDistribucion) {
        Peticion[] peticiones = crearPeticiones(gasolineras);
        Camion[] camiones = crearCamiones(centrosDistribucion);
        Pair[] gasolinerasPos = crearGasolineras(gasolineras);
        int[][] distanciasGasolineras = precalcularDistanciasGasolineras(gasolinerasPos);
        int[][] distanciasCamionesGasolineras = precalcularDistanciasCamionesGasolineras(camiones, gasolinerasPos);

        // compute center
        double cx = 0, cy = 0;
        for (Pair p : gasolinerasPos) { cx += p.first; cy += p.second; }
        cx /= gasolinerasPos.length; cy /= gasolinerasPos.length;

        List<Integer> q1 = new ArrayList<>();
        List<Integer> q2 = new ArrayList<>();
        List<Integer> q3 = new ArrayList<>();
        List<Integer> q4 = new ArrayList<>();

        for (int i = 0; i < peticiones.length; i++) {
            Pair gp = gasolinerasPos[peticiones[i].getGasolinera()];
            if (gp.first >= cx && gp.second >= cy) q1.add(i);
            else if (gp.first < cx && gp.second >= cy) q2.add(i);
            else if (gp.first < cx && gp.second < cy) q3.add(i);
            else q4.add(i);
        }

        final double centerX = cx;
        final double centerY = cy;
        Comparator<Integer> byDistToCenter = (a, b) -> {
            Pair pa = gasolinerasPos[peticiones[a].getGasolinera()];
            Pair pb = gasolinerasPos[peticiones[b].getGasolinera()];
            int da = (int)(Math.abs(pa.first - centerX) + Math.abs(pa.second - centerY));
            int db = (int)(Math.abs(pb.first - centerX) + Math.abs(pb.second - centerY));
            return Integer.compare(da, db);
        };

        q1.sort(byDistToCenter);
        q2.sort(byDistToCenter);
        q3.sort(byDistToCenter);
        q4.sort(byDistToCenter);

        // concatenate quadrants (order: Q1, Q2, Q3, Q4)
        int[] order = new int[peticiones.length];
        int idx = 0;
        for (int v : q1) order[idx++] = v;
        for (int v : q2) order[idx++] = v;
        for (int v : q3) order[idx++] = v;
        for (int v : q4) order[idx++] = v;

        return assignPeticionesInOrder(peticiones, camiones, gasolinerasPos, distanciasGasolineras, distanciasCamionesGasolineras, order);
    }


}
