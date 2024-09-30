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

    @GetMapping("/carregar/ativo/curso/{cursoId}")
    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('FASE_CONTROLLER','CARREGAR_ATIVO_POR_CURSO')")
    public ResponseEntity<?> carregarFaseAtivoPorCurso(@PathVariable Long cursoId){
        List<FaseResponseDom> response = faseService.carregarFaseAtivoPorCurso(cursoId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/carregar/ativo")
    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('FASE_CONTROLLER','CARREGAR_ATIVO')")
    public ResponseEntity<?> carregarFaseAtivo(){
        List<FaseResponseDom> response = faseService.carregarFaseAtivo();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/carregar/{id}")
    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('FASE_CONTROLLER','CARREGAR_POR_ID')")
    public ResponseEntity<?> carregarFasePorId(@PathVariable Long id){
        FaseResponseDom response = faseService.carregarFasePorId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/carregar")
    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('FASE_CONTROLLER','CARREGAR')")
    public ResponseEntity<?> carregarFase(){
        List<FaseResponseDom> response = faseService.carregarFase();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/criar")
    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('FASE_CONTROLLER','CRIAR')")
    public ResponseEntity<?> criarFase(@RequestBody FaseRequestDom fase){
        faseService.criarFase(fase);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @PutMapping("/editar/{id}")
    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('FASE_CONTROLLER','EDITAR')")
    public ResponseEntity<?> editarFase(@PathVariable Long id,@RequestBody FaseRequestDom fase){
        faseService.editarFase(id,fase);
        return ResponseEntity.ok(null);
    }

    @PutMapping("/inativar/{id}")
    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('FASE_CONTROLLER','INATIVAR')")
    public ResponseEntity<?> inativarFase(@PathVariable Long id){
        faseService.inativarFase(id);
        return ResponseEntity.ok(null);
    }

    @PutMapping("/ativar/{id}")
    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('FASE_CONTROLLER','ATIVAR')")
    public ResponseEntity<?> ativarFase(@PathVariable Long id){
        faseService.ativarFase(id);
        return ResponseEntity.ok(null);
    }
}
