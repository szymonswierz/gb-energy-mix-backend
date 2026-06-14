package dev.szymon.gbenergymixbackend.neso;

import dev.szymon.gbenergymixbackend.nesoapi.EnergyMixResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.net.URI;

@FeignClient(name = "nesoClient", url = "https://api.carbonintensity.org.uk")
public interface NesoClient {

    @GetMapping
    EnergyMixResponse getEnergyMix(URI uri);



}
