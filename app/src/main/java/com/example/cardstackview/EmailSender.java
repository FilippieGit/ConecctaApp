package com.example.cardstackview;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
public class EmailSender {

    private final String emailRemetente;
    private final String senhaRemetente;
    private final Session session;

    public EmailSender(String emailRemetente, String senhaRemetente) {
        this.emailRemetente = emailRemetente;
        this.senhaRemetente = senhaRemetente;

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");  // SMTP do Gmail
        props.put("mail.smtp.port", "587");

        session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailRemetente, senhaRemetente);
            }
        });
    }

    public void enviarEmail(String destinatario, String assunto, String corpo) throws MessagingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(emailRemetente));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
        message.setSubject(assunto);
        message.setText(corpo);

        Transport.send(message);
    }
}
