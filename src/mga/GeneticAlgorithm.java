package mga;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;
import mga.crossover.Crossover;
import mga.mutation.Mutation;
import mga.quality.QualityFunction;
import mga.selection.Selection;

public class GeneticAlgorithm<T> {

    // Parameters
    private double crossoverChance = 0.7;
    private double mutationChance = 0.1;
    private double elitistFraction = 0;
    // Settings
    private boolean parallelEvaluation = true;
    // Operations
    private final QualityFunction<T> qualityFunction;
    private final Crossover<T> crossover;
    private final Mutation<T> mutation;
    private final Selection selection;
    // Local variables
    private List<T> population;
    private double[] qualities; // Quality cache; qualities[i] == qualityFunction.computeQuality(population.get(i))
    private T best;
    private double bestQuality;
    private int generation;
    // Debug setting
    private DebugLevel debugLevel = DebugLevel.GENERATION_SUMMARY;

    /**
     * Creates a new genetic algorithm with the given operations. Both quality
     * and selection must be non-null.
     *
     * @param quality
     * @param crossover
     * @param mutation
     * @param selection
     * @throws NullPointerException when quality or selection are null.
     */
    public GeneticAlgorithm(QualityFunction<T> quality, Crossover<T> crossover, Mutation<T> mutation, Selection selection) {
        if (quality == null || selection == null) {
            throw new NullPointerException();
        }

        this.qualityFunction = quality;
        this.crossover = crossover;
        this.mutation = mutation;
        this.selection = selection;
    }

    public double getCrossoverChance() {
        return crossoverChance;
    }

    public void setCrossoverChance(double crossoverChance) {
        this.crossoverChance = crossoverChance;
    }

    public double getMutationChance() {
        return mutationChance;
    }

    public void setMutationChance(double mutationChance) {
        this.mutationChance = mutationChance;
    }

    public double getElitistFraction() {
        return elitistFraction;
    }

    public void setElitistFraction(double elitistFraction) {
        this.elitistFraction = elitistFraction;
    }

    public boolean isParallelEvaluation() {
        return parallelEvaluation;
    }

    public void setParallelEvaluation(boolean parallelEvaluation) {
        this.parallelEvaluation = parallelEvaluation;
    }

    public void setDebugLevel(DebugLevel debugLevel) {
        this.debugLevel = debugLevel;
    }

    /**
     * Initializes this GA with a population consisting of a copy of the given
     * population.
     *
     * @param initialPopulation
     */
    public void initialize(List<T> initialPopulation) {
        this.population = new ArrayList<>(initialPopulation);
        resetState();
        computeQualities();

        if (debugLevel.compareTo(DebugLevel.FULL_POPULATION_DETAILS) >= 0) {
            System.out.println("Initial population:");
            printPopulation();
        }
    }

    /**
     * Returns the individual with the highest quality seen so far.
     *
     * @return
     */
    public T getBest() {
        return best;
    }

    /**
     * Returns the best individual and quality found after the specified number
     * of generations has been evolved.
     *
     * @param nGenerations
     * @return
     */
    public Pair<T, Double> getBestAfter(int nGenerations) {
        for (int i = 0; i < nGenerations; i++) {
            iterate();
        }

        return new Pair<>(best, bestQuality);
    }

    /**
     * Returns the best individual found when either the best quality is above
     * the given threshold, or the maximum number of generations has been used.
     * Note that in the second case, the returned solution may have a quality
     * below the given threshold. Also returns the quality of the returned
     * solution and the number of generations used to find it.
     *
     * @param threshold
     * @param maxGenerations
     * @return
     */
    public Pair<T, Pair<Double, Integer>> getFirstAbove(double threshold, int maxGenerations) {
        while (generation < maxGenerations && bestQuality < threshold) {
            iterate();
        }

        return new Pair<>(best, new Pair<Double, Integer>(bestQuality, generation));
    }

    /**
     * Computes the next generation based on the current settings. Also updates
     * the qualities and best individual.
     */
    public void iterate() {
        if (debugLevel.compareTo(DebugLevel.EVERYTHING) >= 0) {
            System.out.println("Quality: " + Arrays.toString(qualities));
        }

        population = computeNextGeneration();
        computeQualities();
        generation++;

        if (debugLevel.compareTo(DebugLevel.FULL_POPULATION_DETAILS) >= 0) {
            System.out.println(generation + ". New population:");
            printPopulation();
        } else if (debugLevel.compareTo(DebugLevel.GENERATION_SUMMARY) >= 0) {
            System.out.print(generation + ". ");
            printSummary();
        }
    }

    private List<T> computeNextGeneration() {
        List<T> newGeneration = new ArrayList<>(population.size());
        newGeneration.addAll(getElitistIndividuals());

        selection.preprocess(qualities);

        while (newGeneration.size() < population.size()) {
            if (crossover != null && Math.random() < crossoverChance && newGeneration.size() < population.size() - 1) {
                int i1 = selection.select(qualities);
                int i2 = selection.select(qualities);

                Pair<T, T> children = crossover.crossover(population.get(i1), population.get(i2));

                newGeneration.add(mutate(children.getFirst()));
                newGeneration.add(mutate(children.getSecond()));
            } else {
                int i = selection.select(qualities);
                newGeneration.add(mutate(population.get(i)));
            }
        }

        return newGeneration;
    }

    /**
     * Returns a list containing the best few individuals of the current
     * population. The number of individuals returned is determined by the
     * elitistFraction.
     *
     * @return
     */
    private List<T> getElitistIndividuals() {
        if (elitistFraction == 0) {
            return Collections.emptyList();
        }

        int n = (int) Math.ceil(elitistFraction * population.size());

        if (debugLevel.compareTo(DebugLevel.EVERYTHING) >= 0) {
            System.out.println("Copying the best " + n + " individuals to the next generation.");
        }

        if (n == 1) {
            return Collections.singletonList(best);
        }

        TreeSet<Pair<T, Double>> elite = new TreeSet<>((t1, t2) -> -Double.compare(t1.getSecond(), t2.getSecond()));

        for (int i = 0; i < population.size(); i++) {
            if (elite.size() < n) {
                elite.add(new Pair<>(population.get(i), qualities[i]));
            } else {
                Pair<T, Double> worst = elite.last();

                if (qualities[i] > worst.getSecond()) {
                    elite.remove(worst);
                    elite.add(new Pair<>(population.get(i), qualities[i]));
                }
            }
        }

        return elite.stream()
                .map(pair -> pair.getFirst())
                .collect(Collectors.toList());
    }

    /**
     * Applies the current mutation with the correct probability.
     *
     * @param child
     * @return
     */
    private T mutate(T child) {
        if (mutation != null && Math.random() < mutationChance) {
            return mutation.mutate(child);
        } else {
            return child;
        }
    }

    /**
     * Resets all internal state, except for the population.
     */
    private void resetState() {
        generation = 0;
    }

    /**
     * Updates the qualities array, and best.
     */
    private void computeQualities() {
        qualities = new double[population.size()];
        
        if (parallelEvaluation) {
            Arrays.parallelSetAll(qualities, i -> qualityFunction.computeQuality(population.get(i)));
        } else {
            for (int i = 0; i < population.size(); i++) {
                qualities[i] = qualityFunction.computeQuality(population.get(i));
            }
        }

        bestQuality = Double.NEGATIVE_INFINITY;
        best = null;

        for (int i = 0; i < population.size(); i++) {
            if (qualities[i] > bestQuality) {
                best = population.get(i);
                bestQuality = qualities[i];
            }
        }
    }

    // DEBUG-related
    public enum DebugLevel {

        NONE, GENERATION_SUMMARY, FULL_POPULATION_DETAILS, EVERYTHING;
    }

    private void printPopulation() {
        for (int i = 0; i < population.size(); i++) {
            System.out.printf("%s (%f)%n", population.get(i).toString(), qualities[i]);
        }
        printSummary();
        System.out.println();
    }

    private void printSummary() {
        System.out.printf("Best: %s (%f) Average quality: %f%n", best.toString(), bestQuality, Arrays.stream(qualities).sum() / population.size());
    }
}
