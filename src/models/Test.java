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
}
