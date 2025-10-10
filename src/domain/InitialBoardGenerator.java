package domain;

import IA.Gasolina.*;


public class InitialBoardGenerator {

    // Importante, las distancias estaran precalculadas

    public static P1Board randomSolution(Gasolineras gasolineras, CentrosDistribucion centrosDistribucion) {
        P1Board estado = new P1Board(gasolineras.size(), centrosDistribucion.size());
        return estado;
    }
}
