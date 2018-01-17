package mga.selection;

public interface Selection {

    /**
     * Prepares the selection process for the current generation.
     * Call this once per generation, before calling select.
     * @param qualities
     */
    public abstract void preprocess(double[] qualities);

    /**
     * Selects an individual from the population.
     * @param qualities
     * @return
     */
    public abstract int select(double[] qualities);
}
