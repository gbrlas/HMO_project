import java.util.Arrays;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        int[][] acceptableTerms = new int[][] {
                {1},
                {0, 1, 2, 3, 4},
                {0, 2},
                {0, 3, 4},
                {0, 1, 2}
        };

        int numberOfCourses = acceptableTerms.length;
        int numberOfTerms = 5;

        long[] failures = new long[numberOfCourses];
        for (int i = 0; i < numberOfCourses; i++) {
            failures[i] = 0;
        }

        int[] courseOrder = new int[numberOfCourses];
        for (int i = 0; i < numberOfCourses; i++) {
            courseOrder[i]  = i;
        }

        Random rand = new Random();
        int[] courseTerm = new int[numberOfCourses];
        int[] terms = new int[numberOfTerms];
        int numberOfAttempts = 100000;

        // Pomocno polje za kontrolu odabranih termina
        boolean[] termUsed = new boolean[numberOfTerms];

        int failuresCount = 0;
        for (int i = 0; i < numberOfAttempts; i ++) {
            // Za sve kolegije postavi termine na nevaljale
            for (int k = 0; k < numberOfCourses; k++) {
                courseTerm[k] = -1;
            }

            // Ocisiti zastavice odabranih termina
            for (int k = 0; k < termUsed.length; k++) {
                termUsed[k] = false;
            }

            int failedCourse = -1;

            for (int k = 0; k < numberOfCourses; k++) {
                int courseIndex = courseOrder[k];
                int numberOFValidTerms = 0;

                // Pripremi polje s popisom valjalih preostalih termina
                for (int t = 0; t < acceptableTerms[courseIndex].length; t++) {
                    if (termUsed[acceptableTerms[courseIndex][t]]) {
                        continue;
                    }
                    terms[numberOFValidTerms] = acceptableTerms[courseIndex][t];
                    numberOFValidTerms++;
                }

                // Sada odaberi jedan od tih termina
                if (numberOFValidTerms == 0) {
                    failedCourse = courseIndex;
                    break;
                }

                int picked = terms[rand.nextInt(numberOFValidTerms)];
                courseTerm[courseIndex] = picked;
                termUsed[picked] = true;
            }

            if (failedCourse == - 1) {
                failedCourse = check(courseTerm, acceptableTerms);
            }
            if (failedCourse != -1) {
                failuresCount++;
                failures[failedCourse]++;

                // Pronadji poziciju na kojoj se nalazi kolegij na kojem je puklo
                int pos = 0;
                while (courseOrder[pos] != failedCourse) {
                    pos++;
                }

                while (pos > 0 && failures[courseOrder[pos]] > failures[courseOrder[pos - 1]]) {
                    int tmp = courseOrder[pos];
                    courseOrder[pos] = courseOrder[pos - 1];
                    courseOrder[pos - 1] = tmp;
                    pos--;
                }
            }

        }

        System.out.println("Uspjesnost stvaranja: " +
                ((double)(numberOfAttempts - failuresCount) / (double) numberOfAttempts * 100) +
                "%, broj uspjeha: " + (numberOfAttempts - failuresCount));
        System.out.println("Poredak kolegija na kraju: " + Arrays.toString(courseOrder) +
                ", failures=" + Arrays.toString(failures));
    }
}
