package io.wahid.knowledge.application.domain.weather.model;

public final class StationAccumulator {

    public String station;

    public int startYear = Integer.MAX_VALUE;
    public int endYear = Integer.MIN_VALUE;

    public double tempSum = 0;
    public double minTemp = Double.MAX_VALUE;
    public double maxTemp = Double.MIN_VALUE;

    public double rainfallSum = 0;
    public double sunshineSum = 0;
    public double humiditySum = 0;

    public int count = 0;

    // rainfall per month (1–12)
    public double[] monthlyRainfall = new double[12];

    public void accept(WeatherRow row) {
        station = row.station();

        startYear = Math.min(startYear, row.year());
        endYear = Math.max(endYear, row.year());

        tempSum += row.temperature();
        minTemp = Math.min(minTemp, row.temperature());
        maxTemp = Math.max(maxTemp, row.temperature());

        rainfallSum += row.rainfall();
        sunshineSum += row.sunshine();
        humiditySum += row.humidity();

        monthlyRainfall[row.month() - 1] += row.rainfall();

        count++;
    }
}
