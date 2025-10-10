package main;

import aima.search.framework.*;
import aima.search.informed.*;

import domain.*;
import aima_functions.*;
import IA.Gasolina.*;

import java.util.*;

public class Main {

    public static void main(String[] args) throws Exception {

        // ==============================
        // 1️⃣ Configuración inicial
        // ==============================
        defineConstants();
        Random random = new Random(Constants.SEED);

        // Generar entorno del problema
        Gasolineras gasolineras = new Gasolineras(Constants.NUM_GASOLINERAS, Constants.SEED);
        CentrosDistribucion centros = new CentrosDistribucion(Constants.NUM_CENTROSDISTRIBUCION, Constants.CAMIONES_POR_CENTRO, Constants.SEED);
        // 🔹 Esto conecta el entorno con el State
        State.setEntorno(gasolineras, centros);

        // ==============================
        // 2️⃣ Crear el estado inicial
        // ==============================
        State estadoInicial;
        switch (Constants.ALGORITMO_ESTADO_INICIAL) {
            case 3 -> {
                System.out.println("Generando estado inicial GREEDY...");
                estadoInicial = InitialStateGenerator.greedySolution(gasolineras, centros);
            }
            case 2 -> {
                System.out.println("Generando estado inicial ORDENADO...");
                estadoInicial = InitialStateGenerator.randomSolution(gasolineras, centros); // placeholder
            }
            default -> {
                System.out.println("Generando estado inicial ALEATORIO...");
                estadoInicial = InitialStateGenerator.randomSolution(gasolineras, centros);
            }
        }

        System.out.println("Estado inicial generado.");
        System.out.println(estadoInicial);

        // ==============================
        // 3️⃣ Elegir algoritmo de búsqueda
        // ==============================
        Scanner sc = new Scanner(System.in);
        System.out.println("\nSelecciona algoritmo [1: Hill Climbing, 2: Simulated Annealing]: ");
        int alg = sc.nextInt();

        SuccessorFunction sf;
        Search search;
        HeuristicFunction hf = new P1HeuristicFunction();

        if (alg == 2) {
            System.out.println("Usando Simulated Annealing...");
            sf = new P1SuccessorFunctionSA(); // versión aleatoria
            // parámetros SA: iteraciones totales, pasos por temp, k, lambda
            search = new SimulatedAnnealingSearch(20000, 100, 5, 0.001);
        } else {
            System.out.println("Usando Hill Climbing...");
            sf = new P1SuccessorFunction(); // genera todos los sucesores
            search = new HillClimbingSearch();
        }

        // ==============================
        // 4️⃣ Crear el problema
        // ==============================
        Problem problem = new Problem(
                estadoInicial,
                sf,
                new P1GoalTest(),
                hf
        );

        // ==============================
        // 5️⃣ Ejecutar búsqueda
        // ==============================
        long start = System.currentTimeMillis();
        SearchAgent agent = new SearchAgent(problem, search);
        long end = System.currentTimeMillis();

        // ==============================
        // 6️⃣ Mostrar resultados
        // ==============================
        State finalState = (State) search.getGoalState();

        System.out.println("\n========= RESULTADOS =========");
        System.out.println("Tiempo total: " + (end - start) + " ms");
        System.out.println("Valor heurístico final: " + hf.getHeuristicValue(finalState));
        System.out.println(finalState);

        System.out.println("\n-- Instrumentación --");
        printInstrumentation(agent.getInstrumentation());
        System.out.println("===============================");
    }

    // ==============================
    // Métodos auxiliares
    // ==============================

    public static void defineConstants() {
        Scanner sc = new Scanner(System.in);

        System.out.println("==== CONFIGURACIÓN ====");
        System.out.print("Seed [default=123456]: ");
        String seedInput = sc.nextLine();
        if (!seedInput.isEmpty()) {
            try {
                Constants.SEED = Integer.parseInt(seedInput);
            } catch (NumberFormatException e) {
                Constants.SEED = 123456;
            }
        }

        System.out.print("Número de centros de distribución [default=10]: ");
        String centrosIn = sc.nextLine();
        Constants.NUM_CENTROSDISTRIBUCION = centrosIn.isEmpty() ? 10 : Integer.parseInt(centrosIn);

        System.out.print("Número de gasolineras [default=100]: ");
        String gasIn = sc.nextLine();
        Constants.NUM_GASOLINERAS = gasIn.isEmpty() ? 100 : Integer.parseInt(gasIn);

        System.out.print("Camiones por centro [default=1]: ");
        String camIn = sc.nextLine();
        Constants.CAMIONES_POR_CENTRO = camIn.isEmpty() ? 1 : Integer.parseInt(camIn);

        System.out.print("Tipo de estado inicial [1: Aleatorio, 3: Greedy]: ");
        String tipoIn = sc.nextLine();
        Constants.ALGORITMO_ESTADO_INICIAL = tipoIn.isEmpty() ? 1 : Integer.parseInt(tipoIn);
    }

    private static void printInstrumentation(Properties props) {
        for (Object key : props.keySet()) {
            String k = (String) key;
            System.out.println(k + " : " + props.getProperty(k));
        }
    }
}
