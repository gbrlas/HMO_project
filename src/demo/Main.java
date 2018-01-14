package demo;

import genetic_algorithm.Algorithm;
import genetic_algorithm.Permutation;
import genetic_algorithm.Population;
import models.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {

    private static final String GENERIC_INPUT_FILE_PATH = "Instance_Rasporedjivanje_testova/ts";
    private static final String OUTPUT_FOLDER_PATH = "solutions/";
    private static final int NUMBER_OF_PROBLEMS = 10;

    private static final int ONE_MINUTE = 60 * 1000;
    private static final int FIVE_MINUTES = 5 * ONE_MINUTE;


    public static List<Test> tests;
    public static List<String> machines;
    public static Map<String, Integer> resourceOccurrences;

    private static int numberOfTests;

    private static final String TEST_NUMBER_KEY = "tests";
    private static final String TEST_IDENTIFIER = "test";
    private static final String MACHINE_IDENTIFIER = "embedded_board";
    private static final String RESOURCE_IDENTIFIER = "resource";

    private static Permutation best;

    private static double fitnessAtOneMinute;
    private static double fitnessAtFiveMinutes;
    private static double fitnessAtIterationsFinished;

    private static int i;

    /**
     * Hyper parameters
     */
    private static final int MAX_ITERATIONS = 20000;
    public static final int POPULATION_SIZE = 10;


    public static void main(String[] args) {
        try {
            for (i = 1; i <= 1; i++) {
                String inputFileName = GENERIC_INPUT_FILE_PATH + i + ".txt";
                List<String> lines = Files.readAllLines(Paths.get(inputFileName));
                tests = new ArrayList<>();
                machines = new ArrayList<>();
                resourceOccurrences = new HashMap<>();

                parse(lines);

                // write after 1 minute
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        String fileName = OUTPUT_FOLDER_PATH + "res-1m-ts" + i + ".txt";
                        try {
                            printSolutionsInOutputFile(fileName);
                            fitnessAtOneMinute = best.getFitness();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }, ONE_MINUTE);

                // write after 5 minutes
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        String fileName = OUTPUT_FOLDER_PATH + "res-5m-ts" + i + ".txt";
                        try {
                            printSolutionsInOutputFile(fileName);
                            fitnessAtFiveMinutes = best.getFitness();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }, FIVE_MINUTES);

                String fileNameWithoutTimeLimits = OUTPUT_FOLDER_PATH + "res-ne-ts" + i + ".txt";

                System.out.println("Size of population: " + POPULATION_SIZE + "\n");
                System.out.println("Input file: " + inputFileName);
                run(fileNameWithoutTimeLimits);
            }
        } catch (IOException | NumberFormatException e) {
            System.out.print("Input file is invalid.");
            System.exit(-1);
        }
    }

    private static void printSolutionsInOutputFile(String fileName) throws IOException {
        File file = new File(fileName);

        FileWriter outFile;
        try {
            outFile = new FileWriter(file);
            final PrintWriter out = new PrintWriter(outFile);

            out.println(best.getOutput());
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void run(String fileName) {
        Population population = new Population(POPULATION_SIZE, numberOfTests);
        int iterations = 0;

        while (iterations < MAX_ITERATIONS) {
            iterations++;

            population = Algorithm.evolve(population, numberOfTests);
            population.decodeAllChromosomes();

            best = population.getBestChromosome();

            if (iterations % 50 == 0) {
                System.out.println(population.getBestChromosome().getOutput());
                System.out.println("Generation: " + iterations + " Fitness: " + best.getFitness());
            }
        }

        best = population.getBestChromosome();
        fitnessAtIterationsFinished = best.getFitness();

        System.out.println("FINISHED!");
        System.out.println("Generation: " + iterations);
        System.out.println("Genome:");
        System.out.println(best.getOutput());
        System.out.println("Generation: " + iterations + " Fitness: " + best.getFitness());


        try {
            printSolutionsInOutputFile(fileName);
            printFitnessInFile(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printFitnessInFile(String fileName) {
        File file = new File(OUTPUT_FOLDER_PATH + "fitnesses.txt");

        FileWriter outFile;
        try {
            outFile = new FileWriter(file, true);
            final PrintWriter out = new PrintWriter(outFile);
            String output = fileName + "\n" + "1 minute: " + fitnessAtOneMinute + "\n" + "5 minutes: "
                    + fitnessAtFiveMinutes + "\n" + "Final fitness: " + fitnessAtIterationsFinished + "\n";
            out.println(output);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void parse(List<String> lines) {
        for (String line : lines) {
            if (line.contains(TEST_NUMBER_KEY)) {
                numberOfTests = Integer.parseInt(line.split(":")[1].trim());
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
