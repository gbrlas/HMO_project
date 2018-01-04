package genetic_algorithm;

import models.Test;

import java.util.List;

public class Population {

    /**
     * Chromosomes from population
     */
    private TestChromosome[] chromosomes;
    /**
     * Tests from input file
     */
    private List<Test> tests;

    public Population(int lengthOfChromosome, int numberOfBitsForTime, int numberOfBitsForMachines,
                      int sizeOfPopulation, boolean initialize, List<Test> tests) {
        chromosomes = new TestChromosome[sizeOfPopulation];
        this.tests = tests;

        if (initialize) {
            for (int i = 0; i < chromosomes.length; i++) {
                TestChromosome newTestChromosome = new TestChromosome(lengthOfChromosome, numberOfBitsForTime, numberOfBitsForMachines, tests);
                chromosomes[i] = newTestChromosome;
            }
        }


    }

    /**
     * Method returns best chromosome by calculating fitness
     */
    public TestChromosome getBestChromosome() {
        TestChromosome bestUnit = chromosomes[0];
        for (int i = 0; i < chromosomes.length; i++) {
            if (chromosomes[i].getFitness(tests) > bestUnit.getFitness(tests)) {
                bestUnit = chromosomes[i];
            }
        }
        return bestUnit;
    }

    public int getPopulationSize() {
        return chromosomes.length;
    }

    public void setChromosome(TestChromosome newUnit, int index) {
        chromosomes[index] = newUnit;
    }

    public TestChromosome getChromosome(int index) {
        return chromosomes[index];
    }
}