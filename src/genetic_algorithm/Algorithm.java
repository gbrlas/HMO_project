package genetic_algorithm;

import models.Test;

import java.util.List;

public class Algorithm {

    /**
     *  GA parameters
     */
    private static final double parentProbability = 0.5;
    public static double mutation = 0.15;
    private static final int sizeOfTournamentSelection = 3;
    private static final boolean elitism = true;
    private static List<Test> tests;

    /**
     * Method evolves given population
     */
    public static Population evolve(Population population, int lengthOfChromosome, int numberOfBitsForTime,
                                    int numberOfBitsForMachines, List<Test> testList) {
        tests = testList;
        Population newPopulation = new Population(lengthOfChromosome, numberOfBitsForTime, numberOfBitsForMachines,
                population.getPopulationSize(), false, tests);


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
        for (int i = skipFirstChromosome; i < population.getPopulationSize(); i++) {
            TestChromosome mom = tournamentSelection(population, lengthOfChromosome, numberOfBitsForTime, numberOfBitsForMachines);
            TestChromosome dad = tournamentSelection(population, lengthOfChromosome, numberOfBitsForTime, numberOfBitsForMachines);
            TestChromosome newChild = crossover(mom, dad, lengthOfChromosome, numberOfBitsForTime, numberOfBitsForMachines);
            mutate(newChild);
            newPopulation.setChromosome(newChild, i);
        }

        return newPopulation;
    }

    private static TestChromosome crossover(TestChromosome mom, TestChromosome dad, int lengthOfChromosome, int numberOfBitsForTime, int numberOfBitsForMachines) {
        TestChromosome newChild = new TestChromosome(lengthOfChromosome, numberOfBitsForTime, numberOfBitsForMachines, tests);
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

    private static TestChromosome tournamentSelection(Population population, int lengthOfChromosome, int numberOfBitsForTime, int numberOfBitsForMachines) {
        Population tournamentPopulation = new Population(lengthOfChromosome, numberOfBitsForTime, numberOfBitsForMachines, sizeOfTournamentSelection, false, tests);
        for (int i = 0; i < sizeOfTournamentSelection; i++) {
            int index = (int) (Math.random() * population.getPopulationSize());
            tournamentPopulation.setChromosome(population.getChromosome(index), i);
        }

        return tournamentPopulation.getBestChromosome();
    }
}