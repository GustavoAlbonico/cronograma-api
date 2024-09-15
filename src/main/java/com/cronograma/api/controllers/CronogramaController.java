package com.cronograma.api.controllers;

import com.cronograma.api.entitys.enums.EventoStatusEnum;
import com.cronograma.api.exceptions.CronogramaException;
import com.cronograma.api.infra.exceptions.RestErrorMessage;
import com.cronograma.api.useCases.cronograma.CronogramaService;
import com.cronograma.api.useCases.cronograma.domains.CronogramaRequestDom;
import com.cronograma.api.useCases.cronograma.domains.TesteResponseCronogramaDom;
import com.cronograma.api.useCases.evento.domains.EventoRequestDom;
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
public class CronogramaController {

}