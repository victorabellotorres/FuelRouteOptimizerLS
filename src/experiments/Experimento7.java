package experiments;

import aima.search.framework.*;
import aima.search.informed.HillClimbingSearch;
import aima.search.informed.SimulatedAnnealingSearch;
import aima_functions.*;
import domain.*;
import IA.Gasolina.*;

import org.apache.commons.csv.*;
import org.apache.commons.io.*;
import org.apache.commons.codec.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import java.util.Random;

public class Experimento7 {

    //    public static final int ITERACIONES = 10;
    public static final int ITERACIONES_ALEATORIO = 5;
    public static final int NUM_CENTROS_DISTRIBUCION = 10;
    public static final int NUM_GASOLINERAS = 100;
    public static final int NUM_CAMIONES_POR_CENTRO = 1;
    public static int[] MAX_KM_CAMION = {560, 640, 720};
    public static final boolean[] OPERATORS = {true, true, true, false};
    public static final String ALGORITMO_ESTADO_INICIAL = "GreedyCuadrantes";//  "Sin_Assignacion", "Aleatorio", "Aleatorio_1PxV", "GreedyDistancia", "GreedyCuadrantes"
    public static final String HEURISTICA = "Basica"; // "Basica" o "Avanzada"
    private static final String OUTPUT_FILE = "data/experimento7.csv";

    public void run(int[] SEEDS) throws Exception {
        File file = new File(OUTPUT_FILE);
        if (file.exists() && file.length() != 0) {
            // Eliminamos el contenido del archivo
            FileUtils.writeStringToFile(file, "", StandardCharsets.UTF_8);
        }

        P1SuccessorFunction.operatorsEnabled = OPERATORS;

        for (int k = 0; k < MAX_KM_CAMION.length; k++) {
            Camion.MAX_KM = MAX_KM_CAMION[k];
            for (int i = 0; i < SEEDS.length; i++) {
                int SEED = SEEDS[i];
                System.out.println("===========================================================");
                System.out.println("Iteración " + i + "/" + (SEEDS.length - 1));

                System.out.println("Generando gasolineras y centros de distribución...");

                Gasolineras gasolineras = new Gasolineras(NUM_GASOLINERAS, SEED);
                CentrosDistribucion centrosDistribucion = new CentrosDistribucion(NUM_CENTROS_DISTRIBUCION, NUM_CAMIONES_POR_CENTRO, SEED);

                System.out.println("Generando estado inicial...");
                System.out.println("===========================================================");

                System.out.println("Generando estado inicial...");
                System.out.println("===========================================================");
                P1Board estadoInicial = null;
                switch (ALGORITMO_ESTADO_INICIAL) {
                    case "Sin_Assignacion" -> {
                        estadoInicial = InitialBoardGenerator.SolucionSinAsignaciones(gasolineras, centrosDistribucion);
                    }
                    case "Aleatorio" -> {
                        estadoInicial = InitialBoardGenerator.SolucionAsignacionAleatoria(gasolineras, centrosDistribucion);
                    }
                    case "Aleatorio_1PxV" -> {
                        estadoInicial = InitialBoardGenerator.SolucionAsignacionAleatoria1PeticionPorViaje(gasolineras, centrosDistribucion);
                    }
                    case "GreedyDistancia" -> {
                        estadoInicial = InitialBoardGenerator.SolucionAsignacionGreedyDistancia(gasolineras, centrosDistribucion);
                    }
                    case "GreedyCuadrantes" -> {
                        estadoInicial = InitialBoardGenerator.SolucionAsignacionGreedyQuadrants(gasolineras, centrosDistribucion);
                    }
                    // Añadir más casos si se quiere
                }

                HeuristicFunction hf = HEURISTICA.equals("Avanzada") ? new P1HeuristicFunction() : new P1HeuristicFunctionBasica();

                int iteraciones_aleatorio =
                        (Objects.equals(ALGORITMO_ESTADO_INICIAL, "Aleatorio") ||
                                Objects.equals(ALGORITMO_ESTADO_INICIAL, "Aleatorio_1PxV"))
                                ? ITERACIONES_ALEATORIO : 1;
                for (int j = 0; j < iteraciones_aleatorio; j++) {
                    // Hill Climbing
                    SuccessorFunction sf = new P1SuccessorFunction();
                    Search search = new HillClimbingSearch();

                    Problem problem = new Problem(
                            estadoInicial,
                            sf,
                            new P1GoalTest(),
                            hf
                    );

                    long start = System.currentTimeMillis();
                    SearchAgent agent = new SearchAgent(problem, search);
                    long end = System.currentTimeMillis();

                    P1Board estadoFinal = (P1Board) search.getGoalState();

                    StringBuilder errMsg = new StringBuilder();

                    // Debug si el estado final no es solucion
                    if (!estadoFinal.esSolucion(errMsg)) {
                        System.out.println("El estado final no es solución válida: " + errMsg);
                        System.out.println("Estado inicial:");
                        if (!estadoInicial.esSolucion(errMsg))
                            System.out.println("Estado inicial no es solucion: " + errMsg);
                        System.out.println("===========================================================");
                    }
                    String nodosExpandidos = agent.getInstrumentation().getProperty("nodesExpanded");
                    double hInicial = hf.getHeuristicValue(estadoInicial);
                    double hFinal = hf.getHeuristicValue(estadoFinal);
                    exportarDatos(estadoInicial, hInicial, estadoFinal, ALGORITMO_ESTADO_INICIAL, "HillClimbing", HEURISTICA, hFinal, SEED, i, nodosExpandidos, end - start, OPERATORS, MAX_KM_CAMION[k]);

                }
            }
        }

    }

    private void exportarDatos(P1Board estadoInicial, double hInicial, P1Board estadoFinal, String algoritmoEstadoIni, String algorithmSearch, String funcionHeuristica ,double hFinal, int seed, int iteracion, String nodosExpandidos, long timeMili, boolean[] operators, int maxKm) throws IOException {
        BoardMetrics metricsInicial = estadoInicial.getMetrics();
        BoardMetrics metricsFinal = estadoFinal.getMetrics();

        File file = new File(OUTPUT_FILE);
        boolean writeHeader = !file.exists() || file.length() == 0;
        String header = "iteration,seed,time_ms,algorithmEI,algorithmSearch,op1,op2,op3,op4,nodesExpanded,validInicial,validFinal,heurFunction,heurIni,heurFi,costIni,costFi,profitIni,profitFi,AssignedPetitionsIni,AssignedPetitionsFi,trucksUsedIni,trucksUsedFi,totalKmIni,totalKmFi,maxKm\n";

        try (Writer out = new OutputStreamWriter(new FileOutputStream(file, true), StandardCharsets.UTF_8)) {
            if (writeHeader) {
                out.write(header);
            }
            try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT)) {
                printer.printRecord(
                        iteracion,
                        seed,
                        timeMili,
                        algoritmoEstadoIni,
                        algorithmSearch,
                        operators[0],
                        operators[1],
                        operators[2],
                        operators[3],
                        nodosExpandidos,
                        metricsInicial.isValid(),
                        metricsFinal.isValid(),
                        funcionHeuristica,
                        hInicial,
                        hFinal,
                        metricsInicial.getCoste(),
                        metricsFinal.getCoste(),
                        metricsInicial.getBeneficio(),
                        metricsFinal.getBeneficio(),
                        metricsInicial.getPeticionesAssignadas(),
                        metricsFinal.getPeticionesAssignadas(),
                        metricsInicial.getCamionesUsados(),
                        metricsFinal.getCamionesUsados(),
                        metricsInicial.getTotalKm(),
                        metricsFinal.getTotalKm(),
                        maxKm
                );
                printer.flush();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

