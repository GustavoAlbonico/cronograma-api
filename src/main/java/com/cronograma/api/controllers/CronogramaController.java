package com.cronograma.api.controllers;

import com.cronograma.api.useCases.cronograma.CronogramaService;
import com.cronograma.api.useCases.cronograma.domains.CronogramaResponseDom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cronograma")
public class CronogramaController {

    @Autowired
    private CronogramaService cronogramaService;

    @GetMapping("/carregar/periodo/{periodoId}/curso/{cursoId}/fase/{faseId}")
    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('CRONOGRAMA_CONTROLLER','CARREGAR')")
        public ResponseEntity<?> carregarCronograma(@PathVariable Long periodoId,@PathVariable Long cursoId, @PathVariable Long faseId){
        CronogramaResponseDom response = cronogramaService.carregarCronograma(periodoId,cursoId,faseId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/excluir/periodo/{periodoId}/curso/{cursoId}")
    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('CRONOGRAMA_CONTROLLER','EXCLUIR')")
    public ResponseEntity<?> excluirCronogramaPorPeriodoPorCurso(@PathVariable Long periodoId,@PathVariable Long cursoId){
        cronogramaService.excluirCronogramaPorPeriodoPorCurso(periodoId,cursoId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }


}