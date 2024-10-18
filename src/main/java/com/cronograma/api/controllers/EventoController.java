package com.cronograma.api.controllers;
import com.cronograma.api.useCases.cronograma.CronogramaService;
import com.cronograma.api.useCases.cronograma.domains.CronogramaRequestDom;
import com.cronograma.api.useCases.evento.EventoService;
import com.cronograma.api.useCases.evento.domains.EventoCronogramaRequestDom;
import com.cronograma.api.useCases.evento.domains.EventoResponseDom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/evento")
public class EventoController {

    @Autowired
    private EventoService eventoService;

    @PostMapping("/criar/cronograma")
    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('EVENTO_CONTROLLER','CRIAR')")
    public ResponseEntity<?> criarEventoCronograma(@RequestBody EventoCronogramaRequestDom cronograma){
        eventoService.criarEventoCronograma(cronograma);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @GetMapping("/carregar")
    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('EVENTO_CONTROLLER','CARREGAR')")
    public ResponseEntity<?> carregarEvento(){
        List<EventoResponseDom> response = eventoService.carregarEvento();
        return ResponseEntity.ok(response);
    }

}
