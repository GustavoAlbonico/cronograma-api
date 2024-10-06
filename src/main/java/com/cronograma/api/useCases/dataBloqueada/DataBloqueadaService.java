package com.cronograma.api.useCases.dataBloqueada;

import com.cronograma.api.entitys.DataBloqueada;
import com.cronograma.api.exceptions.DataBloqueadaException;
import com.cronograma.api.useCases.dataBloqueada.domains.DataBloqueadaRequestDom;
import com.cronograma.api.useCases.dataBloqueada.domains.DataBloqueadaResponseDom;
import com.cronograma.api.useCases.dataBloqueada.implement.mappers.DataBloqueadaMapper;
import com.cronograma.api.useCases.dataBloqueada.implement.repositorys.DataBloqueadaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DataBloqueadaService {

    private final DataBloqueadaRepository dataBloqueadaRepository;
    private final DataBloqueadaMapper dataBloqueadaMapper;


    @Transactional(readOnly = true)
    public DataBloqueadaResponseDom carregarDataBloqueadaPorId(Long id){
        DataBloqueada dataBloqueadaEncontrada = dataBloqueadaRepository.findById(id)
                .orElseThrow(() -> new DataBloqueadaException("Nenhuma data bloqueada encontrada!"));

        return dataBloqueadaMapper.dataBloqueadaParaDataBloqueadaResponseDom(dataBloqueadaEncontrada);
    }
    @Transactional(readOnly = true)
    public List<DataBloqueadaResponseDom> carregarDataBloqueada(){
        List<DataBloqueada> datasBloqueadasEncontradas = dataBloqueadaRepository.findAll();

        return datasBloqueadasEncontradas.stream()
                .map(dataBloqueadaMapper::dataBloqueadaParaDataBloqueadaResponseDom)
                .sorted(Comparator.comparing(DataBloqueadaResponseDom::getData))
                .toList();
    }

    public void criarDataBloqueada(DataBloqueadaRequestDom dataBloqueadaRequestDom){
        validarCampos(dataBloqueadaRequestDom,null);
        DataBloqueada dataBloqueada = dataBloqueadaMapper.dataBloqueadaResquestDomParaDataBloqueada(dataBloqueadaRequestDom);
        dataBloqueadaRepository.save(dataBloqueada);
    }

    public void editarDataBloqueada(Long id,DataBloqueadaRequestDom dataBloqueadaRequestDom){
        DataBloqueada dataBloqueadaEncontrada = dataBloqueadaRepository.findById(id)
                .orElseThrow(() -> new DataBloqueadaException("Nenhuma data bloqueada encontrada!"));

        validarCampos(dataBloqueadaRequestDom,dataBloqueadaRequestDom.getData());
        dataBloqueadaMapper.dataBloqueadaRequestDomParaDataBloqueadaEncontrada(dataBloqueadaRequestDom,dataBloqueadaEncontrada);
        dataBloqueadaRepository.save(dataBloqueadaEncontrada);
    }

    public void excluirDataBloqueada(Long id){
        DataBloqueada dataBloqueadaEncontrada = dataBloqueadaRepository.findById(id)
                .orElseThrow(() -> new DataBloqueadaException("Nenhuma data bloqueada encontrada!"));
        dataBloqueadaRepository.deleteById(dataBloqueadaEncontrada.getId());
    }

    private void validarCampos(DataBloqueadaRequestDom dataBloqueada, LocalDate dataAtual){
        List<String> errorMessages =  new ArrayList<>();

        if (dataBloqueada.getMotivo() == null || dataBloqueada.getMotivo().isBlank()){
            errorMessages.add("Motivo é um campo obrigatório!");
        }

        if (dataBloqueada.getData() == null){
            errorMessages.add("Data é um campo obrigatório!");
        } else if(dataAtual != null){
            if (
                !dataBloqueada.getData().isEqual(dataAtual) &&
                dataBloqueadaRepository.existsByData(dataBloqueada.getData())
            ) {
                errorMessages.add("Data já cadastrada!");
            }
        } else if (dataBloqueadaRepository.existsByData(dataBloqueada.getData())) {
            errorMessages.add("Data já cadastrada!");
        }

        if(!errorMessages.isEmpty()){
            throw new DataBloqueadaException(errorMessages);
        }

    }
}
