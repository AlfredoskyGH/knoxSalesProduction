package com.stf.oauth.admin.services;

import com.stf.oauth.admin.entities.UserSso;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.MessagingException;

public interface MailSenderService {
    JavaMailSender getJavaMailSender();
    void sendEmailToken(String destinatario, String urlToken, JavaMailSender emailSender) throws MessagingException;
    void sendEmailNewUser(UserSso userSso, String passworTemp, JavaMailSender emailSender) throws MessagingException;
}
