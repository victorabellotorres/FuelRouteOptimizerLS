package domain;

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

    // Métodos auxiliares

    // Devuelve true si el estado es una solución que cumple con los requisitos:
    //      1. No supera el máximo de kilometros
    //      2. No supera el máximo de viajes
    // Devuelve el primer error que encuentra, no todos.
    public boolean esSolucion(String errorMsg) {
        for (int i = 0; i < camiones.length; ++i) {
            int sumaDistancias = 0;
            int viajes = 0;
            // Comprobamos la restricción de kilometros y viajes recorriendo el vector y no mirando directamente los km restantes y miramos que cuadren.
            for (int j = 0; j < Camion.MAX_VIAJES; ++j) {
                int distanciaViaje = getDistanciaViaje(i, j);
                if (distanciaViaje == 0) continue; // Si el viaje no tiene peticiones asignadas, no hace falta seguir comprobando

                ++viajes;

                if (distanciaViaje != camiones[i].getKmUsados()) {
                    errorMsg = "Error: El camión " + i + " tiene un error en el cálculo de kilómetros usados.";
                }
                sumaDistancias += distanciaViaje;
            }
            if (sumaDistancias != camiones[i].getKmUsados()) {
                errorMsg = "Error: El camión " + i + " tiene un error en el cálculo de kilómetros usados.";
                return false;
            }
            if (sumaDistancias > Camion.MAX_KM) {
                errorMsg = "Error: El camión " + i + " supera el máximo de kilómetros.";
                return false;
            }
            if (viajes != camiones[i].getViajesUsados()) {
                errorMsg = "Error: El camión " + i + " tiene un error en el cálculo de viajes usados.";
                return false;
            }
            if (viajes > Camion.MAX_VIAJES) {
                errorMsg = "Error: El camión " + i + " supera el máximo de viajes.";
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
}