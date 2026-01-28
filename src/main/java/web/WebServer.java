package web;

import io.javalin.Javalin;
import com.google.gson.Gson;
import web.dto.SimulationRequest;
import web.dto.SimulationResponse;
import java.util.Collections;

public class WebServer {

    private static final Gson gson = new Gson();

    public static void main(String[] args) {
        
        Javalin app = Javalin.create(config -> {
            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(it -> {
                    it.anyHost(); // Allow requests from Vercel
                });
            });
        })
        .start(8080); // Render expects 8080 by default usually, but we can config env var

        app.get("/", ctx -> ctx.result("Gasolines AI Server is Running!"));

        app.post("/api/solve", ctx -> {
            try {
                // Parse Body
                String body = ctx.body();
                SimulationRequest req = gson.fromJson(body, SimulationRequest.class);
                
                // Run Simulation
                SimulationResponse resp = SimulationService.runSimulation(req);
                
                // Return JSON
                ctx.json(resp);
                
            } catch (Exception e) {
                e.printStackTrace();
                ctx.status(500).json(Collections.singletonMap("error", e.getMessage()));
            }
        });
        
        System.out.println("Server started on port 8080");
    }
}
