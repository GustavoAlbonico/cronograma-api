package com.cronograma.api.controllers;

import com.cronograma.api.useCases.cronograma.CronogramaService;
import com.cronograma.api.useCases.cronograma.domains.CronogramaRequestDom;
import com.cronograma.api.useCases.cronograma.domains.CronogramaResponseDom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/cronograma")
public class CronogramaController {

    @Autowired
    private CronogramaService cronogramaService;

    @PostMapping("/gerar")
    public ResponseEntity<?> gerarCronograma(@RequestBody CronogramaRequestDom cronograma) {

        List<String> response = cronogramaService.gerarCronograma(cronograma);
        return ResponseEntity.status(200).body(response);
    }
}
