import java.text.SimpleDateFormat;
import java.util.Date;

public class OfficialFriend extends OfficialRecipient implements IBirthday{
    private Date birthday;

    public OfficialFriend(String type,String name,String email,String designation,Date birthday){
        super(type,name,email,designation);
        this.birthday = birthday;
    }

    public Date getBirthday() {
        return birthday;
    }

    @Override
    public String createBirthdayMessage(String sendersName) {
        return "Wish you a Happy Birthday.\n"+sendersName;
    }

    protected String getDetails() {
        SimpleDateFormat F = new SimpleDateFormat("yyyy/MM/dd");
        return " | Birthday: "+ F.format(this.birthday);
    }
}

