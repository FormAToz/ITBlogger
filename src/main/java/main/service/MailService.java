package main.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * Сервис для работы с почтой
 */
@Service
public class MailService {
    private final String ENCODING_OPTIONS = "text/html; charset=UTF-8";
    @Value("${spring.mail.username}")
    private String userName;

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Метод отправки сообщения адресату, используя JavaMailSender
     * @param emailTo email адрес получателя
     * @param subject тема письма
     * @param message текст письма
     */
    public void send(String emailTo, String subject, String message) throws MessagingException {
        MimeMessage mailMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mailMessage,"UTF-8");
        helper.setFrom(userName);
        helper.setTo(emailTo);
        helper.setSubject(subject);
        mailMessage.setContent(message, ENCODING_OPTIONS);

        mailSender.send(mailMessage);
    }
}