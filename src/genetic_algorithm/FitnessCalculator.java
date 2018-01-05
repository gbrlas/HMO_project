package genetic_algorithm;

import models.Test;
import utils.ChromosomeUtils;

import java.sql.Time;
import java.time.LocalTime;
import java.util.*;

import static demo.Main.*;

public class FitnessCalculator {
    /**
     * Test and its starting time for given gene
     */
    private static Map<Test, Integer> currentTimeMap;
    /**
     * Test and its machine for given gene
     */
    private static Map<Test, Integer> currentMachineMap;

    /**
     * @param tests are tests from input file which are checked to be satisfied by @param genes
     * @return fitness of current genes array
     */
    public static double calculateFitness(List<Test> tests, byte[] genes) {
        double fitness = 0;
        currentTimeMap = new HashMap<>();
        currentMachineMap = new HashMap<>();

        // loop checks each test from input if it is satisfied
        for (int i = 0; i < tests.size(); i++) {
            int startIndexInGene = i * lengthOfOneTest;

            currentTimeMap.put(tests.get(i), ChromosomeUtils.getStartingTime(genes, startIndexInGene));
            currentMachineMap.put(tests.get(i), ChromosomeUtils.getMachine(genes, startIndexInGene));

            if (testIsNotSatisfied(tests.get(i))) {
                fitness -= 1000;
            } else {
                fitness += 10;
            }
        }

        fitness /= punishLongerSolutionsOnDifferentMachines(tests);


        return fitness;
    }

    private static int punishLongerSolutionsOnDifferentMachines(List<Test> tests) {
        int[] maxDurationsPerMachine = new int[numberOfMachines];


        int zeros = 0;
        int punishmentFactor = 0;

        for (Test test : tests) {
            int machine = currentMachineMap.get(test);
            int duration = currentTimeMap.get(test) + test.getDuration();
            if (duration > maxDurationsPerMachine[machine - 1]) {
                maxDurationsPerMachine[machine - 1] = duration;
            }


            if (currentTimeMap.get(test) == 0) {
                zeros++;
            }

            if (currentTimeMap.get(test) >= (totalTimeWorstScenario / numberOfMachines)) {
                punishmentFactor += 5;
            }
        }


        int maxDuration = 0;
        for (int i = 0; i < numberOfMachines; i++) {
            maxDuration += maxDurationsPerMachine[i];
        }


        if (zeros < numberOfMachines) {
            maxDuration *= (2 * (numberOfMachines - zeros));
        }

        if (punishmentFactor != 0) {
            return maxDuration * punishmentFactor;
        }
        return maxDuration;
    }


    private static boolean testIsNotSatisfied(Test test) {
        return timeConflict(test) || incorrectMachine(test);
    }


    private static boolean otherTestHasSameResourceAsMe(Test currentTest, Test otherTest) {
        List<String> currentResources = currentTest.getRequiredResources();
        List<String> otherResources = otherTest.getRequiredResources();

        for(String currentResource : currentResources) {
            for(String otherResource : otherResources) {
                if (currentResource.equals(otherResource)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Machine is incorrect if there is no machine constraints or
     * our machine is not in list of valid machines
     */
    private static boolean incorrectMachine(Test test) {
        int machine = currentMachineMap.get(test);
        boolean isMachineSupported = test.getRequiredMachines().contains("m" + machine);
        boolean noConstraints = test.getRequiredMachines().size() == 0;
        return !(noConstraints || isMachineSupported);
    }

    private static boolean machineConflict(Test first, Test second) {
        return currentMachineMap.get(first) == currentMachineMap.get(second);
    }

    /**
     *
     *
     */
    private static boolean timeConflict(Test test) {
        for (Map.Entry<Test, Integer> entry : currentTimeMap.entrySet()) {
            // continue if we are checking the same test
            if (entry.getKey().equals(test)) {
                continue;
            } else {
                if (machineConflict(test, entry.getKey()) || otherTestHasSameResourceAsMe(test, entry.getKey())) {
                    if ((entry.getValue() >= currentTimeMap.get(test)
                            && entry.getValue() < currentTimeMap.get(test) + test.getDuration()) ||
                            (currentTimeMap.get(test) >= entry.getValue()
                                    && currentTimeMap.get(test) < entry.getValue() + entry.getKey().getDuration())) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
