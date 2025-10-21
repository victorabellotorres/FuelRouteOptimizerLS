package main;

public final class Constants {
    private Constants() {} // no instanciable

    public static final int MAX_VIAJES = 5;
    public static final int MAX_KM = 640;

    public static final int COSTE_KM = 2;
    public static final int VALOR_DEPOSITO = 1000;

    // Valores por defecto, se pueden cambiar en main
    public static int SEED = 1234;
    public static int NUM_CENTROSDISTRIBUCION = 10;
    public static int NUM_GASOLINERAS = 100;
    public static int ALGORITMO_ESTADO_INICIAL = 1; // 1: Sin Assignacion, 2: Aleatorio, 3: Greedy distancias, 4: Greedy quadrants, 5: Greedy X axis, 6: Por orden
    public static int ALGORITMO_BUSQUEDA = 1; // 1: Hill Climbing, 2: Simulated Annealing
    public static int CAMIONES_POR_CENTRO = 1;

    // Simulated annealing parameters
    public static int SA_NEIGHBORS = 4;
    public static int SA_STEPS = 10000;
    public static int SA_STITER = 1;
    public static int SA_K = 1;
    public static double SA_LAMBDA = 0.001;
}

