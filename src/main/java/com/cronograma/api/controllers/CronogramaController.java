package com.cronograma.api.controllers;

import com.cronograma.api.useCases.cronograma.CronogramaService;
import com.cronograma.api.useCases.cronograma.domains.CronogramaDisciplinaDom;
import com.cronograma.api.useCases.cronograma.domains.CronogramaRequestDom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/cronograma")
public class CronogramaController {

    @Autowired
    private CronogramaService cronogramaService;

    @PostMapping("/gerar")
    public ResponseEntity<?> gerarCronograma(@RequestBody CronogramaRequestDom cronograma) {

        List<CronogramaDisciplinaDom> response = cronogramaService.gerarCronograma(cronograma);
        return ResponseEntity.status(200).body(response);
    }
}