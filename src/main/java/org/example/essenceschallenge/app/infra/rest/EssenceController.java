package org.example.essenceschallenge.app.infra.rest;

import org.example.essenceschallenge.app.service.EssenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EssenceController {

    @Autowired
    private EssenceService essenceService;

    @GetMapping("/essences")
    public ResponseEntity<List<EssenceResponse>> getEssences() throws IOException, InterruptedException {
        return ResponseEntity.ok(essenceService.getAllEssences());
    }

    @GetMapping("/essences/{id}")
    public ResponseEntity<EssenceResponse> getEssenceById(@PathVariable String id) throws IOException, InterruptedException {
        EssenceResponse essence = essenceService.getEssenceById(id);
        return ResponseEntity.ok(essence);
    }
}
