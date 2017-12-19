import genetic_algorithm.Algorithm;
import genetic_algorithm.FitnessCalculator;
import genetic_algorithm.Population;
import models.Test;
import sun.security.krb5.internal.ktab.KeyTabEntry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    private static final String INPUT_FILE_PATH = "Instance_Rasporedjivanje_testova/test_example.txt";

    private static List<Test> tests;
    private static List<String> machines;
    private static Map<String, Integer> resourceOccurrences;

    private static int numberOfTests;
    private static int numberOfMachines;
    private static int numberOfResources;

    private static final String TEST_NUMBER_KEY = "tests";
    private static final String MACHINE_NUMBER_KEY = "machines";
    private static final String RESOURCE_NUMBER_KEY = "resources";
    private static final String TEST_IDENTIFIER = "test";
    private static final String MACHINE_IDENTIFIER = "embedded_board";
    private static final String RESOURCE_IDENTIFIER = "resource";

    private static int lengthOfChromosome;
    private static int numberOfBitsForTime;
    private static int numberOfBitsForMachines;

    private static final int MAX_ITERATIONS = 1000;
    private static final int POPULATION_SIZE = 50;


    public static void main(String[] args) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(INPUT_FILE_PATH));
            tests = new ArrayList<>();
            machines = new ArrayList<>();
            resourceOccurrences = new HashMap<>();

            parse(lines);

            numberOfBitsForMachines = log2(numberOfMachines);
            numberOfBitsForTime = calculateBitsForTime();

            lengthOfChromosome = numberOfTests * (numberOfBitsForMachines + numberOfBitsForTime);

            run();
        } catch (IOException | NumberFormatException e) {
            System.out.print("Input file is invalid.");
            System.exit(-1);
        }
    }

    private static int calculateBitsForTime() {
        int totalTime = 0;
        for (Test test : tests) {
            totalTime += test.getDuration();
        }

        return log2(totalTime);
    }

    private static void run() {
        Population population = new Population(lengthOfChromosome, numberOfBitsForTime, numberOfBitsForMachines, POPULATION_SIZE, true, tests);
        int iterations = 0;

        System.out.println(population.getPopulationSize());

        while (iterations < MAX_ITERATIONS) {
            iterations++;
            System.out.println("Generacija: " + iterations + " Dobrota: " + population.getBestChromosome().getFitness(tests));
            if (iterations % 50 == 0) {
                System.out.println(population.getBestChromosome());
            }
            population = Algorithm.evolve(population, lengthOfChromosome, numberOfBitsForTime, numberOfBitsForMachines, tests);
        }

        iterations++;

        System.out.println("Generacija: " + iterations + " Dobrota: " + population.getBestChromosome().getFitness(tests));
        System.out.println("GOTOVO!");
        System.out.println("Generacija: " + iterations);
        System.out.println("Genom:");
        System.out.println(population.getBestChromosome());

    }

    private static void parse(List<String> lines) {
        for (String line : lines) {
            if (line.contains(TEST_NUMBER_KEY)) {
                numberOfTests = Integer.parseInt(line.split(":")[1].trim());
            } else if (line.contains(MACHINE_NUMBER_KEY)) {
                numberOfMachines = Integer.parseInt(line.split(":")[1].trim());
            } else if (line.contains(RESOURCE_NUMBER_KEY)) {
                numberOfResources = Integer.parseInt(line.split(":")[1].trim());
            } else if (line.startsWith(TEST_IDENTIFIER)) {
                Test newTest = parseTestLine(line);
                tests.add(newTest);
            } else if (line.startsWith(MACHINE_IDENTIFIER)) {
                machines.add(line.split("\'")[1].trim());
            } else if (line.startsWith(RESOURCE_IDENTIFIER)) {
                String name = line.split("\'")[1].trim();
                int occurrence = Integer.parseInt(line.substring(line.indexOf(",") + 1, line.indexOf(")")).trim());
                resourceOccurrences.put(name, occurrence);
            }
        }
    }

    private static Test parseTestLine(String line) {
        String name = line.split("\'")[1];
        int duration = Integer.parseInt(line.split(",")[1].trim());
        String requiredMachinesString = line.substring(line.indexOf("[") + 1, line.indexOf("]"));
        String resourcesString = line.substring(line.lastIndexOf("[") + 1, line.lastIndexOf("]"));

        List<String> requiredMachines = getRequiredList(requiredMachinesString);
        List<String> requiredResources = getRequiredList(resourcesString);

        return new Test(name, duration, requiredMachines, requiredResources);
    }

    private static List<String> getRequiredList(String requiredString) {
        List<String> newList = new ArrayList<>();

        for (String element : requiredString.split(",")) {
            element = element.trim().replaceAll("'", "");
            newList.add(element);
        }

        return newList;
    }

    private static int log2(int value) {
        return Integer.SIZE-Integer.numberOfLeadingZeros(value);
    }
}
