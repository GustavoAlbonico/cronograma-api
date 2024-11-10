package com.cronograma.api.infra.mensageria;

import com.cronograma.api.entitys.Usuario;
import com.cronograma.api.infra.security.TokenService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final TokenService tokenService;

    @Value("spring.mail.username")
    private String remetente;

    @Value("${spring.cors.origin}")
    private String origin;

    public void enviarEmailEsqueciMinhaSenha(Usuario usuario) throws MessagingException {//tratar
        String token = this.tokenService.gerarTokenRedefinirSenha(usuario);
        String url = origin + "/redefinirsenha?auth=" + token;

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setFrom(remetente);
        helper.setTo(usuario.getEmail());
        helper.setSubject("SenacPlan - Esqueci minha senha");

        String corpoHtml = "<html>" +
                "<body style='font-family: Arial, Helvetica, sans-serif; display: flex; justify-content: center; align-items: center;'>" +
                "<div style='border-radius: 10px; padding: 40px; background-color: #a6bdd3; display: flex; align-items: center; justify-content: center; flex-direction: column; gap: 10px;'>" +
                "<h1 style='font-size: 40px; color: #004A8D; margin: 0 0 10px 0;'>SenacPlan</h1>" +
                "<p style='color: #14548f; font-size: 16px; font-weight: bold;'>Para realizar a redefinição de senha</p>" +
                "<a style='padding: 12px 28px; background-color: #FDC180; border-radius: 10px; text-decoration: none; font-weight: bold; color: #074279;' href='" + url + "'>Clique aqui</a>" +
                "</div>" +
                "</body>" +
                "</html>";

        helper.setText(corpoHtml, true);

        mailSender.send(mimeMessage);

    }
}
