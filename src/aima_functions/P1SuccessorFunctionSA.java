package aima_functions;

import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;
import domain.P1Board;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Simple Simulated Annealing successor: sample a few random swaps and return the valid ones.
 * This is intentionally small and random to keep branching low for SA.
 */
public class P1SuccessorFunctionSA implements SuccessorFunction {

    private final Random rand;
    private final int neighborCount;

    public P1SuccessorFunctionSA() {
        this(5);
    }

    public P1SuccessorFunctionSA(int neighborCount) {
        this.neighborCount = Math.max(1, neighborCount);
        this.rand = new Random();
    }

    @Override
    public List getSuccessors(Object o) {
        ArrayList<Successor> retval = new ArrayList<>();

        P1Board s = (P1Board) o;
        int n = s.getPeticiones().length;
        if (n < 2) {
            retval.add(new Successor("noop", new P1Board(s)));
            return retval;
        }

        int attempts = 0;
        int maxAttempts = Math.max(200, neighborCount * 40);
        while (retval.size() < neighborCount && attempts < maxAttempts) {
            attempts++;
            int i = rand.nextInt(n);
            int j = rand.nextInt(n);
            if (i == j) continue;

            P1Board copy = new P1Board(s);
            if (copy.swapPeticiones(i, j)) {
                retval.add(new Successor("swap(" + i + "," + j + ")", copy));
            }
        }

        if (retval.isEmpty()) {
            retval.add(new Successor("noop", new P1Board(s)));
        }

        return retval;
    }

}