package genetic_algorithm;
import java.util.*;


public class Permutation {
    private int[] permutation;
    private Random rnd = new Random();
    private int fitness;
    private String output;

    Permutation(int n) {
        permutation = new int[n];

        ArrayList<Integer> numbers = new ArrayList<>();
        for (int i = 0; i < n; ++i) {
            numbers.add(i);
        }

        for (int i = 0; i < n; ++i) {
            int index = rnd.nextInt(numbers.size());
            permutation[i] = numbers.get(index);
            numbers.remove(index);
        }
    }

    private Permutation(int[] permutation) {
        this.permutation = permutation;
        for (int i = 0; i < permutation.length; ++i) {
            permutation[i] %= permutation.length;
        }
    }

    int[] getPermutation() {
        return permutation;
    }

    private int getValue(int i) {
        return permutation[i];
    }

    private int getSize() {
        return permutation.length;
    }

    Permutation[] crossover(Permutation second) {
        if (permutation.length != second.getSize()) {
            throw new IllegalArgumentException("Permutation size does not match");
        }

        int n = permutation.length;
        ArrayList<Integer> splits = getSplits(n, 5);
        HashMap<Integer, Integer> mapping1 = new HashMap<>();
        HashMap<Integer, Integer> mapping2 = new HashMap<>();
        int[] child1 = this.getPermutation();
        int[] child2 = second.getPermutation();
        boolean[] changed = new boolean[n];

        int lowerBound = 0;

        for (Integer split : splits) {
            int upperBound = split;
            boolean changing = rnd.nextBoolean();
            for (int j = lowerBound; j < upperBound; ++j) {
                changed[j] = changing;
                if (changing) {
                    int value1 = child1[j];
                    int value2 = child2[j];
                    child2[j] = value1;
                    child1[j] = value2;
                    mapping1.put(value1, value2);
                    mapping2.put(value2, value1);
                }
            }
            //change area
            lowerBound = upperBound;
        }

        translateDuplicates(child1, mapping2, changed);
        translateDuplicates(child2, mapping1, changed);

        mutate(Algorithm.mutation, child1);
        mutate(Algorithm.mutation, child2);

        Permutation[] ret = new Permutation[2];
        ret[0] = new Permutation(child1);
        ret[1] = new Permutation(child2);

        return ret;
    }

    private void mutate(double chance, int[] chromosomes) {
        double probability = rnd.nextDouble();
        if (chance > probability) {
            int index1 = rnd.nextInt(chromosomes.length);
            int index2 = rnd.nextInt(chromosomes.length);
            int tmp = chromosomes[index1];
            chromosomes[index1] = chromosomes[index2];
            chromosomes[index2] = tmp;
        }
    }

    /**
     * Split in array accurs before some number eg:
     * split = 1:
     * index:  0 1 2 3
     * array: [i|j,k,p]
     *
     * @param n
     * @param parts
     * @return
     */
    private ArrayList<Integer> getSplits(int n, int parts) {
        ArrayList<Integer> numbers = new ArrayList<>();
        ArrayList<Integer> splits = new ArrayList<>();
        for (int i = 1; i < n; ++i)
            numbers.add(i);

        parts = Math.max(1, parts);
        parts = Math.min(parts, n);
        parts -= 1;

        for (int i = 0; i < parts; ++i) {
            int index = rnd.nextInt(numbers.size());
            int splitPlace = numbers.get(index);
            numbers.remove(index);
            splits.add(splitPlace);
        }

        splits.add(n);
        Collections.sort(splits);

        return splits;
    }

    private ArrayList<Integer> getSplits2(int n, int parts) {
        ArrayList<Integer> ret = new ArrayList<>();
        int step = n / parts;
        int curr = step;
        while (curr <= n) {
            ret.add(curr);
            curr += step;
        }

        return ret;
    }

    private void translateDuplicates(int[] p,
                                     HashMap<Integer, Integer> mapping,
                                     boolean[] changed) {
        for (int i = 0; i < p.length; ++i) {
            if (!changed[i]) {
                while (mapping.containsKey(p[i])) {
                    p[i] = mapping.get(p[i]);
                }
            }
        }
    }

    /**
     * Checks whether permutation has 2 same values
     *
     * @return
     */
    public boolean isValid() {
        HashMap<Integer, Integer> bucket = new HashMap<>();

        for (int aPermutation : permutation) {
            if (bucket.containsKey(aPermutation))
                return false;
            bucket.put(aPermutation, 1);
        }
        return true;
    }

    public boolean equals(Permutation second) {
        for (int i = 0; i < permutation.length; ++i) {
            if (permutation[i] != second.getValue(i))
                return false;
        }
        return true;
    }

    public String toString() {
        StringBuilder ret = new StringBuilder("[");
        for (int aPermutation : permutation) {
            ret.append(Integer.toString(aPermutation + 1)).append(", ");
        }
        return ret.substring(0, ret.length() - 2) + "]";
    }

    public void setFitness(int fitness) {
        this.fitness = fitness;
    }

    public double getFitness() {
        return fitness;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }
}
