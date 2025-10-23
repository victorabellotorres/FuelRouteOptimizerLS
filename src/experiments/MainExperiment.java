package experiments;

import java.util.Scanner;

public class MainExperiment {

    private static final int[] SEEDS = new int[] {
        1234, 1, 91011, 121314, 151617,
        181920, 2134565223, 242526, 669610, 303132
    };

    public static void main(String[] args) throws Exception {

        Scanner sc = new Scanner(System.in);

        System.out.println("======================================");
        System.out.println("   🧪 Proyecto IA - Experimentos");
        System.out.println("======================================");
        System.out.println("Selecciona el experimento a ejecutar:");
        System.out.println("0*  Experimento Estados iniciales - Compara los estados iniciales sin aplicar ningún algoritmo.");
        System.out.println("1️  Experimento 1 - Operadores de sucesores y funcion heurística");
        System.out.println("2️  Experimento 2 - Estados iniciales con Hill Climbing");
        System.out.println("3 ️ Experimento 3 - Simulated Annealing parámetros");

        System.out.println("-1 Todos los experimentos");
        System.out.print("Opción: ");

        int opcion = 1;
        if (sc.hasNextInt()) {
            opcion = sc.nextInt();
        }
        long start = System.currentTimeMillis();
        switch (opcion) {
            case 0 -> {
                System.out.println("\nEjecutando Experimento 1...");
                EstadosIniciales estadosIniciales = new EstadosIniciales();
                estadosIniciales.run(SEEDS);
                System.out.println("\n✅ Experimento 1 completado.");
            }
            case 1 -> {
                System.out.println("\nEjecutando Experimento 1...");
                Experimento1 exp1 = new Experimento1();
                exp1.run(SEEDS);
                System.out.println("\n✅ Experimento 1 completado.");
            }
            case 2 -> {
                System.out.println("\nEjecutando Experimento 2...");
                Experimento2 exp2 = new Experimento2();
                exp2.run(SEEDS);
                System.out.println("\n✅ Experimento 2 completado.");
            }
            case 3 -> {
                System.out.println("\nEjecutando Experimento 3...");
                Experimento3 exp3 = new Experimento3();
                exp3.run(SEEDS);
                System.out.println("\n✅ Experimento 3 completado.");
            }
            case -1 -> {
                System.out.println("\nEjecutando Experimento 1...");
                EstadosIniciales estadosIniciales = new EstadosIniciales();
                estadosIniciales.run(SEEDS);
                System.out.println("\n✅ Experimento 1 completado.");

                System.out.println("\nEjecutando Experimento 1...");
                Experimento1 exp1 = new Experimento1();
                exp1.run(SEEDS);
                System.out.println("\n✅ Experimento 1 completado.");

                System.out.println("\nEjecutando Experimento 2...");
                Experimento2 exp2 = new Experimento2();
                exp2.run(SEEDS);
                System.out.println("\n✅ Experimento 2 completado.");

                System.out.println("\nEjecutando Experimento 3...");
                Experimento3 exp3 = new Experimento3();
                exp3.run(SEEDS);
                System.out.println("\n✅ Experimento 3 completado.");
            }
            default -> {
                System.out.println("⚠️  Opción no válida. Ejecutando Experimento 1 por defecto...");
                Experimento1 exp1 = new Experimento1();
                exp1.run(SEEDS);
                System.out.println("\n✅ Experimento 1 completado.");
            }
        }
        long end = System.currentTimeMillis();

        // Printear tiempo en minutos
        double tiempoMinutos = (end - start) / 60000.0;
        System.out.printf("⏱️  Tiempo total de ejecución: %.2f minutos.%n", tiempoMinutos);

        System.out.println("\n==============================");
        System.out.println("       Experimento finalizado");
        System.out.println("==============================");
    }
}
