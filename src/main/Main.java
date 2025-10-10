package main;

import aima_functions.*;
import aima.search.framework.GraphSearch;
import aima.search.framework.Problem;
import aima.search.framework.Search;
import aima.search.framework.SearchAgent;
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

        //State estadoInicial = buscarEstadoInicial();
        P1Board estado = new P1Board(100, 100);

        // Create the Problem object
        Problem p = new  Problem(estado,
                new P1SuccesorFunctionHC(),
                new P1GoalTest(),
                new P1HeuristicFunction());

        // Instantiate the search algorithm
        // AStarSearch(new GraphSearch()) or IterativeDeepeningAStarSearch()
        Search alg = new AStarSearch(new GraphSearch());

        // Instantiate the SearchAgent object
        SearchAgent agent = new SearchAgent(p, alg);

        // We print the results of the search
        System.out.println();
        printActions(agent.getActions());
        printInstrumentation(agent.getInstrumentation());

        // You can access also to the goal state using the
        // method getGoalState of class Search

    }

    public static void defineConstants() {
        Scanner sc = new Scanner(System.in);

        System.out.print("Valores dados por el enunciado (cambiar únicamente en el código:");
        System.out.print("1. Máximo de viajes por camión: " + Constants.MAX_VIAJES);
        System.out.print("2. Máximo de kilómetros por camión: " + Constants.MAX_KM);

        System.out.print("Selecciona el Estado Inicial [1: Aleatorio(default), 2: Por orden, 3: Greedy]");
        int option = sc.nextInt();
        if (option < 1 || option > 3) {
            System.out.println("Opción no válida. Usando valor por defecto (1: Aleatorio).");
        } else {
            Constants.ALGORITMO_ESTADO_INICIAL = option;
        }


       System.out.print("Selecciona la Seed: [Default: 123456]: ");
       sc.nextLine(); // Consumir el salto de línea pendiente
       String input = sc.nextLine();
       if (!input.isEmpty()) {
           try {
               Constants.SEED = Integer.parseInt(input);
           } catch (NumberFormatException e) {
               System.out.println("Entrada no válida. Usando valor por defecto (123456).");
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