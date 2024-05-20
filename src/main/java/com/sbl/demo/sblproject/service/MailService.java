package com.sbl.demo.sblproject.service;

import com.sbl.demo.sblproject.common.Base;
import com.sbl.demo.sblproject.domain.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService extends Base {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String mailAddr;


    public boolean mailSend(HttpSession session) {

        try {
            User user = getUser(session);
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom(mailAddr);
            simpleMailMessage.setTo(user.getUsiEmail());
            simpleMailMessage.setSubject("title");
            simpleMailMessage.setText("content");
            javaMailSender.send(simpleMailMessage);
            return true;
        } catch (Exception e) {
            throw new MailSendException("");
        }

    }

}
