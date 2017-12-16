package genetic_algorithm;

public class Population {

    private TestChromosome[] chromosomes;

    public Population(int lengthOfChromosome, int sizeOfPopulation, boolean initialize) {
        chromosomes = new TestChromosome[sizeOfPopulation];

        if (initialize) {
            for (int i = 0; i < chromosomes.length; i++) {
                TestChromosome newTestChromosome = new TestChromosome(lengthOfChromosome);
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
            if (bestUnit.getFitness() <= chromosomes[i].getFitness()) {
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