package com.cronograma.api.controllers;
import com.cronograma.api.useCases.cronograma.CronogramaService;
import com.cronograma.api.useCases.cronograma.domains.CronogramaRequestDom;
import com.cronograma.api.useCases.evento.EventoService;
import com.cronograma.api.useCases.evento.domains.EventoCronogramaRequestDom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/evento")
public class EventoController {

    @Autowired
    private EventoService eventoService;

    @PostMapping("/gerarcronograma")
    public ResponseEntity<?> gerarCronograma(@RequestBody EventoCronogramaRequestDom cronograma){
        eventoService.gerarCronograma(cronograma);
        return ResponseEntity.status(201).body(null);
    }

}
