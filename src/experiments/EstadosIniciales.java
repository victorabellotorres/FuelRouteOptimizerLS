package experiments;

import domain.*;
import IA.Gasolina.*;

import org.apache.commons.csv.*;
import org.apache.commons.io.*;
import org.apache.commons.codec.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import java.util.Random;

public class EstadosIniciales {

        public static int ITERACIONES = 10;
        public static int NUM_CENTROS_DISTRIBUCION = 10;
        public static int NUM_GASOLINERAS = 100;
        public static int NUM_CAMIONES_POR_CENTRO = 1;

        public void run() throws IOException {
                File file = new File("data/estados_iniciales.csv");
                if (file.exists() && file.length() != 0) {
                        // Eliminamos el contenido del archivo
                        FileUtils.writeStringToFile(file, "", StandardCharsets.UTF_8);
                }

                Random r = new Random();
                for (int i = 0; i < ITERACIONES; i++) {
                        int SEED = r.nextInt(1000000);
                        System.out.println("===========================================================");
                        System.out.println("Iteración " + i + "/" + (ITERACIONES - 1));

                        System.out.println("Generando gasolineras y centros de distribución...");
                        Gasolineras gasolineras = new Gasolineras(NUM_GASOLINERAS, SEED);
                        CentrosDistribucion centrosDistribucion = new CentrosDistribucion(NUM_CENTROS_DISTRIBUCION, NUM_CAMIONES_POR_CENTRO, SEED);


                        System.out.println("Generando estados iniciales...");
                        System.out.println("===========================================================");

                        System.out.println("Generando estado inicial ORDENADO...");
                        P1Board estadoInicial = InitialBoardGenerator.SolucionAsignacionOrdenada(gasolineras, centrosDistribucion);
                        exportarDatos(estadoInicial, "ORDENADO", SEED, i);


                        System.out.println("Generando estado inicial ALEATORIO...");
                        estadoInicial = InitialBoardGenerator.SolucionAsignacionAleatoria(gasolineras, centrosDistribucion);
                        exportarDatos(estadoInicial, "ALEATORIO", SEED, i);

                        System.out.println("Generando estado inicial GREEDY DISTANCIAS...");
                        estadoInicial = InitialBoardGenerator.SolucionAsignacionGreedyDistancia(gasolineras, centrosDistribucion);
                        exportarDatos(estadoInicial, "GREEDY_DISTANCIAS", SEED, i);

                        System.out.println("Generando estado inicial GREEDY_CUADRANTES...");
                        estadoInicial = InitialBoardGenerator.SolucionAsignacionGreedyQuadrants(gasolineras, centrosDistribucion);
                        exportarDatos(estadoInicial, "GREEDY CUADRANTES", SEED, i);

                        System.out.println("Generando estado inicial GREEDY EJE X...");
                        estadoInicial = InitialBoardGenerator.SolucionAsignacionGreedyEjes(gasolineras, centrosDistribucion);
                        exportarDatos(estadoInicial, "GREEDY_EJE_X", SEED, i);

                }
        }

        private void exportarDatos(P1Board estadoInicial, String tipoEstadoInicial, int seed, int iteracion) throws IOException {
                BoardMetrics metrics = estadoInicial.getMetrics();

                File file = new File("data/estados_iniciales.csv");
                boolean writeHeader = !file.exists() || file.length() == 0;
                String header = "algoritmo,seed,iteration,valid,peticionesAsignadas,peticionesTotales,camionesUsados,camionesTotales,totalKm,kmMedioCamion,beneficio,coste,calidad,errorMessage\n";

                try (Writer out = new OutputStreamWriter(new FileOutputStream(file, true), StandardCharsets.UTF_8)) {
                        if (writeHeader) {
                                out.write(header);
                        }
                        try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT)) {
                                printer.printRecord(
                                        tipoEstadoInicial,
                                        seed,
                                        iteracion,
                                        metrics.isValid(),
                                        metrics.getPeticionesAssignadas(),
                                        metrics.getPeticionesTotales(),
                                        metrics.getCamionesUsados(),
                                        metrics.getCamionesTotales(),
                                        metrics.getTotalKm(),
                                        metrics.getKmMedioCamion(),
                                        metrics.getBeneficio(),
                                        metrics.getCoste(),
                                        metrics.getCalidad(),
                                        metrics.getErrorMessage()
                                );
                                printer.flush();
                        }
                        catch (Exception e) {
                                throw new RuntimeException(e);
                        }
                } catch (IOException e) {
                        throw new RuntimeException(e);
                }
        }
}
