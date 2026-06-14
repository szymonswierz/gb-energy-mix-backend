package dev.szymon.gbenergymixbackend.energymix;

import dev.szymon.gbenergymixbackend.neso.NesoService;
import dev.szymon.gbenergymixbackend.response.DailyEnergyMixResponse;
import dev.szymon.gbenergymixbackend.response.OptimalChargingWindowResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "http://localhost:5173")
public class EnergyMixController {

    private final NesoService nesoService;

    @GetMapping("/energy-mix-daily")
    public ResponseEntity <List<DailyEnergyMixResponse>> getDailyEnergyMix() {

        return ResponseEntity.ok(nesoService.getDailyEnergyMix());
    }

    @GetMapping("/optimal-charging-window/{hours}")
    public ResponseEntity <OptimalChargingWindowResponse> getOptimalChargingWindow(@PathVariable("hours") int hours) {

        return ResponseEntity.ok(nesoService.getOptimalChargingWindow(hours));
    }

}
