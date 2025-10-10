package aima_functions;

import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;
import domain.State;
import java.util.*;

public class P1SuccessorFunction implements SuccessorFunction {

    @Override
    public List getSuccessors(Object o) {
        ArrayList<Successor> sucesores = new ArrayList<>();
        State actual = (State) o;

        for (int i = 0; i < actual.getNumPeticiones(); i++) {
            for (int j = i + 1; j < actual.getNumPeticiones(); j++) {
                State nuevo = new State(actual);
                if (nuevo.swapPeticiones(i, j)) {
                    sucesores.add(new Successor("swap(" + i + "," + j + ")", nuevo));
                }
            }
        }
        return sucesores;
    }
}
