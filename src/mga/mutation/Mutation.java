package mga.mutation;

public interface Mutation<T> {
    public abstract T mutate(T individual);
}
