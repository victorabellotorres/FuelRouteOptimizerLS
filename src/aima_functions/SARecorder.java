// java
package aima_functions;

import domain.P1Board;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicInteger;

public class SARecorder {
    private static String FILE = "";
    private static final AtomicInteger STEP = new AtomicInteger(0);
    private static PrintWriter pw;

    static {
        try {
            if (FILE.isEmpty()) {
                pw = null;
            }
            else {
                File f = new File(FILE);
                boolean isNew = !f.exists() || f.length() == 0;
                pw = new PrintWriter(new FileWriter(f, true));
                if (isNew) {
                    pw.println("step,maxIterations,stiter,k,lambda,timestamp_ms,quality,beneficio,coste,peticionesAsignadas,camionesUsados");
                    pw.flush();
                }
            }
        } catch (IOException e) {
            pw = null;
            System.err.println("SARecorder: cannot open " + FILE + " for writing: " + e.getMessage());
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (pw != null) pw.close();
        }));
    }

    // reset step
    public static synchronized void reset() {
        STEP.set(0);
    }

    public static synchronized void  setFileAndReset(String filePath) {
        FILE = filePath;
        STEP.set(0);
        if (pw != null) {
            pw.close();
        }
        try {
            File f = new File(FILE);
            boolean isNew = !f.exists() || f.length() == 0;
            pw = new PrintWriter(new FileWriter(f, true));
            if (isNew) {
                pw.println("step,maxIterations,stiter,k,lambda,timestamp_ms,beneficio,coste,peticionesAsignadas,camionesUsados");
                pw.flush();
            }
        } catch (IOException e) {
            pw = null;
            System.err.println("SARecorder: cannot open " + FILE + " for writing: " + e.getMessage());
        }
    }

    public static synchronized void record(P1Board board, int max_iterations, int stiter, int k, double lambda) {
        if (pw == null || board == null) return;
        int step = STEP.incrementAndGet();
        long ts = System.currentTimeMillis();
        double beneficio = board.getBeneficio();
        double coste = board.getCoste();
        int petAsign = board.peticionesAssignadas();
        int camUsados = board.camionesUsados();
        pw.println(step + "," + max_iterations + "," + stiter + "," + k + "," + lambda + "," + ts + "," + beneficio + "," + coste + "," + petAsign + "," + camUsados);
        pw.flush();
    }
}
