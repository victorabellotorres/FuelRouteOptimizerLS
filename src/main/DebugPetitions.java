package main;

import domain.*;
import IA.Gasolina.*;

public class DebugPetitions {
    public static void main(String[] args) {
        Gasolineras gasolineras = new Gasolineras(100, 1234);
        Peticion[] peticiones = InitialBoardGenerator.crearPeticiones(gasolineras);
        int[] counts = new int[4];
        for (Peticion p : peticiones) {
            int d = p.getDias();
            if (d < 0 || d > 3) {
                System.out.println("OUT-OF-RANGE dias=" + d);
            } else {
                counts[d]++;
            }
        }
        System.out.println("Total peticiones=" + peticiones.length);
        for (int i = 0; i < counts.length; i++) System.out.println("dias=" + i + " => " + counts[i]);
    }
}
