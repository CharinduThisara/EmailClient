import java.util.Date;

public class RecipientFactory {

    //class dedicated for creating recipients

    private RecipientFactory(){};//no need to create instances
    private static OfficialRecipient createOfficialRecipient(String type,String name,String email,String designation){
        return new OfficialRecipient(type,name,email,designation) ;
    }
    private static OfficialFriend createOfficialFriend(String type,String name,String email,String designation,Date birthday) {
        return new OfficialFriend(type,name,email,designation,birthday);
    }
    private static PersonalRecipient createPersonalRecipient(String type,String name, String nickname, String email, Date birthday) {
        return new PersonalRecipient(type,name,nickname,email,birthday);
    }

    //createRecipient method is overloaded
    public static AbsRecipient createRecipient(String type,String name,String email,String designation){
        //for official recipient
        return createOfficialRecipient(type,name,email,designation);
    }

    public static AbsRecipient createRecipient(String type,String name,String emailORNickName,String designationOREmail,Date birthday){
        if (type.toLowerCase().equals("office_friend")) {
            //for official friend
            return createOfficialFriend(type,name, emailORNickName, designationOREmail, birthday);
        }
        else {
            //for personal rec.
            return createPersonalRecipient(type,name,emailORNickName,designationOREmail,birthday);
        }
    }

}

