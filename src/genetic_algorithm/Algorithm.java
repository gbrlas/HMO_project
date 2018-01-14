package genetic_algorithm;

import static demo.Main.POPULATION_SIZE;

public class Algorithm {

    /**
     *  GA parameters
     */
    static double mutation = 0.8;
    private static final int sizeOfTournamentSelection = 4;
    private static final boolean elitism = true;

    /**
     * Method evolves given population
     */
    public static Population evolve(Population population, int numberOfTests) {
        Population newPopulation = new Population(POPULATION_SIZE, numberOfTests);


        // if elitism is ON, then we skip first chromosome (best one)
        // this variable is actually an offset included in for loop
        int skipFirstChromosome;
        if (elitism) {
            newPopulation.setChromosome(population.getBestChromosome(), 0);
            skipFirstChromosome = 1;
        } else {
            skipFirstChromosome = 0;
        }

        // tournament selection -> crossover
        // and mutation
        for (int i = skipFirstChromosome; i < population.getPopulationSize() - 1; i += 2) {
            Permutation mom = tournamentSelection(population, numberOfTests);
            Permutation dad = tournamentSelection(population, numberOfTests);
            Permutation[] children = mom.crossover(dad);
            newPopulation.setChromosome(children[0], i);
            newPopulation.setChromosome(children[1], i + 1);
        }

        return newPopulation;
    }

    private static Permutation tournamentSelection(Population population, int numberOfTests) {
        Population tournamentPopulation = new Population(POPULATION_SIZE, numberOfTests);
        for (int i = 0; i < sizeOfTournamentSelection; i++) {
            int index = (int) (Math.random() * population.getPopulationSize());
            tournamentPopulation.setChromosome(population.getChromosome(index), i);
        }

        return tournamentPopulation.getBestChromosome();
    }
}