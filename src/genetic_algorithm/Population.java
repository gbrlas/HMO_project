package genetic_algorithm;

import models.Test;

import java.util.List;

public class Population {

    private TestChromosome[] chromosomes;
    private List<Test> tests;

    public Population(int lengthOfChromosome, int numberOfBitsForTime, int numberOfBitsForMachines, int sizeOfPopulation, boolean initialize, List<Test> tests) {
        chromosomes = new TestChromosome[sizeOfPopulation];
        this.tests = tests;

        if (initialize) {
            for (int i = 0; i < chromosomes.length; i++) {
                TestChromosome newTestChromosome = new TestChromosome(lengthOfChromosome, numberOfBitsForTime, numberOfBitsForMachines);
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
            if (bestUnit.getFitness(tests) <= chromosomes[i].getFitness(tests)) {
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

    public void adjustTime() {

    }
}