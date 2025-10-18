package experiments;

//import aima_functions.*;
//import aima.search.framework.GraphSearch;
//import aima.search.framework.Problem;
//import aima.search.framework.Search;
//import aima.search.framework.SearchAgent;
//import aima.search.informed.HillClimbingSearch;
//import aima.search.informed.SimulatedAnnealingSearch;
//import aima.search.informed.AStarSearch;
//
//import java.util.Iterator;
//import java.util.List;
//import java.util.Properties;
//import java.util.Scanner;
//
//import domain.*;
//import IA.Gasolina.*;

public class MainExperiment {

    public static void main(String[] args) throws Exception {
        System.out.println("Ejecutando experimento sobre los estados iniciales (comprueba las métricas iniciales que produce cada algoritmo de inicialización).");
        EstadosIniciales estadosIniciales = new EstadosIniciales();
        estadosIniciales.run();
        System.out.println("Experimento finalizado.\n ===========================================================");
    }
}
