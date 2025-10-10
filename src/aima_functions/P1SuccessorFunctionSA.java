package aima_functions;

import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;
import domain.State;
import java.util.*;

public class P1SuccessorFunctionSA implements SuccessorFunction {

    @Override
    public List getSuccessors(Object o) {
        ArrayList<Successor> sucesores = new ArrayList<>();
        State actual = (State) o;
        Random rand = new Random();

        int n = actual.getNumPeticiones();
        if (n < 2) return sucesores;

        int i = rand.nextInt(n);
        int j = rand.nextInt(n);
        while (i == j) j = rand.nextInt(n);

        State nuevo = new State(actual);
        if (nuevo.swapPeticiones(i, j)) {
            sucesores.add(new Successor("swap(" + i + "," + j + ")", nuevo));
        }
        return sucesores;
    }
}
