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
        if (peticiones[i].getIdCamion() == -1 && peticiones[j].getIdCamion() == -1) return false; // Si alguna de las dos peticiones no tiene un camion asignado, no se puede hacer el swap

        if (peticiones[i].getIdCamion() != -1 && peticiones[j].getIdCamion() != -1) {
            //TODO
        } else if (peticiones[i].getIdCamion() != -1) {
            //TODO
        } else {
            //TODO
        }
        
        return true;
    }
}