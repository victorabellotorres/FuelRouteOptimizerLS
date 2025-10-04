package aima_functions;

import aima.search.framework.SuccessorFunction;

import java.util.ArrayList;
import java.util.List;

import domain.*;

public class P1SuccesorFunction implements SuccessorFunction {

    @Override
    public List getSuccessors(Object o) {
        ArrayList retval = new ArrayList();

        State s = (State) o;

        // afegir aqui els successors

        return retval;
    }

}
