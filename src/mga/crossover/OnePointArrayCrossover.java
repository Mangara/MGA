package mga.crossover;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import mga.Pair;

/**
 * A simple crossover for arrays of equal size. Picks one random index, then
 * creates the children by swapping the two parts of the parents after the
 * index.
 *
 * @param <E>
 */
public class OnePointArrayCrossover<E> implements Crossover<E[]> {

    @Override
    public Pair<E[], E[]> crossover(E[] parent1, E[] parent2) {
        int chromosomeLength = parent1.length;
        int j = ThreadLocalRandom.current().nextInt(chromosomeLength);

        E[] child1 = Arrays.copyOf(parent1, chromosomeLength);
        E[] child2 = Arrays.copyOf(parent2, chromosomeLength);

        // Overwrite the second part of the first individual to the second child and likewise for the second
        System.arraycopy(parent1, j, child2, j, chromosomeLength - j);
        System.arraycopy(parent2, j, child1, j, chromosomeLength - j);

        return new Pair<>(child1, child2);
    }

    @Override
    public String toString() {
        return "1-Point";
    }
}
