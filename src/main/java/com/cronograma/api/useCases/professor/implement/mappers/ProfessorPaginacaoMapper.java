package com.cronograma.api.useCases.professor.implement.mappers;

import com.cronograma.api.entitys.Professor;
import com.cronograma.api.useCases.diaSemanaDisponivel.domains.DiaSemanaDisponivelResponseDom;
import com.cronograma.api.useCases.fase.domains.FaseResponseDom;
import com.cronograma.api.useCases.professor.domains.ProfessorResponseDom;
import com.cronograma.api.utils.paginacao.PaginacaoResponseUtil;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;

import java.util.Comparator;
import java.util.List;
@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ProfessorPaginacaoMapper {

    default PaginacaoResponseUtil<List<ProfessorResponseDom>> pageProfessorParaPaginacaoResponseUtilProfessorResponseDom(
            Page<Professor> professoresEncontrados,
            @Context ProfessorMapper professorMapper
    ){
        List<ProfessorResponseDom> professoresResponseDom = professoresEncontrados.getContent()
                .stream().map(professorMapper::professorParaProfessorResponseDom)
                .peek(professorResponseDom ->
                        professorResponseDom.setDiasSemanaDisponiveis(
                                professorResponseDom.getDiasSemanaDisponiveis().stream()
                                        .sorted(Comparator.comparing(DiaSemanaDisponivelResponseDom::getDiaSemanaEnum))
                                        .toList()
                        )
                )
                .toList();

        PaginacaoResponseUtil<List<ProfessorResponseDom>> paginacaoResponseUtil= new PaginacaoResponseUtil<>();

        paginacaoResponseUtil.setData(professoresResponseDom);
        paginacaoResponseUtil.setExibindo(professoresEncontrados.getNumberOfElements());
        paginacaoResponseUtil.setTotalItens(professoresEncontrados.getTotalElements());
        paginacaoResponseUtil.setPaginaAtual(professoresEncontrados.getNumber());
        paginacaoResponseUtil.setTotalPaginas(professoresEncontrados.getTotalPages());
        paginacaoResponseUtil.setExisteAnterior(professoresEncontrados.hasPrevious());
        paginacaoResponseUtil.setExisteProxima(professoresEncontrados.hasNext());
        paginacaoResponseUtil.setPrimeiraPagina(professoresEncontrados.isFirst());
        paginacaoResponseUtil.setUltimaPagina(professoresEncontrados.isLast());

        return paginacaoResponseUtil;
    }
}
