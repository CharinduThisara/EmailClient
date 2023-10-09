import java.io.*;
import java.util.ArrayList;
import java.util.Date;


public class Email implements Serializable {

    @Serial
    private static final long serialVersionUID = 8790094833264340296L;
    private static int emailCount=0;

    private transient AbsRecipient Receiver;
    private final String from,To,subject,content;
    private final Date date;

    public Email(String from, AbsRecipient receiver, String subject, String content, Date date) {

        this.from = from;
        this.To = receiver.getEmail();
        this.Receiver = receiver;
        this.subject = subject;
        this.content = content;
        this.date = date;
        emailCount++;
    }
    public void serialize(){

        //serialize this Email

        try {


            // to avoid GC involvement
            FileOutputStream newFOS = null;

            // Creating an new FileOutputStream object


            // If there is nothing to be write onto file
            if (emailCount==1) {
                newFOS = new FileOutputStream("H:\\JAVA1\\Email_Client\\SerializedEmails\\Emails.ser", false);
                ObjectOutputStream newOOS = new ObjectOutputStream(newFOS);
                newOOS.writeObject(this);
                newOOS.close();
            }

            // There is content in file to be write on
            else {
                newFOS = new FileOutputStream("H:\\JAVA1\\Email_Client\\SerializedEmails\\Emails.ser", true);
                OneByOneAppendObjectOutputstream newOOS = null;
                newOOS = new OneByOneAppendObjectOutputstream(newFOS);
                newOOS.writeObject(this);

                // Closing the FileOutputStream object
                newOOS.close();
            }

            newFOS.close();
            System.out.println("Email was serialized Successfully!!");
        }


        catch (Exception e) {
            System.out.println("Error Occurred" + e);
        }
    }

    public static ArrayList<Email> deserialize(){
        //deserialize the emails from the Emails.ser file and return all emails

        ArrayList<Email> EList = null;
        try {

            // If file doesn't exists
            FileInputStream fis = null;

            fis = new FileInputStream("H:\\JAVA1\\Email_Client\\SerializedEmails\\Emails.ser");
            ObjectInputStream ois
                    = new ObjectInputStream(fis);

            Email E = null;
            EList = new ArrayList<>();

            while (fis.available() != 0) {
                E = (Email) ois.readObject();
                EList.add(E);
                emailCount++;
            }
            // Closing the connection to release memory
            ois.close();
            fis.close();

            System.out.println("All Emails Deserialized");
        }

        // Catch block to handle the exception
        catch (Exception e) {
            System.out.println("No previous emails found.");

        }
        return EList;
}


    // }

    public String getContent() {
        return content;
    }
    public String getTo() {return To;}
    public String getSubject(){return subject;}
    public Date getDate(){return date;}
}

