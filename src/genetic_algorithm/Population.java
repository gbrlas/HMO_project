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
        for (int i = 0; i < chromosomes.length; i++) {
            if (chromosomes[i].getFitness() < bestUnit.getFitness()) {
                bestUnit = chromosomes[i];
            }
        }

        return bestUnit;
    }

    public int getPopulationSize() {
        return chromosomes.length;
    }

    public void setChromosome(Permutation newUnit, int index) {
        chromosomes[index] = newUnit;
    }

    public Permutation getChromosome(int index) {
        return chromosomes[index];
    }

    public void decodeAllChromosomes() {
        for (int i = 0; i < chromosomes.length; i++) {
            Decoder.decode(chromosomes[i]);
        }
    }
}