package com.tchorek.routes_collector.message.service;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

@NoArgsConstructor
@Service
public class MessageService {

    @Autowired
    MessageUtils messageUtils;

    public void prepareAndSendEmail(String subject, String content){
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Authenticator auth = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(messageUtils.getFromEmail(), messageUtils.getPassword());
            }
        };
        Session session = Session.getInstance(props, auth);
        prepareMessage(session, messageUtils.getToEmail(), subject, content.replaceAll("[\\[\\]]",""));
    }

    private void prepareMessage(Session session, String toEmail, String subject, String body){
        try
        {
            MimeMessage msg = new MimeMessage(session);
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");
            msg.setFrom(new InternetAddress(messageUtils.getFromEmail(), "Monitoring system"));
            msg.setReplyTo(InternetAddress.parse(messageUtils.getFromEmail(), false));
            msg.setSubject(subject, "UTF-8");
            msg.setText(body, "UTF-8");
            msg.setSentDate(new Date());
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
            Transport.send(msg);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
