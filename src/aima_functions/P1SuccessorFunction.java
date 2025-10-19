package aima_functions;

import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;
import domain.Camion;
import domain.P1Board;
import java.util.*;

public class P1SuccessorFunction implements SuccessorFunction {

    public static boolean[] operatorsEnabled = {
            true,   // swapPosicionPeticion
            true,   // swapViajes
            true    // swapCamiones
    };

    @Override
    public List getSuccessors(Object o) {
        P1Board actual = (P1Board) o;
//      sucesores.addAll(successorsSwapPeticiones(actual)); // No va bien

        ArrayList<Successor> sucesores = new ArrayList<>();
        if (operatorsEnabled[0]) sucesores.addAll(successorsSwapPosicionPeticion(actual));
        if (operatorsEnabled[1]) sucesores.addAll(successorsSwapViajes(actual));
        if (operatorsEnabled[2]) sucesores.addAll(successorsSwapCamiones(actual));
        return sucesores;
    }

    private ArrayList successorsSwapPeticiones(P1Board actual) {
        ArrayList<Successor> sucesores = new ArrayList<>();

        for (int i = 0; i < actual.getPeticiones().length; i++) {
            for (int j = i + 1; j < actual.getPeticiones().length; j++) {
                P1Board nuevo = new P1Board(actual);
                if (nuevo.swapPeticiones(i, j)) {
                    sucesores.add(new Successor("swapPeticiones(" + i + "," + j + ")", nuevo));
                }
            }
        }
        return sucesores;
    }

    private ArrayList successorsSwapPosicionPeticion(P1Board actual) {
        ArrayList<Successor> sucesores = new ArrayList<>();

        for (int c = 0; c < actual.getCamiones().length; c++) {
            for (int v = 0; v < Camion.MAX_KM; v++) {
                    for (int p = 0; p < actual.getPeticiones().length; p++) {
                        P1Board nuevo = new P1Board(actual);
                        if (nuevo.swapPosicionPeticion(c, v, true, p)) {
                            sucesores.add(new Successor("swapPosicionPeticion(Camion:" + c + ", Viaje:" + v + ", first, Peticion" + p + ")", nuevo));
                        }
                        nuevo = new P1Board(actual);
                        if (nuevo.swapPosicionPeticion(c, v, false, p)) {
                            sucesores.add(new Successor("swapPosicionPeticion(Camion:" + c + " Viaje:" + v + ", second, Peticion" + p + ")", nuevo));
                        }
                    }
            }
        }
        return sucesores;
    }

    private ArrayList successorsSwapViajes(P1Board actual) {
        ArrayList<Successor> sucesores = new ArrayList<>();

        for (int c1 = 0; c1 < actual.getCamiones().length; c1++) {
            for (int v1 = 0; v1 < Camion.MAX_VIAJES; v1++) {
                for (int c2 = c1+1; c2 < actual.getCamiones().length; c2++) {
                    for (int v2 = 0; v2 < Camion.MAX_VIAJES; v2++) {
                        P1Board nuevo = new P1Board(actual);
                        if (nuevo.swapViajes(c1, v1, c2, v2)) {
                            sucesores.add(new Successor("swapViajes(Camion1:" + c1 + ", Viaje1:" + v1 + ", Camion2:" + c2 + ", Viaje2:" + v2 + ")", nuevo));
                        }
                    }
                }
            }
        }
        return sucesores;
    }

    private ArrayList successorsSwapCamiones(P1Board actual) {
        ArrayList<Successor> sucesores = new ArrayList<>();

        for (int c1 = 0; c1 < actual.getCamiones().length; c1++) {
            for (int c2 = c1+1; c2 < actual.getCamiones().length; c2++) {
                P1Board nuevo = new P1Board(actual);
                if (nuevo.swapCamiones(c1, c2)) {
                    sucesores.add(new Successor("swapCamiones(Camion1:" + c1 + ", Camion2:" + c2 + ")", nuevo));
                }
            }
        }
        return sucesores;
    }

    public static void setOperatorsEnabled(boolean[] operations) {
        operatorsEnabled[0] = operations[0];
        operatorsEnabled[1] = operations[1];
        operatorsEnabled[2] = operations[2];
    }

}

