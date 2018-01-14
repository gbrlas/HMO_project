package genetic_algorithm;

import models.SolutionPresenter;
import models.Test;

import java.util.*;

import static demo.Main.*;

class Decoder {

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


    /**
     * Returns length of decoded solution.
     */
    static void decode(Permutation chromosome) {
        int totalDuration = 0;
        int tempDuration;
        Set<Integer> placedTests = new HashSet<>();

        machinesStartBeingAvailable = new HashMap<>();
        initializeMap(machines, machinesStartBeingAvailable);
        resourcesStartBeingAvailable = new HashMap<>();
        initializeMap(resourceOccurrences.keySet(), resourcesStartBeingAvailable);

        solutionPresenters = new ArrayList<>();

        int time = 0;
        while (placedTests.size() != 500) {

            for (int gene : chromosome.getPermutation()) {
                if (!placedTests.contains(gene)) {
                    Test test = tests.get(gene);
                    String machine;

                    boolean worksOnAllMachines = test.getRequiredMachines().size() == 0;
                    boolean testNeedsResources = test.getRequiredResources().size() != 0;

                    // nadji prvi slobodni stroj
                    if (worksOnAllMachines) {
                        machine = findFirstMachineEmptyAtGivenTime(machinesStartBeingAvailable.keySet(), time);
                    } else {
                        machine = findFirstMachineEmptyAtGivenTime(test.getRequiredMachines(), time);
                    }

                    // ako postoji stroj koji je dostupan u ovom trenutku
                    if (!(machine == null)) {
                        tempDuration = time + test.getDuration();

                        if (testNeedsResources) {
                            // ako nema dostupne sve resurse u tom trenutku, probaj smjestiti sljedeći test
                            if (!allResourcesAvailableAtGivenTime(test, time)) {
                                continue;
                            } else {
                                // svi resursi su mu dostupni u ovom trenutku, zauzmi ih
                                for (String resource : test.getRequiredResources()) {
                                    resourcesStartBeingAvailable.put(resource, tempDuration);
                                }
                            }
                        }

                        if (tempDuration > totalDuration) {
                            totalDuration = tempDuration;
                        }

                        placedTests.add(gene);

                        machinesStartBeingAvailable.put(machine, tempDuration);
                        solutionPresenters.add(new SolutionPresenter(test.getName(), time, machine));
                    }
                }
            }

            time++;
        }


        chromosome.setFitness(totalDuration);
        chromosome.setOutput(getSolutionOutput());
    }

    private static boolean allResourcesAvailableAtGivenTime(Test test, int time) {
        boolean flag = true;

        for (String resource : test.getRequiredResources()) {
            if (resourcesStartBeingAvailable.get(resource) > time) {
                flag  = false;
                break;
            }
        }

        return flag;
    }



    private static String findFirstMachineEmptyAtGivenTime(Collection<String> requiredMachines, int time) {
        for (String machine : requiredMachines) {
            if (machinesStartBeingAvailable.get(machine) <= time) {
                return machine;
            }
        }
        return null;
    }

    private static void initializeMap(Collection<String> keys, Map<String, Integer> map) {
        for (String key : keys) {
            map.put(key, 0);
        }
    }

    private static String getSolutionOutput() {
        StringBuilder sb = new StringBuilder();
        for (SolutionPresenter solutionPresenter : solutionPresenters) {
            sb.append(solutionPresenter.toString());
        }

        return sb.toString();
    }
}
