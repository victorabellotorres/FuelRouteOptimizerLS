package aima_functions;

import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;
import domain.P1Board;
import java.util.*;

public class P1SuccessorFunction implements SuccessorFunction {

    @Override
    public List getSuccessors(Object o) {
        ArrayList<Successor> sucesores = new ArrayList<>();
        P1Board actual = (P1Board) o;

        for (int i = 0; i < actual.getPeticiones().length; i++) {
            for (int j = i + 1; j < actual.getPeticiones().length; j++) {
                P1Board nuevo = new P1Board(actual);
                if (nuevo.swapPeticiones(i, j)) {
                    sucesores.add(new Successor("swap(" + i + "," + j + ")", nuevo));
                }
            }
        }
        return sucesores;
    }
}
