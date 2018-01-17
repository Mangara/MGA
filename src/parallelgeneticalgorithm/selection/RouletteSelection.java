package parallelgeneticalgorithm.selection;

public class RouletteSelection implements Selection {

    protected double[] cumulativeQuality;
    protected double totalQuality;

    @Override
    public void preprocess(double[] qualities) {
        if (cumulativeQuality == null || cumulativeQuality.length != qualities.length) {
            cumulativeQuality = new double[qualities.length];
        }
        
        totalQuality = 0;

        if (qualities.length > 0) {
            cumulativeQuality[0] = qualities[0];
            totalQuality = qualities[0];
        }

        for (int i = 1; i < qualities.length; i++) {
            cumulativeQuality[i] = cumulativeQuality[i - 1] + qualities[i];
            totalQuality += qualities[i];
        }
    }

    @Override
    public int select(double[] qualities) {
        double slice = Math.random() * totalQuality;

        int currentBegin = -1;
        int currentEnd = qualities.length;
        int middle;

        while (currentBegin < (currentEnd - 1)) {
            middle = (currentBegin + currentEnd) / 2;

            if (slice < cumulativeQuality[middle]) {
                currentEnd = middle;
            } else {
                currentBegin = middle;
            }
        }

        return Math.min(qualities.length - 1, currentEnd);
    }

    @Override
    public String toString() {
        return "Roulette";
    }
}
