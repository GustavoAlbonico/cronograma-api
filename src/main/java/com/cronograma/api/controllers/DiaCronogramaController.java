package com.cronograma.api.controllers;

import com.cronograma.api.useCases.diaCronograma.DiaCronogramaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/diacronograma")
public class DiaCronogramaController {

    @Autowired
    private DiaCronogramaService diaCronogramaService;

    @PutMapping("/editar/{primeiroDiaCronogramaId}/{segundoDiaCronogramaId}")
    public ResponseEntity<?> editarCronograma(@PathVariable Long primeiroDiaCronogramaId, @PathVariable Long segundoDiaCronogramaId){
        diaCronogramaService.editarDiaCronograma(primeiroDiaCronogramaId, segundoDiaCronogramaId);
        return ResponseEntity.ok(null);
    }
}
