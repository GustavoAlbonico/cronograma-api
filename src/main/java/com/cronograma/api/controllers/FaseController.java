package com.cronograma.api.controllers;

import com.cronograma.api.useCases.fase.FaseService;
import com.cronograma.api.useCases.fase.domains.FaseRequestDom;
import com.cronograma.api.useCases.fase.domains.FaseResponseDom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fase")
public class FaseController {

    @Autowired
    private FaseService faseService;

    @GetMapping("/carregar")
    public ResponseEntity<?> carregarFase(){
        List<FaseResponseDom> response = faseService.carregarFase();
        return ResponseEntity.ok(response);
    }

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

    @PutMapping("/inativar/{id}")
    public ResponseEntity<?> inativarFase(@PathVariable Long id){
        faseService.inativarFase(id);
        return ResponseEntity.ok(null);
    }

    @PutMapping("/ativar/{id}")
    public ResponseEntity<?> ativarFase(@PathVariable Long id){
        faseService.ativarFase(id);
        return ResponseEntity.ok(null);
    }
}
