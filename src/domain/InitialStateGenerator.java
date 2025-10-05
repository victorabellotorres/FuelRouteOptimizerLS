package domain;

import IA.Gasolina.*;

import java.util.*;



public class InitialStateGenerator {
    public static State randomSolution(Gasolineras gasolineras, CentrosDistribucion centrosDistribucion) {
        State estado = new State(gasolineras.size(), centrosDistribucion.size());
        return estado;
    }
}
