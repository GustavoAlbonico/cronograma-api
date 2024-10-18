package com.cronograma.api.controllers;

import com.cronograma.api.useCases.disciplina.DisciplinaService;
import com.cronograma.api.useCases.disciplina.domains.DisciplinaRequestDom;
import com.cronograma.api.useCases.disciplina.domains.DisciplinaResponseDom;
import com.cronograma.api.utils.paginacao.PaginacaoRequestUtil;
import com.cronograma.api.utils.paginacao.PaginacaoResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/disciplina")
public class DisciplinaController {

    @Autowired
    private DisciplinaService disciplinaService;

    @GetMapping("/carregar/{id}")
    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('DISCIPLINA_CONTROLLER','CARREGAR_POR_ID')")
    public ResponseEntity<?> carregarDisciplinaPorId(@PathVariable Long id){
        DisciplinaResponseDom response = disciplinaService.carregarDisciplinaPorId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/carregar/curso/{cursoId}/fase/{faseId}")
    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('DISCIPLINA_CONTROLLER','CARREGAR')")
    public ResponseEntity<?> carregarDisciplina(@PathVariable Long cursoId, @PathVariable Long faseId, @RequestParam Integer exibir, @RequestParam Integer paginaAtual){
        PaginacaoRequestUtil paginacaoRequestUtil =  new PaginacaoRequestUtil(exibir,paginaAtual);
        PaginacaoResponseUtil<List<DisciplinaResponseDom>> response = disciplinaService.carregarDisciplina(cursoId, faseId, paginacaoRequestUtil);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/criar")
    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('DISCIPLINA_CONTROLLER','CRIAR')")
    public ResponseEntity<?> criarDisciplina(@RequestBody DisciplinaRequestDom disciplinaRequestDom){
        disciplinaService.criarDisciplina(disciplinaRequestDom);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @PutMapping("/editar/{id}")
    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('DISCIPLINA_CONTROLLER','EDITAR')")
    public ResponseEntity<?> editarDisciplina(@PathVariable Long id,@RequestBody DisciplinaRequestDom disciplinaRequestDom){
        disciplinaService.editarDisciplina(id,disciplinaRequestDom);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    @PutMapping("/inativar/{id}")
    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('DISCIPLINA_CONTROLLER','INATIVAR')")
    public ResponseEntity<?> inativarDisciplina(@PathVariable Long id){
        disciplinaService.inativarDisciplina(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    @PutMapping("/ativar/{id}")
    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('DISCIPLINA_CONTROLLER','ATIVAR')")
    public ResponseEntity<?> ativarDisciplina(@PathVariable Long id){
        disciplinaService.ativarDisciplina(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }
}
