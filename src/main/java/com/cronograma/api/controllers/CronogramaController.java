package com.cronograma.api.controllers;

import com.cronograma.api.entitys.enums.EventoStatusEnum;
import com.cronograma.api.exceptions.CronogramaException;
import com.cronograma.api.infra.exceptions.RestErrorMessage;
import com.cronograma.api.useCases.cronograma.CronogramaService;
import com.cronograma.api.useCases.cronograma.domains.CronogramaRequestDom;
import com.cronograma.api.useCases.cronograma.domains.CronogramaResponseDom;
import com.cronograma.api.useCases.cronograma.domains.TesteResponseCronogramaDom;
import com.cronograma.api.useCases.evento.domains.EventoRequestDom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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