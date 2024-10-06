package com.cronograma.api.controllers;

import com.cronograma.api.useCases.dataBloqueada.DataBloqueadaService;
import com.cronograma.api.useCases.dataBloqueada.domains.DataBloqueadaRequestDom;
import com.cronograma.api.useCases.dataBloqueada.domains.DataBloqueadaResponseDom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/databloqueada")
public class DataBloqueadaController {

    @Autowired
    private DataBloqueadaService dataBloqueadaService;

    @GetMapping("/carregar/{id}")
    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('DATA_BLOQUEADA_CONTROLLER','CARREGAR_POR_ID')")
    public ResponseEntity<?> carregarDataBloqueadaPorId(@PathVariable Long id){
        DataBloqueadaResponseDom response = dataBloqueadaService.carregarDataBloqueadaPorId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/carregar")
    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('DATA_BLOQUEADA_CONTROLLER','CARREGAR')")
    public ResponseEntity<?> carregarDataBloqueada(){
        List<DataBloqueadaResponseDom> response = dataBloqueadaService.carregarDataBloqueada();
        return ResponseEntity.ok(response);
    }
    @PostMapping("/criar")
    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('DATA_BLOQUEADA_CONTROLLER','CRIAR')")
    public ResponseEntity<?> criarDataBloqueada(@RequestBody DataBloqueadaRequestDom dataBloqueadaRequestDom){
        dataBloqueadaService.criarDataBloqueada(dataBloqueadaRequestDom);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @PutMapping("/editar/{id}")
    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('DATA_BLOQUEADA_CONTROLLER','EDITAR')")
    public ResponseEntity<?> editarDataBloqueada(@PathVariable Long id,@RequestBody DataBloqueadaRequestDom dataBloqueadaRequestDom){
        dataBloqueadaService.editarDataBloqueada(id, dataBloqueadaRequestDom);
        return ResponseEntity.ok(null);
    }

    @DeleteMapping("/excluir/{id}")
    @PreAuthorize("@nivelAcessoService.validarNivelAcesso('DATA_BLOQUEADA_CONTROLLER','EXCLUIR')")
    public ResponseEntity<?> excluirDataBloqueada(@PathVariable Long id){
        dataBloqueadaService.excluirDataBloqueada(id);
        return ResponseEntity.ok(null);
    }
}
