package com.cronograma.api.useCases.professor;

import com.cronograma.api.entitys.Coordenador;
import com.cronograma.api.entitys.DiaSemanaDisponivel;
import com.cronograma.api.entitys.Professor;
import com.cronograma.api.entitys.Usuario;
import com.cronograma.api.entitys.enums.StatusEnum;
import com.cronograma.api.exceptions.CoordenadorException;
import com.cronograma.api.exceptions.FaseException;
import com.cronograma.api.exceptions.ProfessorException;
import com.cronograma.api.useCases.professor.domains.ProfessorFormularioRequestDom;
import com.cronograma.api.useCases.professor.domains.ProfessorRequestDom;
import com.cronograma.api.useCases.professor.domains.ProfessorResponseDom;
import com.cronograma.api.useCases.professor.domains.ProfessorUsuarioRequestDom;
import com.cronograma.api.useCases.professor.implement.mappers.ProfessorMapper;
import com.cronograma.api.useCases.professor.implement.mappers.ProfessorPaginacaoMapper;
import com.cronograma.api.useCases.professor.implement.repositorys.*;
import com.cronograma.api.useCases.usuario.UsuarioService;
import com.cronograma.api.utils.paginacao.PaginacaoRequestUtil;
import com.cronograma.api.utils.paginacao.PaginacaoResponseUtil;
import com.cronograma.api.utils.regex.RegexUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfessorService {

    private final ProfessorRepository professorRepository;
    private final ProfessorUsuarioRepository professorUsuarioRepository;
    private final ProfessorDisciplinaRepository professorDisciplinaRepository;
    private final ProfessorCoordenadorRepository professorCoordenadorRepository;
    private final ProfessorDiaSemanaDisponivelRepository professorDiaSemanaDisponivelRepository;

    private final ProfessorMapper professorMapper;
    private final ProfessorPaginacaoMapper professorPaginacaoMapper;

    private final UsuarioService usuarioService;


    public List<ProfessorResponseDom> carregarProfessorAtivo(){
        List<Professor> professoresEncontrados = professorRepository.findAllByStatusEnum(StatusEnum.ATIVO);

        return professoresEncontrados.stream()
                .map(professorMapper::professorParaProfessorResponseDom)
                .sorted(Comparator.comparing(ProfessorResponseDom::getNome))
                .toList();
    }

    public ProfessorResponseDom carregarProfessorPorId(Long id){
        Professor professorEncontrado = professorRepository.findById(id)
                .orElseThrow(() -> new ProfessorException("Nenhum professor encontrado!"));

        return professorMapper.professorParaProfessorResponseDom(professorEncontrado);
    }

    public PaginacaoResponseUtil<List<ProfessorResponseDom>> carregarProfessor(PaginacaoRequestUtil paginacaoRequestUtil){
        Page<Professor> professoresEncontrados = professorRepository.findAll(paginacaoRequestUtil.getPageRequest());
        return professorPaginacaoMapper.pageProfessorParaPaginacaoResponseUtilProfessorResponseDom(professoresEncontrados,professorMapper);
    }

    @Transactional
    public void criarProfessor(ProfessorRequestDom professorRequestDom){
        validarCampos(professorRequestDom, null);

        ProfessorUsuarioRequestDom professorUsuarioRequestDom =
                professorMapper.professorRequestDomParaProfessorUsuarioRequestDom(professorRequestDom);

        Usuario usuario = usuarioService.criarUsuario(professorUsuarioRequestDom,"PROFESSOR");

        List<DiaSemanaDisponivel> diasSemanaDisponiveisEncontrados =
                professorDiaSemanaDisponivelRepository.findAllById(professorRequestDom.getDiaSemanaDisponivelIds());

        if (diasSemanaDisponiveisEncontrados.isEmpty()){
            throw new ProfessorException("Nenhum dia da semana encontrado!");
        }

        Professor professor = professorMapper.professorRequestDomParaProfessor(professorRequestDom,usuario,diasSemanaDisponiveisEncontrados);
        professorRepository.save(professor);
    }

    @Transactional
    public void editarProfessor(Long id, ProfessorRequestDom professorRequestDom){
        Professor professorEncontrado = professorRepository.findById(id)
                .orElseThrow(() -> new ProfessorException("Nenhum professor encontrado!"));

        validarCampos(professorRequestDom,professorEncontrado.getUsuario().getCpf());

        ProfessorUsuarioRequestDom professorUsuarioRequestDom =
                professorMapper.professorRequestDomParaProfessorUsuarioRequestDom(professorRequestDom);

        List<DiaSemanaDisponivel> diasSemanaDisponiveisEncontrados =
                professorDiaSemanaDisponivelRepository.findAllById(professorRequestDom.getDiaSemanaDisponivelIds());

        if (diasSemanaDisponiveisEncontrados.isEmpty()){
            throw new ProfessorException("Nenhum dia da semana encontrado!");
        }

        professorMapper.professorRequestDomParaProfessorEncontrado(professorRequestDom,professorEncontrado,diasSemanaDisponiveisEncontrados);

        usuarioService.editarUsuario(professorEncontrado.getUsuario().getId(),professorUsuarioRequestDom);
        professorRepository.save(professorEncontrado);
    }

    public Long associarProfessor(Long coordenadorId){
        Coordenador coordenadorEncontrado = professorCoordenadorRepository.findById(coordenadorId)
                .orElseThrow(() -> new ProfessorException("Nenhum coordenador encontrado!"));

        if (coordenadorEncontrado.getUsuario().getProfessor() != null){
            throw new ProfessorException("O coordenador já está associado a um professor");
        }

        Professor professor = professorMapper.coordenadorEncontradoParaProfessor(coordenadorEncontrado);
       return professorRepository.save(professor).getId();
    }

    public void formularioProfessor(ProfessorFormularioRequestDom professorFormularioRequestDom){
        Professor professorEncontrado = usuarioService.buscarUsuarioAutenticado().getProfessor();

        if (!professorEncontrado.getDiasSemanaDisponivel().isEmpty()){
            throw new ProfessorException("Os dias da semana disponíveis já foram informados neste periodo!");
        }

        List<DiaSemanaDisponivel> diasSemanaDisponiveisEncontrados =
                professorDiaSemanaDisponivelRepository.findAllById(professorFormularioRequestDom.getDiaSemanaDisponivelIds());

        if (diasSemanaDisponiveisEncontrados.isEmpty()){
            throw new ProfessorException("Nenhum dia da semana encontrado!");
        }

        professorMapper.professorFormularioRequestDomParaProfessor(professorFormularioRequestDom, professorEncontrado,diasSemanaDisponiveisEncontrados);
        professorRepository.save(professorEncontrado);
    }

    public void inativarProfessor(Long id){
        Professor professorEncontrado = professorRepository.findById(id)
                .orElseThrow( () -> new ProfessorException("Nenhum professor encontrado!"));

        if (professorEncontrado.getStatusEnum().equals(StatusEnum.INATIVO)){
            throw new ProfessorException("O professor já está Inativado");
        }
        if(professorDisciplinaRepository.existsByProfessorId(id)){
            throw new ProfessorException("O professor está sendo utilizado em disciplinas");
        }

        professorEncontrado.setStatusEnum(StatusEnum.INATIVO);
        professorRepository.save(professorEncontrado);

        if(professorEncontrado.getUsuario().getCoordenador() == null && professorEncontrado.getUsuario().getAluno() == null){
            usuarioService.inativarUsuario(professorEncontrado.getUsuario().getId());
        }
    }

    public void ativarProfessor(Long id){
        Professor professorEncontrado = professorRepository.findById(id)
                .orElseThrow( () -> new ProfessorException("Nenhum professor encontrado!"));

        if (professorEncontrado.getStatusEnum().equals(StatusEnum.ATIVO)){
            throw new FaseException("O professor já está Ativo");
        } else {
            professorEncontrado.setStatusEnum(StatusEnum.ATIVO);
            professorRepository.save(professorEncontrado);

            if(professorEncontrado.getUsuario().getStatusEnum().equals(StatusEnum.INATIVO)){
                usuarioService.ativarUsuario(professorEncontrado.getUsuario().getId());
            }
        }
    }

    private void validarCampos(ProfessorRequestDom professor, String cpfAtual){
        List<String> errorMessages =  new ArrayList<>();

        if(professor.getNome() == null || professor.getNome().isBlank()){
            errorMessages.add("Nome é um campo obrigatório!");
        }

        if(professor.getCpf() == null || RegexUtil.retornarNumeros(professor.getCpf()).isBlank()){
            errorMessages.add("Cpf é um campo obrigatório!");
        } else if(RegexUtil.retornarNumeros(professor.getCpf()).length() != 11){
            errorMessages.add("Cpf inválido!");
        } else if (cpfAtual != null){
            if (
                !RegexUtil.retornarNumeros(professor.getCpf()).equals(cpfAtual) &&
                professorUsuarioRepository.existsByCpf(RegexUtil.retornarNumeros(professor.getCpf()))
            ) {
                errorMessages.add("Cpf já está sendo utilizado!");
            }
        } else if (professorUsuarioRepository.existsByCpf(RegexUtil.retornarNumeros(professor.getCpf()))) {
            errorMessages.add("Cpf já está sendo utilizado!");
        }

        if(professor.getEmail() == null || professor.getEmail().isBlank()){
            errorMessages.add("E-mail é um campo obrigatório!");
        } else if (!RegexUtil.validarEmail(professor.getEmail())) {
            errorMessages.add("E-mail inválido!");
        }

        if(professor.getTelefone() == null || RegexUtil.retornarNumeros(professor.getTelefone()).isBlank()){
            errorMessages.add("Telefone é um campo obrigatório!");
        }else if(
                RegexUtil.retornarNumeros(professor.getTelefone()).length() > 50 ||
                        RegexUtil.retornarNumeros(professor.getTelefone()).length() < 11 ||
                        RegexUtil.retornarNumeros(professor.getTelefone()).charAt(2) != '9'
        ){
            errorMessages.add("Telefone inválido!");
        }

        if(professor.getDiaSemanaDisponivelIds() == null || professor.getDiaSemanaDisponivelIds().isEmpty()){
            throw new ProfessorException("Nenhum dia da semana informado!");
        }

        if(!errorMessages.isEmpty()){
            throw new CoordenadorException(errorMessages);
        }

    }

}