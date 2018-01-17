package parallelgeneticalgorithm.quality;

public interface QualityFunction<T> {

    /**
     * Assigns a quality score to the given individual. A higher score is
     * better.
     *
     * @param individual
     * @return
     */
    public abstract double computeQuality(T individual);
}
