package com.cronograma.api.useCases.coordenador;

import com.cronograma.api.entitys.Coordenador;
import com.cronograma.api.entitys.Usuario;
import com.cronograma.api.exceptions.CoordenadorException;
import com.cronograma.api.useCases.coordenador.domains.CoordenadorRequestDom;
import com.cronograma.api.useCases.coordenador.domains.CoordenadorResponseDom;
import com.cronograma.api.useCases.coordenador.domains.CoordenadorUsuarioRequestDom;
import com.cronograma.api.useCases.coordenador.implement.mappers.CoordenadorMapper;
import com.cronograma.api.useCases.coordenador.implement.repositorys.CoordenadorCursoRepository;
import com.cronograma.api.useCases.coordenador.implement.repositorys.CoordenadorRepository;
import com.cronograma.api.useCases.coordenador.implement.repositorys.CoordenadorUsuarioRepository;
import com.cronograma.api.useCases.usuario.UsuarioService;
import com.cronograma.api.utils.regex.RegexUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CoordenadorService {

    private final CoordenadorRepository coordenadorRepository;
    private final CoordenadorCursoRepository coordenadorCursoRepository;
    private final CoordenadorUsuarioRepository coordenadorUsuarioRepository;

    private final CoordenadorMapper coordenadorMapper;
    private final UsuarioService usuarioService;

    public CoordenadorResponseDom carregarCoordenadorPorId(Long id){
        Coordenador coordenadorEncontrado = coordenadorRepository.findById(id)
                .orElseThrow(() -> new CoordenadorException("Nenhum coordenador encontrado!"));

        return coordenadorMapper.coordenadorParaCoordenadorResponseDom(coordenadorEncontrado);
    }

    public List<CoordenadorResponseDom> carregarCoordenador(){
        List<Coordenador> coordenadoresEncontrados = coordenadorRepository.findAll();

        return coordenadoresEncontrados.stream()
                .map(coordenadorMapper::coordenadorParaCoordenadorResponseDom)
                .sorted(Comparator.comparing(CoordenadorResponseDom::getNome))
                .toList();
    }

    @Transactional
    public void criarCoordenador(CoordenadorRequestDom coordenadorRequestDom){
        validarCampos(coordenadorRequestDom,null);

        CoordenadorUsuarioRequestDom coordenadorUsuarioRequestDom =
                coordenadorMapper.coordenadorRequestDomParaCoordenadorUsuarioRequestDom(coordenadorRequestDom);

        Usuario usuario = usuarioService.criarUsuario(coordenadorUsuarioRequestDom,"COORDENADOR");
        Coordenador coordenador = coordenadorMapper.coordenadorRequestDomParaCoordenador(coordenadorRequestDom,usuario);
        coordenadorRepository.save(coordenador);
    }

    @Transactional
    public void editarCoordenador(Long id, CoordenadorRequestDom coordenadorRequestDom){
        Coordenador coordenadorEncontrado = coordenadorRepository.findById(id)
                .orElseThrow(() -> new CoordenadorException("Nenhum coordenador encontrado!"));

        validarCampos(coordenadorRequestDom,coordenadorEncontrado.getUsuario().getCpf());

        CoordenadorUsuarioRequestDom coordenadorUsuarioRequestDom =
                coordenadorMapper.coordenadorRequestDomParaCoordenadorUsuarioRequestDom(coordenadorRequestDom);
        coordenadorMapper.coordenadorRequestDomParaCoordenadorEncontrado(coordenadorRequestDom,coordenadorEncontrado);

        usuarioService.editarUsuario(coordenadorEncontrado.getUsuario().getId(),coordenadorUsuarioRequestDom);
        coordenadorRepository.save(coordenadorEncontrado);
    }

    @Transactional
    public void excluirCoordenador(Long id){
        Coordenador coordenadorEncontrado = coordenadorRepository.findById(id)
                .orElseThrow(() -> new CoordenadorException("Nenhum coordenador encontrado!"));

        if(coordenadorCursoRepository.existsByCoordenadorId(id)){
            throw new CoordenadorException("O coordenador está sendo utilizado em cursos");
        }

        coordenadorRepository.deleteById(id);

        if(coordenadorEncontrado.getUsuario().getProfessor() == null && coordenadorEncontrado.getUsuario().getAluno() == null){
            usuarioService.excluirUsuario(coordenadorEncontrado.getUsuario().getId());
        }
    }
    
    private void validarCampos(CoordenadorRequestDom coordenador, String cpfAtual){
        List<String> errorMessages =  new ArrayList<>();

        if(coordenador.getNome() == null || coordenador.getNome().isBlank()){
            errorMessages.add("Nome é um campo obrigatório!");
        }

        if(coordenador.getCpf() == null || RegexUtil.retornarNumeros(coordenador.getCpf()).isBlank()){
            errorMessages.add("Cpf é um campo obrigatório!");
        } else if(RegexUtil.retornarNumeros(coordenador.getCpf()).length() != 11){
            errorMessages.add("Cpf inválido!");
        }else if (cpfAtual != null){
            if (
                !RegexUtil.retornarNumeros(coordenador.getCpf()).equals(cpfAtual) &&
                coordenadorUsuarioRepository.existsByCpf(RegexUtil.retornarNumeros(coordenador.getCpf()))
            ) {
                errorMessages.add("Cpf já está sendo utilizado!");
            }
        } else if (coordenadorUsuarioRepository.existsByCpf(RegexUtil.retornarNumeros(coordenador.getCpf()))) {
            errorMessages.add("Cpf já está sendo utilizado!");
        }

        if(coordenador.getEmail() == null || coordenador.getEmail().isBlank()){
            errorMessages.add("E-mail é um campo obrigatório!");
        } else if (!RegexUtil.validarEmail(coordenador.getEmail())) {
            errorMessages.add("E-mail inválido!");
        }

        if(coordenador.getTelefone() == null || RegexUtil.retornarNumeros(coordenador.getTelefone()).isBlank()){
            errorMessages.add("Telefone é um campo obrigatório!");
        }else if(
             RegexUtil.retornarNumeros(coordenador.getTelefone()).length() > 50 ||
             RegexUtil.retornarNumeros(coordenador.getTelefone()).length() < 11 ||
             RegexUtil.retornarNumeros(coordenador.getTelefone()).charAt(2) != '9'
        ){
            errorMessages.add("Telefone inválido!");
        }
        
        if(!errorMessages.isEmpty()){
            throw new CoordenadorException(errorMessages);
        }
        
    }

}
