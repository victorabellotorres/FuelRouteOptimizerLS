package domain;

public final class BoardMetrics {
    private final boolean valid;
    private final String errorMessage;
    private final int peticionesAsignadas;
    private final int peticionesTotales;
    private final int camionesUsados;
    private final int camionesTotales;
    private final int totalKm;
    private final double kmMedioCamion;
    private final double beneficio;
    private final double coste;
    private final double calidad;
    private final double beneficioDelDia;

    public BoardMetrics(boolean valid, String errorMessage, int peticionesAsignadas, int peticionesTotales, int camionesUsados, int camionesTotales, int totalKm, double kmMedioCamion, double beneficio, double coste, double beneficioDelDia) {
        this.valid = valid;
        this.errorMessage = errorMessage;
        this.peticionesAsignadas = peticionesAsignadas;
        this.peticionesTotales = peticionesTotales;
        this.camionesUsados = camionesUsados;
        this.camionesTotales = camionesTotales;
        this.totalKm = totalKm;
        this.kmMedioCamion = kmMedioCamion;
        this.beneficio = beneficio;
        this.coste = coste;
        this.calidad = beneficio-coste;
        this.beneficioDelDia = beneficioDelDia;
    }

    public boolean isValid() { return valid; }
    public String getErrorMessage() { return errorMessage; }
    public int getPeticionesAssignadas() { return peticionesAsignadas; }
    public int getPeticionesTotales() { return peticionesTotales; }
    public int getCamionesUsados() { return camionesUsados; }
    public int getCamionesTotales() { return camionesTotales; }
    public int getTotalKm() { return totalKm; }
    public double getKmMedioCamion() { return kmMedioCamion; }
    public double getBeneficio() { return beneficio; }
    public double getCoste() { return coste; }
    public double getCalidad() { return calidad; }
    public double getBeneficioDelDia() { return beneficioDelDia; }

    @Override
    public String toString() {
        return "BoardMetrics{" +
                "valid=" + valid +
                ", errorMessage='" + errorMessage + '\'' +
                ", peticionesAsignadas=" + peticionesAsignadas +
                ", peticionesTotales=" + peticionesTotales +
                ", camionesUsados=" + camionesUsados +
                ", camionesTotales=" + camionesTotales +
                ", totalKm=" + totalKm +
                ", kmMedioCamion=" + kmMedioCamion +
                ", beneficio=" + beneficio +
                ", coste=" + coste +
                ", calidad=" + calidad +
                ", beneficioDelDia=" + beneficioDelDia +
                '}';
    }
}