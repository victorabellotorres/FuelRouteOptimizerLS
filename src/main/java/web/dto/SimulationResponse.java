package web.dto;

import java.util.List;
import domain.Pair;

public class SimulationResponse {
    public double beneficio;
    public double coste;
    public long tiempoEjecucionMs;
    public List<RouteInfo> rutas;
    public List<Pair> gasolineras; // Coordenadas
    public List<Pair> centros;     // Coordenadas
    public List<String> log;       // Instrumentation log
}
