package com.cronograma.api.controllers;
import com.cronograma.api.useCases.cronograma.CronogramaService;
import com.cronograma.api.useCases.cronograma.domains.CronogramaRequestDom;
import com.cronograma.api.useCases.evento.EventoService;
import com.cronograma.api.useCases.evento.domains.EventoCronogramaRequestDom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/evento")
public class EventoController {

    @Autowired
    private EventoService eventoService;

    @PostMapping("/criar/cronograma")
    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('EVENTO_CONTROLLER','CRIAR')")
    public ResponseEntity<?> criarEventoCronograma(@RequestBody EventoCronogramaRequestDom cronograma){
        eventoService.criarEventoCronograma(cronograma);
        return ResponseEntity.status(201).body(null);
    }

}
