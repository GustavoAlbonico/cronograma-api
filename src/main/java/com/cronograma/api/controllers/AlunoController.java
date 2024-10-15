package com.cronograma.api.controllers;

import com.cronograma.api.useCases.aluno.AlunoService;
import com.cronograma.api.useCases.aluno.domains.AlunoImportarRequestDom;
import com.cronograma.api.useCases.aluno.domains.AlunoRequestDom;
import com.cronograma.api.useCases.aluno.domains.AlunoResponseDom;
import com.cronograma.api.utils.paginacao.PaginacaoRequestUtil;
import com.cronograma.api.utils.paginacao.PaginacaoResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/aluno")
public class AlunoController {

    @Autowired
    private AlunoService alunoService;

    @GetMapping("/carregar/{id}")
    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('ALUNO_CONTROLLER','CARREGAR_POR_ID')")
    public ResponseEntity<?> carregarAlunoPorId(@PathVariable Long id){
        AlunoResponseDom response = alunoService.carregarAlunoPorId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/carregar/curso/{cursoId}/fase/{faseId}")
    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('ALUNO_CONTROLLER','CARREGAR')")
    public ResponseEntity<?> carregarAluno(@PathVariable Long cursoId, @PathVariable Long faseId, @RequestParam Integer exibir, @RequestParam Integer paginaAtual){
        PaginacaoRequestUtil paginacaoRequestUtil =  new PaginacaoRequestUtil(exibir,paginaAtual);
        PaginacaoResponseUtil<List<AlunoResponseDom>> response = alunoService.carregarAluno(cursoId, faseId, paginacaoRequestUtil);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/criar")
    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('ALUNO_CONTROLLER','CRIAR')")
    public ResponseEntity<?> criarAluno(@RequestBody AlunoRequestDom alunoRequestDom){
        alunoService.criarAluno(alunoRequestDom);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @PostMapping("/importar")
    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('ALUNO_CONTROLLER','IMPORTAR')")
    public ResponseEntity<?> importarAluno(@ModelAttribute AlunoImportarRequestDom alunoImportarRequestDom) throws IOException {
        alunoService.importarAluno(alunoImportarRequestDom);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @PutMapping("/editar/{id}")
    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('ALUNO_CONTROLLER','EDITAR')")
    public ResponseEntity<?> editarAluno(@PathVariable Long id,@RequestBody AlunoRequestDom alunoRequestDom){
        alunoService.editarAluno(id, alunoRequestDom);
        return ResponseEntity.ok(null);
    }

    @DeleteMapping("/excluir/{id}")
    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('ALUNO_CONTROLLER','EXCLUIR')")
    public ResponseEntity<?> excluirAluno(@PathVariable Long id){
        alunoService.excluirAluno(id);
        return ResponseEntity.ok(null);
    }
}
