package genetic_algorithm;

import models.Test;

import java.util.Arrays;
import java.util.List;

public class FitnessCalculator {

    public static int calculateFitness(List<Test> allTests, byte[] genes,
                                       int numberOfBitsForTime, int numberOfBitsForMachines) {
        int fitness = 0;

        for (int i = 0; i < allTests.size(); i++) {
            int startIndexInGene = i * (numberOfBitsForTime + numberOfBitsForMachines);
            int endIndexInGene = startIndexInGene + (numberOfBitsForMachines + numberOfBitsForTime);

            if (testIsSatisfied(allTests.get(i), Arrays.copyOfRange(genes, startIndexInGene, endIndexInGene))) {
                fitness += 1;
            } else {
                fitness -= 1;
            }
        }

        return fitness;
    }

    private static boolean testIsSatisfied(Test test, byte[] bytes) {

    }
}
