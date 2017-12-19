package genetic_algorithm;

import models.Test;
import utils.MathHelper;

import java.util.Arrays;
import java.util.List;

/**
 * One chromosome represents one possible solution.
 * So it must contain all tests and their values for current given solution.
 *
 * It will be modeled in a following way:
 *
 * a) chromosome contains only binary values
 * b) length will depend of total hours of all tests and number of tests
 * c) values for each test are in ascending order
 *
 * For example:
 *
 * test1: 2h, machine 1 or 2
 * test2: 3h, machine 1 or 2
 * test3: 5h, machine 1 or 2
 * test4: 1h, machine 1 or 2
 *
 * In this case, worst scenario is 11h long (all tests on same machine one by one,
 * so we need 4 fields for that matter (2^4 = 16). There are two machines, so we need
 * only one field for machine value.
 *
 * Initial chromosome will then look like this:
 *
 * [0000 1 0010 0 0011 1 1100 1]
 *
 * Explanation:
 *
 * 0000 1-> test1 will start in time element 0 and would run on machine 2
 * 0010 0-> test2 will start in time element 2 and would run on machine 1
 *
 * You can see here that this example would have bad fitness, (for example)
 * because three tests are running on same machine and last test will start in
 * time element of 12 which is later than worst scenario. But that is not
 * important here, we have fitness function for that matter.
 *
 * One last thing that is important to mention here is that here we do not have
 * resource values set in chromosome. It is simply because it is also covered
 * when calculating fitness function.
 */
public class TestChromosome {

    private int lengthOfChromosome;
    private byte[] genes;

    private int numberOfBitsForTime;
    private int numberOfBitsForMachines;

    private int numberOfTests;

    public TestChromosome(int lengthOfChromosome, int numberOfBitsForTime, int numberOfBitsForMachines) {
        this.lengthOfChromosome = lengthOfChromosome;
        this.genes = new byte[lengthOfChromosome];

        this.numberOfBitsForTime = numberOfBitsForTime;
        this.numberOfBitsForMachines = numberOfBitsForMachines;

        this.numberOfTests = lengthOfChromosome / (numberOfBitsForMachines + numberOfBitsForTime);

        initializeGenome();
    }

    private void initializeGenome() {
        for (int i = 0; i < lengthOfChromosome; i++) {
            byte gene = (byte) Math.round(Math.random());
            genes[i] = gene;
        }
    }

    public byte getGene(int index) {
        return genes[index];
    }

    public void setGene(int index, byte value) {
        genes[index] = value;
    }

    public int getFitness(List<Test> tests) {
        return FitnessCalculator.calculateFitness(tests, genes, numberOfBitsForTime, numberOfBitsForMachines);
    }

    public int getSize() {
        return lengthOfChromosome;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < numberOfTests; i++) {
            int startIndexInGene = i * (numberOfBitsForTime + numberOfBitsForMachines);
            builder.append("Task t" + (i+1) + "\t" + "Starting time: " + MathHelper.getIntFromByteBinary(Arrays.copyOfRange(genes, startIndexInGene, startIndexInGene + numberOfBitsForTime)) +
                    "\t" + "Machine: " + (MathHelper.getIntFromByteBinary(Arrays.copyOfRange(genes,
                    startIndexInGene + numberOfBitsForTime, startIndexInGene + numberOfBitsForTime + numberOfBitsForMachines)) + 1) + "\n");
        }

        builder.append("\n");

        return builder.toString();
    }
}
