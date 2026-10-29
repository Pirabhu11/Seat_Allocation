package com.allocation.services;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailService {

    // You will need to put your Gmail address and a Google "App Password" here!
    private static final String SENDER_EMAIL = "99250040553@klu.ac.in";
    private static final String APP_PASSWORD = "fxrn bktk acyp lmxe";

    public void sendAllocationEmail(String recipientEmail, String studentName, String subjectCode, String roomNumber, int deskRow, int deskCol, int seatPos, String dateTime) {
        
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, APP_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Examination Seat Allotment Order - " + subjectCode);
            
            String seatPositionString = (seatPos == 1) ? "Left" : (seatPos == 2) ? "Center" : "Right";
            
            String emailBody = "Dear " + studentName + ",\n"
                    + "Here is your examination seat allotment order:\n"
                    + "Subject Code: " + subjectCode + "\n"
                    + "Date & Time: " + dateTime + "\n"
                    + "Room Number: " + roomNumber + "\n"
                    + "Desk Location: Row " + deskRow + ", Column " + deskCol + " Seat Position: " + seatPositionString + "\n\n"
                    + "ALL THE BEST";

            message.setText(emailBody);

            Transport.send(message);
            System.out.println("Email successfully sent to: " + recipientEmail);

        } catch (MessagingException e) {
            e.printStackTrace();
            System.err.println("Failed to send email to " + recipientEmail);
        }
    }
}
