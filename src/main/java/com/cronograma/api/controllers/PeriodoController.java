package com.cronograma.api.controllers;

import com.cronograma.api.useCases.fase.domains.FaseRequestDom;
import com.cronograma.api.useCases.fase.domains.FaseResponseDom;
import com.cronograma.api.useCases.periodo.PeriodoService;
import com.cronograma.api.useCases.periodo.domains.PeriodoRequestDom;
import com.cronograma.api.useCases.periodo.domains.PeriodoResponseDom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/periodo")
public class PeriodoController {

    @Autowired
    private PeriodoService periodoService;

    @GetMapping("/carregar/{id}")
    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('PERIODO_CONTROLLER','CARREGAR_POR_ID')")
    public ResponseEntity<?> carregarPeriodoPorId(@PathVariable Long id){
        PeriodoResponseDom response = periodoService.carregarPeriodoPorId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/carregar")
    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('PERIODO_CONTROLLER','CARREGAR')")
    public ResponseEntity<?> carregarPeriodo(){
        List<PeriodoResponseDom> response = periodoService.carregarPeriodo();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/criar")
    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('PERIODO_CONTROLLER','CRIAR')")
    public ResponseEntity<?> criarPeriodo(@RequestBody PeriodoRequestDom periodoRequestDom){
        periodoService.criarPeriodo(periodoRequestDom);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @PutMapping("/editar/{id}")
    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('PERIODO_CONTROLLER','EDITAR')")
    public ResponseEntity<?> editarPeriodo(@PathVariable Long id,@RequestBody PeriodoRequestDom periodoRequestDom){
        periodoService.editarPeriodo(id,periodoRequestDom);
        return ResponseEntity.ok(null);
    }

    @DeleteMapping("/excluir/{id}")
    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('PERIODO_CONTROLLER','EXCLUIR')")
    public ResponseEntity<?> excluirPeriodo(@PathVariable Long id){
        periodoService.excluirPeriodo(id);
        return ResponseEntity.ok(null);
    }
}
