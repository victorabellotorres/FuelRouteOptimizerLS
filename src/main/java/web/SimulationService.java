package web;

import aima.search.framework.Problem;
import aima.search.framework.Search;
import aima.search.framework.SearchAgent;
import aima.search.framework.SuccessorFunction;
import aima.search.informed.HillClimbingSearch;
import aima.search.informed.SimulatedAnnealingSearch;
import domain.*;
import main.Constants;
import web.dto.RouteInfo;
import web.dto.SimulationRequest;
import web.dto.SimulationResponse;
import aima_functions.*;
import IA.Gasolina.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Properties;

public class SimulationService {

    // Synchronized because the underlying Code (Constants, P1Board static vars) is not thread-safe
    public synchronized static SimulationResponse runSimulation(SimulationRequest req) throws Exception {
        // 1. Configurar Constantes
        Constants.NUM_GASOLINERAS = req.numGasolineras > 0 ? req.numGasolineras : 100;
        Constants.NUM_CENTROSDISTRIBUCION = req.numCentros > 0 ? req.numCentros : 10;
        Constants.CAMIONES_POR_CENTRO = req.camionesPorCentro > 0 ? req.camionesPorCentro : 1;
        Constants.SEED = req.seed != 0 ? req.seed : 1234;
        
        Constants.ALGORITMO_ESTADO_INICIAL = req.algoritmoInicial > 0 ? req.algoritmoInicial : 1;
        Constants.ALGORITMO_BUSQUEDA = req.algoritmoBusqueda > 0 ? req.algoritmoBusqueda : 1;
        
        // SA params
        Constants.SA_STEPS = req.saSteps;
        Constants.SA_STITER = req.saStiter;
        Constants.SA_K = req.saK;
        Constants.SA_LAMBDA = req.saLambda;

        // 2. Crear Estado Inicial
        Gasolineras gasolineras = new Gasolineras(Constants.NUM_GASOLINERAS, Constants.SEED);
        CentrosDistribucion centros = new CentrosDistribucion(Constants.NUM_CENTROSDISTRIBUCION, Constants.CAMIONES_POR_CENTRO, Constants.SEED);
        
        P1Board estadoInicial = null;
        switch (Constants.ALGORITMO_ESTADO_INICIAL) {
            case 1 -> estadoInicial = InitialBoardGenerator.SolucionSinAsignaciones(gasolineras, centros);
            case 2 -> estadoInicial = InitialBoardGenerator.SolucionAsignacionAleatoria(gasolineras, centros);
            default -> estadoInicial = InitialBoardGenerator.SolucionSinAsignaciones(gasolineras, centros);
        }

        // 3. Configurar Busqueda
        SuccessorFunction sf;
        Search search;
        P1HeuristicFunction hf = new P1HeuristicFunction();

        if (Constants.ALGORITMO_BUSQUEDA == 2) {
            sf = new P1SuccessorFunctionSA();
            search = new SimulatedAnnealingSearch(Constants.SA_STEPS, Constants.SA_STITER, Constants.SA_K, Constants.SA_LAMBDA);
        } else {
            sf = new P1SuccessorFunction();
            search = new HillClimbingSearch();
        }

        // 4. Run
        Problem problem = new Problem(estadoInicial, sf, new P1GoalTest(), hf);
        long start = System.currentTimeMillis();
        SearchAgent agent = new SearchAgent(problem, search);
        long end = System.currentTimeMillis();

        P1Board finalState = (P1Board) search.getGoalState();

        // 5. Build Response
        SimulationResponse resp = new SimulationResponse();
        resp.beneficio = finalState.getBeneficio();
        resp.coste = finalState.getCoste();
        resp.tiempoEjecucionMs = end - start;
        resp.log = new ArrayList<>();
        
        Properties instr = agent.getInstrumentation();
        Iterator keys = instr.keySet().iterator();
        while (keys.hasNext()) {
             String key = (String) keys.next();
             resp.log.add(key + "=" + instr.getProperty(key));
        }

        // Coordinates
        resp.gasolineras = new ArrayList<>();
        // Gasolineras API (IA.Gasolina) has getX(), getY() but usually accessed via index?
        // Wait, Gasolineras object is a list.
        for (int i = 0; i < gasolineras.size(); i++) {
             // Assuming Pair for compat, though Gasolineras gives (x,y)
             // We need to check exact API of Gasolineras class.
             // Based on P1Board, it has `Pair[] gasolineras`.
             // But P1Board constructor: `gasolineras = new Pair[numGasolineras];` initialized to (-1,-1)? 
             // Ah, P1Board uses Pair as POSITIONS? Or just placeholders?
             // Looking at P1Board source line 26: `gasolineras[i] = new Pair(-1, -1);`
             // Wait, P1Board doesn't seem to store the coordinates of gas stations inside itself permanently?
             // It calls `P1Board.distanciasGasolineras` (static).
             
             // WE NEED THE COORDINATES for the Frontend!
             // `Gasolineras` object (passed to generator) has them.
             // Let's assume `gasolineras.get(i)` returns a {int coordX, int coordY}.
             // I will use reflection or check the jar if I could, but assuming standard getters.
             // Actually, `Gasolineras` extends `ArrayList<Gasolinera>`.
             // `Gasolinera` has `getCoordX()`, `getCoordY()`.
             int x = gasolineras.get(i).getCoordX();
             int y = gasolineras.get(i).getCoordY();
             resp.gasolineras.add(new Pair(x, y));
        }
        
        resp.centros = new ArrayList<>();
        for (int i = 0; i < centros.size(); i++) {
             int x = centros.get(i).getCoordX();
             int y = centros.get(i).getCoordY();
             resp.centros.add(new Pair(x, y));
        }

        // Rutas
        resp.rutas = buildRoutes(finalState, centros, gasolineras);

        return resp;
    }

    private static List<RouteInfo> buildRoutes(P1Board board, CentrosDistribucion centros, Gasolineras gasolineras) {
        List<RouteInfo> routes = new ArrayList<>();
        Camion[] camiones = board.getCamiones();
        Peticion[] peticiones = board.getPeticiones();

        int camionesPorCentro = Constants.CAMIONES_POR_CENTRO;

        for (int cId = 0; cId < camiones.length; cId++) {
            Camion c = camiones[cId];
            if (c.getViajesUsados() == 0) continue; 

            int centroId = cId / camionesPorCentro;
            RouteInfo r = new RouteInfo();
            r.camionId = cId;
            r.centroId = centroId;
            r.puntos = new ArrayList<>();

            int cx = centros.get(centroId).getCoordX();
            int cy = centros.get(centroId).getCoordY();
            
            for (Pair viaje : c.getViajes()) {
                if (viaje.isEmpty()) continue;
                
                r.puntos.add(new RouteInfo.Point(cx, cy, "CENTRO", centroId));
                
                if (viaje.first != -1) {
                    addStop(r, peticiones[viaje.first], gasolineras);
                }
                
                if (viaje.second != -1) {
                     addStop(r, peticiones[viaje.second], gasolineras);
                }
                
                r.puntos.add(new RouteInfo.Point(cx, cy, "CENTRO", centroId));
            }
            routes.add(r);
        }
        return routes;
    }
    
    private static void addStop(RouteInfo r, Peticion p, Gasolineras gasolineras) {
         int gasId = p.getGasolinera();
         int gx = gasolineras.get(gasId).getCoordX();
         int gy = gasolineras.get(gasId).getCoordY();
         r.puntos.add(new RouteInfo.Point(gx, gy, "GASOLINERA", gasId));
    }
}
