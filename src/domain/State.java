package domain;

public class State {

    private Camion[] camiones;
    private int[] diasPeticiones;

    public State(int NumCamiones, int NumPeticiones) {
        camiones = new Camion[NumCamiones];
        for (int i = 0; i < NumCamiones; ++i) {
            camiones[i] = new Camion();
        }
        diasPeticiones = new int[NumPeticiones];
        for (int i = 0; i < NumPeticiones; ++i) {
            diasPeticiones[i] = -1;
        }
    }
}