package com.cronograma.api.useCases.aluno.implement.mappers;

import com.cronograma.api.entitys.Aluno;
import com.cronograma.api.entitys.Usuario;
import com.cronograma.api.useCases.aluno.domains.AlunoFaseResponseDom;
import com.cronograma.api.useCases.aluno.domains.AlunoResponseDom;
import com.cronograma.api.utils.paginacao.PaginacaoResponseUtil;
import org.mapstruct.*;
import org.springframework.data.domain.Page;

import java.util.Comparator;
import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface AlunoPaginacaoMapper {

    default PaginacaoResponseUtil<List<AlunoResponseDom>> pageAlunoParaPaginacaoResponseUtilAlunoResponseDom(
            Page<Aluno> alunosEncontrados
    ){
        List<AlunoResponseDom> alunosResponseDom = alunosEncontrados.getContent()
                .stream().map(this::alunoParaAlunoResponseDom)
                .peek(alunoParaAlunoResponseDom ->
                        alunoParaAlunoResponseDom.setFases(
                                alunoParaAlunoResponseDom.getFases().stream()
                                        .sorted(Comparator.comparing(AlunoFaseResponseDom::getNumero))
                                        .toList()
                        )
                )
                .toList();

        PaginacaoResponseUtil<List<AlunoResponseDom>> paginacaoResponseUtil= new PaginacaoResponseUtil<>();

        paginacaoResponseUtil.setData(alunosResponseDom);
        paginacaoResponseUtil.setExibindo(alunosEncontrados.getNumberOfElements());
        paginacaoResponseUtil.setTotalItens(alunosEncontrados.getTotalElements());
        paginacaoResponseUtil.setPaginaAtual(alunosEncontrados.getNumber());
        paginacaoResponseUtil.setTotalPaginas(alunosEncontrados.getTotalPages());
        paginacaoResponseUtil.setExisteAnterior(alunosEncontrados.hasPrevious());
        paginacaoResponseUtil.setExisteProxima(alunosEncontrados.hasNext());
        paginacaoResponseUtil.setPrimeiraPagina(alunosEncontrados.isFirst());
        paginacaoResponseUtil.setUltimaPagina(alunosEncontrados.isLast());

        return paginacaoResponseUtil;
    }

    @Mapping(source = "usuario",target = "nome", qualifiedByName = "buscarNome")
    @Mapping(source = "usuario",target = "cpf", qualifiedByName = "buscarCpf")
    @Mapping(source = "usuario",target = "email", qualifiedByName = "buscarEmail")
    AlunoResponseDom alunoParaAlunoResponseDom(Aluno aluno);

    @Named("buscarNome")
    default String buscarNome(Usuario usuario){
        return usuario.getNome();
    }
    @Named("buscarEmail")
    default String buscarEmail(Usuario usuario){
        return usuario.getEmail();
    }

    @Named("buscarCpf")
    default String buscarCpf(Usuario usuario){
        return usuario.getCpf();
    }

}
