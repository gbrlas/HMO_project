package genetic_algorithm;

import models.ScheduleHole;
import models.SolutionPresenter;
import models.Test;

import java.util.*;

import static demo.Main.machines;
import static demo.Main.resourceOccurrences;
import static demo.Main.tests;

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

    private static Map<String, List<ScheduleHole>> machineHoles;
    private static Map<String, List<ScheduleHole>> resourceHoles;

    private static List<SolutionPresenter> solutionPresenters;


    /**
     * Returns length of decoded solution.
     */
    static void decode(Permutation chromosome) {
        int totalDuration = 0;
        int tempDuration;

        machinesStartBeingAvailable = new HashMap<>();
        initializeMap(machines, machinesStartBeingAvailable);
        resourcesStartBeingAvailable = new HashMap<>();
        initializeMap(resourceOccurrences.keySet(), resourcesStartBeingAvailable);

        machineHoles = new HashMap<>();
        resourceHoles = new HashMap<>();

        solutionPresenters = new ArrayList<>();

        for (int gene : chromosome.getPermutation()) {
            Test test = tests.get(gene);

            if (fitsAHole(test)) {
                continue;
            }

            String machine;
            if (test.getRequiredMachines().size() == 0) {
                machine = findFirstEmptyMachine();
            } else {
                machine = findFirstEmptyMachine(test.getRequiredMachines());
            }

            int testStartingTime = machinesStartBeingAvailable.get(machine);
            if (test.getRequiredResources().size() != 0) {
                testStartingTime = findTimeWithFreeResources(test, testStartingTime, machine);
            }

            tempDuration = testStartingTime + test.getDuration();
            machinesStartBeingAvailable.put(machine, tempDuration);
            if (tempDuration > totalDuration) {
                totalDuration = tempDuration;
            }

            solutionPresenters.add(new SolutionPresenter(test.getName(), testStartingTime, machine));
        }

        chromosome.setFitness(totalDuration);
        chromosome.setOutput(getSolutionOutput());
    }

    private static boolean fitsAHole(Test test) {
        List<ScheduleHole> holesThatCanFitOnMachines = new ArrayList<>();
        boolean worksOnEveryMachine = test.getRequiredMachines().size() == 0;
        for (String holeMachine : machineHoles.keySet()) {
            if (worksOnEveryMachine || test.getRequiredMachines().contains(holeMachine)) {
                holesThatCanFitOnMachines.addAll(machineHoles.get(holeMachine));
            }
        }

        boolean worksOnEveryResource = test.getRequiredResources().size() == 0;
        if (!worksOnEveryResource) {
            List<ScheduleHole> holesThatCanFitOnResources = new ArrayList<>();
            for (String holeResource : resourceHoles.keySet()) {
                if (test.getRequiredResources().contains(holeResource)) {
                    holesThatCanFitOnResources.addAll(resourceHoles.get(holeResource));
                }
            }

            if (holesThatCanFitOnResources.isEmpty()) {
                return false;
            }

            ScheduleHole resourceHole = findHoleThatSatisfiesAllResourceHoles(holesThatCanFitOnResources);
            if (resourceHole == null) {
                return false;
            }

            for (ScheduleHole holeOnMachine : holesThatCanFitOnMachines) {
                if (resourceHole.fits(holeOnMachine)) {
                    deleteHole(resourceHole, holeOnMachine, test);
                    solutionPresenters.add(new SolutionPresenter(test.getName(), resourceHole.getStart(), resourceHole.getMachine()));
                    return true;
                }
            }
        } else {
            for (ScheduleHole holeOnMachine : holesThatCanFitOnMachines) {
                if (holeOnMachine.getDuration() >= test.getDuration()) {
                    if (holeOnMachine.getDuration() == test.getDuration()) {
                        machineHoles.get(holeOnMachine.getMachine()).remove(holeOnMachine);
                    } else {
                        holeOnMachine.setStart(holeOnMachine.getStart() + test.getDuration());
                    }
                    solutionPresenters.add(new SolutionPresenter(test.getName(), holeOnMachine.getStart(), holeOnMachine.getMachine()));
                    return true;
                }
            }
        }

        return false;
    }

    private static void deleteHole(ScheduleHole resourceHole, ScheduleHole holeOnMachine, Test test) {
        resourceHoles.get(resourceHole.getResource()).remove(resourceHole);

        // remove new holes in resources
        for (String resource : test.getRequiredResources()) {
            if (resourceHoles.containsKey(resource)) {
                List<ScheduleHole> iterationList = new ArrayList<>(resourceHoles.get(resource));
                for (ScheduleHole hole : iterationList) {
                    resourceHoles.get(resource).remove(hole);
                    List<ScheduleHole> newHoles = newScheduleHoles(resourceHole, hole);
                    if (newHoles != null) {
                        resourceHoles.get(resource).addAll(newHoles);
                    }
                }
            }
        }

        List<ScheduleHole> newHoles = newScheduleHoles(resourceHole, holeOnMachine);
        machineHoles.get(holeOnMachine.getMachine()).remove(holeOnMachine);
        if (newHoles != null) {
            machineHoles.get(holeOnMachine.getMachine()).addAll(newHoles);
        }

    }

    private static List<ScheduleHole> newScheduleHoles(ScheduleHole addedHole, ScheduleHole holeThatWIllBeBroken) {
        List<ScheduleHole> newHoles = new ArrayList<>();

        ScheduleHole newHole;
        if (addedHole.getStart() == holeThatWIllBeBroken.getStart()) {
            if (addedHole.getEnd() == holeThatWIllBeBroken.getEnd()) {
                return null;
            } else {
                newHole = new ScheduleHole(addedHole.getEnd(), holeThatWIllBeBroken.getEnd(),
                        holeThatWIllBeBroken.getMachine(), holeThatWIllBeBroken.getResource());
                newHoles.add(newHole);
            }
        } else if (addedHole.getEnd() == holeThatWIllBeBroken.getEnd()) {
            newHole = new ScheduleHole(holeThatWIllBeBroken.getStart(), addedHole.getStart(),
                    holeThatWIllBeBroken.getMachine(), holeThatWIllBeBroken.getResource());
            newHoles.add(newHole);
        } else {
            // two new holes are generated here
            newHole = new ScheduleHole(holeThatWIllBeBroken.getStart(), addedHole.getStart(),
                    holeThatWIllBeBroken.getMachine(), holeThatWIllBeBroken.getResource());
            newHoles.add(newHole);

            newHole = new ScheduleHole(addedHole.getEnd(), holeThatWIllBeBroken.getEnd(),
                    holeThatWIllBeBroken.getMachine(), holeThatWIllBeBroken.getResource());
            newHoles.add(newHole);
        }

        return newHoles;
    }

    /**
     * Find smallest hole and check if it fits in all other -> that is only place where test can fit
     * Other way null is returned.
     */
    private static ScheduleHole findHoleThatSatisfiesAllResourceHoles(List<ScheduleHole> holesThatCanFitOnResources) {
        int minDuration = Integer.MAX_VALUE;
        ScheduleHole smallestHole = holesThatCanFitOnResources.get(0);
        for (ScheduleHole hole : holesThatCanFitOnResources) {
            if (hole.getDuration() < minDuration) {
                minDuration = hole.getDuration();
                smallestHole = hole;
            }
        }

        for (ScheduleHole hole : holesThatCanFitOnResources) {
            if (!smallestHole.fits(hole)) {
                return null;
            }
        }

        return smallestHole;
    }

    private static String findFirstEmptyMachine(Collection<String> requiredMachines) {
        int minimalTime = Integer.MAX_VALUE;

        for (Map.Entry<String, Integer> machineAndStartingTime : machinesStartBeingAvailable.entrySet()) {
            if (machineAndStartingTime.getValue() < minimalTime && requiredMachines.contains(machineAndStartingTime.getKey())) {
                minimalTime = machineAndStartingTime.getValue();
            }
        }

        for (String machine : requiredMachines) {
            if (machinesStartBeingAvailable.get(machine) == minimalTime) {
                return machine;
            }
        }

        return null;
    }

    private static String findFirstEmptyMachine() {
        return findFirstEmptyMachine(machinesStartBeingAvailable.keySet());
    }

    private static int findTimeWithFreeResources(Test test, int testStartingTime, String machine) {
        List<String> requiredResources = test.getRequiredResources();
        int startOfResourcesBeingAvailable = 0;
        String currentResource = requiredResources.get(0);

        for (String resource : requiredResources) {
            int resourceTime = resourcesStartBeingAvailable.get(resource);
            if (resourceTime > startOfResourcesBeingAvailable) {
                startOfResourcesBeingAvailable = resourceTime;
                currentResource = resource;
            }
        }

        int finalStartTime = Math.max(startOfResourcesBeingAvailable,
                testStartingTime);

        checkIfMachineHoleIsCreated(requiredResources, testStartingTime, machine);
        checkIfResourceHoleIsCreated(finalStartTime, test, machine);

        for (String resource : requiredResources) {
            resourcesStartBeingAvailable.put(resource, startOfResourcesBeingAvailable + test.getDuration());
        }

        return finalStartTime;
    }

    private static void checkIfResourceHoleIsCreated(int finalStartTime, Test test, String machine) {
        ScheduleHole hole;
        List<String> usedResources = test.getRequiredResources();
        for (String resource : usedResources) {
            int resourceAvailableAt = resourcesStartBeingAvailable.get(resource);
            if (finalStartTime > resourceAvailableAt) {
                hole = new ScheduleHole(resourceAvailableAt, finalStartTime, machine, resource);
            } else {
                continue;
            }

            if (!resourceHoles.containsKey(resource)) {
                resourceHoles.put(resource, new ArrayList<>());
            }

            resourceHoles.get(resource).add(hole);
        }
    }

    private static void checkIfMachineHoleIsCreated(List<String> usedResources, int testStartingTime, String machine) {
        ScheduleHole hole;
        for (String resource : usedResources) {
            if (resourcesStartBeingAvailable.get(resource) < testStartingTime) {
                hole = new ScheduleHole(resourcesStartBeingAvailable.get(resource), testStartingTime, machine, resource);
            } else {
                return;
            }

            if (!machineHoles.containsKey(machine)) {
                machineHoles.put(machine, new ArrayList<>());
            }

            machineHoles.get(machine).add(hole);
        }
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
