package com.cronograma.api.utils.paginacao;

import com.cronograma.api.exceptions.PaginacaoException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PaginacaoRequestUtil {
    private Integer exibir;
    private Integer paginaAtual;

    public PageRequest getPageRequest(List<String> sortFields){
       this.validarCampos();

       return PageRequest.of(
               this.getPaginaAtual() - 1,
               this.getExibir(),
               Sort.by(
                       sortFields.stream()
                               .map(sortField -> new Sort.Order(Sort.Direction.ASC, sortField))
                               .toArray(Sort.Order[]::new)
               )
       );
    }

    public PageRequest getPageRequest(){
        this.validarCampos();

        return PageRequest.of(
                this.getPaginaAtual() - 1,
                this.getExibir()
        );
    }

    private void validarCampos(){
        if (this.exibir == null || this.exibir < 1){
            throw new PaginacaoException("Exibir é um campo obrigatório para paginação!");
        }
        if (this.paginaAtual == null || this.paginaAtual < 1){
            throw new PaginacaoException("PaginaAtual é um campo obrigatório para paginação!");
        }
    }
}
