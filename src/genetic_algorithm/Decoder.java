package genetic_algorithm;

import models.SolutionPresenter;
import models.Test;

import java.util.*;

import static demo.Main.machines;
import static demo.Main.resourceOccurrences;
import static demo.Main.tests;

public class Decoder {

    /**
     * For given machine, map returns value of time unit
     * when it is available
     */
    private static Map<String, Integer> machinesStartBeingAvailable;

    /**
     * For given resource, map returns value of time unit
     * when it is available
     */
    private static Map<String, Integer> resourcesStartBeingAvailable;

    private static List<SolutionPresenter> solutionPresenters;

    private static Random rand = new Random();


    /**
     * Returns length of decoded solution.
     */
    public static void decode(Permutation chromosome) {
        int totalDuration = 0;
        int tempDuration;

        machinesStartBeingAvailable = new HashMap<>();
        initializeMap(machines, machinesStartBeingAvailable);
        resourcesStartBeingAvailable = new HashMap<>();
        initializeMap(resourceOccurrences.keySet(), resourcesStartBeingAvailable);

        solutionPresenters = new ArrayList<>();

        for(int gene : chromosome.getPermutation()) {
            Test test = tests.get(gene);

            String machine;
            if (test.getRequiredMachines().size() == 0) {
                machine = findFirstEmptyMachine();
            } else {
                machine = findFirstEmptyMachine(test.getRequiredMachines());
            }

            int testStartingTime = machinesStartBeingAvailable.get(machine);
            if (test.getRequiredResources().size() == 0) {
                machinesStartBeingAvailable.put(machine, testStartingTime + test.getDuration());
            } else {
                testStartingTime = findTimeWithFreeResources(test, testStartingTime);
            }

            tempDuration = testStartingTime + test.getDuration();
            if (tempDuration > totalDuration) {
                totalDuration = tempDuration;
            }

            solutionPresenters.add(new SolutionPresenter(test.getName(), testStartingTime,  machine));
        }

        chromosome.setFitness(totalDuration);
        chromosome.setOutput(getSolutionOutput());
    }

    private static String findFirstEmptyMachine(Collection<String> requiredMachines) {
        int minimalTime = Integer.MAX_VALUE;

        for (Map.Entry<String, Integer> machineAndStartingTime : machinesStartBeingAvailable.entrySet()) {
            if (machineAndStartingTime.getValue() < minimalTime && requiredMachines.contains(machineAndStartingTime.getKey())) {
                minimalTime = machineAndStartingTime.getValue();
            }
        }

        List<String> minimalTimeMachines = new ArrayList<>();
        for (Map.Entry<String, Integer> machineAndStartingTime : machinesStartBeingAvailable.entrySet()) {
            if (machineAndStartingTime.getValue() == minimalTime) {
                minimalTimeMachines.add(machineAndStartingTime.getKey());
            }
        }

        return minimalTimeMachines.get(rand.nextInt(minimalTimeMachines.size()));
    }

    private static String findFirstEmptyMachine() {
        return findFirstEmptyMachine(machinesStartBeingAvailable.keySet());
    }

    private static int findTimeWithFreeResources(Test test, int testStartingTime) {
        List<String> requiredResources = test.getRequiredResources();
        int startOfResourcesBeingAvailable = 0;

        for (String resource : requiredResources) {
            int resourceTime = resourcesStartBeingAvailable.get(resource);
            if (resourceTime > startOfResourcesBeingAvailable) {
                startOfResourcesBeingAvailable = resourceTime;
            }
        }

        startOfResourcesBeingAvailable = Math.max(startOfResourcesBeingAvailable,
                testStartingTime);
        for (String resource : requiredResources) {
            resourcesStartBeingAvailable.put(resource, startOfResourcesBeingAvailable + test.getDuration());
        }

        return startOfResourcesBeingAvailable;
    }


    private static void initializeMap(Collection<String> keys, Map<String, Integer> map) {
        for (String key : keys) {
            map.put(key, 0);
        }
    }

    private static String getSolutionOutput() {
        StringBuilder sb = new StringBuilder();
        for(SolutionPresenter solutionPresenter : solutionPresenters) {
            sb.append(solutionPresenter.toString());
        }

        return sb.toString();
    }
}
