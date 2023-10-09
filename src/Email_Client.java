//200652D
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public final class Email_Client {
    public static MyAccount myAccount;
    public static Map<String,AbsRecipient> recipientList;
    public static Map<String,AbsRecipient> BDRecipientList;
    public static Map<String,List<Email>> sentBox;

    public static ArrayList<String> validRec ;

    public static void Start() {

        try {
            File Records = new File("Account.txt");
            Records.createNewFile();//create a new file if and only if the file named doesn't exist.


            File sendersData = new File("Account.txt");//get sender's account info from a text file
            Scanner AccountData = new Scanner(sendersData);
            String[] loginFields = new String[3]; //Username,email,password
            int i = 0;
            while (AccountData.hasNextLine() && i < 3) {
                String loginField = AccountData.nextLine().strip();

                if (loginField.isEmpty()) {
                    continue;
                }

                loginFields[i] = loginField;
                i++;
            }
            if (loginFields[0] == null || loginFields[1] == null || loginFields[2] == null) {
                System.out.println("no login data available or invalid login data");
                return;
            } else {
                myAccount = new MyAccount(loginFields[0], loginFields[1], loginFields[2]);//initialize myAccount
            }
        } catch (IOException e) {
            System.out.println("An IO error occurred.");
            e.printStackTrace();
        } catch (SecurityException e) {
            System.out.println("File access Denied");
        }

        //initialize lists and maps
        recipientList = new HashMap<>();
        BDRecipientList = new HashMap<>();
        sentBox = new HashMap<>();
        validRec = new ArrayList<>();

        //add valid types of recipients
        validRec.add("official");
        validRec.add("office_friend");
        validRec.add("personal");



        SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy/MM/dd");

        //restore previous emails
        ArrayList<Email> emailObjs = Email.deserialize();
        if (emailObjs != null) {

            Enumeration<Email> e = Collections.enumeration(emailObjs);
            while (e.hasMoreElements()) {
                Email temp = e.nextElement();
                String Today = DateFormat.format(temp.getDate());

                if (sentBox.get(Today) != null) {
                    sentBox.get(DateFormat.format(temp.getDate())).add(temp);
                } else {
                    List<Email> newEntry = new ArrayList<>();
                    newEntry.add(temp);
                    sentBox.put(Today, newEntry);
                }

            }
        }

        Scanner Records;
        //open the file and create recipient objects then store inside Lists
        try {
            File RecipientsData = new File("clientList.txt");
            Records = new Scanner(RecipientsData);
        } catch (FileNotFoundException e) {
            System.out.println("Recipients list unavailable.");
            return;
        }
        updateObjectLists(Records, true);

    }
    public static void updateObjectLists(Scanner Records,boolean initialize){//initialize true means when the program starts
        //read the scanner and create objects then store inside recipient Lists
        while (Records.hasNextLine()) {
            String data = Records.nextLine();

            if(data.isEmpty()){continue;}

            String[] Fields = data.strip().split(":");

            String type;
            String[] DataFields;

            if(Fields.length==2) {
                type = Fields[0];
                DataFields = Fields[1].strip().split(",");
            }else {continue;}

            AbsRecipient tempRecipient;

            if (DataFields.length==3) {//if an official recipient

                if(recipientList.get(DataFields[1]) == null) {
                    tempRecipient = RecipientFactory.createRecipient(type,DataFields[0],DataFields[1],DataFields[2]);
                    recipientList.put(tempRecipient.getEmail(), tempRecipient);
                    if(!initialize){updateRecords("clientList.txt",data);} //update clientList.txt
                }
                else {
                    System.out.println("Email Already Exists");
                }
            } else if (DataFields.length==4) {//if a personal rec. or an office friend
                boolean condition1 = (recipientList.get(DataFields[1]) == null) && type.equalsIgnoreCase("office_friend");
                boolean condition2 = (recipientList.get(DataFields[2]) == null) && type.equalsIgnoreCase("personal");
                if(condition1 || condition2) {
                    //create a recipient
                    tempRecipient = RecipientFactory.createRecipient(type,DataFields[0],DataFields[1],DataFields[2],FormatDate(DataFields[3],"yyyy/MM/dd"));

                    //add to the lists
                    recipientList.put(tempRecipient.getEmail(),tempRecipient);

                    //this block run only when adding a new recipient from Command line input
                    if(!initialize){updateRecords("clientList.txt","\n"+data);}

                    //send birthday messages if the new recipient's birthday is today
                    Date today = new Date();
                    boolean chkDay = ((IBirthday)tempRecipient).getBirthday().getDate()==today.getDate();
                    boolean chkMon = ((IBirthday)tempRecipient).getBirthday().getMonth()==today.getMonth();
                    if(chkDay && chkMon ) {
                        BDRecipientList.put(tempRecipient.getEmail(), tempRecipient);
                        System.out.println("Sending Birthday greetings to "+tempRecipient.getName());
                        sendMail(tempRecipient, "Happy Birthday", ((IBirthday) tempRecipient).createBirthdayMessage(myAccount.getUsername()));//send birthday greetings
                    }
                }
                else {
                    System.out.println("Email Already Exists");
                }

            }else {
                System.out.println("Invalid data entry");
            }

        }
        Records.close();


    }

    public static void updateRecords(String FileName,String content){
        //update changes to FileName.txt

        try {
            File Records = new File(FileName);
            Records.createNewFile();//create a new file if and only if the file named doesn't exist.
            //String path = System.getProperty("user.dir") + "\\src\\test.txt"; to get current directory

            FileWriter RecordToUpdate= new FileWriter(FileName,true);
            BufferedWriter writeBuffer = new BufferedWriter(RecordToUpdate);
            writeBuffer.write(content);
            writeBuffer.close();
            RecordToUpdate.close();
            System.out.println("clientList.txt updated");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }catch (SecurityException e){
            System.out.println("File access Denied");
        }
    }
    public static Date FormatDate(String date,String format){
        //check the validity of a given date(string) and return a date obj.

        Date tempDate;
        try {
            SimpleDateFormat DateFormat = new SimpleDateFormat(format);
            tempDate = DateFormat.parse(date);
        }catch (ParseException parseException){
            System.out.println("Invalid date");
            return null;
        }
        return tempDate;
    }
    public static void addRecipient(String input){
        //add a new recipient for a valid input given

        String[] InputFields= input.strip().split(":"); //input format - Official: nimal,nimal@gmail.com,ceo
        if (InputFields.length!=2){
            System.out.println("Invalid Recipient data input");
        }
        else {
            if (!validRec.contains(InputFields[0].strip().toLowerCase())){
                System.out.println("invalid recipient type");
                return;
            }

            String[] nextRecords=InputFields[1].strip().split(",");
            String FormattedRecipientData;
            InputFields[0] = InputFields[0].substring(0,1).toUpperCase() + InputFields[0].substring(1);//capitalize the first letter of type
            if (nextRecords.length==3){
                FormattedRecipientData = InputFields[0]+": "+nextRecords[0]+","+nextRecords[1]+","+nextRecords[2];
            } else if (nextRecords.length==4) {
                FormattedRecipientData = InputFields[0]+": "+nextRecords[0]+","+nextRecords[1]+","+nextRecords[2]+","+nextRecords[3];
            }
            else {
                System.out.println("Invalid Recipient data input");
                return;
            }
            //create recipients and update lists
            updateObjectLists(new Scanner(FormattedRecipientData),false);
        }
    }
    public static void sendMail(AbsRecipient receiver,String subject, String content){
        //send mails using given parameters via my account

        Date today =new Date();

        //create an email
        Email emailObj = new Email(myAccount.getMyEmail(),receiver, subject,content,today);
        SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String Today = DateFormat.format(today);

        if (myAccount.sendEmail(emailObj)){//if email was sent add to the list
            if (sentBox.get(Today) != null) {
                emailObj.serialize();//serialize the email
                sentBox.get(DateFormat.format(today)).add(emailObj);
            }
            else {
                List<Email> newEntry = new ArrayList<>();
                emailObj.serialize();//serialize the email
                newEntry.add(emailObj);
                sentBox.put(Today,newEntry);
            }
        }

    }
    public static void SHOWBDayRecipients(Date date) {
        //show birthday recipients on a given date
        boolean present=false;
        //find recipients with the given birthday.

        for (String mail: recipientList.keySet() ) {
            AbsRecipient temp = recipientList.get(mail);
            if (temp instanceof IBirthday) {
                boolean chkDay = ((IBirthday)temp).getBirthday().getDate()==date.getDate();
                boolean chkMon = ((IBirthday)temp).getBirthday().getMonth()==date.getMonth();
                if(chkDay && chkMon ) {
                    present=true;
                    System.out.println(temp.getName());
                }
            }
        }
        if(!present){//if no one is having birthday
            System.out.println("None");
        }
    }
    public static void AllRecipients() {
        //print all the recipients available

        AbsRecipient temp;
        for (String mail: recipientList.keySet() ) {
            temp = recipientList.get(mail);
            System.out.println(temp.getINFO()+"\n");
        }
    }
    public static void sentMails(Date date){
        //show all the mails which were sent on a given date

        SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String emailDate = DateFormat.format(date);

        List<Email> emails = sentBox.get(emailDate) ;
        if (emails != null) {
            for (Email email : emails) {
                System.out.println("TO: "+email.getTo()+" Subject: "+email.getSubject()+ " Content: "+ email.getContent());
            }
        }
        else {
            System.out.println("None");
        }
    }
    public static void recipientCount(){
        //prints total number of recipients

        System.out.println(AbsRecipient.getCount());
    }

    public static void main(String[] args) {

        System.out.println("Enter option type: \n"
                + "1 - Adding a new recipient\n"
                + "2 - Sending an email\n"
                + "3 - Printing out all the recipients who have birthdays\n"
                + "4 - Printing out details of all the emails sent\n"
                + "5 - Printing out the number of recipient objects in the application\n"
                + "6 - Printing out all the recipients\n"
                + "7 - Exit");
        Start();//initialize and update lists of recipients and send birthday greetings
        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {

                int option = scanner.nextInt();
                    switch (option) {
                        case 1:
                            System.out.println("Ex: \nPersonal: <NAME>,<NICKNAME>,<EMAIL>,<BIRTHDAY (YYYY/MM/DD)>\n" +
                                    "Official: <NAME>,<EMAIL>,<DESIGNATION>\n" +
                                    "Office_friend: <NAME>,<EMAIL>,<DESIGNATION>,<BIRTHDAY (YYYY/MM/DD)>");
                            scanner.nextLine();
                            String input1 = scanner.nextLine();// input format - Official: nimal,nimal@gmail.com,ceo
                            addRecipient(input1);

                            break;
                        case 2:
                            scanner.nextLine();
                            //send mail
                            String email, subject, content;
                            System.out.println("<Email>, <Subject>, <content>");
                            String input2 = scanner.nextLine();// input format - email, subject, content
                            String[] temp = input2.strip().split(",");
                            if (temp.length==3) {
                                email = temp[0].strip();
                                subject = temp[1].strip();
                                content = temp[2].strip();
                                if (recipientList.containsKey(email)) {
                                    sendMail(recipientList.get(email), subject, content);// code to send an email
                                } else {
                                    System.out.println("recipient not available. Press 1 to add new recipient");
                                }
                            }else {
                                System.out.println("please input in the correct format");}
                            break;
                        case 3:
                            System.out.println("Enter Date:");
                            String date1 = scanner.next();// input format - yyyy/MM/dd (ex: 2018/09/17)}

                            Date tempDate = FormatDate(date1,"yyyy/MM/dd");
                            Date dateToday = new Date();

                            boolean chkDay = dateToday.getDate()==tempDate.getDate();
                            boolean chkMon = dateToday.getMonth()==tempDate.getMonth();

                            if (chkDay && chkMon ) {
                                if(BDRecipientList.isEmpty()){
                                    System.out.println("None");
                                    break;}
                                for (String mail : BDRecipientList.keySet()) {
                                    System.out.println(BDRecipientList.get(mail).getName());
                                }
                            } else {
                                SHOWBDayRecipients(tempDate);// code to print recipients who have birthdays on the given date
                            }
                            break;
                        case 4:
                            System.out.println("Date of Mail:");
                            String date2 = scanner.next();// input format - yyyy/MM/dd (ex: 2018/09/17)
                            Date emailDate = FormatDate(date2.strip(),"yyyy/MM/dd");
                            sentMails(emailDate);// code to print the details of all the emails sent on the input date
                            break;
                        case 5:
                            recipientCount();// code to print the number of recipient objects in the application
                            break;
                        case 6:
                            AllRecipients();//print all recipients
                            break;
                        case 7:
                            return;//exit
                        default:
                            System.out.println("Invalid choice");
                    }
            } catch (Exception e) {
                System.out.println("Invalid input");
                if (scanner.hasNext())
                    scanner.nextLine(); //read until nextline
            }
        }
    }

        // start email client
        // code to create objects for each recipient in clientList.txt
        // use necessary variables, methods and classes

    }

// create more classes needed for the implementation (remove the  public access modifier from classes when you submit your code)




