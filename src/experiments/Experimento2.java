package experiments;

import domain.*;
import aima_functions.*;
import IA.Gasolina.*;
import main.Constants;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import aima.search.framework.*;
import aima.search.informed.HillClimbingSearch;

public class Experimento2 {

    // 🔹 Reducimos repeticiones para probar más rápido
    private static final int REPETICIONES = 5;
    private static final String OUTPUT_FILE = "data/experimento2.csv";

    // ✅ Método principal para ejecutar desde terminal
    public static void main(String[] args) throws Exception {
        // 🔹 Ajustamos constantes de entorno antes de ejecutar
        Constants.NUM_GASOLINERAS = 50;
        Constants.NUM_CENTROSDISTRIBUCION = 5;
        Constants.CAMIONES_POR_CENTRO = 1;
        Constants.SEED = 1234;

        new Experimento2().run();
    }

    public void run() throws Exception {
        try (FileWriter writer = new FileWriter(OUTPUT_FILE)) {
            writer.write("iteration,seed,initial,algorithm,timeMs,initialHeur,finalHeur,assigned,nodesExpanded,profitIni,profitFi,costIni,costFi\n");

            // Inicializadores disponibles
            int[] inicializadores = {1, 2, 3, 4, 5};
            String[] nombres = {"Ordenada", "Aleatoria", "GreedyDistancia", "GreedyQuadrants", "GreedyEjes"};

            // 🔁 Bucle principal: repetimos el experimento con distintas seeds
            for (int rep = 1; rep <= REPETICIONES; rep++) {
                int seed = 1234 + rep;
                System.out.println("\n==============================");
                System.out.println("🧪 REPETICIÓN " + rep + " — Seed: " + seed);
                System.out.println("==============================");

                // 🔹 Mismo entorno para todos los inicializadores en esta repetición
                Gasolineras gas = new Gasolineras(Constants.NUM_GASOLINERAS, seed);
                CentrosDistribucion centros = new CentrosDistribucion(Constants.NUM_CENTROSDISTRIBUCION, Constants.CAMIONES_POR_CENTRO, seed);

                // 🔁 Para cada tipo de inicializador
                for (int idx = 0; idx < inicializadores.length; idx++) {
                    int initType = inicializadores[idx];
                    String nombre = nombres[idx];
                    System.out.println("\n=== 🧩 INICIALIZADOR: " + nombre + " ===");

                    // --- Generar estado inicial según el tipo ---
                    P1Board estadoInicial = switch (initType) {
                        case 2 -> InitialBoardGenerator.SolucionAsignacionAleatoria(gas, centros);
                        case 3 -> InitialBoardGenerator.SolucionAsignacionGreedyDistancia(gas, centros);
                        case 4 -> InitialBoardGenerator.SolucionAsignacionGreedyQuadrants(gas, centros);
                        case 5 -> InitialBoardGenerator.SolucionAsignacionGreedyEjes(gas, centros);
                        default -> InitialBoardGenerator.SolucionAsignacionOrdenada(gas, centros);
                    };

                    // --- Configurar Hill Climbing ---
                    P1HeuristicFunction hf = new P1HeuristicFunction();
                    SuccessorFunction sf = new P1SuccessorFunction();
                    Search search = new HillClimbingSearch();
                    Problem problem = new Problem(estadoInicial, sf, new P1GoalTest(), hf);

                    // --- Métricas iniciales ---
                    double heurIni = hf.getHeuristicValue(estadoInicial);
                    double profitIni = estadoInicial.getBeneficio();
                    double costIni = estadoInicial.getCoste();

                    // --- Ejecutar búsqueda ---
                    long start = System.currentTimeMillis();
                    SearchAgent agent = new SearchAgent(problem, search);
                    long end = System.currentTimeMillis();

                    // --- Resultados finales ---
                    P1Board estadoFinal = (P1Board) search.getGoalState();
                    double heurFi = hf.getHeuristicValue(estadoFinal);
                    double profitFi = estadoFinal.getBeneficio();
                    double costFi = estadoFinal.getCoste();
                    int nodesExpanded = Integer.parseInt(agent.getInstrumentation().getProperty("nodesExpanded"));
                    int assigned = estadoFinal.peticionesAssignadas();

                    // --- Guardar línea en CSV ---
                    writer.write(rep + "," + seed + "," + nombre + ",HillClimbing," +
                            (end - start) + "," + heurIni + "," + heurFi + "," +
                            assigned + "," + nodesExpanded + "," +
                            profitIni + "," + profitFi + "," + costIni + "," + costFi + "\n");

                    // --- Log en consola ---
                    System.out.printf("→ [%s | Seed=%d] heurIni=%.2f heurFi=%.2f Δ=%.2f profitFi=%.2f time=%.2fs%n",
                            nombre, seed, heurIni, heurFi, (heurFi - heurIni),
                            profitFi, (end - start) / 1000.0);
                }
            }

            System.out.println("\n✅ Experimento 2 completado. Resultados guardados en: " + OUTPUT_FILE);
        } catch (IOException e) {
            System.err.println("❌ Error al escribir el archivo CSV: " + e.getMessage());
        }
    }
}
