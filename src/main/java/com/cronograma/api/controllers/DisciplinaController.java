package com.cronograma.api.controllers;

import com.cronograma.api.useCases.aluno.domains.AlunoResponseDom;
import com.cronograma.api.useCases.disciplina.DisciplinaService;
import com.cronograma.api.useCases.disciplina.domains.DisciplinaRequestDom;
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
@RequestMapping("/disciplina")
public class DisciplinaController {

    @Autowired
    private DisciplinaService disciplinaService;

//    @GetMapping("/carregar/ativo")
//    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('PROFESSOR_CONTROLLER','CARREGAR_ATIVO')")
//    public ResponseEntity<?> carregarProfessorAtivo(){
//        List<ProfessorResponseDom> response = professorService.carregarProfessorAtivo();
//        return ResponseEntity.ok(response);
//    }
//    @GetMapping("/carregar/{id}")
//    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('ALUNO_CONTROLLER','CARREGAR_POR_ID')")
//    public ResponseEntity<?> carregarAlunoPorId(@PathVariable Long id){
//        AlunoResponseDom response = alunoService.carregarAlunoPorId(id);
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping("/carregar/curso/{cursoId}/fase/{faseId}")
//    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('ALUNO_CONTROLLER','CARREGAR')")
//    public ResponseEntity<?> carregarAluno(@PathVariable Long cursoId, @PathVariable Long faseId, @RequestBody PaginacaoRequestUtil paginacaoRequestUtil){
//        PaginacaoResponseUtil<List<AlunoResponseDom>> response = alunoService.carregarAluno(cursoId, faseId, paginacaoRequestUtil);
//        return ResponseEntity.ok(response);
//    }

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
        return ResponseEntity.ok(null);
    }
//
//    @PutMapping("/inativar/{id}")
//    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('PROFESSOR_CONTROLLER','INATIVAR')")
//    public ResponseEntity<?> inativarProfessor(@PathVariable Long id){
//        professorService.inativarProfessor(id);
//        return ResponseEntity.ok(null);
//    }
//
//    @PutMapping("/ativar/{id}")
//    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('PROFESSOR_CONTROLLER','ATIVAR')")
//    public ResponseEntity<?> ativarProfessor(@PathVariable Long id){
//        professorService.ativarProfessor(id);
//        return ResponseEntity.ok(null);
//    }
}
