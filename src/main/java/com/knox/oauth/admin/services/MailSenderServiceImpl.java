package com.knox.oauth.admin.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.knox.oauth.admin.entities.UserSso;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Properties;


@Service
public class MailSenderServiceImpl implements MailSenderService {
    @Autowired
    public SimpleMailMessage template;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    @Value("${spring.mail.port}")
    private Integer port;

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(port);

        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }

    @Bean
    public SimpleMailMessage templateMessageToken() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setText(
                "<a>Recuperar Contraseña</a>");
        return message;
    }

    public void sendEmailToken(String destinatario, String urlToken, JavaMailSender emailSender) throws MessagingException {
        MimeMessage mimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "utf-8");
        String htmlMsg = "<h3>Para continuar con el cambio de contraseña haz clic <a href='" + urlToken + "'>aqu&iacute;</a></h3>";
        mimeMessage.setContent(htmlMsg, "text/html");
        helper.setTo(destinatario);
        helper.setSubject("Recuperación de Contraseña");
        emailSender.send(mimeMessage);
    }

    public void sendEmailNewUser(UserSso userSso, String passwordTemp, JavaMailSender emailSender) throws MessagingException {
        MimeMessage mimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "utf-8");
        String htmlMsg = "<h3>Estimado(a) " + userSso.getFirstname() + " " + userSso.getLastname() + "</h3>" +
                "<h3>Su registro dentro de nuestro módulo de autenticación ha sido satisfactorio.</h2>" +
                "<h3>Su clave de acceso temporal es: " + passwordTemp + "</h2>";
        mimeMessage.setContent(htmlMsg, "text/html");
        helper.setTo(userSso.getEmail());
        helper.setSubject("Registro SSO");
        emailSender.send(mimeMessage);
    }
}
