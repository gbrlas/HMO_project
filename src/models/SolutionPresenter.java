package models;

public class SolutionPresenter {
    private int startingTime;
    private String machine;

    private String testName;

    public SolutionPresenter(String testName, int startingTime, String machine) {
        this.startingTime = startingTime;
        this.machine = machine;
        this.testName = testName;
    }

    @Override
    public String toString() {
        return String.format("\'%s\',%d,\'%s\'.\n", testName, startingTime, machine);
    }
}
