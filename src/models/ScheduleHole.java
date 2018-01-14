package models;

public class ScheduleHole {
    private int start;
    private int end;
    private String machine;
    private String resource;

    public ScheduleHole(int start, int end, String machine, String resource) {
        this.start = start;
        this.end = end;
        this.machine = machine;
        this.resource = resource;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public int getDuration() {
        return end - start;
    }

    public String getMachine() {
        return machine;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public boolean fits(ScheduleHole other) {
        return start >= other.start && end <= other.end;

    }
}
