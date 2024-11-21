package com.cronograma.api.controllers;

import com.cronograma.api.useCases.coordenador.CoordenadorService;
import com.cronograma.api.useCases.coordenador.domains.CoordenadorRequestDom;
import com.cronograma.api.useCases.coordenador.domains.CoordenadorResponseDom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/coordenador")
public class CoordenadorController {
    @Autowired
    private CoordenadorService coordenadorService;

    @GetMapping("/carregar/{id}")
    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('COORDENADOR_CONTROLLER','CARREGAR_POR_ID')")
    public ResponseEntity<?> carregarCoordenadorPorId(@PathVariable Long id){
        CoordenadorResponseDom response = coordenadorService.carregarCoordenadorPorId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/carregar")
    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('COORDENADOR_CONTROLLER','CARREGAR')")
    public ResponseEntity<?> carregarCoordenador(){
        List<CoordenadorResponseDom> response = coordenadorService.carregarCoordenador();
        return ResponseEntity.ok(response);
    }
    @PostMapping("/criar")
    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('COORDENADOR_CONTROLLER','CRIAR')")
    public ResponseEntity<?> criarCoordenador(@RequestBody CoordenadorRequestDom coordenadorRequestDom){
        coordenadorService.criarCoordenador(coordenadorRequestDom);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @PostMapping("/associar/professor/{professorId}")
    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('COORDENADOR_CONTROLLER','ASSOCIAR')")
    public ResponseEntity<?> associarCoordenador(@PathVariable Long professorId){
        coordenadorService.associarCoordenador(professorId);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @PutMapping("/editar/{id}")
    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('COORDENADOR_CONTROLLER','EDITAR')")
    public ResponseEntity<?> editarCoordenador(@PathVariable Long id,@RequestBody CoordenadorRequestDom coordenadorRequestDom){
        coordenadorService.editarCoordenador(id, coordenadorRequestDom);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    @DeleteMapping("/excluir/{id}")
    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('COORDENADOR_CONTROLLER','EXCLUIR')")
    public ResponseEntity<?> excluirCoordenador(@PathVariable Long id){
        coordenadorService.excluirCoordenador(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

}
