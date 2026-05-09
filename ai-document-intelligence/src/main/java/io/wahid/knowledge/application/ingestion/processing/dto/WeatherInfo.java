package io.wahid.knowledge.application.ingestion.processing.dto;

import com.opencsv.bean.CsvBindByPosition;
import io.wahid.knowledge.dto.WeatherRow;

public class WeatherInfo {
    @CsvBindByPosition(position = 0, required = true)
    private String station;
    @CsvBindByPosition(position = 1, required = true)
    private int year;
    @CsvBindByPosition(position = 2, required = true)
    private int month;
    @CsvBindByPosition(position = 3, required = true)
    private int day;
    @CsvBindByPosition(position = 4, required = true)
    private double rainfall;
    @CsvBindByPosition(position = 5, required = true)
    private double sunshine;
    @CsvBindByPosition(position = 6, required = true)
    private double humidity;
    @CsvBindByPosition(position = 7, required = true)
    private double temperature;

    public String getStation() {
        return station;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public double getRainfall() {
        return rainfall;
    }

    public double getSunshine() {
        return sunshine;
    }

    public double getHumidity() {
        return humidity;
    }

    public double getTemperature() {
        return temperature;
    }

    public WeatherRow getWeatherRow() {
        return new WeatherRow(station, year, month, day, rainfall, sunshine, humidity, temperature);
    }
}
