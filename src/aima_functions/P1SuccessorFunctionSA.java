package aima_functions;

import aima.search.framework.HeuristicFunction;
import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;
import domain.Camion;
import domain.P1Board;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Simple Simulated Annealing successor: sample a few random swaps and return the valid ones.
 * This is intentionally small and random to keep branching low for SA.
 */
public class P1SuccessorFunctionSA implements SuccessorFunction {

    public static int neighborCount = 1;



    public static boolean[] operatorsEnabled = {
            true,    // swapPeticiones
            true,    // movePeticionToPosition + removePeticion
            true,   // swapViajes
            true    // swapCamiones
    };


    @Override
    public List getSuccessors(Object o) {
        P1Board actual = (P1Board) o;
        ArrayList<Successor> sucesores = new ArrayList<>();

        Random rand = new Random();

        // escogemos un operador
        Successor successor = null;
        while (successor == null) {
            int op = rand.nextInt(operatorsEnabled.length);
            while (!operatorsEnabled[op]) {
                op = rand.nextInt(operatorsEnabled.length);
            }

            if (op == 0) {
                successor = (successorSwapPeticiones(actual));
            }
            if (op == 1) {
                // Con probabilidad de 50% se escoge uno o otro
                if (rand.nextBoolean()) {
                    successor = (successorMovePeticionToPosition(actual));
                } else {
                    successor = (successorRemovePeticionOnPosition(actual));
                }
            }
            if (op == 2) {
                successor = (successorSwapViajes(actual));
            }
            if (op == 3) {
                successor = (successorSwapCamiones(actual));
            }
        }
        sucesores.add(successor);

        return sucesores;
    }

    private Successor successorSwapPeticiones(P1Board actual) {
        Successor successor = null;
        P1Board next = null;
        Random rand = new Random();
        int maxAttemps = 10; // para poner un limite y evitar bucles infinitos
        while (maxAttemps > 0 && next == null) {
            int i = rand.nextInt(actual.getPeticiones().length);
            int j = rand.nextInt(actual.getPeticiones().length);
            if (i != j) {
                P1Board nuevo = new P1Board(actual);
                if (nuevo.swapPeticiones(i, j)) {
                    next = nuevo;
                    successor = new Successor("swapPeticiones(" + i + "," + j + ")", nuevo);
                }
            }
            --maxAttemps;
        }

        return successor;
    }

    private Successor successorMovePeticionToPosition(P1Board actual) {
        Successor successor = null;
        Random rand = new Random();
        boolean[][] viajesConSitio = new boolean[actual.getCamiones().length][Camion.MAX_VIAJES];

        for (int p = 0; p < actual.getPeticiones().length; p++) {
            for (int c = 0; c < actual.getCamiones().length; c++) {
                for (int v = 0; v < Camion.MAX_VIAJES; v++) {
                    viajesConSitio[c][v] = actual.getCamiones()[c].getViajes()[v].first == -1 || actual.getCamiones()[c].getViajes()[v].second == -1;
                }
            }
        }
        int maxAttempts = 10; // para poner un limite y evitar bucles infinitos
        int maxAttempts2 = 5;
        P1Board next = null;
        while (maxAttempts > 0 && next == null) {
            int c = rand.nextInt(actual.getCamiones().length);
            int v = rand.nextInt(Camion.MAX_VIAJES);
            if (viajesConSitio[c][v]) {
                boolean first = rand.nextBoolean();
                if (first && actual.getCamiones()[c].getViajes()[v].first != -1) {
                    first = false;
                } else if (!first && actual.getCamiones()[c].getViajes()[v].second != -1) {
                    first = true;
                }
                while ( maxAttempts2 > 0 && next == null) {
                    int p = rand.nextInt(actual.getPeticiones().length);
                    P1Board nuevo = new P1Board(actual);
                    if (nuevo.movePeticionToPosicion(c, v, first, p)) {
                        next = nuevo;
                        if (first)
                            successor = new Successor("movePeticionToPosition(Peticion:" + p + ", Camion:" + c + ", Viaje:" + v + ", first)", nuevo);
                        else
                            successor = new Successor("movePeticionToPosition(Peticion:" + p + ", Camion:" + c + ", Viaje:" + v + ", second)", nuevo);
                    }
                    --maxAttempts2;
                }
            }
            --maxAttempts;
        }
        return successor;
    }

    private Successor successorRemovePeticionOnPosition(P1Board actual) {
        Successor successor = null;
        Random rand = new Random();
        boolean[][] viajesConPeticiones = new boolean[actual.getCamiones().length][Camion.MAX_VIAJES];

        for (int p = 0; p < actual.getPeticiones().length; p++) {
            for (int c = 0; c < actual.getCamiones().length; c++) {
                for (int v = 0; v < Camion.MAX_VIAJES; v++) {
                    viajesConPeticiones[c][v] = actual.getCamiones()[c].getViajes()[v].first != -1 || actual.getCamiones()[c].getViajes()[v].second != -1;
                }
            }
        }
        int maxAttempts = 10; // para poner un limite y evitar bucles infinitos
        int maxAttempts2 = 5;
        P1Board next = null;
        while (maxAttempts > 0 && next == null) {
            int c = rand.nextInt(actual.getCamiones().length);
            int v = rand.nextInt(Camion.MAX_VIAJES);
            if (viajesConPeticiones[c][v]) {
                boolean first = rand.nextBoolean();
                if (first && actual.getCamiones()[c].getViajes()[v].first == -1) {
                    first = false;
                } else if (!first && actual.getCamiones()[c].getViajes()[v].second == -1) {
                    first = true;
                }
                while ( maxAttempts2 > 0 && next == null) {
                    int p = rand.nextInt(actual.getPeticiones().length);
                    P1Board nuevo = new P1Board(actual);
                    if (nuevo.movePeticionToPosicion(c, v, first, p)) {
                        next = nuevo;
                        if (first)
                            successor = (new Successor("removePeticionOnPosition(Camion:" + c + ", Viaje:" + v + ", first)", nuevo));
                        else
                            successor = (new Successor("removePeticionOnPosition(Camion:" + c + ", Viaje:" + v + ", second)", nuevo));
                    }
                    --maxAttempts2;
                }
            }
            --maxAttempts;
        }
        return successor;
    }

    private Successor successorSwapViajes(P1Board actual) {
        Successor successor = null;
        Random rand = new Random();
        P1Board next = null;
        int maxAttemps = 10; // para poner un limite y evitar bucles infinitos

        while (maxAttemps > 0 && next == null) {
            int c1 = rand.nextInt(actual.getCamiones().length);
            int c2 = rand.nextInt(actual.getCamiones().length);
            int v1 = rand.nextInt(Camion.MAX_VIAJES);
            int v2 = rand.nextInt(Camion.MAX_VIAJES);
            while (c1 == c2 && v1 == v2) {
                v2 = rand.nextInt(Camion.MAX_VIAJES);
            }
            P1Board nuevo = new P1Board(actual);
            if (nuevo.swapViajes(c1, v1, c2, v2)) {
                next = nuevo;
                successor = new Successor("swapViajes(Camion1:" + c1 + ", Viaje1:" + v1 + ", Camion2:" + c2 + ", Viaje2:" + v2 + ")", nuevo);
            }
            --maxAttemps;
        }

        return successor;
    }

    private Successor successorSwapCamiones(P1Board actual) {
        Successor successor = null;
        Random rand = new Random();
        P1Board next = null;
        int maxAttemps = 10; // para poner un limite y evitar bucles infinitos

        while (maxAttemps > 0 && next == null) {
            int c1 = rand.nextInt(actual.getCamiones().length);
            int c2 = rand.nextInt(actual.getCamiones().length);
            while (c1 == c2) {
                c2 = rand.nextInt(actual.getCamiones().length);
            }
            P1Board nuevo = new P1Board(actual);
            if (nuevo.swapCamiones(c1, c2)) {
                next = nuevo;
                successor = new Successor("swapCamiones(Camion1:" + c1 + ", Camion2:" + c2 + ")", nuevo);
            }
            --maxAttemps;
        }

        return successor;
    }

    public static void setOperatorsEnabled(boolean[] operations) {
        operatorsEnabled[0] = operations[0];
        operatorsEnabled[1] = operations[1];
        operatorsEnabled[2] = operations[2];
        operatorsEnabled[3] = operations[3];
    }

}