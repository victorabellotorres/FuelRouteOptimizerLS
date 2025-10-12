package main;

public final class Constants {
    private Constants() {} // no instanciable

    public static final int MAX_VIAJES = 5;
    public static final int MAX_KM = 640;

    // Valores por defecto, se pueden cambiar en main
    public static int SEED = 1234;
    public static int NUM_CENTROSDISTRIBUCION = 10;
    public static int NUM_GASOLINERAS = 100;
    public static int ALGORITMO_ESTADO_INICIAL = 1; // 1: Por orden, 2: Aleatorio, 3: Greedy
    public static int ALGORITMO_BUSQUEDA = 1; // 1: Hill Climbing, 2: Simulated Annealing
    public static int CAMIONES_POR_CENTRO = 1;
}

