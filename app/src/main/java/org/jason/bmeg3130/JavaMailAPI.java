package org.jason.bmeg3130;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.jason.bmeg3130.Utils;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class JavaMailAPI extends AsyncTask<Void, Void, Void> {

    private final Context context;
    private Session session;
    private final String email;
    private final String subject;
    private final String message;
    private ProgressDialog progressDialog;

    public JavaMailAPI(Context context, String email, String subject, String message) {
        this.context = context;
        this.email = email;
        this.subject = subject;
        this.message = message;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //Show progress dialog while sending email
        progressDialog = ProgressDialog.show(context,"Sending message", "Please wait...",false,false);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        //Dismiss progress dialog when message successfully send
        progressDialog.dismiss();

        //Show success toast
        Toast.makeText(context,"Email sent successfully",Toast.LENGTH_SHORT).show();
    }
    
    @Override
    protected Void doInBackground(Void... voids) {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com"); //SMTP Host
        properties.put("mail.smtp.port", "465"); //SMTP Port
        properties.put("mail.smtp.debug", "true");
        properties.put("mail.smtp.auth", "true"); //Enabling SMTP Authentication
//        properties.put("mail.smtp.socketFactory.port", "465"); //SSL Port
//        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); //SSL Factory Class
//        properties.put("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.ssl.enable", "true");
//        properties.put("mail.smtp.starttls.required", "true");

        Session session = Session.getDefaultInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(Utils.EMAIL, Utils.PASSWORD);
            }
        });
        session.setDebug(true);

        MimeMessage mimeMessage = new MimeMessage(session);
        try {
            System.out.println("start trying");
            mimeMessage.setFrom(new InternetAddress(Utils.EMAIL));
            mimeMessage.addRecipients(Message.RecipientType.TO, String.valueOf(new InternetAddress(email)));
            mimeMessage.setSubject(subject);
            mimeMessage.setText(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        try {
            System.out.println("start trying again");
            Transport.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
//        Transport transport = null;
//        try {
//            transport = session.getTransport("smtp");
//            transport.connect("smtp.gmail.com", 465, Utils.EMAIL, Utils.PASSWORD);
//            transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
//            transport.close();
//        } catch (MessagingException e) {
//            e.printStackTrace();
//        }


        return null;

    }
}