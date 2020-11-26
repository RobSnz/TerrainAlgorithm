package Terrain;

public class Quadrant {

    private int value;
    private boolean visited;

    public Quadrant() {
        visited = false;
    }

    public int getValue() {
        return value;
    }

    public boolean getVisited() {
        return visited;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setVisited() {
        visited = true;
    }

    public void setFalse() {
        visited = false;
    }
}
