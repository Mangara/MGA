# MGA

A Java library for classic [genetic algorithms](https://en.wikipedia.org/wiki/Genetic_algorithm).

## Getting Started

To use the library, download [MGA.jar](MGA-v1.0.jar) and, optionally, [MGA-sources.jar](MGA-sources-v1.0.jar).

To build the project, open it with a recent version of [NetBeans](https://netbeans.org/).

### Example Usage

```java
// Our quality function - this is what we want to maximize
QualityFunction<Double> evaluation = v -> -2 * v * v + 20 * v + 3;

// The crossover - randomly combines two individuals into two new ones
Crossover<Double> weightedAverage = (Double parent1, Double parent2) -> {
    double weight = ThreadLocalRandom.current().nextDouble();
    double child1 = weight * parent1 + (1 - weight) * parent2;
    double child2 = (1 - weight) * parent1 + weight * parent2;
    return new Pair<>(child1, child2);
};

// The mutation - a small random change to a single individual
Mutation<Double> bump = v -> v + 2 * (ThreadLocalRandom.current().nextDouble() - 0.5);

// The GA object. We have to pick a selection strategy as well; RankSelection is a good default.
GeneticAlgorithm<Double> ga = new GeneticAlgorithm<>(evaluation, weightedAverage, bump, new RankSelection());

// The initial population
List<Double> population = ThreadLocalRandom.current().doubles(20, 0, 10).boxed()
                                                     .collect(Collectors.toList());

// Initialization
ga.initialize(population);

// Run for a set number of generations
Pair<Double, Double> best = ga.getBestAfter(20);

System.out.println("Approximate maximum at (" + best.getFirst() + ", " + best.getSecond() + ")");
```

## Authors

* **Sander Verdonschot** - [Mangara](https://github.com/Mangara)

## License

This project is licensed under the MIT License - see the [LICENSE.txt](LICENSE.txt) file for details
