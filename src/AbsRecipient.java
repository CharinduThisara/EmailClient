
public abstract class AbsRecipient {
    //abstract class to include all basic functionality of a recipient
    private static int recipientCount=0;

    private String type,name,email;

    public AbsRecipient(String type, String name, String email){
        this.type = type;
        this.email=email;
        this.name=name;
        recipientCount++;
    }
    public static int getCount(){return recipientCount;}

    public String getName() {
        return name;
    }
    public String getEmail() {
        return email;
    }
    public String getType() {return type;}

    protected abstract String getDetails();//get subclass details

    public String getINFO(){
        return this.type+" >> Name: "+this.name+" | Email: "+ this.email+getDetails();
    }


}
