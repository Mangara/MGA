package mga.mutation;

public interface Mutation<T> {

    /**
     * Returns a new individual that is slightly changed from the given one.
     *
     * @param individual
     * @return
     */
    public abstract T mutate(T individual);
}
