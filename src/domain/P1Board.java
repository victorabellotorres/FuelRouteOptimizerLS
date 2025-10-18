package domain;

import main.Constants;

public class P1Board {

    // Atributos estáticos
    public static int[][] distanciasGasolineras = null; // distanciasGasolineras[i][j] = distancia de la gasolinera i a la j
    public static int[][] distanciasCamionesGasolineras = null; // distanciasCamionesGasolineras[i][j] = distancia del camion i a la gasolinera j

    // Atributos
    private Camion[] camiones;
    private Peticion[] peticiones;
    private Pair[] gasolineras;

    // Constructor por tamaños
    public P1Board(int NumCamiones, int numGasolineras,int NumPeticiones) {
        camiones = new Camion[NumCamiones];
        for (int i = 0; i < NumCamiones; ++i) {
            Pair posicion = new Pair(-1, -1);
            camiones[i] = new Camion(posicion);
        }

        gasolineras = new Pair[numGasolineras];
        for (int i = 0; i < numGasolineras; ++i) {
            gasolineras[i] = new Pair(-1, -1);
        }

        peticiones = new Peticion[NumPeticiones];
        for (int i = 0; i < NumPeticiones; ++i) {
            peticiones[i] = new Peticion();
        }

        distanciasGasolineras = new int[numGasolineras][numGasolineras];
        distanciasCamionesGasolineras = new int[NumCamiones][numGasolineras];
    }

    // Constructor por vectores
    public P1Board(Camion[] camiones, Pair[] gasolineras, Peticion[] peticiones, int[][] distanciasGasolineras, int[][] distanciasCamionesGasolineras) {
        this.camiones = camiones;
        this.gasolineras = gasolineras;
        this.peticiones = peticiones;

        P1Board.distanciasGasolineras = distanciasGasolineras;
        P1Board.distanciasCamionesGasolineras = distanciasCamionesGasolineras;
    }

    // Constructor por copia
    public P1Board(P1Board board) {
        this.camiones = new Camion[board.camiones.length];
        for (int i = 0; i < board.camiones.length; ++i) {
            this.camiones[i] = new Camion(board.camiones[i]);
        }

        this.gasolineras = new Pair[board.gasolineras.length];
        for (int i = 0; i < board.gasolineras.length; ++i) {
            this.gasolineras[i] = new Pair(board.gasolineras[i].first, board.gasolineras[i].second);
        }

        this.peticiones = new Peticion[board.peticiones.length];
        for (int i = 0; i < board.peticiones.length; ++i) {
            this.peticiones[i] = new Peticion(board.peticiones[i]);
        }
    }

    // Getters i setters
    public Camion[] getCamiones() {
        return camiones;
    }
    public void setCamiones(Camion[] camiones) {
        this.camiones = camiones;
    }
    public Pair[] getGasolineras() {
        return gasolineras;
    }
    public void setGasolineras(Pair[] gasolineras) {
        this.gasolineras = gasolineras;
    }
    public Peticion[] getPeticiones() {
        return peticiones;
    }
    public void setPeticiones(Peticion[] peticiones) {
        this.peticiones = peticiones;
    }

    // Comprobar que el estado cumple con las restricciones
    public boolean esValido() {
        for (Camion c : camiones) {
            if (c.getKmUsados() > Camion.MAX_KM) return false;
            if (c.getViajesUsados() > Camion.MAX_VIAJES) return false;
        }
        return true;
    }

    // Operadores

    // Intercambia la peticion en la posición dada por el idCamion, idViaje y first con la peticion dada por idPeticion2, si no hay ninguna peticion en la posición dada se comporta como un move
    // Parámetros: first: true si se quiere intercambiar la primera peticion del viaje, false si se quiere intercambiar la segunda peticion del viaje
    // Devuelve false si el swap no cumple con algunas de las restricciones de kilometros o viajes, o el swap no tiene sentido (la petición ya esta en la posición dada por idCamion e idViaje)
    public boolean swapPosicionPeticion(int idCamion, int idViaje, boolean first, int idPeticion2) {
        // Casos sin sentidos
        if (idCamion >= camiones.length || idCamion < 0) return false;
        if (idViaje >= Camion.MAX_VIAJES || idViaje < 0) return false;
        if (idPeticion2 >= peticiones.length || idPeticion2 < 0) return false;
        // Si estan en el mismo viaje
        if (peticiones[idPeticion2].getIdCamion() == idCamion && peticiones[idPeticion2].getIdViaje() == idViaje) return false;

        Camion camion = camiones[idCamion];
        Pair v = camion.getViajes()[idViaje];
        int idPeticion1;
        if (first) idPeticion1 = v.first;
        else idPeticion1 = v.second;
        // Si son la misma peticion
        if (idPeticion1 == idPeticion2) return false;

        if (idPeticion1 == -1) {
            return movePeticionToPosicion(idCamion, idViaje, first, idPeticion2);
        }
        else {
            return swapPeticiones(idPeticion1, idPeticion2);
        }

    }

    // Intercambia dos peticiones en el array de peticiones
    // Devuelve false si el swap no cumple con algunas de las restricciones de kilometros o viajes, o el swap no tiene sentido (e.g. i y j no estan asignadas a ningún camion)
    public boolean swapPeticiones(int i, int j) {
        if (i < 0 || i >= peticiones.length || j < 0 || j >= peticiones.length || i == j) return false;

        // Hacemos el swap comprobando el límite de kilometros restantes, no hace falta comprobar el límite de viajes porque no se añadiran viajes al hacer un intercambio
        if (peticiones[i].getIdCamion() == -1 && peticiones[j].getIdCamion() == -1) return false; // Si alguna de las dos peticiones no tiene un camion asignado, no importa hacer el swap

        if (peticiones[i].getIdCamion() != -1 && peticiones[j].getIdCamion() != -1) { // Si las dos peticiones tienen un camion asignado
            if (peticiones[i].getIdCamion() == peticiones[j].getIdCamion()) { // Si son del mismo camion
                int distancia = camiones[peticiones[i].getIdCamion()].getKmUsados();
                distancia -= getDistanciaViaje(peticiones[i].getIdCamion(), peticiones[i].getIdViaje());
                distancia -= getDistanciaViaje(peticiones[j].getIdCamion(), peticiones[j].getIdViaje());

                camiones[peticiones[i].getIdCamion()].getViajes()[peticiones[i].getIdViaje()].swap(i, j);

                camiones[peticiones[j].getIdCamion()].getViajes()[peticiones[j].getIdViaje()].swap(j, i);

                int tempIdViaje_i = peticiones[i].getIdViaje();
                peticiones[i].setIdViaje(peticiones[j].getIdViaje());
                peticiones[j].setIdViaje(tempIdViaje_i);

                int nuevaDistancia = getDistanciaViaje(peticiones[j].getIdCamion(), peticiones[j].getIdViaje());
                nuevaDistancia += getDistanciaViaje(peticiones[i].getIdCamion(), peticiones[i].getIdViaje());

                distancia += nuevaDistancia;
                if (distancia > Camion.MAX_KM) {
                    // Deshacemos el swap
                    camiones[peticiones[i].getIdCamion()].getViajes()[peticiones[i].getIdViaje()].swap(j, i);
                    camiones[peticiones[j].getIdCamion()].getViajes()[peticiones[j].getIdViaje()].swap(i, j);

                    // Deshacemos los cambios en las peticiones
                    peticiones[j].setIdViaje(peticiones[i].getIdViaje());
                    peticiones[i].setIdViaje(tempIdViaje_i);

                    return false;
                }
                camiones[peticiones[i].getIdCamion()].setKmRestantes(Camion.MAX_KM - distancia);
            } else {
                // Si son de diferentes camiones
                int distancia1 = camiones[peticiones[i].getIdCamion()].getKmUsados();
                int distancia2 = camiones[peticiones[j].getIdCamion()].getKmUsados();

                distancia1 -= getDistanciaViaje(peticiones[i].getIdCamion(), peticiones[i].getIdViaje());
                distancia2 -= getDistanciaViaje(peticiones[j].getIdCamion(), peticiones[j].getIdViaje());

                camiones[peticiones[i].getIdCamion()].getViajes()[peticiones[i].getIdViaje()].swap(i, j);

                camiones[peticiones[j].getIdCamion()].getViajes()[peticiones[j].getIdViaje()].swap(j, i);

                // Actualizamos los datos de las peticioness
                int tempIdCamion_i = peticiones[i].getIdCamion();
                peticiones[i].setIdCamion(peticiones[j].getIdCamion());
                peticiones[j].setIdCamion(tempIdCamion_i);

                int tempIdViaje_i = peticiones[i].getIdViaje();
                peticiones[i].setIdViaje(peticiones[j].getIdViaje());
                peticiones[j].setIdViaje(tempIdViaje_i);

                int nuevaDistancia1 = getDistanciaViaje(peticiones[j].getIdCamion(), peticiones[j].getIdViaje());
                int nuevaDistancia2 = getDistanciaViaje(peticiones[i].getIdCamion(), peticiones[i].getIdViaje());

                distancia1 += nuevaDistancia1;
                distancia2 += nuevaDistancia2;

                if (distancia1 > Camion.MAX_KM || distancia2 > Camion.MAX_KM) {
                    // Deshacemos el swap
                    camiones[peticiones[i].getIdCamion()].getViajes()[peticiones[i].getIdViaje()].swap(j, i);
                    camiones[peticiones[j].getIdCamion()].getViajes()[peticiones[j].getIdViaje()].swap(i, j);

                    // Deshacemos los cambios en las peticiones
                    peticiones[j].setIdCamion(peticiones[i].getIdCamion());
                    peticiones[i].setIdCamion(tempIdCamion_i);

                    peticiones[j].setIdViaje(peticiones[i].getIdViaje());
                    peticiones[i].setIdViaje(tempIdViaje_i);


                    return false;
                }
                camiones[peticiones[i].getIdCamion()].setKmRestantes(Camion.MAX_KM - distancia2);
                camiones[peticiones[j].getIdCamion()].setKmRestantes(Camion.MAX_KM - distancia1);

            }
        } else if (peticiones[i].getIdCamion() != -1) { // Si solo una de las dos peticiones (i en este caso) tiene un camion asignado
            int distancia = camiones[peticiones[i].getIdCamion()].getKmUsados();
            distancia -= getDistanciaViaje(peticiones[i].getIdCamion(), peticiones[i].getIdViaje());

            camiones[peticiones[i].getIdCamion()].getViajes()[peticiones[i].getIdViaje()].swap(i, j);

            peticiones[j].setIdCamion(peticiones[i].getIdCamion());
            peticiones[j].setIdViaje(peticiones[i].getIdViaje());
            peticiones[i].setIdCamion(-1);
            peticiones[i].setIdViaje(-1);
            int nuevaDistancia = getDistanciaViaje(peticiones[j].getIdCamion(), peticiones[j].getIdViaje());

            distancia += nuevaDistancia;
            if (distancia > Camion.MAX_KM) {
                // Deshacemos el swap
                camiones[peticiones[j].getIdCamion()].getViajes()[peticiones[j].getIdViaje()].swap(j, i);

                peticiones[i].setIdCamion(peticiones[j].getIdCamion());
                peticiones[i].setIdViaje(peticiones[j].getIdViaje());
                peticiones[j].setIdCamion(-1);
                peticiones[j].setIdViaje(-1);
                return false;
            }
            camiones[peticiones[j].getIdCamion()].setKmRestantes(Camion.MAX_KM - distancia);
        } else {
            // Si solo una de las dos peticiones (j en este caso) tiene un camion asignado
            int distancia = camiones[peticiones[j].getIdCamion()].getKmUsados();
            distancia -= getDistanciaViaje(peticiones[j].getIdCamion(), peticiones[j].getIdViaje());

            camiones[peticiones[j].getIdCamion()].getViajes()[peticiones[j].getIdViaje()].swap(j, i);

            peticiones[i].setIdCamion(peticiones[j].getIdCamion());
            peticiones[i].setIdViaje(peticiones[j].getIdViaje());
            peticiones[j].setIdCamion(-1);
            peticiones[j].setIdViaje(-1);
            int nuevaDistancia = getDistanciaViaje(peticiones[i].getIdCamion(), peticiones[i].getIdViaje());

            distancia += nuevaDistancia;
            if (distancia > Camion.MAX_KM) {
                // Deshacemos el swap
                camiones[peticiones[i].getIdCamion()].getViajes()[peticiones[i].getIdViaje()].swap(j, i);

                peticiones[j].setIdCamion(peticiones[i].getIdCamion());
                peticiones[j].setIdViaje(peticiones[i].getIdViaje());
                peticiones[i].setIdCamion(-1);
                peticiones[i].setIdViaje(-1);
                return false;
            }
            camiones[peticiones[i].getIdCamion()].setKmRestantes(Camion.MAX_KM - distancia);
        }

        return true;
    }

    public boolean movePeticionToPosicion(int idCamion, int idViaje, boolean first, int idPeticion) {
        // Casos sin sentidos
        if (idCamion >= camiones.length || idCamion < 0) return false;
        if (idViaje >= Camion.MAX_VIAJES || idViaje < 0) return false;
        if (idPeticion >= peticiones.length || idPeticion < 0) return false;

        Camion camion = camiones[idCamion];
        Pair v = camion.getViajes()[idViaje];
        int idPeticionPos;
        if (first) idPeticionPos = v.first;
        else idPeticionPos = v.second;
        // Si hay una peticion en la posicion
        if (idPeticionPos != -1) return false;

        // Calculamos la nueva distancia del camión si se añade la petición
        int distanciaActual = getDistanciaViaje(idCamion, idViaje);

        if (first) camiones[idCamion].getViajes()[idViaje].first = idPeticion;
        else camiones[idCamion].getViajes()[idViaje].second = idPeticion;
        // Actualizamos el vector de posiciones
        peticiones[idPeticion].setIdCamion(idCamion);
        peticiones[idPeticion].setIdViaje(idViaje);

        int nuevaDistancia = getDistanciaViaje(idCamion, idViaje);

        int distanciaCamion = camiones[idCamion].getKmUsados() - distanciaActual + nuevaDistancia;
        if (distanciaCamion > Camion.MAX_KM) {
            // Deshacemos los cambios
            if (first) camiones[idCamion].getViajes()[idViaje].first = -1;
            else camiones[idCamion].getViajes()[idViaje].second = -1;

            peticiones[idPeticion].setIdCamion(-1);
            peticiones[idPeticion].setIdViaje(-1);
            return false;
        }
        camiones[idCamion].setKmRestantes(Camion.MAX_KM - distanciaCamion);
        if (first && v.second == -1) {
            camiones[idCamion].setViajesRestantes(camiones[idCamion].getViajesRestantes() - 1);
        } else if (!first && v.first == -1) {
            camiones[idCamion].setViajesRestantes(camiones[idCamion].getViajesRestantes() - 1);
        }
        return true;
    }

    public boolean swapViajes(int idCamion1, int idViaje1, int idCamion2, int idViaje2) {
        // Casos sin sentidos
        if (idCamion1 >= camiones.length || idCamion1 < 0) return false;
        if (idViaje1 >= Camion.MAX_VIAJES || idViaje1 < 0) return false;
        if (idCamion2 >= camiones.length || idCamion2 < 0) return false;
        if (idViaje2 >= Camion.MAX_VIAJES || idViaje2 < 0) return false;

        if (idCamion1 == idCamion2) return false;

        Pair viaje1 = camiones[idCamion1].getViajes()[idViaje1];
        Pair viaje2 = camiones[idCamion2].getViajes()[idViaje2];

        int distanciaViaje1before = getDistanciaViaje(idCamion1, idViaje1);
        int distanciaViaje2before = getDistanciaViaje(idCamion2, idViaje2);

        // Intercambiamos los viajes y actualizamos las peticiones
        camiones[idCamion1].setViaje(idViaje1, new Pair(viaje2.first, viaje2.second));
        camiones[idCamion2].setViaje(idViaje2, new Pair(viaje1.first, viaje1.second));

        if (viaje1.first != -1) {
            peticiones[viaje1.first].setIdCamion(idCamion2);
            peticiones[viaje1.first].setIdViaje(idViaje2);
        }
        if (viaje1.second != -1) {
            peticiones[viaje1.second].setIdCamion(idCamion2);
            peticiones[viaje1.second].setIdViaje(idViaje2);
        }

        if (viaje2.first != -1) {
            peticiones[viaje2.first].setIdCamion(idCamion1);
            peticiones[viaje2.first].setIdViaje(idViaje1);
        }

        if (viaje2.second != -1) {
            peticiones[viaje2.second].setIdCamion(idCamion1);
            peticiones[viaje2.second].setIdViaje(idViaje1);
        }

        int distanciaViaje1after = getDistanciaViaje(idCamion1, idViaje1);
        int distanciaViaje2after = getDistanciaViaje(idCamion2, idViaje2);

        if (camiones[idCamion1].getKmUsados() - distanciaViaje1before + distanciaViaje2after > Camion.MAX_KM || camiones[idCamion2].getKmUsados() - distanciaViaje2before + distanciaViaje1after > Camion.MAX_KM) {
            // Deshacemos el intercambio
//            camiones[idCamion1].setViaje(idViaje1, viaje1);
//            camiones[idCamion2].setViaje(idViaje2, viaje2);

//            if (viaje1.first != -1) {
//                peticiones[viaje1.first].setIdCamion(idCamion1);
//                peticiones[viaje1.first].setIdViaje(idViaje1);
//            }
//            if (viaje1.second != -1) {
//                peticiones[viaje1.second].setIdCamion(idCamion1);
//                peticiones[viaje1.second].setIdViaje(idViaje1);
//            }
//
//            if (viaje2.first != -1) {
//                peticiones[viaje2.first].setIdCamion(idCamion2);
//                peticiones[viaje2.first].setIdViaje(idViaje2);
//            }
//
//            if (viaje2.second != -1) {
//                peticiones[viaje2.second].setIdCamion(idCamion2);
//                peticiones[viaje2.second].setIdViaje(idViaje2);
//            }
            return false;
        }

        // Actualizamos los km y viajes de cada camion
        camiones[idCamion1].setKmRestantes(Camion.MAX_KM - (camiones[idCamion1].getKmUsados() - distanciaViaje1before + distanciaViaje1after));
        camiones[idCamion2].setKmRestantes(Camion.MAX_KM - (camiones[idCamion2].getKmUsados() - distanciaViaje2before + distanciaViaje2after));

        if (!viaje1.isEmpty()) {
            camiones[idCamion1].setViajesRestantes(camiones[idCamion1].getViajesRestantes() + 1);
            camiones[idCamion2].setViajesRestantes(camiones[idCamion2].getViajesRestantes() - 1);
        }
        if (!viaje2.isEmpty()) {
            camiones[idCamion2].setViajesRestantes(camiones[idCamion2].getViajesRestantes() + 1);
            camiones[idCamion1].setViajesRestantes(camiones[idCamion1].getViajesRestantes() - 1);
        }

        return true;
    }

    public boolean swapCamiones(int idCamion1, int idCamion2) {
        // Casos sin sentidos
        if (idCamion1 >= camiones.length || idCamion1 < 0) return false;
        if (idCamion2 >= camiones.length || idCamion2 < 0) return false;
        if (idCamion1 == idCamion2) return false;


        Camion next_camion2 = new Camion(camiones[idCamion1]);
        next_camion2.setPosicion(camiones[idCamion2].getPosicion());

        Camion next_camion1 = new Camion(camiones[idCamion2]);
        next_camion1.setPosicion(camiones[idCamion1].getPosicion());

        camiones[idCamion1] = next_camion1;
        camiones[idCamion2] = next_camion2;

        // Actualizamos las peticiones
        for (int i = 0; i < peticiones.length; ++i) {
            if (peticiones[i].getIdCamion() == idCamion1) {
                peticiones[i].setIdCamion(idCamion2);
            } else if (peticiones[i].getIdCamion() == idCamion2) {
                peticiones[i].setIdCamion(idCamion1);
            }
        }

        int kmCamion1 = recalcularKmCamion(idCamion1);
        int kmCamion2 = recalcularKmCamion(idCamion2);
        if (kmCamion1 > Camion.MAX_KM || kmCamion2 > Camion.MAX_KM) {
            return false;
        }

        return true;
    }

    // Métodos auxiliares

    // Devuelve true si el estado es una solución que cumple con los requisitos:
    //      1. No supera el máximo de kilometros
    //      2. No supera el máximo de viajes
    // Devuelve el primer error que encuentra, no todos.
    public boolean esSolucion(StringBuilder errorMsg) {
        for (int i = 0; i < camiones.length; ++i) {
            int sumaDistancias = 0;
            int viajes = 0;
            // Comprobamos la restricción de kilometros y viajes recorriendo el vector y no mirando directamente los km restantes y miramos que cuadren.
            for (int j = 0; j < Camion.MAX_VIAJES; ++j) {
                if (camiones[i].getViajes()[j].isEmpty()) continue; // Si el viaje no tiene peticiones asignadas, no hace falta seguir comprobando
                int distanciaViaje = getDistanciaViaje(i, j);

                ++viajes;
                sumaDistancias += distanciaViaje;
            }
            if (sumaDistancias != camiones[i].getKmUsados()) {
                errorMsg.append("Error: El camión " + i + " tiene un error en el cálculo de kilómetros usados.");
                return false;
            }
            if (sumaDistancias > Camion.MAX_KM) {
                errorMsg.append("Error: El camión " + i + " supera el máximo de kilómetros.");
                return false;
            }
            if (viajes != camiones[i].getViajesUsados()) {
                errorMsg.append("Error: El camión " + i + " tiene un error en el cálculo de viajes usados.");
                return false;
            }
            if (viajes > Camion.MAX_VIAJES) {
                errorMsg.append("Error: El camión " + i + " supera el máximo de viajes.");
                return false;
            }
        }

        return true;
    }

    public int peticionesAssignadas() {
        int count = 0;
        for (Peticion p : peticiones) {
            if (p.getIdCamion() != -1) count++;
        }
        return count;
    }

    public int camionesUsados() {
        int count = 0;
        for (Camion c : camiones) {
            if (c.getViajesUsados() != 0) count++;
        }
        return count;
    }

    private int getDistanciaViaje(int idCamion, int idViaje) {
        if (idViaje < 0 || idViaje >= Camion.MAX_VIAJES) return 0;
        Pair v = camiones[idCamion].getViajes()[idViaje];

        if (v.first == -1 && v.second == -1) return 0;
        else if (v.first != -1 && v.second == -1) return 2 * P1Board.distanciasCamionesGasolineras[idCamion][peticiones[v.first].getGasolinera()];
        else if (v.first == -1 && v.second != -1) return 2 * P1Board.distanciasCamionesGasolineras[idCamion][peticiones[v.second].getGasolinera()];
        else {
            int idGasolinera1 = peticiones[v.first].getGasolinera();
            int idGasolinera2 = peticiones[v.second].getGasolinera();
            return P1Board.distanciasCamionesGasolineras[idCamion][idGasolinera1] + P1Board.distanciasGasolineras[idGasolinera1][idGasolinera2] + P1Board.distanciasCamionesGasolineras[idCamion][idGasolinera2];
        }
    }

    public double getDistanciaMediaCamiones(int idGasolinera) {

        int suma = 0;
        int count = 0;
        for (int i = 0; i < distanciasCamionesGasolineras.length; ++i) {
            suma += distanciasCamionesGasolineras[i][idGasolinera];
            ++count;
        }
        return suma / count;
    }

    public int recalcularKmCamion(int idCamion) {
        int suma = 0;
        for (int j = 0; j < Camion.MAX_VIAJES; ++j) {
            suma += getDistanciaViaje(idCamion, j);
        }
        camiones[idCamion].setKmRestantes(Camion.MAX_KM - suma);
        return suma;
    }

    public double getCalidad() {
        double beneficio = 0.0;
        double coste = 0.0;



        // Heurística (AIMA minimiza, por lo que restamos el beneficio al coste para que salga negativo)
        return beneficio-coste;
    }

    public double getBeneficio() {
        double beneficio = 0.0;
        // Beneficio: peticiones atendidas hoy (solo las que tienen camion asignado)
        for (Peticion p : peticiones) {
            double porcentaje = 0.0;
            // Si la peticion no tiene camion asignado, el beneficio es el que se obtiene si asumimos que se atiende un día después.
            if (p.getIdCamion() == -1) porcentaje = Math.max(0.0, 100.0 - Math.pow(2.0, Math.max(0, p.getDias()+1)));
            else {
                porcentaje = Math.max(0.0, 100.0 - Math.pow(2.0, Math.max(0, p.getDias())));
            }
            beneficio += main.Constants.VALOR_DEPOSITO * (porcentaje / 100.0);
        }
        return beneficio;
    }

    public double getCoste() {
        double coste = 0.0;
        // Coste: kilómetros totales recorridos
        for (Camion c : camiones) {
            coste += c.getKmUsados() * main.Constants.COSTE_KM;
        }
        return coste;
    }

    // ==============================
    // Metricas
    // ==============================
    public BoardMetrics getMetrics() {
        StringBuilder err = new StringBuilder();
        boolean valid = esSolucion(err);
        String errorMsg = err.length() == 0 ? "" : err.toString();

        int peticionesAssignadas = peticionesAssignadas();
        int peticionesTotales = peticiones.length;
        int camionesUsados = camionesUsados();
        int camionesTotales = camiones.length;

        int totalKm = 0;
        for (Camion c : camiones) {
            int km = c.getKmUsados();
            totalKm += km;
        }
        double kmMediosPorCamion = camiones.length > 0 ? (double) totalKm / camiones.length : 0.0;

        return new BoardMetrics(valid, errorMsg, peticionesAssignadas, peticionesTotales, camionesUsados, camionesTotales, totalKm, kmMediosPorCamion, getBeneficio(), getCoste());
    }

    // ==============================
    // Representación
    // ==============================
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ESTADO ===\n");
        for (int i = 0; i < camiones.length; i++) {
            sb.append("Camión ").append(i).append(" -> ").append(camiones[i].toString()).append("\n");
        }
        for (int i = 0; i < peticiones.length; i++) {
            if (peticiones[i].getIdCamion() != -1) {
                sb.append("Petición ").append(i).append(" (Gasolinera: ").append(peticiones[i].getGasolinera()).append("(").append(gasolineras[peticiones[i].getGasolinera()]).append(")").append(", Días: ").append(peticiones[i].getDias()).append(") -> Asignada al Camión ").append(peticiones[i].getIdCamion()).append(", Viaje ").append(peticiones[i].getIdViaje()).append("\n");
            } else {
                sb.append("Petición ").append(i).append(" (Gasolinera: ").append(peticiones[i].getGasolinera()).append("(").append(gasolineras[peticiones[i].getGasolinera()]).append(")").append(", Días: ").append(peticiones[i].getDias()).append(") -> No asignada\n");
            }
        }
        return sb.toString();
    }
}