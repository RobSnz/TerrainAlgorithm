package Terrain;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

public class Grid {

    private Quadrant[][] boxes;
    private Random rand;

    private int numRows;
    private int numCols;
    private int boxSize;
    private int storedMoves;

    public Grid(int row, int col) {
        this.numRows = row;
        this.numCols = col;

        rand = new Random();

        boxes = new Quadrant[numRows][numCols];

        for(int i = 0; i < numRows; i++) {
            for(int j = 0; j < numCols; j++) {
                boxes[i][j] = new Quadrant();
            }
        }
    }

    public int getNumCols() {
        return numCols;
    }

    public int getNumRows() {
        return numRows;
    }

    public int getBoxSize() {
        return boxSize;
    }

    public int getQuadrantValue(int x, int y) {
        return boxes[x][y].getValue();
    }

    public int getStoredMoves() {
        return storedMoves;
    }

    public boolean getBoxVisited(int row, int col) {
        return boxes[row][col].getVisited();
    }

    public void setQuadrantValue(int x, int y, int value) {
        boxes[y][x].setValue(value);
    }

    public void setBoxSize(int size) {
        boxSize = size;
    }

    public void setStoredMoves(int moves, boolean reset) {
        if(reset) {
            storedMoves = moves;
        } else {
            storedMoves += moves;
        }
    }

    public void setBoxVisited(int row, int col) {
        boxes[row][col].setVisited();
    }

    public void drawGrid(Graphics g) {
        int offsetX = 5;
        int offsetY = 5;

        for(int x = 0; x < numRows; x++) {
            for(int y = 0; y < numCols; y++) {
                if(boxes[x][y].getVisited()) {
                    g.setColor(Color.CYAN);
                    g.fillRect(offsetX, offsetY, boxSize, boxSize);
                } else if(boxes[x][y].getValue() <= 0) {
                    g.setColor(new Color(76, 255, 0));
                    g.fillRect(offsetX, offsetY, boxSize, boxSize);
                } else if(boxes[x][y].getValue() <= 5) {
                    g.setColor(new Color(120, 193, 0));
                    g.fillRect(offsetX, offsetY, boxSize, boxSize);
                } else if(boxes[x][y].getValue() <= 10) {
                    g.setColor(new Color(255, 123, 0));
                    g.fillRect(offsetX, offsetY, boxSize, boxSize);
                } else if(boxes[x][y].getValue() > 10) {
                    g.setColor(new Color(255, 0, 0));
                    g.fillRect(offsetX, offsetY, boxSize, boxSize);
                }

                g.setColor(Color.BLACK);
                g.drawRect(offsetX, offsetY, boxSize, boxSize);
                g.drawString(Integer.toString(boxes[x][y].getValue()), offsetX + boxSize / 2 - 5, offsetY + boxSize / 2 + 5);

                offsetX += boxSize;
            }
            offsetY += boxSize;
            offsetX = 5;
        }
    }

    void resetVisited() {
        for(int i = 0; i < numRows; i++) {
            for(int j = 0; j < numCols; j++) {
                boxes[i][j].setFalse();
            }
        }
    }
}
