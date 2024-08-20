package com.cronograma.api.useCases.cronograma;

import com.cronograma.api.entitys.Disciplina;
import com.cronograma.api.useCases.cronograma.domains.CronogramaRequestDom;
import com.cronograma.api.useCases.disciplina.implement.repositorys.DisciplinaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CronogramaService {

    @Autowired
    private DisciplinaRepository disciplinaRepository;

    public List<String> gerarCronograma(CronogramaRequestDom cronograma){


        List<String> listaString = new ArrayList<>();
        return listaString;
    }
}
