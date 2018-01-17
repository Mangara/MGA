package parallelgeneticalgorithm.selection;

import java.util.Comparator;
import java.util.stream.IntStream;

/**
 * Roulette selection with fixed cumulative qualities that only depend on the
 * rank of the individual.
 */
public class RankSelection extends RouletteSelection {

    private double p = 0.9;
    private int[] rankToIndividual;

    public RankSelection() {
    }

    public RankSelection(double p) {
        this.p = p;
    }

    public double getP() {
        return p;
    }

    public void setP(double p) {
        this.p = p;
        cumulativeQuality = null;
    }

    @Override
    public void preprocess(double[] qualities) {
        int n = qualities.length;

        if (cumulativeQuality == null || cumulativeQuality.length != n) {
            // We need to rebuild the cumulative qualities
            cumulativeQuality = new double[n];
            rankToIndividual = new int[n];

            double quality = p;
            cumulativeQuality[0] = p;
            totalQuality = p;

            for (int i = 1; i < n; i++) {
                quality *= p;
                cumulativeQuality[i] = cumulativeQuality[i - 1] + quality;
                totalQuality += quality;
            }
        }

        rankToIndividual = IntStream.range(0, n)
                .parallel()
                .mapToObj(i -> i) // The IntStream "sorted" method does not support custom Comparators
                .sorted(Comparator.comparing(i -> qualities[i]))
                .mapToInt(i -> i)
                .toArray();
    }

    @Override
    public int select(double[] qualities) {
        int rank = super.select(qualities); // The roulette selection gives us the rank of the individual we want to return
        return rankToIndividual[rank];
    }

    @Override
    public String toString() {
        return "Rank";
    }
}
