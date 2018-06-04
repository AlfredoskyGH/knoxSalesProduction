package com.knox.oauth.admin.services;

import org.springframework.mail.javamail.JavaMailSender;

import com.knox.oauth.admin.entities.UserSso;

import javax.mail.MessagingException;

public interface MailSenderService {
    JavaMailSender getJavaMailSender();
    void sendEmailToken(String destinatario, String urlToken, JavaMailSender emailSender) throws MessagingException;
    void sendEmailNewUser(UserSso userSso, String passworTemp, JavaMailSender emailSender) throws MessagingException;
}
