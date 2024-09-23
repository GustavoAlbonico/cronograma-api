package com.cronograma.api.useCases.fase;

import com.cronograma.api.entitys.Fase;
import com.cronograma.api.exceptions.FaseException;
import com.cronograma.api.useCases.fase.domains.FaseRequestDom;
import com.cronograma.api.useCases.fase.implement.mappers.FaseMapper;
import com.cronograma.api.useCases.fase.implement.repositorys.FaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FaseService {
    @Autowired
    private FaseRepository faseRepository;

    @Autowired
    private FaseMapper faseMapper;

    public void criarFase(FaseRequestDom fase) {
        validarCampos(fase);
        Fase faseEntidade = faseMapper.faseRequestDomParaFase(fase);
        faseRepository.save(faseEntidade);
    }

    public void editarFase(Long id,FaseRequestDom fase){
       Fase faseEncontrada = faseRepository.findById(id).orElseThrow( () -> new FaseException("Nenhuma fase encontrada!"));
       validarCampos(fase);
       faseMapper.faseRequestDomParaFaseEncontrada(fase,faseEncontrada);
       faseRepository.save(faseEncontrada);
    }

    private void validarCampos(FaseRequestDom fase){
        List<String> errorMessages =  new ArrayList<>();

        if(fase.getNumero() == null){
            errorMessages.add("Número é um campo obrigatório!");
        } else if(fase.getNumero() < 1){
            errorMessages.add("Número inválido!");
        } else if(faseRepository.findByNumero(fase.getNumero()).isPresent()){
            errorMessages.add("Número da fase já existe");
        }

        if(!errorMessages.isEmpty()){
            throw new FaseException(errorMessages);
        }
    }
}
