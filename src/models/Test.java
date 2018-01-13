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

    public void setName(String name) {
        this.name = name;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setRequiredMachines(List<String> requiredMachines) {
        this.requiredMachines = requiredMachines;
    }

    public void setRequiredResources(List<String> requiredResources) {
        this.requiredResources = requiredResources;
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

        if (duration != test.duration) return false;
        if (name != null ? !name.equals(test.name) : test.name != null) return false;
        if (requiredMachines != null ? !requiredMachines.equals(test.requiredMachines) : test.requiredMachines != null)
            return false;
        return requiredResources != null ? requiredResources.equals(test.requiredResources) : test.requiredResources == null;
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
