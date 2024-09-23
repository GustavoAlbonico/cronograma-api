package com.cronograma.api.controllers;

import com.cronograma.api.useCases.fase.FaseService;
import com.cronograma.api.useCases.fase.domains.FaseRequestDom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fase")
public class FaseController {

    @Autowired
    private FaseService faseService;

    @PostMapping("/criar")
    public ResponseEntity<?> criarFase(@RequestBody FaseRequestDom fase){
        faseService.criarFase(fase);
        return ResponseEntity.status(201).body(null);
    }

    @PutMapping("/editar/{id}")
    public ResponseEntity<?> editarFase(@PathVariable Long id,@RequestBody FaseRequestDom fase){
        faseService.editarFase(id,fase);
        return ResponseEntity.ok(null);
    }
}
