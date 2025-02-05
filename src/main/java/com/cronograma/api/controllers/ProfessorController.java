package com.cronograma.api.controllers;

import com.cronograma.api.useCases.professor.ProfessorService;
import com.cronograma.api.useCases.professor.domains.ProfessorFormularioRequestDom;
import com.cronograma.api.useCases.professor.domains.ProfessorRequestDom;
import com.cronograma.api.useCases.professor.domains.ProfessorResponseDom;
import com.cronograma.api.utils.paginacao.PaginacaoRequestUtil;
import com.cronograma.api.utils.paginacao.PaginacaoResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/professor")
public class ProfessorController {

    @Autowired
    private ProfessorService professorService;

    @GetMapping("/carregar/ativo")
    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('PROFESSOR_CONTROLLER','CARREGAR_ATIVO')")
    public ResponseEntity<?> carregarProfessorAtivo(){
        List<ProfessorResponseDom> response = professorService.carregarProfessorAtivo();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/carregar/{id}")
    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('PROFESSOR_CONTROLLER','CARREGAR_POR_ID')")
    public ResponseEntity<?> carregarProfessorPorId(@PathVariable Long id){
        ProfessorResponseDom response = professorService.carregarProfessorPorId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/carregar")
    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('PROFESSOR_CONTROLLER','CARREGAR')")
    public ResponseEntity<?> carregarProfessor(@RequestParam Integer exibir, @RequestParam Integer paginaAtual){
        PaginacaoRequestUtil paginacaoRequestUtil =  new PaginacaoRequestUtil(exibir,paginaAtual);
        PaginacaoResponseUtil<List<ProfessorResponseDom>> response = professorService.carregarProfessor(paginacaoRequestUtil);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/carregar/diasemanadisponivel")
    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('PROFESSOR_CONTROLLER','CARREGAR_DIA_SEMANA_DISPONIVEL')")
    public ResponseEntity<?> carregarDiaSemanaDisponivelProfessor(){
        ProfessorResponseDom response = professorService.carregarDiaSemanaDisponivelProfessor();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/criar")
    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('PROFESSOR_CONTROLLER','CRIAR')")
    public ResponseEntity<?> criarProfessor(@RequestBody ProfessorRequestDom professorRequestDom){
        professorService.criarProfessor(professorRequestDom);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @PostMapping("/associar/coordenador/{coordenadorId}")
    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('PROFESSOR_CONTROLLER','ASSOCIAR')")
    public ResponseEntity<?> associarProfessor(@PathVariable Long coordenadorId){
        Long professorId = professorService.associarProfessor(coordenadorId);
        return ResponseEntity.status(HttpStatus.CREATED).body(professorId);
    }

    @PutMapping("/formulario")
    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('PROFESSOR_CONTROLLER','FORMULARIO')")
    public ResponseEntity<?> formularioProfessor(@RequestBody ProfessorFormularioRequestDom professorFormularioRequestDom){
        professorService.formularioProfessor(professorFormularioRequestDom);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    @PutMapping("/editar/{id}")
    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('PROFESSOR_CONTROLLER','EDITAR')")
    public ResponseEntity<?> editarProfessor(@PathVariable Long id,@RequestBody ProfessorRequestDom professorRequestDom){
        professorService.editarProfessor(id,professorRequestDom);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    @PutMapping("/inativar/{id}")
    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('PROFESSOR_CONTROLLER','INATIVAR')")
    public ResponseEntity<?> inativarProfessor(@PathVariable Long id){
        professorService.inativarProfessor(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    @PutMapping("/ativar/{id}")
    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('PROFESSOR_CONTROLLER','ATIVAR')")
    public ResponseEntity<?> ativarProfessor(@PathVariable Long id){
        professorService.ativarProfessor(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }
}
