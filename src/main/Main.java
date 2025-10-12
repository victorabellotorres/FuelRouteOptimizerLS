package main;

import aima_functions.*;
import aima.search.framework.GraphSearch;
import aima.search.framework.Problem;
import aima.search.framework.Search;
import aima.search.framework.SearchAgent;
import aima.search.informed.HillClimbingSearch;
import aima.search.informed.AStarSearch;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import domain.*;
import IA.Gasolina.*;

public class Main {

    public static void main(String[] args) throws Exception{

        // Definir las constantes del problema
        defineConstants();

        // Crear el estado inicial
        Gasolineras gasolineras = new Gasolineras(Constants.NUM_GASOLINERAS, Constants.SEED);
        CentrosDistribucion centrosDistribucion = new CentrosDistribucion(Constants.NUM_CENTROSDISTRIBUCION, Constants.CAMIONES_POR_CENTRO, Constants.SEED);

        // ==============================
        // 2️⃣ Crear el estado inicial
        // ==============================
        P1Board estadoInicial = null;
        switch (Constants.ALGORITMO_ESTADO_INICIAL) {
            case 3 -> {
                System.out.println("Generando estado inicial GREEDY...");
                //estadoInicial = InitialBoardGenerator.greedySolution(gasolineras, centros);
            }
            case 2 -> {
                System.out.println("No implementado aun");
                //estadoInicial = InitialBoardGenerator.randomSolution(gasolineras, centros);
            }
            default -> {
                System.out.println("Generando estado inicial ORDENADO...");
                estadoInicial = InitialBoardGenerator.SolucionAsignacionOrdenada(gasolineras, centrosDistribucion); // placeholder
            }
        }

        System.out.println("Estado inicial generado.");
        System.out.println(estadoInicial);


        P1SuccessorFunction sf = null;
        Search search = new HillClimbingSearch();
        P1HeuristicFunction hf = new P1HeuristicFunction();

        if (Constants.ALGORITMO_BUSQUEDA == 2) {
            System.out.println("Usando Simulated Annealing...");
            System.out.println("No implementado aun");

//            sf = new P1SuccessorFunctionSA(); // versión aleatoria
//            // parámetros SA: iteraciones totales, pasos por temp, k, lambda
//            search = new SimulatedAnnealingSearch(20000, 100, 5, 0.001);
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
        P1Board finalState = (P1Board) search.getGoalState();

        System.out.println("\n========= RESULTADOS =========");
        System.out.println("Tiempo total: " + (end - start) + " ms");
        System.out.println("Valor heurístico final: " + hf.getHeuristicValue(finalState));
        System.out.println(finalState);

        System.out.println("\n-- Instrumentación --");
        printInstrumentation(agent.getInstrumentation());
        System.out.println("===============================");

    }

    public static void defineConstants() {
        Scanner sc = new Scanner(System.in);

        System.out.print("Valores dados por el enunciado (cambiar únicamente en el código:");
        System.out.print("1. Máximo de viajes por camión: " + Constants.MAX_VIAJES);
        System.out.print("2. Máximo de kilómetros por camión: " + Constants.MAX_KM);

        System.out.print("Selecciona el Estado Inicial [1: Por orden(default), 2: Aleatorio, 3: Greedy]");
        int option = sc.nextInt();
        if (option < 1 || option > 3) {
            System.out.println("Opción no válida. Usando valor por defecto (1: Aleatorio).");
        } else {
            Constants.ALGORITMO_ESTADO_INICIAL = option;
        }

        System.out.print("Selecciona el Algoritmo de Busqueda [1: Hill Climbing(default), 2: Simulated Annealing]");
        option = sc.nextInt();
        if (option < 1 || option > 2) {
            System.out.println("Opción no válida. Usando valor por defecto (1: Hill Climbing).");
        } else {
            Constants.ALGORITMO_ESTADO_INICIAL = option;
        }

       System.out.print("Selecciona la Seed: [R = random, Default: 1234]: ");
       sc.nextLine(); // Consumir el salto de línea pendiente
       String input = sc.nextLine();
       if (!input.isEmpty()) {
           try {
               if (input.equalsIgnoreCase("R")) {
                   Constants.SEED = (int) System.currentTimeMillis();
               } else {
                   Constants.SEED = Integer.parseInt(input);
               }
           } catch (NumberFormatException e) {
               System.out.println("Entrada no válida. Usando valor por defecto (1234).");
           }
       }

        System.out.print("Selecciona el número de centros de distribución [Default = 10]:");
        int num = sc.nextInt();
        if (num < 1) {
            System.out.println("Opción no válida. Usando valor por defecto (10).");
        } else {
            Constants.NUM_CENTROSDISTRIBUCION = num;
        }

        System.out.print("Selecciona el número de gasolineras [Default = 100]:");
        num = sc.nextInt();
        if (num < 1) {
            System.out.println("Opción no válida. Usando valor por defecto (10).");
        } else {
            Constants.NUM_GASOLINERAS = num;
        }

        System.out.print("Selecciona el número de camiones por centro [Default = 1]:");
        num = sc.nextInt();
        if (num < 1) {
            System.out.println("Opción no válida. Usando valor por defecto (10).");
        } else {
            Constants.CAMIONES_POR_CENTRO = num;
        }
    }

    private static void printInstrumentation(Properties properties) {
        Iterator keys = properties.keySet().iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            String property = properties.getProperty(key);
            System.out.println(key + " : " + property);
        }

    }

    private static void printActions(List actions) {
        for (int i = 0; i < actions.size(); i++) {
            String action = (String) actions.get(i);
            System.out.println(action);
        }
    }

}