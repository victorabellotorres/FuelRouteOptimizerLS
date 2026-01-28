package web.dto;

public class SimulationRequest {
    public int numGasolineras; // = 100
    public int numCentros;    // = 10
    public int camionesPorCentro; // = 1
    public int seed;          // = 1234
    public int algoritmoInicial; // 1-6
    public int algoritmoBusqueda; // 1: HillClimbing, 2: SA
    
    // SA params (optional)
    public int saSteps = 2500;
    public int saStiter = 5;
    public int saK = 1;
    public double saLambda = 0.01;
}
