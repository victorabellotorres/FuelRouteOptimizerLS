package experiments;

import aima.search.framework.*;
import aima.search.informed.HillClimbingSearch;
import aima_functions.P1GoalTest;
import aima_functions.P1HeuristicFunction;
import aima_functions.P1HeuristicFunctionBasica;
import aima_functions.P1SuccessorFunction;
import domain.*;
import IA.Gasolina.*;

import org.apache.commons.csv.*;
import org.apache.commons.io.*;
import org.apache.commons.codec.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import java.util.Random;

public class Experimento1 {

//    public static final int ITERACIONES = 10;
    public static final int ITERACIONES_ALEATORIO = 5;
    public static final int NUM_CENTROS_DISTRIBUCION = 10;
    public static final int NUM_GASOLINERAS = 100;
    public static final int NUM_CAMIONES_POR_CENTRO = 1;
    public static final boolean[][] OPERATORS = {
            // SwapPosicionPeticion, (MovePeticionToPosition + RemovePeticionOnPosition), SwapViajes, SwapCamiones
            {true, false, false, false},
            {false, true, false, false},
            {false, false, true, false},
            {true, false, true, true},
            {false, true, true, true},
            {true, true, false, false},
            {true, true, true, true}
    };

    private static final String OUTPUT_FILE = "data/experimento1.csv";


    public void run(int[] SEEDS) throws Exception {
        File file = new File(OUTPUT_FILE);
        if (file.exists() && file.length() != 0) {
            // Eliminamos el contenido del archivo
            FileUtils.writeStringToFile(file, "", StandardCharsets.UTF_8);
        }

        Random r = new Random();
        for (int i = 0; i < SEEDS.length; i++) {
            int SEED = SEEDS[i];
            System.out.println("===========================================================");
            System.out.println("Iteración " + i + "/" + (SEEDS.length - 1));

            System.out.println("Generando gasolineras y centros de distribución...");
            Gasolineras gasolineras = new Gasolineras(NUM_GASOLINERAS, SEED);
            CentrosDistribucion centrosDistribucion = new CentrosDistribucion(NUM_CENTROS_DISTRIBUCION, NUM_CAMIONES_POR_CENTRO, SEED);


            System.out.println("Generando estado inicial...");
            System.out.println("===========================================================");

            P1Board estadoInicial = InitialBoardGenerator.SolucionAsignacionAleatoria(gasolineras, centrosDistribucion);
            String algoritmoEstadoIni = "Aleatorio";

            SuccessorFunction sf = new P1SuccessorFunction();
            Search search = new HillClimbingSearch();
            HeuristicFunction hf1 = new P1HeuristicFunction();
            HeuristicFunction hf2 = new P1HeuristicFunctionBasica();

            HeuristicFunction[] hfs = {hf1, hf2};



            for (int op = 0; op < OPERATORS.length; op++) {
                P1SuccessorFunction.operatorsEnabled = OPERATORS[op];
                for (int j = 0; j < ITERACIONES_ALEATORIO; j++) {
                    for (int h = 0; h < hfs.length; h++) {
                        HeuristicFunction hf = hfs[h];
                        System.out.println("Operadores: " + Arrays.toString(OPERATORS[op]) + " | Heurística: " + (h == 0 ? "Avanzada" : "Basica") + " | Iteración Aleatoria: " + (j + 1) + "/" + ITERACIONES_ALEATORIO);
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
                            System.out.println(estadoInicial);
                            System.out.println("Estado final:");
                            System.out.println(estadoFinal);
                            System.out.println("===========================================================");
                            System.out.println(OPERATORS[op]);
                            System.out.println("===========================================================");
                            List actions = agent.getActions();
                            for (int a = 0; i < actions.size(); a++) {
                                String action = (String) actions.get(a);
                                System.out.println(action);
                            }
                            System.out.println("===========================================================");
                        }

                        String nodosExpandidos = agent.getInstrumentation().getProperty("nodesExpanded");

                        double hInicial = hf.getHeuristicValue(estadoInicial);
                        double hFinal = hf.getHeuristicValue(estadoFinal);

                        exportarDatos(estadoInicial, hInicial, estadoFinal,algoritmoEstadoIni,"HilllClimbing", (h == 0 ? "Avanzada" : "Basica"), hFinal, SEED, i, nodosExpandidos, end - start, OPERATORS[op]);
                    }
                }
            }
        }
    }

    private void exportarDatos(P1Board estadoInicial, double hInicial, P1Board estadoFinal, String algoritmoEstadoIni, String algorithmSearch, String funcionHeuristica ,double hFinal, int seed, int iteracion, String nodosExpandidos, long timeMili, boolean[] operators) throws IOException {
        BoardMetrics metricsInicial = estadoInicial.getMetrics();
        BoardMetrics metricsFinal = estadoFinal.getMetrics();

        File file = new File(OUTPUT_FILE);
        boolean writeHeader = !file.exists() || file.length() == 0;
        String header = "iteration,seed,time_ms,algorithmEI,algorithmSearch,op1,op2,op3,op4,nodesExpanded,validInicial,validFinal,heurFunction,heurIni,heurFi,costIni,costFi,profitIni,profitFi,AssignedPetitionsIni,AssignedPetitionsFi,trucksUsedIni,trucksUsedFi,totalKmIni,totalKmFi\n";

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
                        metricsFinal.getTotalKm()
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
