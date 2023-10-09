import java.text.SimpleDateFormat;
import java.util.Date;

public class PersonalRecipient extends AbsRecipient implements IBirthday{
    private String nickname;
    private Date birthday;

    public PersonalRecipient(String type,String name, String nickname, String email, Date birthday){
        super(type, name,email);
        this.nickname=nickname;
        this.birthday = birthday;
    }

    public Date getBirthday() {
        return birthday;
    }

    @Override
    public String createBirthdayMessage(String sendersName) {
        return  "hugs and love on your birthday.\n"+sendersName;
    }

    @Override
     protected String getDetails() {
        SimpleDateFormat F = new SimpleDateFormat("yyyy/MM/dd");
        return " | Nick-name: "+this.nickname+" | Birthday: "+ F.format(this.birthday);
    }
}
