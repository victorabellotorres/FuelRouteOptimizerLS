package domain;

public final class Pair {
    public int first, second;

    public Pair(int first, int second) { this.first = first; this.second = second; }
    public void set(int first, int second) { this.first = first; this.second = second; }

    @Override public String toString() { return "(" + first + "," + second + ")"; }

    // Si lo vas a usar en HashSet/HashMap como clave, añade equals/hashCode
    @Override public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Pair other)) return false;
        return first == other.first && second == other.second;
    }
    @Override public int hashCode() {
        int r = Integer.hashCode(first);
        return 31 * r + Integer.hashCode(second);
    }
}
