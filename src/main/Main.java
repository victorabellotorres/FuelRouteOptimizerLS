package main;

import aima_functions.*;
import aima.search.framework.GraphSearch;
import aima.search.framework.Problem;
import aima.search.framework.Search;
import aima.search.framework.SearchAgent;
import aima.search.informed.HillClimbingSearch;
import aima.search.informed.SimulatedAnnealingSearch;
import aima.search.informed.AStarSearch;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import domain.*;
import IA.Gasolina.*;

public class Main {

    public static void main(String[] args) throws Exception{

        // ==============================
        // 1. Definir constantes
        // ==============================
        definirConstants();

        // ==============================
        // 2️. Crear el estado inicial
        // ==============================
        P1Board estadoInicial = crearEstadoInicial();

        // ==============================
        // 3️. Definir funciones de búsqueda
        // ==============================
        aima.search.framework.SuccessorFunction sf = new P1SuccessorFunction();
        Search search = new HillClimbingSearch();
        P1HeuristicFunction hf = new P1HeuristicFunction();

        definirFunciones(sf, search, hf);


        // =============================
        // Imprimir estadísticas del estado inicial
        // =============================
        System.out.println("\n--- Estadísticas del estado inicial ---");
        imprimirEstadisticasEstado(estadoInicial, hf);


        // ==============================
        // 4️. Crear el problema
        // ==============================
        Problem problem = new Problem(
                estadoInicial,
                sf,
                new P1GoalTest(),
                hf
        );

        // ==============================
        // 5️. Ejecutar búsqueda
        // ==============================
        long start = System.currentTimeMillis();
        SearchAgent agent = new SearchAgent(problem, search);
        long end = System.currentTimeMillis();

        // ==============================
        // 6️. Mostrar resultados
        // ==============================
        P1Board finalState = (P1Board) search.getGoalState();

        System.out.println("\n========= RESULTADOS =========");
        System.out.println("Tiempo total: " + (end - start) + " ms");

        System.out.println("Estadísticas del estado final:");
        // nodos expandidos:
        System.out.println("\n-- Instrumentación --");
        printInstrumentation(agent.getInstrumentation());
        System.out.println("===============================");
        imprimirEstadisticasEstado(finalState, hf);
        System.out.println("===============================");

        System.out.println("\n-- Acciones --");
        printActions(agent.getActions());
        System.out.println("===============================");

    }

    public static P1Board crearEstadoInicial() {
        // Crear el estado inicial
        Gasolineras gasolineras = new Gasolineras(Constants.NUM_GASOLINERAS, Constants.SEED);
        CentrosDistribucion centrosDistribucion = new CentrosDistribucion(Constants.NUM_CENTROSDISTRIBUCION, Constants.CAMIONES_POR_CENTRO, Constants.SEED);

        P1Board estadoInicial = null;
//        estadoInicial = InitialBoardGenerator.SolucionSinAsignaciones(gasolineras, centrosDistribucion);
        switch (Constants.ALGORITMO_ESTADO_INICIAL) {
            case 1 -> {
                System.out.println("Generando estado inicial SIN ASIGNACIONES...");
                estadoInicial = InitialBoardGenerator.SolucionSinAsignaciones(gasolineras, centrosDistribucion);
            }
            case 2 -> {
                System.out.println("Generando estado inicial ALEATORIO...");
                estadoInicial = InitialBoardGenerator.SolucionAsignacionAleatoria(gasolineras, centrosDistribucion);
            }
            case 3 -> {
                System.out.println("Generando estado inicial GREEDY (DISTANCIAS)...");
                estadoInicial = InitialBoardGenerator.SolucionAsignacionGreedyQuadrants(gasolineras, centrosDistribucion);
            }
            case 4 -> {
                System.out.println("Generando estado inicial GREEDY (QUADRANTS)...");
                estadoInicial = InitialBoardGenerator.SolucionAsignacionGreedyQuadrants(gasolineras, centrosDistribucion);
            }
            case 5 -> {
                System.out.println("Generando estado inicial GREEDY (EIXOS)...");
                estadoInicial = InitialBoardGenerator.SolucionAsignacionGreedyEjes(gasolineras, centrosDistribucion);
            }
            case 6 -> {
                System.out.println("Generando estado inicial ORDENADO...");
                estadoInicial = InitialBoardGenerator.SolucionAsignacionOrdenada(gasolineras, centrosDistribucion); // placeholder
            }
        }

        System.out.println("Estado inicial generado.");
        return estadoInicial;
    }

    public static void definirFunciones(aima.search.framework.SuccessorFunction sf, Search search, P1HeuristicFunction hf) {
         // Definir las funciones de búsqueda según el algoritmo seleccionado
        if (Constants.ALGORITMO_BUSQUEDA == 2) {
            System.out.println("Usando Simulated Annealing...");
            // Use SA-specific successor and conservative default parameters
            sf = new P1SuccessorFunctionSA(Constants.SA_NEIGHBORS);
            // iterations, stepsPerTemp, k, lambda
            // Increase iterations to allow up to ~20001 node expansions
            search = new SimulatedAnnealingSearch(Constants.SA_STEPS, Constants.SA_STITER, Constants.SA_K, Constants.SA_LAMBDA);
        } else {
            System.out.println("Usando Hill Climbing...");
            sf = new P1SuccessorFunction(); // genera todos los sucesores
            search = new HillClimbingSearch();
        }

        hf = new P1HeuristicFunction();
    }

    public static void imprimirEstadisticasEstado(P1Board estado, P1HeuristicFunction hf) {
        double initialHeur = hf.getHeuristicValue(estado);

        StringBuilder errorMsg = new StringBuilder();
        boolean esSolucion = estado.esSolucion(errorMsg);
        if (esSolucion) {
            System.out.println("El estado es una solución válida.");
        } else {
            System.out.println("El estado NO es una solución válida. Error: " + errorMsg.toString());
        }

        System.out.println("Valor heurístico: " + initialHeur);
        System.out.println("Beneficio total: " + (estado.getBeneficio()-estado.getCoste()));
        System.out.println("Peticiones asignadas: " + estado.peticionesAssignadas() + "/" + estado.getPeticiones().length);
        System.out.println("Camiones usados: " + estado.camionesUsados() + "/" + estado.getCamiones().length);

        System.out.println(estado);
    }


    public static void definirConstants() {
        Scanner sc = new Scanner(System.in);

        System.out.print("Valores dados por el enunciado (cambiar únicamente en el código:");
        System.out.print("1. Máximo de viajes por camión: " + Constants.MAX_VIAJES);
        System.out.print("2. Máximo de kilómetros por camión: " + Constants.MAX_KM);

            System.out.print("Selecciona el Estado Inicial [1: Sin assignación (default), 2: Aleatorio, 3: Greedy distancias, 4: Greedy quadrants, 5: Greedy X axis, 6: Por orden básico]: ");
        int option = 1;
        if (sc.hasNextInt()) {
            option = sc.nextInt();
            if (option < 1 || option > 5) {
                System.out.println("Opción no válida. Usando valor por defecto (1: Sin assignacion).");
                option = 1;
            }
        } else {
            // keep default
        }
        Constants.ALGORITMO_ESTADO_INICIAL = option;

        System.out.print("Selecciona el Algoritmo de Busqueda [1: Hill Climbing(default), 2: Simulated Annealing]: ");
        option = 1;
        if (sc.hasNextInt()) {
            option = sc.nextInt();
            if (option < 1 || option > 2) {
                System.out.println("Opción no válida. Usando valor por defecto (1: Hill Climbing).");
                option = 1;
            }
        }
        Constants.ALGORITMO_BUSQUEDA = option;

        System.out.print("Selecciona la Seed: [R = random, Default: 1234]: ");
        String input = "";
        if (sc.hasNext()) input = sc.next();
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

        System.out.print("Selecciona el número de centros de distribución [Default = 10]: ");
        int num = Constants.NUM_CENTROSDISTRIBUCION;
        if (sc.hasNextInt()) {
            num = sc.nextInt();
            if (num < 1) {
                System.out.println("Opción no válida. Usando valor por defecto (10).");
                num = Constants.NUM_CENTROSDISTRIBUCION;
            }
        }
        Constants.NUM_CENTROSDISTRIBUCION = num;

        System.out.print("Selecciona el número de gasolineras [Default = 100]: ");
        num = Constants.NUM_GASOLINERAS;
        if (sc.hasNextInt()) {
            num = sc.nextInt();
            if (num < 1) {
                System.out.println("Opción no válida. Usando valor por defecto (100).");
                num = Constants.NUM_GASOLINERAS;
            }
        }
        Constants.NUM_GASOLINERAS = num;

        System.out.print("Selecciona el número de camiones por centro [Default = 1]: ");
        num = Constants.CAMIONES_POR_CENTRO;
        if (sc.hasNextInt()) {
            num = sc.nextInt();
            if (num < 1) {
                System.out.println("Opción no válida. Usando valor por defecto (1).");
                num = Constants.CAMIONES_POR_CENTRO;
            }
        }
        Constants.CAMIONES_POR_CENTRO = num;
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