/* *****************************************************************************
 *  Name:    Eugene Rebedailo
 *  NetID:   marengohue
 *  Precept: P00
 *
 *  Description: Assignment 1 for Prinston's Algorithms course on Coursera
 *
 ******************************************************************************/

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {

    // A union-find model for an N*N grid
    // has additional two nodes (N*N and N*N+1) representing
    // the nodes above and below the grid, respectively
    private final WeightedQuickUnionUF percolationUnionModel;

    // A union-find model for an N*N grid with a single extra node on top
    // Used to eliminate backwash
    private final WeightedQuickUnionUF floodUnionModel;

    // A grid of boolean values showing if the grid cell is open or not
    private final boolean[] openGridCells;

    // Size of the square grid
    private final int gridSize;

    // Current amount of the open sites in the grid
    private int openSiteCount;

    // creates n-by-n grid, with all sites initially blocked
    public Percolation(int n) {
        if (n < 1) throw new IllegalArgumentException();
        percolationUnionModel = new WeightedQuickUnionUF(n * n + 2);
        floodUnionModel = new WeightedQuickUnionUF(n * n + 1);
        openGridCells = new boolean[n * n];
        gridSize = n;
    }

    // Get the array index in the UF model based on X,Y coordinates of the site
    private int getIndex(int row, int column) {
        if (row == 0) {
            return gridSize * gridSize;
        }
        if (row == gridSize + 1) {
            return gridSize * gridSize + 1;
        }
        return (row - 1) * gridSize + (column - 1);
    }

    // opens the site (row, col) if it is not open already
    public void open(int row, int col) {
        validateIndicies(row, col);
        if (isOpenInternal(row, col)) return;
        int thisIndex = getIndex(row, col);
        if (isOpenInternal(row + 1, col)) {
            percolationUnionModel.union(thisIndex, getIndex(row + 1, col));
            if (row < gridSize) {
                floodUnionModel.union(thisIndex, getIndex(row + 1, col));
            }
        }
        if (isOpenInternal(row - 1, col)) {
            percolationUnionModel.union(thisIndex, getIndex(row - 1, col));
            floodUnionModel.union(thisIndex, getIndex(row - 1, col));
        }
        if (isOpenInternal(row, col + 1)) {
            percolationUnionModel.union(getIndex(row, col), getIndex(row, col + 1));
            floodUnionModel.union(thisIndex, getIndex(row, col + 1));
        }
        if (isOpenInternal(row, col - 1)) {
            percolationUnionModel.union(getIndex(row, col), getIndex(row, col - 1));
            floodUnionModel.union(thisIndex, getIndex(row, col - 1));
        }
        openSiteCount++;
        openGridCells[getIndex(row, col)] = true;
    }

    // Throws if the row/col are out of valid bounds
    private void validateIndicies(int row, int col) {
        if (row > gridSize || row < 1 || col > gridSize || col < 1) {
            throw new IllegalArgumentException(row + " " + col);
        }
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        validateIndicies(row, col);
        return isOpenInternal(row, col);
    }

    // Internally-used isOpen that assumes the following:
    // A row above the first only contains the very top of UF model
    // A row belove the last only contains the very bottom of it
    // All of the left and right walls of the simulation are always closed
    // (Helps with handling corner cases)
    private boolean isOpenInternal(int row, int col) {
        if (row == 0 || row == gridSize + 1) return true;
        if (col < 1 || col > gridSize) return false;
        return openGridCells[getIndex(row, col)];
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        validateIndicies(row, col);
        if (!isOpen(row, col)) return false;
        return floodUnionModel.connected(
                gridSize * gridSize,
                getIndex(row, col)
        );
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return openSiteCount;
    }

    // does the system percolate?
    public boolean percolates() {
        return percolationUnionModel.connected(
                gridSize * gridSize,
                gridSize * gridSize + 1);
    }
}
