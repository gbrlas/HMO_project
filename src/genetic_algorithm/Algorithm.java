package genetic_algorithm;

public class Algorithm {

    /**
     *  GA parameters
     */
    private static final double parentProbability = 0.5;
    private static final double mutation = 0.015;
    private static final int sizeOfTournamentSelection = 3;
    private static final boolean elitism = true;

    /**
     * Method evolves given population
     */
    public static Population evoluiraj(Population population, int lengthOfChromosome) {
        Population newPopulation = new Population(lengthOfChromosome,
                population.getPopulationSize(), false);

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
        for (int i = skipFirstChromosome; i < population.getPopulationSize(); i++) {
            TestChromosome mom = tournamentSelection(population, lengthOfChromosome);
            TestChromosome dad = tournamentSelection(population, lengthOfChromosome);
            TestChromosome newChild = crossover(mom, dad, lengthOfChromosome);
            newPopulation.setChromosome(newChild, i);
        }

        // mutation
        for (int i = skipFirstChromosome; i < newPopulation.getPopulationSize(); i++) {
            mutate(newPopulation.getChromosome(i));
        }

        return newPopulation;
    }

    private static TestChromosome crossover(TestChromosome mom, TestChromosome dad, int lengthOfChromosome) {
        TestChromosome newChild = new TestChromosome(lengthOfChromosome);
        for (int i = 0; i < mom.getSize(); i++) {
            if (Math.random() <= parentProbability) {
                newChild.setGene(i, mom.getGene(i));
            } else {
                newChild.setGene(i, dad.getGene(i));
            }
        }
        return newChild;
    }

    private static void mutate(TestChromosome chromosome) {
        for (int i = 0; i < chromosome.getSize(); i++) {
            if (Math.random() <= mutation) {
                byte gene = (byte) Math.round(Math.random());
                chromosome.setGene(i, gene);
            }
        }
    }

    private static TestChromosome tournamentSelection(Population population, int lengthOfChromosome) {
        Population tournamentPopulation = new Population(lengthOfChromosome, sizeOfTournamentSelection, false);
        for (int i = 0; i < sizeOfTournamentSelection; i++) {
            int index = (int) (Math.random() * population.getPopulationSize());
            tournamentPopulation.setChromosome(population.getChromosome(index), i);
        }

        return tournamentPopulation.getBestChromosome();
    }
}