package io.wahid.knowledge.dto;

public class YearlyAccumulator {
    int year;
    int count;

    double rainfallSum;
    double temperatureSum;
    double sunshineSum;
    double humiditySum;

    double minTemp = Double.MAX_VALUE;
    double maxTemp = Double.MIN_VALUE;

    public void accept(WeatherRow row) {
        count++;
        rainfallSum += row.rainfall();
        temperatureSum += row.temperature();
        sunshineSum += row.sunshine();
        humiditySum += row.humidity();

        minTemp = Math.min(minTemp, row.temperature());
        maxTemp = Math.max(maxTemp, row.temperature());
    }

    public int getYear() {
        return year;
    }

    public double getRainfallSum() {
        return rainfallSum;
    }

    public double getTemperatureSum() {
        return temperatureSum;
    }

    public double getSunshineSum() {
        return sunshineSum;
    }

    public double getHumiditySum() {
        return humiditySum;
    }

    public double getMinTemp() {
        return minTemp;
    }

    public double getMaxTemp() {
        return maxTemp;
    }

    public int getCount() {
        return count;
    }
}
