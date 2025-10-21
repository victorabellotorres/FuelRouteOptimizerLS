package experiments;

import domain.*;
import aima_functions.*;
import IA.Gasolina.*;
import main.Constants;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import aima.search.framework.*;
import aima.search.informed.HillClimbingSearch;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FileUtils;

public class Experimento2 {

    // 🔹 Reducimos repeticiones para probar más rápido
//    public static final int ITERACIONES = 10; // Viene definida por SEEDS
    public static final int ITERACIONES_ALEATORIO = 5;
    public static final int NUM_CENTROS_DISTRIBUCION = 10;
    public static final int NUM_GASOLINERAS = 100;
    public static final int NUM_CAMIONES_POR_CENTRO = 1;
    public static final String HEURISTICA = "Basica"; // "Basica" o "Avanzada"
    public static final String[] ALGORITMOS_ESTADO_INICIAL = {
            "Sin_Assignacion", "Aleatorio", "Aleatorio_1PxV", "GreedyDistancia"
    };
    public static boolean[] OPERATORS = {true, true, true, true};
    private static final String OUTPUT_FILE = "data/experimento2.csv";

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


            System.out.println("Generando estados iniciales...");
            System.out.println("===========================================================");

            // Definir funciones
            SuccessorFunction sf = new P1SuccessorFunction();
            Search search = new HillClimbingSearch();
            HeuristicFunction hf = HEURISTICA.equals("Avanzada") ? new P1HeuristicFunction() : new P1HeuristicFunctionBasica();


            for (int e = 0; e < ALGORITMOS_ESTADO_INICIAL.length; e++) {
                P1SuccessorFunction.operatorsEnabled = OPERATORS;

                String algoritmoEstadoIni = ALGORITMOS_ESTADO_INICIAL[e];
                int iteraciones_aleatorio = Objects.equals(algoritmoEstadoIni, "Aleatorio") || Objects.equals(algoritmoEstadoIni, "Aleatorio_1PxV") ? ITERACIONES_ALEATORIO : 1;
                for (int j = 0; j < iteraciones_aleatorio; ++j) {
                    P1Board estadoInicial = null;
                    switch (algoritmoEstadoIni) {
                        case "Sin_Assignacion" -> {
                            estadoInicial = InitialBoardGenerator.SolucionAsignacionOrdenada(gasolineras, centrosDistribucion);
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
                        // Añadir más casos si se quiere
                    }
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
                        System.out.println(OPERATORS);
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

                    exportarDatos(estadoInicial, hInicial, estadoFinal,algoritmoEstadoIni, (i == 0 ? "Avanzada" : "Basica"), hFinal, SEED, i, nodosExpandidos, end - start, OPERATORS);
                }
            }
        }
    }

    private void exportarDatos(P1Board estadoInicial, double hInicial, P1Board estadoFinal, String algoritmoEstadoIni, String funcionHeuristica ,double hFinal, int seed, int iteracion, String nodosExpandidos, long timeMili, boolean[] operators) throws IOException {
        BoardMetrics metricsInicial = estadoInicial.getMetrics();
        BoardMetrics metricsFinal = estadoFinal.getMetrics();

        File file = new File(OUTPUT_FILE);
        boolean writeHeader = !file.exists() || file.length() == 0;
        String header = "iteration,seed,time_ms,algorithmEI,op1,op2,op3,op4,nodesExpanded,validInicial,validFinal,heurFunction,heurIni,heurFi,costIni,costFi,dailyProfitIni,dailyProfitFi,profitIni,profitFi,AssignedPetitionsIni,AssignedPetitionsFi,trucksUsedIni,trucksUsedFi,totalKmIni,totalKmFi\n";

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
                        metricsInicial.getBeneficioDelDia(),
                        metricsFinal.getBeneficioDelDia(),
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
