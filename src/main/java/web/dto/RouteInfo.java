package web.dto;

import java.util.List;
import domain.Pair;

public class RouteInfo {
    public int camionId;
    public int centroId; // Or starting coordinate
    public List<Point> puntos; // Ordered list of points visited: Centro -> Gasolinera -> Gasolinera -> Centro
    
    public static class Point {
        public int x, y;
        public String type; // "CENTRO", "GASOLINERA"
        public int id;      // ID of the gas station or center

        public Point(int x, int y, String type, int id) {
            this.x = x;
            this.y = y;
            this.type = type;
            this.id = id;
        }
    }
}
