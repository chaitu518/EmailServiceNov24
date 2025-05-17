package com.example.emailservice.consumers;

import com.example.emailservice.dto.SendEmailDto;
import com.example.emailservice.services.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

import java.util.Properties;

@Service
public class SendEmailConsumer {
    private ObjectMapper objectMapper;
    private EmailService emailService;

    public SendEmailConsumer(ObjectMapper objectMapper, EmailService emailService) {
        this.objectMapper = objectMapper;
        this.emailService = emailService;
    }

    //This method should be called if we receive an event for sending an email(Sigup)
    //This method/consumer should register itself to the singUp topic.
    @KafkaListener(topics = "singUp", groupId = "emailService")
    public void handleSignUpEvent(String message) {
        //We are getting String message.
        //Convert this string message to object using ObjectMapper
        try {
            SendEmailDto sendEmailDto = objectMapper.readValue(message, SendEmailDto.class);

            String smtpHostServer = "smtp.gmail.com";
            String emailID = "sampleid@gmail.com";

            Properties props = System.getProperties();
            props.put("mail.smtp.host", smtpHostServer);
            props.put("mail.smtp.port", "587"); //TLS Port
            props.put("mail.smtp.auth", "true"); //enable authentication
            props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS

            Authenticator auth = new Authenticator() {
                //override the getPasswordAuthentication method
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(sendEmailDto.getFrom(), "");
                }
            };

            Session session = Session.getInstance(props, auth);

            emailService.sendEmail(session, sendEmailDto.getTo(), sendEmailDto.getSubject(), sendEmailDto.getBody());
        } catch (Exception e) {
            System.out.println("Something went wrong.");
        }
    }
}
