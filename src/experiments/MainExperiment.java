package experiments;

import java.util.Scanner;

public class MainExperiment {

    public static void main(String[] args) throws Exception {

        Scanner sc = new Scanner(System.in);

        System.out.println("======================================");
        System.out.println("   🧪 Proyecto IA - Experimentos");
        System.out.println("======================================");
        System.out.println("Selecciona el experimento a ejecutar:");
        System.out.println("1️⃣  Experimento 1 - Operadores de sucesores");
        System.out.println("2️⃣  Experimento 2 - Inicializadores (Hill Climbing)");
        System.out.print("Opción [1/2]: ");

        int opcion = 1;
        if (sc.hasNextInt()) {
            opcion = sc.nextInt();
        }

        switch (opcion) {
            case 1 -> {
                System.out.println("\nEjecutando Experimento 1...");
                Experimento1 exp1 = new Experimento1();
                exp1.run();
                System.out.println("\n✅ Experimento 1 completado.");
            }
            case 2 -> {
                System.out.println("\nEjecutando Experimento 2...");
                Experimento2 exp2 = new Experimento2();
                exp2.run();
                System.out.println("\n✅ Experimento 2 completado.");
            }
            default -> {
                System.out.println("⚠️  Opción no válida. Ejecutando Experimento 1 por defecto...");
                Experimento1 exp1 = new Experimento1();
                exp1.run();
                System.out.println("\n✅ Experimento 1 completado.");
            }
        }

        System.out.println("\n==============================");
        System.out.println("       Experimento finalizado");
        System.out.println("==============================");
    }

    public static void experimentoEstadosIniciales() throws Exception {
        System.out.println("Ejecutando experimento sobre los estados iniciales (comprueba las métricas iniciales que produce cada algoritmo de inicialización).");
        EstadosIniciales estadosIniciales = new EstadosIniciales();
        estadosIniciales.run();
        System.out.println("Experimento finalizado.\n===========================================================");
    }
}
