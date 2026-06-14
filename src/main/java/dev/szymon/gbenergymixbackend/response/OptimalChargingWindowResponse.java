package dev.szymon.gbenergymixbackend.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OptimalChargingWindowResponse {

    private String from;

    private String to;

    private double averageCleanEnergyPercentage;

}
