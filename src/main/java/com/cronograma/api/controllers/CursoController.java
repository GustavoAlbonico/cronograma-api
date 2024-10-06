package com.cronograma.api.controllers;

import com.cronograma.api.useCases.curso.CursoService;
import com.cronograma.api.useCases.curso.domains.CursoPorPeriodoResponseDom;
import com.cronograma.api.useCases.curso.domains.CursoPorUsuarioResponseDom;
import com.cronograma.api.useCases.curso.domains.CursoRequestDom;
import com.cronograma.api.useCases.curso.domains.CursoResponseDom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/curso")
public class CursoController {

    @Autowired
    private CursoService cursoService;

    @GetMapping("/carregar/periodo/{id}")
    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('CURSO_CONTROLLER','CARREGAR_POR_PERIODO')")
    public ResponseEntity<?> carregarCursoPorPeriodo(@PathVariable Long id){
        List<CursoPorPeriodoResponseDom> response = cursoService.carregarCursoPorPeriodo(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/carregar/ativo")
    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('CURSO_CONTROLLER','CARREGAR_ATIVO')")
    public ResponseEntity<?> carregarCursoAtivo(){
        List<CursoResponseDom> response = cursoService.carregarCursoAtivo();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/carregar/{id}")
    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('CURSO_CONTROLLER','CARREGAR_POR_ID')")
    public ResponseEntity<?> carregarCursoPorId(@PathVariable Long id){
        CursoResponseDom response = cursoService.carregarCursoPorId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/carregar/usuario")
    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('CURSO_CONTROLLER','CARREGAR_POR_USUARIO')")
    public ResponseEntity<?> carregarCursoPorUsuario(){
        List<CursoPorUsuarioResponseDom> response = cursoService.carregarCursoPorUsuario();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/carregar")
    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('CURSO_CONTROLLER','CARREGAR')")
    public ResponseEntity<?> carregarCurso(){
        List<CursoResponseDom> response = cursoService.carregarCurso();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/criar")
    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('CURSO_CONTROLLER','CRIAR')")
    public ResponseEntity<?> criarCurso(@RequestBody CursoRequestDom cursoRequestDom){
        cursoService.criarCurso(cursoRequestDom);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @PutMapping("/editar/{id}")
    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('CURSO_CONTROLLER','EDITAR')")
    public ResponseEntity<?> editarCurso(@PathVariable Long id, @RequestBody CursoRequestDom cursoRequestDom){
        cursoService.editarCurso(id,cursoRequestDom);
        return ResponseEntity.ok(null);
    }

    @PutMapping("/inativar/{id}")
    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('CURSO_CONTROLLER','INATIVAR')")
    public ResponseEntity<?> inativarCurso(@PathVariable Long id){
        cursoService.inativarCurso(id);
        return ResponseEntity.ok(null);
    }

    @PutMapping("/ativar/{id}")
    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('CURSO_CONTROLLER','ATIVAR')")
    public ResponseEntity<?> ativarCurso(@PathVariable Long id){
        cursoService.ativarCurso(id);
        return ResponseEntity.ok(null);
    }
}
