package genetic_algorithm;

import models.Test;
import utils.MathHelper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FitnessCalculator {
    private static Map<Test, Integer> timeMap;
    private static Map<Test, Integer> machineMap;
    private static int maxFitness = Integer.MIN_VALUE;

    public static int calculateFitness(List<Test> allTests, byte[] genes,
                                       int numberOfBitsForTime, int numberOfBitsForMachines) {
        int fitness = 0;
        timeMap = new HashMap<>();
        machineMap = new HashMap<>();

        for (int i = 0; i < allTests.size(); i++) {
            int startIndexInGene = i * (numberOfBitsForTime + numberOfBitsForMachines);

            timeMap.put(allTests.get(i), MathHelper.getIntFromByteBinary(Arrays.copyOfRange(genes, startIndexInGene, startIndexInGene + numberOfBitsForTime)));
            machineMap.put(allTests.get(i), MathHelper.getIntFromByteBinary(Arrays.copyOfRange(genes,
                    startIndexInGene + numberOfBitsForTime, startIndexInGene + numberOfBitsForTime + numberOfBitsForMachines)));

            if (testIsSatisfied(allTests.get(i))) {
                fitness += 1;
            } else {
                fitness -= 2;
            }
        }

        if (fitness > maxFitness) {
            maxFitness = fitness;
        }

        return fitness;
    }

    private static boolean testIsSatisfied(Test test) {
        if (timeConflict(test) || incorrectMachine(test)) {
            return false;
        }

        return true;
    }

    private static boolean incorrectMachine(Test test) {
        return !(test.getRequiredMachines().size() == 0 || test.getRequiredMachines().contains("m" + machineMap.get(test)));
    }

    private static boolean resourseConflict() {
        return false;
    }

    private static boolean machineConflict(Test first, Test second) {
        return machineMap.get(first) == machineMap.get(second);
    }

    private static boolean timeConflict(Test test) {
        for (Map.Entry<Test, Integer> entry : timeMap.entrySet()) {
            if (entry.getKey().equals(test)) {
                continue;
            } else {
                if (machineConflict(test, entry.getKey())) {
                    if (timeMap.get(test) + test.getDuration() > entry.getValue() ||
                            timeMap.get(entry.getKey()) + entry.getKey().getDuration() > timeMap.get(test)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static int getMaxFitness() {
        return maxFitness;
    }
}
