package domain;

public class P1Board {

    private Camion[] camiones;
    private Peticion[] peticiones

    //vector de peticiones, cada peticion tiene un idcamion, idviaje, y un objeto peticion
    private

    // Crea
    public P1Board(int NumCamiones, int NumPeticiones) {
        camiones = new Camion[NumCamiones];
        for (int i = 0; i < NumCamiones; ++i) {
            camiones[i] = new Camion();
        }

        peticiones = new int[NumPeticiones];
        for (int i = 0; i < NumPeticiones; ++i) {
            peticiones[i] = new Peticion();
        }
    }

    public P1Board(Camion[] camiones, Peticion[] peticiones) {
        camiones = new Camion[NumCamiones];
        for (int i = 0; i < NumCamiones; ++i) {
            camiones[i] = new Camion();
        }

        peticiones = new int[NumPeticiones];
    }
}