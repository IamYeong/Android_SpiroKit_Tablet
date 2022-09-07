package kr.co.theresearcher.spirokitfortab.graph;

public class Coordinate {

    private double time;
    private double lps;
    private double volume;

    public Coordinate(double t, double lps, double v) {
        this.time = t;
        this.lps = lps;
        this.volume = v;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public double getLps() {
        return lps;
    }

    public void setLps(double lps) {
        this.lps = lps;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }
}
