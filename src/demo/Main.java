package demo;

import genetic_algorithm.Algorithm;
import genetic_algorithm.Permutation;
import genetic_algorithm.Population;
import models.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Main {

    private static final String GENERIC_INPUT_FILE_PATH = "Instance_Rasporedjivanje_testova/ts";
    private static final String OUTPUT_FOLDER_PATH = "solutions";
    private static final int NUMBER_OF_PROBLEMS = 10;

    private static final int ONE_MINUTE = 5*1000;
    private static final int FIVE_MINUTE = 5 * ONE_MINUTE;


    public static List<Test> tests;
    public static List<String> machines;
    public static Map<String, Integer> resourceOccurrences;

    private static int numberOfTests;
    public static int numberOfMachines;
    private static int numberOfResources;

    private static final String TEST_NUMBER_KEY = "tests";
    private static final String MACHINE_NUMBER_KEY = "machines";
    private static final String RESOURCE_NUMBER_KEY = "resources";
    private static final String TEST_IDENTIFIER = "test";
    private static final String MACHINE_IDENTIFIER = "embedded_board";
    private static final String RESOURCE_IDENTIFIER = "resource";

    private static Permutation best;

    /**
     * Hyper parameters
     */
    private static final int MAX_ITERATIONS = 10000;
    public static final int POPULATION_SIZE = 10;


    public static void main(String[] args) {
        try {
            for (int i = 1; i <= NUMBER_OF_PROBLEMS; i++) {
                List<String> lines = Files.readAllLines(Paths.get(GENERIC_INPUT_FILE_PATH + i + ".txt"));
                tests = new ArrayList<>();
                machines = new ArrayList<>();
                resourceOccurrences = new HashMap<>();

                parse(lines);

                Timer timer = new Timer();
                int finalI = i;
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            System.out.println("AAAAAAAAAAAAAA");
                            printSolutionsInOutputFile(finalI);
                            System.exit(-1);
                        } catch (IOException e) {
                            System.out.print("Error while print to file");
                            System.exit(-1);
                        }
                    }
                }, ONE_MINUTE);

                run();
            }
        } catch (IOException | NumberFormatException e) {
            System.out.print("Input file is invalid.");
            System.exit(-1);
        }
    }

    private static void printSolutionsInOutputFile(int indexOfTask) throws IOException {
        List<String> lines = Arrays.asList(best.toString());
        Path file = Paths.get(OUTPUT_FOLDER_PATH + "/res-1m-" + indexOfTask + ".txt");
        Files.write(file, lines, Charset.forName("UTF-8"));
    }

    private static void run() {
        Population population = new Population(POPULATION_SIZE, numberOfTests);
        int iterations = 0;

        System.out.println("Size of population: " + population.getPopulationSize());

        while (iterations < MAX_ITERATIONS) {
            iterations++;

            population = Algorithm.evolve(population, numberOfTests);
            population.decodeAllChromosomes();

            if (iterations % 50 == 0) {
                System.out.println(population.getBestChromosome().getOutput());
                System.out.println("Generation: " + iterations + " Fitness: " + population.getBestChromosome().getFitness());
            }
        }

        best = population.getBestChromosome();

        System.out.println("FINISHED!");
        System.out.println("Generation: " + iterations);
        System.out.println("Genome:");
        System.out.println(best.getOutput());
        System.out.println("Generation: " + iterations + " Fitness: " + best.getFitness());

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

        String[] machines = requiredString.split(",");
        if (machines[0].equals("")) {
            return newList;
        }

        for (String element : machines) {
            element = element.trim().replaceAll("'", "");
            newList.add(element);
        }

        return newList;
    }
}
