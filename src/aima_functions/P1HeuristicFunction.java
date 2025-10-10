package aima_functions;

import aima.search.framework.HeuristicFunction;
import domain.*;

public class P1HeuristicFunction implements HeuristicFunction {

    private static final double COSTE_KM = 2.0;
    private static final double VALOR_DEP = 1000.0;

    @Override
    public double getHeuristicValue(Object o) {
        State estado = (State) o;
        double beneficio = 0.0;
        double coste = 0.0;

        // Beneficio: peticiones atendidas hoy
        int[] diasPeticiones = estado.getDiasPeticiones();
        for (int d : diasPeticiones) {
            if (d >= 0) {  // petición atendida
                double porcentaje = 100.0 - Math.pow(2.0, d);
                if (porcentaje < 0) porcentaje = 0;
                beneficio += VALOR_DEP * (porcentaje / 100.0);
            }
        }

        // Coste: kilómetros totales recorridos
        for (Camion c : estado.getCamiones()) {
            int kmUsados = Camion.MAX_KM - c.getKmRestantes();
            coste += kmUsados * COSTE_KM;
        }

        // Heurística (AIMA minimiza)
        return coste - beneficio;
    }
}
