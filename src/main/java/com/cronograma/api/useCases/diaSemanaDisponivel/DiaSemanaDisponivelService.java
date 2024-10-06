package com.cronograma.api.useCases.diaSemanaDisponivel;

import com.cronograma.api.entitys.DiaSemanaDisponivel;
import com.cronograma.api.useCases.diaSemanaDisponivel.domains.DiaSemanaDisponivelResponseDom;
import com.cronograma.api.useCases.diaSemanaDisponivel.implement.mappers.DiaSemanaDisponivelMapper;
import com.cronograma.api.useCases.diaSemanaDisponivel.implement.repositorys.DiaSemanaDisponivelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DiaSemanaDisponivelService {

    private final DiaSemanaDisponivelRepository diaSemanaDisponivelRepository;
    private final DiaSemanaDisponivelMapper diaSemanaDisponivelMapper;

    @Transactional(readOnly = true)
    public List<DiaSemanaDisponivelResponseDom> carregarDiaSemanaDisponivel(){
        List<DiaSemanaDisponivel> diasSemanaDisponiveis = diaSemanaDisponivelRepository.findAll();

        return diasSemanaDisponiveis.stream()
                .map(diaSemanaDisponivelMapper::diaSemanaDisponivelParaDiaSemanaDisponivelResponseDom)
                .sorted(Comparator.comparing(DiaSemanaDisponivelResponseDom::getDiaSemanaEnum))
                .toList();
    }
}
