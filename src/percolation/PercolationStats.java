/* *****************************************************************************
 *  Name:    Eugene Rebedailo
 *  NetID:   marengohue
 *  Precept: P00
 *
 * Description: Second part of the Assignment 1 for Prinston's
 * Algorithms course on Coursera
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {

    // Confidence factor used to calculate 95% confidence interval
    private static final double CONFIDENCE_95 = 1.96d;

    // Size of the grid to test
    private final int gridSize;

    // Trial count for the stats collector
    private final int trials;

    // Experiment results
    private final double[] experiments;

    // cached mean
    private Double meanValue = null;

    // cached stddev
    private Double stddevValue = null;

    // cached confidenceHi
    private Double confidenceHiValue = null;

    // cached confidenceLo
    private Double confidenceLoValue = null;

    // Creates the percolation stats obeject and runs the calculation
    public PercolationStats(int n, int trials) {
        if (n <= 0 || trials <= 0) {
            throw new IllegalArgumentException();
        }
        gridSize = n;
        this.trials = trials;
        experiments = new double[trials];
        for (int i = 0; i < trials; i++) {
            experiments[i] = runSingleExperiment();
        }
    }

    // Executes one percolation test and returns the percolation threshold
    private double runSingleExperiment() {
        Percolation p = new Percolation(gridSize);
        while (!p.percolates()) {
            p.open(StdRandom.uniform(1, gridSize + 1), StdRandom.uniform(1, gridSize + 1));
        }
        return 1.0d * p.numberOfOpenSites() / (gridSize * gridSize);
    }

    // Returns mean percolation threshold after T attempts
    public double mean() {
        if (meanValue == null) {
            meanValue = StdStats.mean(experiments);
        }
        return meanValue;
    }

    // Returns std deviation after T attempts
    public double stddev() {
        if (stddevValue == null) {
            stddevValue = StdStats.stddev(experiments);
        }
        return stddevValue;
    }

    // Return high value of the 95% confidence interval
    public double confidenceHi() {
        if (confidenceHiValue == null) {
            confidenceHiValue = mean() + CONFIDENCE_95 * stddev() / Math.sqrt(trials);
        }
        return confidenceHiValue;
    }

    // Returns lo value of the 95% confidence interval
    public double confidenceLo() {
        if (confidenceLoValue == null) {
            confidenceLoValue = mean() - CONFIDENCE_95 * stddev() / Math.sqrt(trials);
        }
        return confidenceLoValue;
    }

    // Runs the percolation stats calculation
    public static void main(String[] args) {
        PercolationStats stats = new PercolationStats(
                Integer.parseInt(args[0]),
                Integer.parseInt(args[1]));
        System.out.println("mean                    = " + stats.mean());
        System.out.println("stddev                  = " + stats.stddev());
        System.out.println(
                "95% confidence interval = ["
                        + stats.confidenceLo() + ", " + stats.confidenceHi()
                        + "]");
    }
}
