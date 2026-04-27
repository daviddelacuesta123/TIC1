package com.logistica.logistica_urbana.presentation.controller;

import com.logistica.logistica_urbana.application.dto.response.DashboardResponseDTO;
import com.logistica.logistica_urbana.application.service.DashboardUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final DashboardUseCase dashboardUseCase;

    public DashboardController(DashboardUseCase dashboardUseCase) {
        this.dashboardUseCase = dashboardUseCase;
    }

    @GetMapping
    public ResponseEntity<DashboardResponseDTO> obtenerDashboard() {
        return ResponseEntity.ok(dashboardUseCase.obtenerMetricasDashboard());
    }
}