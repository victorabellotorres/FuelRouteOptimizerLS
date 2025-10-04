package aima_functions;

import aima.search.framework.GoalTest;

public class P1GoalTest implements GoalTest {

    @Override
    public boolean isGoalState(Object state) {
        //siempre devuelve falso, en busqueda local desconocemos el estado final.
        return false;
    }
}
