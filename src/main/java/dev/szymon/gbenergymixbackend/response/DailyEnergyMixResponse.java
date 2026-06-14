package dev.szymon.gbenergymixbackend.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class DailyEnergyMixResponse {

    private LocalDate date;
    private List<EnergySourcePercentage> energySourcePercentages;
    private double cleanEnergyPercentage;

}
