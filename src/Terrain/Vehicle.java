package Terrain;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class Vehicle implements KeyListener, Runnable {

    private final Grid grid;
    private final String imageName;

    private BufferedImage image;

    private ArrayList<String> bestPath;

    private int[][] addedQuadrants;

    private int row, col, rowNumber, colNumber, startPoint, visibleRows, currentPosition;

    private double percentage;

    private boolean isRunning, canDraw;

    public Vehicle(Grid grid) { // Initialises manual vehicle
        grid.resetVisited();
        this.grid = grid;
        imageName = "Jeep.png";
        canDraw = true;
    }

    public Vehicle(Grid grid, int intelligence) { // Initialises automated vehicle
        grid.resetVisited();
        this.grid = grid;
        imageName = "Jeep.png";
        canDraw = false;
        row = Integer.MAX_VALUE;
        col = Integer.MAX_VALUE;

        setupVehicleType(intelligence);

        Thread t = new Thread(this);

        t.start();
    }

    private void setupVehicleType(int intelligence) { // Sets up information regarding the intelligence of the vehicle
        switch(intelligence) {
            case 0:
                percentage = .00;
                break;
            case 20:
                percentage = .20;
                break;
            case 40:
                percentage = .40;
                break;
            case 60:
                percentage = .60;
                break;
            case 80:
                percentage = .80;
                break;
            case 100:
                percentage = 1.00;
                break;
            default:
                break;
        }

        visibleRows = (int) (percentage * grid.getNumRows());
        addedQuadrants = new int[visibleRows][grid.getNumCols()];
        currentPosition = grid.getNumRows() + 1;

        isRunning = true;
        bestPath = new ArrayList<>();
    }

    public boolean vehicleCanDraw() { // Method for the GUI to call basically which allows it to place the automated vehicle only after its position has been set
        return canDraw;
    }

    public void setPosition(int row, int col) { // Sets where the vehicle is in the row and calculates the position the draw method should place it
        this.row = row;
        this.col = col;

        rowNumber = row * grid.getBoxSize();
        colNumber = col * grid.getBoxSize();

        grid.setStoredMoves(grid.getQuadrantValue(row, col), false);
        grid.setBoxVisited(row, col);
    }

    private void calculateNewGrid() { // Calculates the values of best possible path for the new grid which the vehicle can see
        System.out.println("Calculating Grid:");

        int topBoxNumber = currentPosition - visibleRows - 1;

        for(int j = 0; j < addedQuadrants[0].length; j++) {
            if(topBoxNumber >= 0) {
                addedQuadrants[0][j] = grid.getQuadrantValue(topBoxNumber, j);
            } else {
                addedQuadrants[0][j] = Integer.MAX_VALUE;
            }
        }

        System.out.println("Top box number:" + topBoxNumber);

        for(int i = 1; i < addedQuadrants.length; i++) {
            for(int j = 0; j < addedQuadrants[i].length; j++) {
                int lowestNumber = Integer.MAX_VALUE;

                for(int k = -1; k <= 1; k++) {
                    if(j + k < 0 || j + k >= grid.getNumCols()) {
                        continue;
                    }

                    if(addedQuadrants[i - 1][j + k] < lowestNumber) {
                        lowestNumber = addedQuadrants[i - 1][j + k];
                    }
                }

                if(lowestNumber == Integer.MAX_VALUE) {
                    lowestNumber = 0;
                }

                if(i + topBoxNumber >= 0) {
                    addedQuadrants[i][j] = lowestNumber + grid.getQuadrantValue(i + topBoxNumber, j);
                } else {
                    addedQuadrants[i][j] = Integer.MAX_VALUE;
                }
            }
        }
        printGrid();
    }

    private void printGrid() { // Prints visual representation of calculated grid into the console
        for(int i = 0; i < addedQuadrants.length; i++) {
            for(int j = 0; j < addedQuadrants[i].length; j++) {
                System.out.print(addedQuadrants[i][j]);

                if(addedQuadrants[i][j] >= 0 && addedQuadrants[i][j] <= 9) {
                    System.out.print("   ");
                } else if(addedQuadrants[i][j] <= -10) {
                    System.out.print(" ");
                } else if(addedQuadrants[i][j] >= 10 || addedQuadrants[i][j] <= -1) {
                    System.out.print("  ");
                }
                if(j != addedQuadrants[i].length - 1) {
                    System.out.print("|");
                }
            }
            System.out.println("");
        }
    }

    private void intelligencePath() { // Handles where the vehicle should move next if it has a level of intelligence
        calculateNewGrid(); // Sets up grid for traversal

        int lowestNumber = Integer.MAX_VALUE;

        for(int j = 0; j < addedQuadrants[0].length; j++) { // Iterates through the calculated grid to find the lowest number to start at
            if(addedQuadrants[addedQuadrants.length - 1][j] < lowestNumber) {
                lowestNumber = addedQuadrants[addedQuadrants.length - 1][j];
                startPoint = j;
                System.out.println("Start point: " + j);
            }
        }

        System.out.println("Lowest Number: " + lowestNumber);

        int lastPosition = startPoint;
        int nextPosition = 0;

        if(percentage < 1) {
            currentPosition--;
            calculateNewGrid();
        } else {
            visibleRows--;
        }

        for(int i = grid.getNumRows() - 1; i > 0; i--) {
            lowestNumber = Integer.MAX_VALUE;

            for(int j = lastPosition - 1; j <= lastPosition + 1; j++) {
                if(j < 0 || j >= grid.getNumCols()) {
                    continue;
                }

                if(addedQuadrants[visibleRows - 1][j] < lowestNumber) {
                    lowestNumber = addedQuadrants[visibleRows - 1][j];
                    nextPosition = j;
                }
            }

            if(nextPosition < lastPosition) {
                bestPath.add("Left");
            } else if(nextPosition == lastPosition) {
                bestPath.add("Up");
            } else {
                bestPath.add("Right");
            }

            lastPosition = nextPosition;
            nextPosition = 0;

            if(percentage < 1) {
                currentPosition--;
                calculateNewGrid();
            } else {
                visibleRows--;
            }
        }

        setPosition(grid.getNumRows() - 1, startPoint);
        canDraw = true;
    }

    private void greedyPath() { // Method used if the intelligence is 0% or the vehicle can only see 0 rows
        int numRows = grid.getNumRows();
        int lowestNumber = grid.getQuadrantValue(numRows - 1, 0);

        for(int i = 1; i < grid.getNumCols(); i++) {
            if(grid.getQuadrantValue(numRows - 1, i) < lowestNumber) {
                lowestNumber = grid.getQuadrantValue(numRows - 1, i);
                startPoint = i;
            }
        }

        int lastPosition = startPoint;
        int nextPosition = 0;

        for(int i = numRows - 2; i >= 0; i--) {

            lowestNumber = Integer.MAX_VALUE;

            for(int j = lastPosition - 1; j <= lastPosition + 1; j++) {
                if(j < 0 || j >= grid.getNumCols()) {
                    continue;
                }

                if(grid.getQuadrantValue(i, j) < lowestNumber) {
                    lowestNumber = grid.getQuadrantValue(i, j);
                    nextPosition = j;
                }
            }

            if(nextPosition < lastPosition) {
                bestPath.add("Left");
            } else if(nextPosition == lastPosition) {
                bestPath.add("Up");
            } else {
                bestPath.add("Right");
            }

            lastPosition = nextPosition;
            nextPosition = 0;
        }

        setPosition(numRows - 1, startPoint);
        canDraw = true;
    }

    public void drawVehicle(Graphics g) { // Sends information to the GUI about where to draw the vehicle
        try {
            image = ImageIO.read(new File(imageName));
            g.drawImage(image, colNumber + 10, rowNumber + 10, grid.getBoxSize() - 10, grid.getBoxSize() - 10, null);
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    private void move() { // Handles where the automated vehicle should move next
        if(bestPath.isEmpty()) {
            if(visibleRows == 0) {
                greedyPath();
            } else {
                intelligencePath();
            }
        } else {
            switch(bestPath.get(0)) {
                case "Left":
                    setPosition(row - 1, col - 1);
                    break;
                case "Up":
                    setPosition(row - 1, col);
                    break;
                default:
                    setPosition(row - 1, col + 1);
                    break;
            }
            bestPath.remove(0);
        }
    }

    @Override
    public void run() { // Runs the vehicle thread
        while(isRunning) {
            try {
                Thread.sleep(100);
                move();
            } catch(InterruptedException ex) {
                ex.printStackTrace();
            }

            if(row == 0) {
                isRunning = false;
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) { // Handles the moving of a manual vehicle
        switch(e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                if(row > 0 && col > 0) {
                    setPosition(row - 1, col - 1);
                }
                break;

            case KeyEvent.VK_UP:
                if(row > 0) {
                    setPosition(row - 1, col);
                }
                break;

            case KeyEvent.VK_RIGHT:
                if(row > 0 && col < grid.getNumCols()) {
                    setPosition(row - 1, col + 1);
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
