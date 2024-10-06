package com.cronograma.api.controllers;

import com.cronograma.api.useCases.diaSemanaDisponivel.DiaSemanaDisponivelService;
import com.cronograma.api.useCases.diaSemanaDisponivel.domains.DiaSemanaDisponivelResponseDom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/diasemanadisponivel")
public class DiaSemanaDisponivelController {

    @Autowired
    private DiaSemanaDisponivelService diaSemanaDisponivelService;

    @GetMapping("/carregar")
    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('DIA_SEMANA_DISPONIVEL_CONTROLLER','CARREGAR')")
    public ResponseEntity<?> carregarDiaSemanaDisponivel(){
        List<DiaSemanaDisponivelResponseDom> response = diaSemanaDisponivelService.carregarDiaSemanaDisponivel();
        return ResponseEntity.ok(response);
    }
}
