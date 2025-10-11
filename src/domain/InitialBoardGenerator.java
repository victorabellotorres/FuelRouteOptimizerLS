package domain;

import IA.Gasolina.*;


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
                            int distancia =  distanciasCamionesGasolineras[i][v.first] + distanciasCamionesGasolineras[i][peticiones[p].getGasolinera()] + distanciasGasolineras[v.first][peticiones[p].getGasolinera()]; // ida a la primera, de la primera a la segunda y vuelta al centro
                            if (distancia > (camion.getKmRestantes() + (distanciasCamionesGasolineras[i][v.first])*2)) {
                                continue; // alomejor puede atender la petición en otro viaje si la otra gasolinera del viaje esta cerca
                            }
                            // Asignar la peticion al viaje
                            camion.setKmRestantes(camion.getKmRestantes() - distancia + (distanciasCamionesGasolineras[i][v.first])*2);
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


}
