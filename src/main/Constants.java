package main;

public final class Constants {
    private Constants() {} // no instanciable

    public static final int MAX_VIAJES = 5;
    public static final int MAX_KM = 640;

    // Valores por defecto, se pueden cambiar en main
    public static int SEED = 123456;
    public static int NUM_CENTROSDISTRIBUCION = 10;
    public static int NUM_GASOLINERAS = 100;
    public static int ALGORITMO_ESTADO_INICIAL = 1; // 1: Aleatorio, 2: Secuencial, 3: Greedy
    public static int CAMIONES_POR_CENTRO = 1;
}

