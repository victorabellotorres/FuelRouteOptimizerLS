//package experiments;
//
//import aima.search.framework.Search;
//import aima.search.informed.HillClimbingSearch;
//import aima_functions.P1HeuristicFunction;
//import aima_functions.P1SuccessorFunction;
//import domain.*;
//import IA.Gasolina.*;
//import aima.*;
//
//import org.apache.commons.csv.*;
//import org.apache.commons.io.*;
//import org.apache.commons.codec.*;
//import java.io.*;
//import java.nio.charset.StandardCharsets;
//import java.util.*;
//
//import java.util.Random;
//
//public class Experimento2 {
//
//    public static int ITERACIONES = 10;
//    public static int ITERACIONES_ALEATORIO = 5;
//    public static int NUM_CENTROS_DISTRIBUCION = 10;
//    public static int NUM_GASOLINERAS = 100;
//    public static int NUM_CAMIONES_POR_CENTRO = 1;
//    public static String[] ALGORITMOS_ESTADO_INICIAL = {
//            "ORDENADO",
//            "ALEATORIO",
//            "GREEDY_DISTANCIAS",
//            "GREEDY_CUADRANTES",
//            "GREEDY_EJE_X"
//    };
//
//    public void run() throws IOException {
//        File file = new File("data/experimento1.csv");
//        if (file.exists() && file.length() != 0) {
//            // Eliminamos el contenido del archivo
//            FileUtils.writeStringToFile(file, "", StandardCharsets.UTF_8);
//        }
//
//        Random r = new Random();
//        for (int i = 0; i < ITERACIONES; i++) {
//            int SEED = r.nextInt(1000000);
//            System.out.println("===========================================================");
//            System.out.println("Iteración " + i + "/" + (ITERACIONES - 1));
//
//            System.out.println("Generando gasolineras y centros de distribución...");
//            Gasolineras gasolineras = new Gasolineras(NUM_GASOLINERAS, SEED);
//            CentrosDistribucion centrosDistribucion = new CentrosDistribucion(NUM_CENTROS_DISTRIBUCION, NUM_CAMIONES_POR_CENTRO, SEED);
//
//
//            System.out.println("Generando estados iniciales...");
//            System.out.println("===========================================================");
//
//            P1Board[] estadosIniciales = new P1Board[ALGORITMOS_ESTADO_INICIAL.length];
//            estadosIniciales[0] = InitialBoardGenerator.SolucionAsignacionOrdenada(gasolineras, centrosDistribucion);
//            estadosIniciales[1] = InitialBoardGenerator.SolucionAsignacionAleatoria(gasolineras, centrosDistribucion);
//            estadosIniciales[2] = InitialBoardGenerator.SolucionAsignacionGreedyDistancia(gasolineras, centrosDistribucion);
//            estadosIniciales[3] = InitialBoardGenerator.SolucionAsignacionGreedyQuadrants(gasolineras, centrosDistribucion);
//            estadosIniciales[4] = InitialBoardGenerator.SolucionAsignacionGreedyEjes(gasolineras, centrosDistribucion);
//
//            for (int j = 0; j < ALGORITMOS_ESTADO_INICIAL.length; j++) {
//                String tipoEstadoInicial = ALGORITMOS_ESTADO_INICIAL[j];
//                P1Board estadoInicial = estadosIniciales[j];
//
//                int iteracionesActuales = tipoEstadoInicial.equals("ALEATORIO") ? ITERACIONES_ALEATORIO : 1;
//                for (int k = 0; k < iteracionesActuales; k++) {
//                    aima.search.framework.SuccessorFunction sf = new P1SuccessorFunction();
//                    Search search = new HillClimbingSearch();
//                    P1HeuristicFunction hf = new P1HeuristicFunction();
//                }
//                exportarDatos(estadoInicial, tipoEstadoInicial, SEED, i);
//            }
//        }
//
//        private void exportarDatos(P1Board estadoInicial, String tipoEstadoInicial, int seed, int iteracion) throws IOException {
//            BoardMetrics metrics = estadoInicial.getMetrics();
//
//            File file = new File("data/estados_iniciales.csv");
//            boolean writeHeader = !file.exists() || file.length() == 0;
//            String header = "algoritmo,seed,iteration,valid,peticionesAsignadas,peticionesTotales,camionesUsados,camionesTotales,totalKm,kmMedioCamion,beneficio,coste,calidad,errorMessage\n";
//
//            try (Writer out = new OutputStreamWriter(new FileOutputStream(file, true), StandardCharsets.UTF_8)) {
//                if (writeHeader) {
//                    out.write(header);
//                }
//                try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT)) {
//                    printer.printRecord(
//                            tipoEstadoInicial,
//                            seed,
//                            iteracion,
//                            metrics.isValid(),
//                            metrics.getPeticionesAssignadas(),
//                            metrics.getPeticionesTotales(),
//                            metrics.getCamionesUsados(),
//                            metrics.getCamionesTotales(),
//                            metrics.getTotalKm(),
//                            metrics.getKmMedioCamion(),
//                            metrics.getBeneficio(),
//                            metrics.getCoste(),
//                            metrics.getCalidad(),
//                            metrics.getErrorMessage()
//                    );
//                    printer.flush();
//                }
//                catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
//    }
