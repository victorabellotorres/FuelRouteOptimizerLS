package aima_functions;

import aima.search.framework.HeuristicFunction;
import domain.*;

public class P1HeuristicFunctionBasica implements HeuristicFunction {

    private static final double COSTE_KM = 2.0;
    private static final double VALOR_DEP = 1000.0;

    @Override
    public double getHeuristicValue(Object o) {
        P1Board estado = (P1Board) o;

        return HeuristicaBasica(estado);
    }

    private double HeuristicaBasica(P1Board estado) {
        double beneficio = 0.0;
        double coste = 0.0;

        // Beneficio: peticiones atendidas hoy (solo las que tienen camion asignado)
        Peticion[] peticiones = estado.getPeticiones();
        for (Peticion p : peticiones) {
            double porcentaje = 0.0;
            // Si la peticion no tiene camion asignado, el beneficio es el que se obtiene si asumimos que se atiende un día después.
            if (p.getIdCamion() == -1) porcentaje = Math.max(0.0, 100.0 - Math.pow(2.0, Math.max(0, p.getDias()+1)));
            else {
                porcentaje = Math.max(0.0, 100.0 - Math.pow(2.0, Math.max(0, p.getDias())));
            }
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