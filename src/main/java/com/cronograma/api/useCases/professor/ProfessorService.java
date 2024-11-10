package com.cronograma.api.useCases.professor;

import com.cronograma.api.entitys.*;
import com.cronograma.api.entitys.enums.DiaSemanaEnum;
import com.cronograma.api.entitys.enums.StatusEnum;
import com.cronograma.api.exceptions.CoordenadorException;
import com.cronograma.api.exceptions.FaseException;
import com.cronograma.api.exceptions.ProfessorException;
import com.cronograma.api.useCases.professor.domains.ProfessorFormularioRequestDom;
import com.cronograma.api.useCases.professor.domains.ProfessorRequestDom;
import com.cronograma.api.useCases.professor.domains.ProfessorResponseDom;
import com.cronograma.api.useCases.professor.domains.ProfessorUsuarioRequestDom;
import com.cronograma.api.useCases.professor.implement.mappers.ProfessorEncontradoMapper;
import com.cronograma.api.useCases.professor.implement.mappers.ProfessorFormularioMapper;
import com.cronograma.api.useCases.professor.implement.mappers.ProfessorMapper;
import com.cronograma.api.useCases.professor.implement.mappers.ProfessorPaginacaoMapper;
import com.cronograma.api.useCases.professor.implement.repositorys.*;
import com.cronograma.api.useCases.usuario.UsuarioService;
import com.cronograma.api.utils.paginacao.PaginacaoRequestUtil;
import com.cronograma.api.utils.paginacao.PaginacaoResponseUtil;
import com.cronograma.api.utils.regex.RegexUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfessorService {

    private final ProfessorRepository professorRepository;
    private final ProfessorUsuarioRepository professorUsuarioRepository;
    private final ProfessorCoordenadorRepository professorCoordenadorRepository;
    private final ProfessorDiaSemanaDisponivelRepository professorDiaSemanaDisponivelRepository;
    private final ProfessorNivelAcessoRepository professorNivelAcessoRepository;
    private final ProfessorDiaCronogramaRepository professorDiaCronogramaRepository;

    private final ProfessorMapper professorMapper;
    private final ProfessorPaginacaoMapper professorPaginacaoMapper;
    private final ProfessorFormularioMapper professorFormularioMapper;
    private final ProfessorEncontradoMapper professorEncontradoMapper;

    private final UsuarioService usuarioService;


    @Transactional(readOnly = true)
    public List<ProfessorResponseDom> carregarProfessorAtivo(){
        List<Professor> professoresEncontrados = professorRepository.findAllByStatusEnum(StatusEnum.ATIVO);

        return professoresEncontrados.stream()
                .map(professorMapper::professorParaProfessorResponseDom)
                .sorted(Comparator.comparing(ProfessorResponseDom::getNome))
                .toList();
    }

    @Transactional(readOnly = true)
    public ProfessorResponseDom carregarProfessorPorId(Long id){
        Professor professorEncontrado = professorRepository.findById(id)
                .orElseThrow(() -> new ProfessorException("Nenhum professor encontrado!"));

        return professorMapper.professorParaProfessorResponseDom(professorEncontrado);
    }

    @Transactional(readOnly = true)
    public PaginacaoResponseUtil<List<ProfessorResponseDom>> carregarProfessor(PaginacaoRequestUtil paginacaoRequestUtil){
        Page<Professor> professoresEncontrados = professorRepository.findAll(paginacaoRequestUtil.getPageRequest(List.of("usuario.nome","statusEnum")));
        return professorPaginacaoMapper.pageProfessorParaPaginacaoResponseUtilProfessorResponseDom(professoresEncontrados,professorMapper);
    }

    @Transactional
    public void criarProfessor(ProfessorRequestDom professorRequestDom){
        validarCampos(professorRequestDom, null);

        ProfessorUsuarioRequestDom professorUsuarioRequestDom =
                professorMapper.professorRequestDomParaProfessorUsuarioRequestDom(professorRequestDom);

        Usuario usuario = usuarioService.criarUsuario(professorUsuarioRequestDom,"PROFESSOR");

        List<DiaSemanaDisponivel> diasSemanaDisponiveisEncontrados = null;
        if(!professorRequestDom.getDiaSemanaDisponivelIds().isEmpty()){
            diasSemanaDisponiveisEncontrados =
                    professorDiaSemanaDisponivelRepository.findAllById(professorRequestDom.getDiaSemanaDisponivelIds());

            if(diasSemanaDisponiveisEncontrados.size() < professorRequestDom.getDiaSemanaDisponivelIds().size()){
                throw new ProfessorException("Uma ou mais dos dias da semana disponiveis informados não foram encontrados!");
            }
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

        List<DiaSemanaDisponivel> diasSemanaDisponiveisEncontrados = null;
        if(!professorRequestDom.getDiaSemanaDisponivelIds().isEmpty()){
            diasSemanaDisponiveisEncontrados =
                    professorDiaSemanaDisponivelRepository.findAllById(professorRequestDom.getDiaSemanaDisponivelIds());

            if(diasSemanaDisponiveisEncontrados.size() < professorRequestDom.getDiaSemanaDisponivelIds().size()){
                throw new ProfessorException("Um ou mais dos dias da semana disponiveis informados não foram encontrados!");
            }

        }

        validarDiasSemanaDisponveisEstaEmUso(id,diasSemanaDisponiveisEncontrados);

        professorEncontradoMapper.professorRequestDomParaProfessorEncontrado(professorRequestDom,professorEncontrado,diasSemanaDisponiveisEncontrados);

        usuarioService.editarUsuario(professorEncontrado.getUsuario().getId(),professorUsuarioRequestDom);
        professorRepository.save(professorEncontrado);
    }

    public Long associarProfessor(Long coordenadorId){
       Coordenador coordenadorEncontrado = professorCoordenadorRepository.findById(coordenadorId)
               .orElseThrow(() -> new ProfessorException("Nenhum coordenador encontrado!"));

       if (coordenadorEncontrado.getUsuario().getProfessor() != null){
           throw new ProfessorException("O coordenador já está associado!");
       }

       Professor professor = professorMapper.coordenadorEncontradoParaProfessor(coordenadorEncontrado);

       Set<NivelAcesso> niveisAcessos = coordenadorEncontrado.getUsuario().getNiveisAcesso();
       NivelAcesso nivelAcessoProfessor = professorNivelAcessoRepository.findByNome("PROFESSOR")
               .orElseThrow(() -> new CoordenadorException("Nenhum nivel de acesso encontrado!"));
       niveisAcessos.add(nivelAcessoProfessor);

       usuarioService.editarUsuarioNivelAcesso(coordenadorEncontrado.getUsuario().getId(),niveisAcessos);
       return professorRepository.save(professor).getId();
    }

    public void formularioProfessor(ProfessorFormularioRequestDom professorFormularioRequestDom){
        Professor professorEncontrado = usuarioService.buscarUsuarioAutenticado().getProfessor();

        if(professorFormularioRequestDom.getDiaSemanaDisponivelIds().isEmpty()){
            throw new ProfessorException("Não é possivel enviar um formulário vazio!");
        }

        List<DiaSemanaDisponivel> diasSemanaDisponiveisEncontrados =
                professorDiaSemanaDisponivelRepository.findAllById(professorFormularioRequestDom.getDiaSemanaDisponivelIds());

        if (diasSemanaDisponiveisEncontrados.isEmpty()){
            throw new ProfessorException("Nenhum dia da semana encontrado!");
        }

        if(diasSemanaDisponiveisEncontrados.size() < professorFormularioRequestDom.getDiaSemanaDisponivelIds().size()){
            throw new ProfessorException("Um ou mais dos dias da semana disponiveis informados não foram encontrados!");
        }

        validarDiasSemanaDisponveisEstaEmUso(professorEncontrado.getId(),diasSemanaDisponiveisEncontrados);

        professorFormularioMapper.professorFormularioRequestDomParaProfessor(professorFormularioRequestDom, professorEncontrado,diasSemanaDisponiveisEncontrados);
        professorRepository.save(professorEncontrado);
    }

    @Transactional(readOnly = true)
    public ProfessorResponseDom carregarDiaSemanaDisponivelProfessor(){
        Usuario usuario = usuarioService.buscarUsuarioAutenticado();
        return professorMapper.professorParaProfessorResponseDomDiasSemanaDisponiveis(usuario.getProfessor());
    }

    public void inativarProfessor(Long id){
        Professor professorEncontrado = professorRepository.findById(id)
                .orElseThrow( () -> new ProfessorException("Nenhum professor encontrado!"));

        if (professorEncontrado.getStatusEnum().equals(StatusEnum.INATIVO)){
            throw new ProfessorException("O professor já está Inativado");
        }
        if(!professorEncontrado.getDisciplinas().isEmpty()){
            throw new ProfessorException("O professor está sendo utilizado em disciplinas");
        }

        professorEncontrado.setStatusEnum(StatusEnum.INATIVO);
        professorRepository.save(professorEncontrado);

        Set<NivelAcesso> niveisAcesso = professorEncontrado.getUsuario().getNiveisAcesso().stream()
                .filter(nivelAcesso -> !nivelAcesso.getNome().equals("PROFESSOR"))
                .collect(Collectors.toSet());
        usuarioService.editarUsuarioNivelAcesso(professorEncontrado.getUsuario().getId(), niveisAcesso);

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

            Set<NivelAcesso> niveisAcessos = professorEncontrado.getUsuario().getNiveisAcesso();
            NivelAcesso nivelAcessoProfessor = professorNivelAcessoRepository.findByNome("PROFESSOR")
                    .orElseThrow(() -> new CoordenadorException("Nenhum nivel de acesso encontrado!"));
            niveisAcessos.add(nivelAcessoProfessor);

            usuarioService.editarUsuarioNivelAcesso(professorEncontrado.getUsuario().getId(),niveisAcessos);

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

        if(!errorMessages.isEmpty()){
            throw new CoordenadorException(errorMessages);
        }

    }

    private void validarDiasSemanaDisponveisEstaEmUso(Long professorId,List<DiaSemanaDisponivel> diasSemanaDisponiveisEncontrados){

        List<DiaSemanaEnum> diasSemanaEnumEmUso =
                professorDiaCronogramaRepository.buscarTodosDiasSemanaEnumPorCronogramaGeradoPorPeriodoAtivoPorProfessor(professorId);

        List<DiaSemanaEnum> errorDiasSemanaEmUso;

        if(diasSemanaDisponiveisEncontrados == null){
            errorDiasSemanaEmUso = diasSemanaEnumEmUso;
        } else {
            errorDiasSemanaEmUso = diasSemanaEnumEmUso.stream()
                    .filter(diaSemanaEnum -> diasSemanaDisponiveisEncontrados.stream()
                            .noneMatch(diaSemanaDisponivel -> diaSemanaDisponivel.getDiaSemanaEnum().equals(diaSemanaEnum)))
                    .toList();
        }

        if(!errorDiasSemanaEmUso.isEmpty()){
            final String diasSemanaEmUso = String.join(
                    (errorDiasSemanaEmUso.size() == 2 ? " e " :", "),
                    errorDiasSemanaEmUso.stream().map(DiaSemanaEnum::getDiaSemanaEnumLabel).toList()
            );

            throw new ProfessorException(
                    diasSemanaEmUso +
                    (errorDiasSemanaEmUso.size() > 1 ? " estão" :" está") +
                    " sendo utilizado" +
                    (errorDiasSemanaEmUso.size() > 1 ? "s" :"") +
                    " em um cronograma do período atual!"
            );
        }
    }

}