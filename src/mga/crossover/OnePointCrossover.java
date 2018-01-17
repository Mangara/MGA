package mga.crossover;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import mga.Pair;

/**
 * A simple crossover for lists of equal size. Picks one random index, then
 * creates the children by swapping the two parts of the parents after the
 * index.
 *
 * @param <E>
 */
public class OnePointCrossover<E> implements Crossover<List<E>> {

    @Override
    public Pair<List<E>, List<E>> crossover(List<E> parent1, List<E> parent2) {
        int chromosomeLength = parent1.size();
        int j = ThreadLocalRandom.current().nextInt(chromosomeLength);

        List<E> child1 = new ArrayList<>(chromosomeLength);
        List<E> child2 = new ArrayList<>(chromosomeLength);

        child1.addAll(parent1.subList(0, j));
        child1.addAll(parent2.subList(j, chromosomeLength));

        child2.addAll(parent2.subList(0, j));
        child2.addAll(parent1.subList(j, chromosomeLength));

        return new Pair<>(child1, child2);
    }

    @Override
    public String toString() {
        return "1-Point";
    }
}
