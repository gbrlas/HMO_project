package models;

import java.util.List;

public class Test {

    private String name;
    private int duration;
    private List<String> requiredMachines;
    private List<String> requiredResources;

    public Test(String name, int duration, List<String> requiredMachines, List<String> requiredResources) {
        this.name = name;
        this.duration = duration;
        this.requiredMachines = requiredMachines;
        this.requiredResources = requiredResources;
    }

    public String getName() {
        return name;
    }

    public List<String> getRequiredResources() {
        return requiredResources;
    }

    @Override
    public String toString() {
        String requiredMachinesString = String.join(",", requiredMachines);
        String requiredResourcesString = String.join(",", requiredResources);

        return String.format("\'%s\', %d, required machines: %s, resources: %s",
                name, duration, requiredMachinesString, requiredResourcesString);
    }

    public int getDuration() {
        return duration;
    }

    public List<String> getRequiredMachines() {
        return requiredMachines;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Test test = (Test) o;

        return duration == test.duration
                && (name != null ? name.equals(test.name) : test.name == null)
                && (requiredMachines != null ? requiredMachines.equals(test.requiredMachines) : test.requiredMachines == null)
                && (requiredResources != null ? requiredResources.equals(test.requiredResources) : test.requiredResources == null);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + duration;
        result = 31 * result + (requiredMachines != null ? requiredMachines.hashCode() : 0);
        result = 31 * result + (requiredResources != null ? requiredResources.hashCode() : 0);
        return result;
    }
}
