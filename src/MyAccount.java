import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class MyAccount {
    // All the senders account managing and sending emails are done here
    private String myEmail;
    private String password;
    private String username;

    public MyAccount(String username,String email, String password) {
        this.myEmail = email;
        this.password = password;
        this.username = username;
    }

    private Session setLogin(){
        try {
            //setup login details
            Properties properties = new Properties();
            properties.put("mail.smtp.host", "smtp.gmail.com");
            properties.put("mail.smtp.port", "587");
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");

            Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(myEmail, password);
                }
            });
            return session;
        }
        catch (Exception e) {
            System.out.println("Message couldn't be sent\n" + e.getLocalizedMessage());
            return null;
        }
    }

    private Message prepareMessage(Session session,Email emailobj){
        //prepare the message adding content
        if (session != null) {
            try {
                Message message = new MimeMessage(session);

                message.addHeader("Content-type", "text/HTML; charset=UTF-8");
                message.addHeader("format", "flowed");
                message.addHeader("Content-Transfer-Encoding", "8bit");

                message.setFrom(new InternetAddress(myEmail, username));//username "testing cse"
                message.setRecipient(Message.RecipientType.TO, new InternetAddress(emailobj.getTo()));

                message.setSubject(emailobj.getSubject());

                message.setText(emailobj.getContent());

                message.setSentDate(emailobj.getDate());
                System.out.println("Message is ready");
                return message;
            } catch (Exception e) {
                System.out.println("Message couldn't be created\n" + e.getLocalizedMessage());
                return null;
            }
        }
        else
            return null;
    }
    public boolean sendEmail(Email email){
        //send email
        try {
            Transport.send(prepareMessage(setLogin(),email));
            System.out.println("Email Sent Successfully!!");
            return true;
        }
        catch (Exception e){
            System.out.println("Message couldn't be sent\n" + e.getLocalizedMessage());
            return false;
        }
    }
    public String getMyEmail() {return myEmail;}
    public String getUsername() {return username;}
}
