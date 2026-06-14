package dev.szymon.gbenergymixbackend.neso;

import dev.szymon.gbenergymixbackend.exception.ExternalApiException;
import dev.szymon.gbenergymixbackend.exception.InsufficientDataException;
import dev.szymon.gbenergymixbackend.exception.InvalidHoursException;
import dev.szymon.gbenergymixbackend.model.CleanEnergyInterval;
import dev.szymon.gbenergymixbackend.nesoapi.EnergyMixData;
import dev.szymon.gbenergymixbackend.nesoapi.EnergyMixResponse;
import dev.szymon.gbenergymixbackend.nesoapi.GenerationMix;
import dev.szymon.gbenergymixbackend.response.DailyEnergyMixResponse;
import dev.szymon.gbenergymixbackend.response.EnergySourcePercentage;
import dev.szymon.gbenergymixbackend.response.OptimalChargingWindowResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

@RequiredArgsConstructor
@Service
public class NesoService {

    private final NesoClient nesoClient;

    public EnergyMixResponse getEnergyMix(String from, String to) {

        String url = "https://api.carbonintensity.org.uk/generation/" + from + "/" + to;

        EnergyMixResponse response = nesoClient.getEnergyMix(URI.create(url));

        if (response == null || response.getData() == null) {

            throw new ExternalApiException("Failed to fetch data from NESO API");
        }
        return response;
    }

    public List<DailyEnergyMixResponse> getDailyEnergyMix() {

        LocalDate today = LocalDate.now(ZoneOffset.UTC);

        OffsetDateTime from = today.atStartOfDay().atOffset(ZoneOffset.UTC);

        OffsetDateTime to = today.plusDays(3).atStartOfDay().atOffset(ZoneOffset.UTC);

        List<EnergyMixData> energyMixDataList = getEnergyMix(from.toString(), to.toString()).getData();

        Map<LocalDate, List<EnergyMixData>> intervalsMapFilteredByDay = new LinkedHashMap<>();

        intervalsMapFilteredByDay.put(today, new ArrayList<>());
        intervalsMapFilteredByDay.put(today.plusDays(1), new ArrayList<>());
        intervalsMapFilteredByDay.put(today.plusDays(2), new ArrayList<>());

        for (EnergyMixData element : energyMixDataList) {

            LocalDate elementDate = OffsetDateTime.parse(element.getFrom()).toLocalDate();

            if (intervalsMapFilteredByDay.containsKey(elementDate)) {
                intervalsMapFilteredByDay.get(elementDate).add(element);
            }

        }


        List<DailyEnergyMixResponse> dailyEnergyMixResponseList = new ArrayList<>();

        for (LocalDate date : intervalsMapFilteredByDay.keySet()) {


            List<EnergyMixData> energyMixDataForDay = intervalsMapFilteredByDay.get(date);

            Map<String, Double> sumElementsMap = new LinkedHashMap<>();


            for (EnergyMixData singleIntervalEnergyMixData : energyMixDataForDay) {
                for (GenerationMix generationMix : singleIntervalEnergyMixData.getGenerationmix()) {

                    String fuel = generationMix.getFuel();
                    double perc = generationMix.getPerc();

                    double currentSum = sumElementsMap.getOrDefault(fuel, 0.0);

                    sumElementsMap.put(fuel, currentSum + perc);
                }

            }

            List<EnergySourcePercentage> energySourcePercentages = new ArrayList<>();

            int intervalsCount = energyMixDataForDay.size();

            if (intervalsCount == 0) {
                continue;
            }

            for (String fuel : sumElementsMap.keySet()) {

                double sum = sumElementsMap.get(fuel);
                double average = sum / intervalsCount;

                EnergySourcePercentage energySourcePercentage = new EnergySourcePercentage();
                energySourcePercentage.setFuel(fuel);
                energySourcePercentage.setPercentage(average);
                energySourcePercentages.add(energySourcePercentage);
            }

            double cleanEnergyCounterPercentage = 0.0;

            for (EnergySourcePercentage element : energySourcePercentages) {

                if (Objects.equals(element.getFuel(), "biomass") ||
                        Objects.equals(element.getFuel(), "nuclear") ||
                        Objects.equals(element.getFuel(), "hydro") ||
                        Objects.equals(element.getFuel(), "wind") ||
                        Objects.equals(element.getFuel(), "solar")) {

                    cleanEnergyCounterPercentage += element.getPercentage();


                }

            }

            DailyEnergyMixResponse dailyEnergyMixResponse = new DailyEnergyMixResponse(date, energySourcePercentages, cleanEnergyCounterPercentage);

            dailyEnergyMixResponseList.add(dailyEnergyMixResponse);

        }
        return dailyEnergyMixResponseList;
    }

    public OptimalChargingWindowResponse getOptimalChargingWindow(int hours) {

        if (hours > 6 || hours < 1) {
            throw new InvalidHoursException("Hours must be between 1 and 6");
        }

        OffsetDateTime from = OffsetDateTime.now(ZoneOffset.UTC).withSecond(0).withNano(0);

        OffsetDateTime to = from.plusDays(2);

        List<EnergyMixData> energyMixDataList = getEnergyMix(from.toString(), to.toString()).getData();

        List<CleanEnergyInterval> cleanEnergyIntervalList = new ArrayList<>();

        for (EnergyMixData energyMixData : energyMixDataList) {

            double cleanFuelSum = 0.0;

            for (GenerationMix generationMix : energyMixData.getGenerationmix()) {

                if (Objects.equals(generationMix.getFuel(), "biomass") ||
                        Objects.equals(generationMix.getFuel(), "nuclear") ||
                        Objects.equals(generationMix.getFuel(), "hydro") ||
                        Objects.equals(generationMix.getFuel(), "wind") ||
                        Objects.equals(generationMix.getFuel(), "solar")) {

                    cleanFuelSum += generationMix.getPerc();
                }

            }

            cleanEnergyIntervalList.add(new CleanEnergyInterval(energyMixData.getFrom(), energyMixData.getTo(), cleanFuelSum));

        }

        int intervals = hours * 2;

        double biggestAverage = 0.0;

        int bestStartIndex = 0;

        int bestEndIndex = 0;

        if (cleanEnergyIntervalList.size() < intervals) {

            throw new InsufficientDataException("Data is insufficient to create a response");
        }

        for (int i = 0; i <= cleanEnergyIntervalList.size() - intervals; i++) {

            double cleanFuelSum = 0.0;

            int endIndex = i + intervals - 1;

            for (int j = 0; j < intervals; j++) {

                cleanFuelSum += cleanEnergyIntervalList.get(i + j).getCleanEnergyPercentage();

            }

            double tempAverage = cleanFuelSum / intervals;

            if (tempAverage > biggestAverage) {

                biggestAverage = tempAverage;

                bestStartIndex = i;

                bestEndIndex = endIndex;

            }

        }

        return new OptimalChargingWindowResponse(cleanEnergyIntervalList.get(bestStartIndex).getFrom(), cleanEnergyIntervalList.get(bestEndIndex).getTo(), biggestAverage);
    }

}




