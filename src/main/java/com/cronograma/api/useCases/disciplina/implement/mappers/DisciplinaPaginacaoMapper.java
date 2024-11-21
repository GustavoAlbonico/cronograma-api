package com.cronograma.api.useCases.disciplina.implement.mappers;

import com.cronograma.api.entitys.Disciplina;
import com.cronograma.api.useCases.disciplina.domains.DisciplinaResponseDom;
import com.cronograma.api.utils.paginacao.PaginacaoResponseUtil;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface DisciplinaPaginacaoMapper {

    default PaginacaoResponseUtil<List<DisciplinaResponseDom>> pageDisciplinaParaPaginacaoResponseUtilDisciplinaResponseDom(
            Page<Disciplina> disciplinasEncontradas,
            @Context DisciplinaMapper disciplinaMapper
    ){
        List<DisciplinaResponseDom> disciplinasResponseDom = disciplinasEncontradas.getContent()
                .stream().map(disciplinaMapper::disciplinaParaDisciplinaResponseDom)
                .toList();

        PaginacaoResponseUtil<List<DisciplinaResponseDom>> paginacaoResponseUtil= new PaginacaoResponseUtil<>();

        paginacaoResponseUtil.setData(disciplinasResponseDom);
        paginacaoResponseUtil.setExibindo(disciplinasEncontradas.getNumberOfElements());
        paginacaoResponseUtil.setTotalItens(disciplinasEncontradas.getTotalElements());
        paginacaoResponseUtil.setPaginaAtual(disciplinasEncontradas.getNumber());
        paginacaoResponseUtil.setTotalPaginas(disciplinasEncontradas.getTotalPages());
        paginacaoResponseUtil.setExisteAnterior(disciplinasEncontradas.hasPrevious());
        paginacaoResponseUtil.setExisteProxima(disciplinasEncontradas.hasNext());
        paginacaoResponseUtil.setPrimeiraPagina(disciplinasEncontradas.isFirst());
        paginacaoResponseUtil.setUltimaPagina(disciplinasEncontradas.isLast());

        return paginacaoResponseUtil;
    }
}
