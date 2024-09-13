package com.cronograma.api.controllers;

import com.cronograma.api.exceptions.CronogramaException;
import com.cronograma.api.infra.exceptions.RestErrorMessage;
import com.cronograma.api.useCases.cronograma.CronogramaService;
import com.cronograma.api.useCases.cronograma.domains.CronogramaRequestDom;
import com.cronograma.api.useCases.cronograma.domains.TesteResponseCronogramaDom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cronograma")
public class CronogramaController {

    @Autowired
    private CronogramaService cronogramaService;

    @PostMapping("/gerar")
    public ResponseEntity<?> gerarCronograma(@RequestBody CronogramaRequestDom cronograma) {

        try {
            List<TesteResponseCronogramaDom> response = cronogramaService.gerarCronograma(cronograma);
            System.out.println("aaaaa");
            return ResponseEntity.status(201).body(response);
        } catch (CronogramaException cronogramaException) {
            RestErrorMessage errorResponse = new RestErrorMessage(HttpStatus.BAD_REQUEST,cronogramaException.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception exception) {
            RestErrorMessage errorResponse = new RestErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR,"Erro não mapeado!");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}