package dev.szymon.gbenergymixbackend.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnergySourcePercentage {

    private String fuel;
    private double percentage;

}
