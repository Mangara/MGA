package parallelgeneticalgorithm.crossover;

import parallelgeneticalgorithm.Pair;

public interface Crossover<T> {

    /**
     * Forms two new children by crossing over parent1 and parent2.
     * @param parent1
     * @param parent2
     * @return 
     */
    public abstract Pair<T, T> crossover(T parent1, T parent2);

}
