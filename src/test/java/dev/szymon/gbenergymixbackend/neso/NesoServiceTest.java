package dev.szymon.gbenergymixbackend.neso;

import dev.szymon.gbenergymixbackend.exception.ExternalApiException;
import dev.szymon.gbenergymixbackend.exception.InsufficientDataException;
import dev.szymon.gbenergymixbackend.exception.InvalidHoursException;
import dev.szymon.gbenergymixbackend.nesoapi.EnergyMixData;
import dev.szymon.gbenergymixbackend.nesoapi.EnergyMixResponse;
import dev.szymon.gbenergymixbackend.nesoapi.GenerationMix;
import dev.szymon.gbenergymixbackend.response.DailyEnergyMixResponse;
import dev.szymon.gbenergymixbackend.response.OptimalChargingWindowResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NesoServiceTest {

    @Mock
    private NesoClient nesoClient;

    @InjectMocks
    private NesoService nesoService;

    @Test
    void shouldReturnEnergyMixDaily() {

        GenerationMix wind = new GenerationMix("wind", 80.0);
        GenerationMix solar = new GenerationMix("solar", 20.0);

        LocalDate today = LocalDate.now(ZoneOffset.UTC);

        EnergyMixData day1 = new EnergyMixData();
        day1.setFrom(today + "T00:00Z");
        day1.setTo(today + "T23:59Z");
        day1.setGenerationmix(List.of(wind, solar));

        EnergyMixData day2 = new EnergyMixData();
        day2.setFrom(today.plusDays(1) + "T00:00Z");
        day2.setTo(today.plusDays(1) + "T23:59Z");
        day2.setGenerationmix(List.of(wind, solar));

        EnergyMixData day3 = new EnergyMixData();
        day3.setFrom(today.plusDays(2) + "T00:00Z");
        day3.setTo(today.plusDays(2) + "T23:59Z");
        day3.setGenerationmix(List.of(wind, solar));

        EnergyMixResponse response = new EnergyMixResponse();
        response.setData(List.of(day1, day2, day3));

        when(nesoClient.getEnergyMix(any(URI.class))).thenReturn(response);

        List<DailyEnergyMixResponse> result = nesoService.getDailyEnergyMix();

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(today, result.get(0).getDate());
    }

    @Test
    void shouldReturnOptimalChargingWindow() {

        GenerationMix wind = new GenerationMix("wind", 100.0);
        GenerationMix solar = new GenerationMix("solar", 0.0);

        LocalDate today = LocalDate.now(ZoneOffset.UTC);

        EnergyMixData data1 = new EnergyMixData();
        data1.setFrom(today + "T00:00Z");
        data1.setTo(today + "T00:30Z");
        data1.setGenerationmix(List.of(wind, solar));

        EnergyMixData data2 = new EnergyMixData();
        data2.setFrom(today + "T00:30Z");
        data2.setTo(today + "T01:00Z");
        data2.setGenerationmix(List.of(wind, solar));

        EnergyMixData data3 = new EnergyMixData();
        data3.setFrom(today + "T01:00Z");
        data3.setTo(today + "T01:30Z");
        data3.setGenerationmix(List.of(wind, solar));

        EnergyMixResponse response = new EnergyMixResponse();
        response.setData(List.of(data1, data2, data3));

        when(nesoClient.getEnergyMix(any(URI.class))).thenReturn(response);

        OptimalChargingWindowResponse result = nesoService.getOptimalChargingWindow(1);

        assertNotNull(result);
        assertEquals(100.0, result.getAverageCleanEnergyPercentage());
    }

    @Test
    void shouldThrowExternalApiExceptionWhenApiReturnsNull() {

        when(nesoClient.getEnergyMix(any(URI.class))).thenReturn(null);

        assertThrows(ExternalApiException.class, () -> nesoService.getDailyEnergyMix());
    }

    @Test
    void shouldThrowExceptionWhenHoursInvalid() {

        assertThrows(InvalidHoursException.class, () -> nesoService.getOptimalChargingWindow(0));
    }

    @Test
    void shouldThrowInsufficientDataException() {

        GenerationMix wind = new GenerationMix("wind", 100.0);

        EnergyMixData data = new EnergyMixData();

        LocalDate today = LocalDate.now(ZoneOffset.UTC);

        data.setFrom(today + "T00:00Z");
        data.setTo(today + "T00:30Z");
        data.setGenerationmix(List.of(wind));

        EnergyMixResponse response = new EnergyMixResponse();
        response.setData(List.of(data));

        when(nesoClient.getEnergyMix(any(URI.class))).thenReturn(response);

        assertThrows(InsufficientDataException.class, () -> nesoService.getOptimalChargingWindow(3));
    }

}