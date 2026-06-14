package dev.szymon.gbenergymixbackend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CleanEnergyInterval {

    private String from;
    private String to;
    private double cleanEnergyPercentage;

}
