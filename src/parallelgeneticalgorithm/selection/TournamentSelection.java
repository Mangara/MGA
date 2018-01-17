package parallelgeneticalgorithm.selection;

import java.util.Arrays;
import java.util.Random;

public class TournamentSelection implements Selection {

    private static final Random rand = new Random();
    private int n = 20;
    private double p = 0.9;

    public TournamentSelection() {
    }

    public TournamentSelection(int n, double p) {
        this.n = n;
        this.p = p;
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public double getP() {
        return p;
    }

    public void setP(double p) {
        this.p = p;
    }

    @Override
    public void preprocess(double[] qualities) {
    }

    @Override
    public int select(double[] qualities) {
        // Select n random individuals
        int[] contestants = new int[n];

        for (int i = 0; i < n; i++) {
            contestants[i] = rand.nextInt(qualities.length);
        }

        if (rand.nextDouble() < p) {
            // Return the best
            return Arrays.stream(contestants)
                    .reduce((c1, c2) -> qualities[c1] > qualities[c2] ? c1 : c2)
                    .getAsInt();
        } else {
            // Return a random one
            return contestants[rand.nextInt(n)];
        }
    }

    @Override
    public String toString() {
        return "Tournament";
    }
}
