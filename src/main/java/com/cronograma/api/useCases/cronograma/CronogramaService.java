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

        //verificar se existe apenas um periodo ativo
        //verificar se todas as disciplinas possuem professor se sim verificar se de seg a sabado tem professor cadastrado

        //buscar a quantidade de dias que as disciplinas vao utilizar e com isso definir quantos dias da semana elas vao utilizar
        //buscar a quantidade de dias disponivel por dia da semana com base no periodo

        //vai pegar a disciplina do professor X junto disso os dias que precisa essa disciplina
        //e os dias que esse professor tem disponivel com a quantidade de dias disponiveis pelo periodo

        //LEMBRETE ORDENAR A LISTA PELO PROFESSOR QUE TIVER MENOS DIAS DISPONIVEIS + AULA QUE TIVER MENOS DIAS

        //TALVEZ LOOP COM BASE NA QUANTIDADE DE DIAS
        //vai fazer uma subtração da quantidade de dia disponivel x quantidade da disciplina
        //e vai definir o dia da semana que sobra menos dias e adicionar esse dia + disciplina uma lista

        //se na proxima disciplina não tiver dia disponivel para ela entao vai pegar a lista de dia + disciplina e adiciona

        //se o dia escolhido no loop for igual a um dia que está na lista PENSAR
        // a uma listaBloqueados (nessa lista se cair vai ignorar e pular para o proximo)




        //verificar se existe conflito



        List<String> listaString = new ArrayList<>();
        return listaString;
    }
}
