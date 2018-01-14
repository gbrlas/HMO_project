package genetic_algorithm;

public class Population {

    /**
     * Chromosomes from population
     */
    private Permutation[] chromosomes;

    public Population(int populationSize, int lengthOfChromosome ) {
        this.chromosomes = new Permutation[populationSize];

        for (int i=0; i < populationSize;i++) {
            chromosomes[i] = new Permutation(lengthOfChromosome);
        }
    }

    /**
     * Method returns best chromosome by calculating fitness
     */
    public Permutation getBestChromosome() {
        Permutation bestUnit = chromosomes[0];
        for (Permutation chromosome : chromosomes) {
            if (chromosome.getFitness() < bestUnit.getFitness()) {
                bestUnit = chromosome;
            }
        }

        return bestUnit;
    }

    int getPopulationSize() {
        return chromosomes.length;
    }

    void setChromosome(Permutation newUnit, int index) {
        chromosomes[index] = newUnit;
    }

    Permutation getChromosome(int index) {
        return chromosomes[index];
    }

    public void decodeAllChromosomes() {
        for (Permutation chromosome : chromosomes) {
            Decoder.decode(chromosome);
        }
    }
}