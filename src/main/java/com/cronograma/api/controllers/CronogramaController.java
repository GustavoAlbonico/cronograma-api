package com.cronograma.api.controllers;

import com.cronograma.api.useCases.cronograma.CronogramaService;
import com.cronograma.api.useCases.cronograma.domains.CronogramaResponseDom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin//remover
@RestController
@RequestMapping("/cronograma")
public class CronogramaController {

    @Autowired
    private CronogramaService cronogramaService;

    @GetMapping("/carregar/periodo/{periodoId}/curso/{cursoId}/fase/{faseId}")
        public ResponseEntity<?> carregarCronograma(@PathVariable Long periodoId,@PathVariable Long cursoId, @PathVariable Long faseId){
        CronogramaResponseDom response = cronogramaService.carregarCronograma(periodoId,cursoId,faseId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/excluir/{cronogramaId}")
    public ResponseEntity<?> excluirCronograma(@PathVariable Long cronogramaId){
        cronogramaService.excluirCronograma(cronogramaId);
        return ResponseEntity.ok(null);
    }
}