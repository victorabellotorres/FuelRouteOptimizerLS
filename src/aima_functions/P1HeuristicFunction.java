package aima_functions;

import aima.search.framework.HeuristicFunction;
import domain.*;

public class P1HeuristicFunction implements HeuristicFunction {

    private static final double COSTE_KM = 2.0;
    private static final double VALOR_DEP = 1000.0;

    @Override
    public double getHeuristicValue(Object o) {
        P1Board estado = (P1Board) o;
        double beneficio = 0.0;
        double coste = 0.0;

        // Beneficio: peticiones atendidas hoy
        Peticion[] peticiones = estado.getPeticiones();
        for (Peticion p : peticiones) {
            double porcentaje = 100.0 - Math.pow(2.0, p.getDias());
            if (porcentaje < 0) porcentaje = 0;
            beneficio += VALOR_DEP * (porcentaje / 100.0);
        }

        // Coste: kilómetros totales recorridos
        for (Camion c : estado.getCamiones()) {
            coste += c.getKmUsados() * COSTE_KM;
        }

        // Heurística (AIMA minimiza, por lo que restamos el beneficio al coste para que salga negativo)
        return coste - beneficio;
    }
}