package com.cronograma.api.useCases.aluno.domains;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class AlunoImportarRequestDom {
    private Long cursoId;
    private Long faseId;
    private MultipartFile arquivo;
}
