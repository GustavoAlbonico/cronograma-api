package com.cronograma.api.useCases.aluno;

import com.cronograma.api.entitys.*;
import com.cronograma.api.exceptions.AlunoException;
import com.cronograma.api.exceptions.UsuarioException;
import com.cronograma.api.useCases.aluno.domains.*;
import com.cronograma.api.useCases.aluno.implement.mappers.AlunoImportarMapper;
import com.cronograma.api.useCases.aluno.implement.mappers.AlunoMapper;
import com.cronograma.api.useCases.aluno.implement.mappers.AlunoPaginacaoMapper;
import com.cronograma.api.useCases.aluno.implement.repositorys.*;
import com.cronograma.api.useCases.usuario.UsuarioService;
import com.cronograma.api.utils.paginacao.PaginacaoRequestUtil;
import com.cronograma.api.utils.paginacao.PaginacaoResponseUtil;
import com.cronograma.api.utils.regex.RegexUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final AlunoUsuarioRepository alunoUsuarioRepository;
    private final AlunoCursoRepository alunoCursoRepository;
    private final AlunoFaseRepository alunoFaseRepository;
    private final AlunoNivelAcessoRepository alunoNivelAcessoRepository;

    private final AlunoMapper alunoMapper;
    private final AlunoPaginacaoMapper alunoPaginacaoMapper;
    private final AlunoImportarMapper alunoImportarMapper;

    private final UsuarioService usuarioService;

    @Transactional(readOnly = true)
    public AlunoResponseDom carregarAlunoPorId(Long id){
        Aluno alunoEncontrado = alunoRepository.findById(id)
                .orElseThrow(() -> new AlunoException("Nenhum Aluno encontrado!"));

        return alunoMapper.alunoParaAlunoResponseDom(alunoEncontrado);
    }

    @Transactional(readOnly = true)
    public PaginacaoResponseUtil<List<AlunoResponseDom>> carregarAluno(Long cursoId, Long faseId, PaginacaoRequestUtil paginacaoRequestUtil){
        Page<Aluno> alunosEncontrados = alunoRepository.buscarTodosPorCursoPorFase(cursoId,faseId,paginacaoRequestUtil.getPageRequest());
        return alunoPaginacaoMapper.pageAlunoParaPaginacaoResponseUtilAlunoResponseDom(alunosEncontrados);
    }

    @Transactional
    public void importarAluno(AlunoImportarRequestDom alunoImportarRequestDom) throws IOException {
        MultipartFile arquivo = alunoImportarRequestDom.getArquivo();

        if (!Objects.equals(arquivo.getContentType(), "text/csv")){
            throw new AlunoException("Formato de arquivo inválido!");
        }

        Reader reader = new InputStreamReader(arquivo.getInputStream());
        CSVFormat csvFormat = CSVFormat.Builder.create()
                .setDelimiter(';')
                .setIgnoreSurroundingSpaces(true)
                .setHeader()
                .build();
        Iterable<CSVRecord> linhas = csvFormat.parse(reader);

        List<AlunoImportarDom> alunosImportarDom = new ArrayList<>();
        for (CSVRecord linha : linhas) {
            alunosImportarDom.add(alunoImportarMapper.csvRecordParaAlunoImportarDom(linha));
        }

        validarCamposImportar(alunosImportarDom);

        Curso cursoEncontrado = alunoCursoRepository.findById(alunoImportarRequestDom.getCursoId())
                .orElseThrow(() -> new AlunoException("Nenhum Curso encontrado!"));

        Fase faseEncontrada = alunoFaseRepository.findById(alunoImportarRequestDom.getFaseId())
                .orElseThrow(() -> new AlunoException("Nenhuma Fase encontrada!"));

        Set<NivelAcesso> niveisAcesso = alunoNivelAcessoRepository.findByNome("ALUNO")
                .orElseThrow(() -> new AlunoException("Nenhum nivel de acesso encontrado!"));


        for (AlunoImportarDom alunoImportarDom : alunosImportarDom){
            AlunoUsuarioRequestDom alunoUsuarioRequestDom =
                    alunoImportarMapper.alunoImportarDomParaAlunoUsuarioRequestDom(alunoImportarDom);

            Usuario usuario = usuarioService.criarUsuarioImportar(alunoUsuarioRequestDom, niveisAcesso);
            Aluno aluno = alunoImportarMapper.alunoImportarDomParaAluno(alunoImportarDom, usuario, cursoEncontrado, faseEncontrada);
            alunoRepository.save(aluno);
        }
    }


    @Transactional
    public void criarAluno(AlunoRequestDom alunoRequestDom){
        validarCampos(alunoRequestDom,null);

        AlunoUsuarioRequestDom alunoUsuarioRequestDom =
                alunoMapper.alunoRequestDomParaAlunoUsuarioRequestDom(alunoRequestDom);

        Curso cursoEncontrado = alunoCursoRepository.findById(alunoRequestDom.getCursoId())
                .orElseThrow(() -> new AlunoException("Nenhum Curso encontrado!"));

        List<Fase> fasesEncontradas = alunoFaseRepository.findAllById(alunoRequestDom.getFaseIds());

        if(fasesEncontradas.isEmpty()){
            throw new AlunoException("Nenhuma Fase encontrada!");
        }
        if(fasesEncontradas.size() < alunoRequestDom.getFaseIds().size()){
            throw new AlunoException("Uma ou mais das Fases informadas não foram encontradas!");
        }

        Usuario usuario = usuarioService.criarUsuario(alunoUsuarioRequestDom,"ALUNO");
        Aluno aluno = alunoMapper.alunoRequestDomParaAluno(alunoRequestDom,usuario,cursoEncontrado,fasesEncontradas);

        alunoRepository.save(aluno);
    }

    @Transactional
    public void editarAluno(Long id, AlunoRequestDom alunoRequestDom){
        Aluno alunoEncontrado = alunoRepository.findById(id)
                .orElseThrow(() -> new AlunoException("Nenhum aluno encontrado!"));

        validarCampos(alunoRequestDom,alunoEncontrado.getUsuario().getCpf());

        AlunoUsuarioRequestDom alunoUsuarioRequestDom =
                alunoMapper.alunoRequestDomParaAlunoUsuarioRequestDom(alunoRequestDom);

        Curso cursoEncontrado = alunoCursoRepository.findById(alunoRequestDom.getCursoId())
                .orElseThrow(() -> new AlunoException("Nenhum Curso encontrado!"));

        List<Fase> fasesEncontradas = alunoFaseRepository.findAllById(alunoRequestDom.getFaseIds());

        if(fasesEncontradas.isEmpty()){
            throw new AlunoException("Nenhuma Fase encontrada!");
        }
        if(fasesEncontradas.size() < alunoRequestDom.getFaseIds().size()){
            throw new AlunoException("Uma ou mais das Fases informadas não foram encontradas!");
        }

        alunoMapper.alunoRequestDomParaAlunoEncontrado(alunoRequestDom,alunoEncontrado,cursoEncontrado,fasesEncontradas);

        usuarioService.editarUsuario(alunoEncontrado.getUsuario().getId(),alunoUsuarioRequestDom);
        alunoRepository.save(alunoEncontrado);
    }

    @Transactional
    public void excluirAluno(Long id){
        Aluno alunoEncontrado = alunoRepository.findById(id)
                .orElseThrow(() -> new AlunoException("Nenhum coordenador encontrado!"));

        alunoRepository.deleteById(id);

        if(alunoEncontrado.getUsuario().getCoordenador() == null && alunoEncontrado.getUsuario().getProfessor() == null){
            usuarioService.excluirUsuario(alunoEncontrado.getUsuario().getId());
        }
    }

    private void validarCampos(AlunoRequestDom aluno, String cpfAtual){
        List<String> errorMessages =  new ArrayList<>();

        if(aluno.getNome() == null || aluno.getNome().isBlank()){
            errorMessages.add("Nome é um campo obrigatório!");
        }

        if(aluno.getCpf() == null || RegexUtil.retornarNumeros(aluno.getCpf()).isBlank()){
            errorMessages.add("Cpf é um campo obrigatório!");
        } else if(RegexUtil.retornarNumeros(aluno.getCpf()).length() != 11){
            errorMessages.add("Cpf inválido!");
        }else if (cpfAtual != null){
            if (
                    !RegexUtil.retornarNumeros(aluno.getCpf()).equals(cpfAtual) &&
                            alunoUsuarioRepository.existsByCpf(RegexUtil.retornarNumeros(aluno.getCpf()))
            ) {
                errorMessages.add("Cpf já está sendo utilizado!");
            }
        } else if (alunoUsuarioRepository.existsByCpf(RegexUtil.retornarNumeros(aluno.getCpf()))) {
            errorMessages.add("Cpf já está sendo utilizado!");
        }

        if(aluno.getEmail() == null || aluno.getEmail().isBlank()){
            errorMessages.add("E-mail é um campo obrigatório!");
        } else if (!RegexUtil.validarEmail(aluno.getEmail())) {
            errorMessages.add("E-mail inválido!");
        }

        if(aluno.getTelefone() == null || RegexUtil.retornarNumeros(aluno.getTelefone()).isBlank()){
            errorMessages.add("Telefone é um campo obrigatório!");
        }else if(
                RegexUtil.retornarNumeros(aluno.getTelefone()).length() > 50 ||
                        RegexUtil.retornarNumeros(aluno.getTelefone()).length() < 11 ||
                        RegexUtil.retornarNumeros(aluno.getTelefone()).charAt(2) != '9'
        ){
            errorMessages.add("Telefone inválido!");
        }

        if (aluno.getCursoId() == null){
            errorMessages.add("Curso é um campo obrigatório!");
        }

        if (aluno.getFaseIds() == null || aluno.getFaseIds().isEmpty()){
            errorMessages.add("Fase é um campo obrigatório!");
        }

        if(!errorMessages.isEmpty()){
            throw new AlunoException(errorMessages);
        }

    }

    private void validarCamposImportar(List<AlunoImportarDom> alunos){
        List<String> errorMessages =  new ArrayList<>();

        for (int posicao = 0 ; posicao < alunos.size() ;posicao ++){
            String errorMessage = "";

            if(alunos.get(posicao).getCpf() == null || RegexUtil.retornarNumeros(alunos.get(posicao).getCpf()).isBlank()){
                errorMessage = errorMessage.concat(" Cpf");
            } else if(RegexUtil.retornarNumeros(alunos.get(posicao).getCpf()).length() != 11){
                errorMessage = errorMessage.concat(" Cpf");
            } else if (alunoUsuarioRepository.existsByCpf(RegexUtil.retornarNumeros(alunos.get(posicao).getCpf()))) {
                if (alunos.get(posicao).getNome() != null && !alunos.get(posicao).getNome().isBlank()){
                    errorMessages.add(alunos.get(posicao).getNome() + " possui Cpf que está sendo utilizado!");
                } else {
                    errorMessages.add("Aluno na " + (posicao + 1) + "ª linha Cpf que está sendo utilizado!");
                }
            } else {
              int finalPosicao = posicao;
              if(
                 alunos.stream().anyMatch(alunoImportarDom ->
                         !alunoImportarDom.getNome().equals(alunos.get(finalPosicao).getNome()) &&
                                 alunoImportarDom.getCpf().equals(alunos.get(finalPosicao).getCpf()))
              ){
                  if (alunos.get(posicao).getNome() != null && !alunos.get(posicao).getNome().isBlank()){
                      errorMessages.add(alunos.get(posicao).getNome() + " possui Cpf que está sendo utilizado no arquivo!");
                  } else {
                      errorMessages.add("Aluno na "+ (posicao + 1) + "ª linha Cpf que está sendo utilizado no arquivo!" );
                  }
              }
            }

            if(alunos.get(posicao).getNome() == null || alunos.get(posicao).getNome().isBlank()){
                errorMessage = errorMessage.concat(", Nome");
            }

            if(alunos.get(posicao).getEmail() == null || alunos.get(posicao).getEmail().isBlank()){
                errorMessage = errorMessage.concat(" E-mail");
            } else if (!RegexUtil.validarEmail(alunos.get(posicao).getEmail())) {
                errorMessage = errorMessage.concat(" E-mail");
            }

            if(alunos.get(posicao).getTelefone() == null || RegexUtil.retornarNumeros(alunos.get(posicao).getTelefone()).isBlank()){
                errorMessage = errorMessage.concat(" Telefone");
            }else if(
                    RegexUtil.retornarNumeros(alunos.get(posicao).getTelefone()).length() > 50 ||
                            RegexUtil.retornarNumeros(alunos.get(posicao).getTelefone()).length() < 11 ||
                            RegexUtil.retornarNumeros(alunos.get(posicao).getTelefone()).charAt(2) != '9'
            ){
                errorMessage = errorMessage.concat(" Telefone");
            }

            if (!errorMessage.isBlank()){
                if (alunos.get(posicao).getNome() != null && !alunos.get(posicao).getNome().isBlank()){
                    errorMessages.add(alunos.get(posicao).getNome() + " possui" + errorMessage + " inválido(s)");
                } else {
                    errorMessages.add("Aluno na "+ (posicao + 1) + "ª linha possui" + errorMessage + " inválido(s)" );
                }
            }
        }

        if(!errorMessages.isEmpty()){
            throw new AlunoException(errorMessages);
        }

    }
}
