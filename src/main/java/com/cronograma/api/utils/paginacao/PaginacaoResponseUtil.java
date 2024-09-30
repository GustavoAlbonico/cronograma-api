package com.cronograma.api.utils.paginacao;

import lombok.Getter;

@Getter
public class PaginacaoResponseUtil<T> {
    private T data;
    private Integer exibindo;
    private Long totalItens;
    private Integer paginaAtual;
    private Integer totalPaginas;
    private Boolean existeAnterior;
    private Boolean existeProxima;
    private Boolean primeiraPagina;
    private Boolean ultimaPagina;

    public void setData(T data) {
        this.data = data;
    }

    public void setExibindo(Integer exibindo) {
        this.exibindo = exibindo;
    }

    public void setTotalItens(Long totalItens) {
        this.totalItens = totalItens;
    }

    public void setPaginaAtual(Integer paginaAtual) {
        this.paginaAtual = paginaAtual + 1;
    }

    public void setTotalPaginas(Integer totalPaginas) {
        this.totalPaginas = totalPaginas;
    }

    public void setExisteAnterior(Boolean existeAnterior) {
        this.existeAnterior = existeAnterior;
    }

    public void setExisteProxima(Boolean existeProxima) {
        this.existeProxima = existeProxima;
    }

    public void setPrimeiraPagina(Boolean primeiraPagina) {
        this.primeiraPagina = primeiraPagina;
    }

    public void setUltimaPagina(Boolean ultimaPagina) {
        this.ultimaPagina = ultimaPagina;
    }
}
