package io.wahid.publication.ai.util;

public final class TrendMath {

    private TrendMath() {}

    public static double slope(double[] x, double[] y) {
        int n = x.length;
        if (n != y.length || n < 2) {
            return 0.0;
        }

        double sumX = 0, sumY = 0, sumXY = 0, sumXX = 0;

        for (int i = 0; i < n; i++) {
            sumX += x[i];
            sumY += y[i];
            sumXY += x[i] * y[i];
            sumXX += x[i] * x[i];
        }

        double denominator = n * sumXX - sumX * sumX;
        if (denominator == 0) {
            return 0.0;
        }

        return (n * sumXY - sumX * sumY) / denominator;
    }
}
