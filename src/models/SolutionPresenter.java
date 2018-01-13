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

    public int getStartingTime() {
        return startingTime;
    }

    public void setStartingTime(int startingTime) {
        this.startingTime = startingTime;
    }

    public String getMachine() {
        return machine;
    }

    public void setMachine(String machine) {
        this.machine = machine;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    @Override
    public String toString() {
        return String.format("\'%s\',%d,\'%s\'.\n", testName, startingTime, machine);
    }
}
