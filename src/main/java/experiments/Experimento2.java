package experiments;

import domain.*;
import aima_functions.*;
import IA.Gasolina.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import aima.search.framework.*;
import aima.search.informed.HillClimbingSearch;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FileUtils;

public class Experimento2 {

    // 🔹 Paràmetres globals
    public static final int ITERACIONES_ALEATORIO = 2;
    public static final int NUM_CENTROS_DISTRIBUCION = 10;
    public static final int NUM_GASOLINERAS = 100;
    public static final int NUM_CAMIONES_POR_CENTRO = 1;
    public static final String HEURISTICA = "Avanzada"; // "Basica" o "Avanzada"

    // 🔹 Definim els mètodes per generar estats inicials
    public static final String[] ALGORITMOS_ESTADO_INICIAL = {
            "Sin_Assignacion",
            "Aleatorio",
            "GreedyCuadrantes",
            "Aleatorio_1PxV",
            "GreedyDistancia"
    };

    // 🔹 Operadors activats (canviar si cal provar combinacions)
    public static boolean[] OPERATORS = {true, true, true, false};

    private static final String OUTPUT_FILE = "data/experimento2.csv";


    public void run(int[] SEEDS) throws Exception {
        File file = new File(OUTPUT_FILE);
        if (file.exists() && file.length() != 0) {
            FileUtils.writeStringToFile(file, "", StandardCharsets.UTF_8);
        }

        for (int i = 0; i < SEEDS.length; i++) {
            int SEED = SEEDS[i];
            System.out.println("===========================================================");
            System.out.println("Iteració " + i + "/" + (SEEDS.length - 1));
            System.out.println("Seed: " + SEED);
            System.out.println("===========================================================");

            // 🔸 Generem les dades d’entrada
            Gasolineras gasolineras = new Gasolineras(NUM_GASOLINERAS, SEED);
            CentrosDistribucion centrosDistribucion = new CentrosDistribucion(NUM_CENTROS_DISTRIBUCION, NUM_CAMIONES_POR_CENTRO, SEED);

            // 🔸 Configurem funcions del problema
            SuccessorFunction sf = new P1SuccessorFunction();
            Search search = new HillClimbingSearch();
            HeuristicFunction hf = HEURISTICA.equals("Avanzada")
                    ? new P1HeuristicFunction()
                    : new P1HeuristicFunctionBasica();

            // 🔸 Recorrem els tipus d’inicialitzadors
            for (String algoritmoEstadoIni : ALGORITMOS_ESTADO_INICIAL) {

                P1SuccessorFunction.operatorsEnabled = OPERATORS;
                int iteraciones_aleatorio =
                        (Objects.equals(algoritmoEstadoIni, "Aleatorio") ||
                                Objects.equals(algoritmoEstadoIni, "Aleatorio_1PxV"))
                                ? ITERACIONES_ALEATORIO : 1;

                for (int j = 0; j < iteraciones_aleatorio; ++j) {
                    System.out.println("Generant estat inicial amb: " + algoritmoEstadoIni);

                    P1Board estadoInicial = switch (algoritmoEstadoIni) {
                        case "Sin_Assignacion" -> InitialBoardGenerator.SolucionSinAsignaciones(gasolineras, centrosDistribucion);
                        case "Aleatorio" -> InitialBoardGenerator.SolucionAsignacionAleatoria(gasolineras, centrosDistribucion);
                        case "Aleatorio_1PxV" -> InitialBoardGenerator.SolucionAsignacionAleatoria1PeticionPorViaje(gasolineras, centrosDistribucion);
                        case "GreedyCuadrantes" -> InitialBoardGenerator.SolucionAsignacionGreedyQuadrants(gasolineras, centrosDistribucion);
                        case "GreedyDistancia" -> InitialBoardGenerator.SolucionAsignacionGreedyDistancia(gasolineras, centrosDistribucion);
                        default -> null;
                    };

                    if (estadoInicial == null) {
                        throw new IllegalStateException("❌ No s'ha pogut generar estat inicial per a: " + algoritmoEstadoIni);
                    }

                    Problem problem = new Problem(estadoInicial, sf, new P1GoalTest(), hf);

                    // 🔹 Execució del Hill Climbing
                    long start = System.currentTimeMillis();
                    SearchAgent agent = new SearchAgent(problem, search);
                    long end = System.currentTimeMillis();

                    P1Board estadoFinal = (P1Board) search.getGoalState();

                    // 🔹 Validació del resultat
                    StringBuilder errMsg = new StringBuilder();
                    if (!estadoFinal.esSolucion(errMsg)) {
                        System.out.println("⚠️ Estat final no vàlid: " + errMsg);
                        System.out.println("===========================================================");
                        System.out.println("Estat inicial:");
                        if (!estadoInicial.esSolucion(errMsg))
                            System.out.println("Inicial tampoc vàlid: " + errMsg);
                        System.out.println(estadoInicial);
                        System.out.println("-----------------------------------------------------------");
                        System.out.println("Estat final:");
                        System.out.println(estadoFinal);
                        System.out.println("-----------------------------------------------------------");

                        System.out.println("Operadors actius: " + java.util.Arrays.toString(OPERATORS));

                        List<?> actions = agent.getActions();
                        for (int a = 0; a < actions.size(); a++) {
                            String action = (String) actions.get(a);
                            System.out.println("→ " + action);
                        }
                        System.out.println("===========================================================");
                    }

                    String nodosExpandidos = agent.getInstrumentation().getProperty("nodesExpanded");

                    double hInicial = hf.getHeuristicValue(estadoInicial);
                    double hFinal = hf.getHeuristicValue(estadoFinal);

                    exportarDatos(
                            estadoInicial, hInicial,
                            estadoFinal, algoritmoEstadoIni,
                            "HillClimbing", HEURISTICA,
                            hFinal, SEED, i,
                            nodosExpandidos, end - start, OPERATORS
                    );
                }
            }
        }
    }

    // 🔸 Exportació de resultats a CSV
    private void exportarDatos(P1Board estadoInicial, double hInicial, P1Board estadoFinal,
                               String algoritmoEstadoIni, String algorithmSearch, String funcionHeuristica,
                               double hFinal, int seed, int iteracion, String nodosExpandidos,
                               long timeMili, boolean[] operators) throws IOException {

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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
