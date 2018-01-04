package utils;

import demo.Main;

import java.util.Arrays;

import static demo.Main.*;

public class ChromosomeUtils {

    public static int getMachine(byte[] genes, int startIndexInGene) {
        return MathHelper.getIntFromByteBinary(Arrays.copyOfRange(genes,
                startIndexInGene + numberOfBitsForTime, startIndexInGene
                        + numberOfBitsForTime + numberOfBitsForMachines)) % numberOfMachines + 1;
    }

    /**
     * Parses starting time from chromosome.
     */
    public static int getStartingTime(byte[] genes, int startIndexInGene) {
        return MathHelper.getIntFromByteBinary(Arrays.copyOfRange(genes,
                startIndexInGene, startIndexInGene + numberOfBitsForTime)) % totalTimeWorstScenario;
    }
}
